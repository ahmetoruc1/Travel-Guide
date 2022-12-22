package com.ahmetoruc.deneme.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ahmetoruc.deneme.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        auth= Firebase.auth
        firestore= Firebase.firestore

        val currentUser=auth.currentUser
        if (currentUser!=null){
            val intent=Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signup(view:View) {
        val email = binding.signUpEmail.text.toString()
        val password = binding.signUpPassword.text.toString()


        if (!email.equals("") || !password.equals("") && binding.signUpPassword.text == binding.signUpConfirmpassword.text) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(this@SignUpActivity, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@SignUpActivity, it.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }


        } else {

            Toast.makeText(this@SignUpActivity, "Enter email and password or Password and Confirm Password does not match", Toast.LENGTH_LONG)
                .show()

        }
    }

    /*
    fun signup(view: View) {
        val email = binding.signUpEmail.text.toString()
        val password = binding.signUpPassword.text.toString()

        if (email.equals("") || password.equals("")) {
            Toast.makeText(this@SignUpActivity, "Enter email and password", Toast.LENGTH_LONG)
                .show()
            /*if (binding.signUpPassword.text!=binding.signUpConfirmpassword.text){
                Toast.makeText(this@SignUpActivity,"Password and Confirm Password does not match",Toast.LENGTH_SHORT)
            }else{*/
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val intent = Intent(this@SignUpActivity, FeedActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@SignUpActivity, it.localizedMessage, Toast.LENGTH_LONG)
                            .show()
           // }
                /*
                if(auth.currentUser!=null){
                    val postMap= hashMapOf<String,Any>()
                    postMap.put("User Name",binding.signUpUsername.text.toString())

                    firestore.collection("Post").add(postMap).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener { ex->
                        Toast.makeText(this,ex.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }*/
        }
        }
    }*/
}