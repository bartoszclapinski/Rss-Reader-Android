package com.example.rssreader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.rssreader.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("47809683924-u9rsrkced59dujuheqite8r1vj192lng.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.buttonGoogle.setOnClickListener {
            Log.d(TAG, "onCreate: begin Google SignIN")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        binding.textLinkRegister.setOnClickListener {
            Log.d(TAG, "onCreate: register text link click")
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            finish()
        }

        binding.buttonLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.inputEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.inputPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val userEmail = binding.inputEmail.text.toString().trim { it <= ' ' }
                    val userPassword = binding.inputPassword.text.toString().trim { it <= ' ' }

                    firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@MainActivity,
                                        "Logged Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent =
                                        Intent(this@MainActivity, MainFeedActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("uid", firebaseAuth.currentUser!!.uid)
                                    intent.putExtra("email", userEmail)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                }
            }
        }
    }


    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this@MainActivity, MainFeedActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult Err: ${e}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email

                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: ${uid}")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: ${email}")

                if (authResult.additionalUserInfo!!.isNewUser) {
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created \n $email")
                    Toast.makeText(
                        this@MainActivity,
                        "Account created \n $email",
                        Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user \n $email")
                    Toast.makeText(
                        this@MainActivity,
                        "Existing user \n $email",
                        Toast.LENGTH_SHORT)
                        .show()
                }

                startActivity(Intent(this@MainActivity, MainFeedActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Login failed due to \n ${e.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Login failed due to \n ${e.message}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
    }


}