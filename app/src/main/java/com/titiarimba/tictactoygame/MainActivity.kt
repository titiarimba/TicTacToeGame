/*
 * Created by Titi Arimba on 10/9/18 9:44 PM
 * Last Modified on 10/9/18 9:44 PM
 */

package com.titiarimba.tictactoygame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    //database instance
    private var database= FirebaseDatabase.getInstance()
    private var myRef = database.reference

    var myEmail:String?=null

    private var mFirebaseAnalytics:FirebaseAnalytics?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var b:Bundle=intent.extras
        myEmail=b.getString("email")
        incomingCalls()
    }

    protected fun buClick(view: android.view.View){
        val buSelected = view as android.widget.Button
        var cellID = 0
        when(buSelected.id){
            R.id.button -> cellID = 1
            R.id.button2 -> cellID = 2
            R.id.button3 -> cellID = 3
            R.id.button4 -> cellID = 4
            R.id.button5 -> cellID = 5
            R.id.button6 -> cellID = 6
            R.id.button7 -> cellID = 7
            R.id.button8 -> cellID = 8
            R.id.button9 -> cellID = 9
        }

//        Toast.makeText(this, "ID:"+ cellID, Toast.LENGTH_LONG).show()

        myRef.child("PlayerOnline").child(sessionID!!).child(cellID.toString()).setValue(myEmail)
//        playGame(cellID, buSelected)
//        CheckWiner()
    }

    var player1 = java.util.ArrayList<Int>()
    var player2 = java.util.ArrayList<Int>()
    var activePlayer = 1

    fun playGame(cellID:Int, buSelected: Button){
        if (activePlayer == 1){
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellID)
            activePlayer = 2
        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.pink)
            player2.add(cellID)
            activePlayer = 1
        }

        buSelected.isEnabled=false
        CheckWiner()
    }

    fun CheckWiner(){

        var winer = -1

        //row 1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)){
            winer = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)){
            winer = 2
        }

        //row 2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)){
            winer = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)){
            winer = 2
        }

        //row 3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)){
            winer = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)){
            winer = 2
        }


        //col 1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)){
            winer = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)){
            winer = 2
        }

        //col 2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)){
            winer = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)){
            winer = 2
        }

        //col 3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)){
            winer = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)){
            winer = 2
        }

        if (winer != -1){

            if (winer==1){
                Toast.makeText(this, "Player 1 win the game", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Player 2 win the game", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun AutoPlay(cellID: Int){

        /*
        var emptyCells = ArrayList<Int>()
        for ( cellID in 1..9){

            if (!(player1.contains(cellID) || player2.contains(cellID))){
                emptyCells.add(cellID)
            }
        }

        var r = Random()
        val randIndex = r.nextInt(emptyCells.size-0)+0
        val cellID = emptyCells.get(randIndex)
        */


        var buSelect:Button?
        when(cellID){
            1-> buSelect=button
            2-> buSelect=button2
            3-> buSelect=button3
            4-> buSelect=button4
            5-> buSelect=button5
            6-> buSelect=button6
            7-> buSelect=button7
            8-> buSelect=button8
            9-> buSelect=button9
            else -> {
                buSelect=button
            }
        }

        playGame(cellID, buSelect)

    }

    protected fun btnRequestEvent(view: View){
        var userEmail = edtEmail.text.toString()
        myRef.child("Users").child(SplitString(userEmail)).child("Request")
                .push().setValue(myEmail)
        PlayerOnline(SplitString(myEmail!!)+SplitString(userEmail))
        PlayerSymbol="X"
    }

    protected fun btnAcceptEvent(view: View){
        var userEmail = edtEmail.text.toString()
        myRef.child("Users").child(SplitString(userEmail)).child("Request")
                .push().setValue(myEmail)
        PlayerOnline(SplitString(myEmail!!)+SplitString(userEmail))
        PlayerSymbol="O"
    }

    var sessionID:String?=null
    var PlayerSymbol:String?=null

    fun PlayerOnline(sessionID:String){
        this.sessionID=sessionID
        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionID)
                .addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        try {
                            player1.clear()
                            player2.clear()
                            val td = p0.value as HashMap<String,Any>
                            if (td!=null){
                                var value:String
                                for (key in td.keys){
                                    value=td[key] as String

                                    if (value!= myEmail){
                                        activePlayer = if (PlayerSymbol==="X")1 else 2
                                    }else{
                                        activePlayer = if (PlayerSymbol==="X")2 else 1
                                    }
                                    AutoPlay(key.toInt())
                                }
                            }
                        }catch (ex:Exception){}
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
    }

    fun incomingCalls(){
        myRef.child("Users").child(SplitString(myEmail!!)).child("Request")
                .addValueEventListener(object:ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot) {

                        try {

                            val td = p0.value as HashMap<String, Any>
                            if (td!=null){

                                var value:String
                                for (key in td.keys){
                                    value=td[key] as String
                                    edtEmail.setText(value)
                                    myRef.child("Users").child(SplitString(myEmail!!)).child("Request").setValue(true)
                                    break
                                }
                            }
                        }catch (ex:Exception){}
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
    }

    fun SplitString(str:String):String{
        var split = str.split("@")
        return split[0]
    }
}
