package com.example.notesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.voice.VoiceInteractionSession.ActivityId
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {

    private lateinit var signInButton: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "SignInActivity"
    private lateinit var auth :FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton = findViewById(R.id.signInButton)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()
        auth =Firebase.auth

        signInButton.setOnClickListener{
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        val  currentUser =auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn(){
       val signInIntent = googleSignInClient.signInIntent

        getResult.launch(signInIntent)
    }
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            class YourActivity : AppCompatActivity() {

                // ...
                private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
                private var showOneTapUI = true
                // ...

                override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    super.onActivityResult(requestCode, resultCode, data)

                    when (requestCode) {
                        REQ_ONE_TAP -> {
                            try {
                                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                                val idToken = credential.googleIdToken
                                when {
                                    idToken != null -> {
                                        // Got an ID token from Google. Use it to authenticate
                                        // with Firebase.
                                        Log.d(TAG, "Got ID token.")
                                    }
                                    else -> {
                                        // Shouldn't happen.
                                        Log.d(TAG, "No ID token!")
                                    }
                                }
                            } catch (e: ApiException) {

                            }
                            }
                        }
                    }

                }

        }
    }
    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        GlobalScope.launch(Dispatchers.IO) {
            val auth =auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }

        }
    }
    private fun updateUI(firebaseUser:FirebaseUser?){
        if (firebaseUser !=null){
            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}