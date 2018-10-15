/*
 * Created by Titi Arimba on 10/13/18 12:10 AM
 * Last Modified on 10/13/18 12:10 AM
 */

package com.titiarimba.tictactoygame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth?=null
    private var database=FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()
    }

    fun buLoginEvent(view: View){
        LoginToFirebase(edtName.text.toString(), edtPassword.text.toString())
    }

    fun LoginToFirebase(email:String, password:String){
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    if (task.isSuccessful){

                    Toast.makeText(applicationContext, "Successfull Login", Toast.LENGTH_LONG).show()

                    //save in database
                    var currentUser=mAuth!!.currentUser
                    if (currentUser!=null) {
                        myRef.child("Users").child(SplitString(currentUser.email.toString())).setValue(currentUser.uid)
                    }
                    LoadMain()
                    } else {
                    Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()

                    }
                }
    }

    override fun onStart() {
        super.onStart()
        LoadMain()
    }

    fun LoadMain(){

        var currentUser=mAuth!!.currentUser

        if (currentUser!=null) {

            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)

            startActivity(intent)
        }
    }

    fun SplitString(str:String):String{
        var split = str.split("@")
        return split[0]
    }
}
