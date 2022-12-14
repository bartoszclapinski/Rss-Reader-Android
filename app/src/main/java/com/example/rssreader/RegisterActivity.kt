package com.example.rssreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.rssreader.databinding.ActivityMainBinding
import com.example.rssreader.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "REGISTER_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textLinkLogin.setOnClickListener{
            Log.d(TAG, "onCreate: login text link click")
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }

        binding.buttonRegister.setOnClickListener{
            when {
                TextUtils.isEmpty(binding.inputEmail.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.inputPassword.text.toString().trim{it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val userEmail = binding.inputEmail.text.toString().trim { it <= ' ' }
                    val userPassword = binding.inputPassword.text.toString().trim { it <= ' ' }

                    firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@RegisterActivity, MainFeedActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("uid", firebaseUser.uid)
                                intent.putExtra("email", userEmail)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
        }
        }
    }
}