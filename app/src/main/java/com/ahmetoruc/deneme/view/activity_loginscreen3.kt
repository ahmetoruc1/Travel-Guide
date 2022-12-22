package com.ahmetoruc.deneme.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ahmetoruc.deneme.databinding.ActivityLoginscreen3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class activity_loginscreen3 : AppCompatActivity() {
    private lateinit var binding: ActivityLoginscreen3Binding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginscreen3Binding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


        auth= Firebase.auth

        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent=Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

        /*binding.signUpintent.setOnClickListener{
            val intent=Intent(this@activity_loginscreen3,SignUpActivity::class.java)
            startActivity(intent)
        }*/
    }
    fun signin(view: View){
        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()
        //val username=binding.usernameText.text.toString()

        auth.signInWithEmailAndPassword(email,password)


        if (email.equals("")||password.equals("")){
            Toast.makeText(this@activity_loginscreen3,"Enter email and Password",Toast.LENGTH_LONG).show()

        }else{
            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val intent=Intent(this@activity_loginscreen3, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@activity_loginscreen3,it.localizedMessage,Toast.LENGTH_LONG).show()
                }

        }
        //intent.putExtra("username",username)
    }
    fun signup(view:View){
        val email=binding.emailText.text.toString()
        val password=binding.passwordText.text.toString()

        if (email.equals("")||password.equals("")){
            Toast.makeText(this@activity_loginscreen3,"Enter email and password",Toast.LENGTH_LONG).show()
        }else{
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val intent=Intent(this@activity_loginscreen3, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@activity_loginscreen3,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }

    }
    fun signUp(view:View){
            val intent=Intent(this@activity_loginscreen3,SignUpActivity::class.java)
            startActivity(intent)

    }
}