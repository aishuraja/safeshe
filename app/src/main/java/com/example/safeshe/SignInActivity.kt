package com.example.safeshe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        title="Login"

        auth= FirebaseAuth.getInstance()
    }

    fun login(view: View){
        val email= "subhajit@gmail.com"
        var password = "fewwewe"
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun goToRegister(view: View){
        val intent= Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }
}