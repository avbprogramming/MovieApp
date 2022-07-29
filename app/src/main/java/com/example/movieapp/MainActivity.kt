package com.example.movieapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database =
            Firebase.database.getReferenceFromUrl("https://movieapp-e0dd5-default-rtdb.europe-west1.firebasedatabase.app/")
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
// Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            // отключение Smart Lock, необходимо вначале, в противном случае необходимо подключать SHA
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse // результат с экрана Firebase Auth
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d("testLog", "RegistrationActivity registration success ${response?.email}")
            val authUser =
                FirebaseAuth.getInstance().currentUser // создаем объект текущего пользователя
            authUser?.let { //если он существует, мы сохраняем его в БД
                val email = it.email.toString() // извлекаем умаил пользователя
                val uid = it.uid // извлекаем uid пользователя
                val firebaseUser =
                    User(email, uid) // создаем новый объект USER с параметрами email и uid
                Log.d("testLog", "Registration Activity firebaseUser $firebaseUser")
                database.child("users").child(uid)
                    .setValue(firebaseUser) // сохраняем нашего пользователя в бибилиотеку

                val intentToAnotherScreen = Intent(this, MoviesActivity::class.java)
                startActivity(intentToAnotherScreen)
                Toast.makeText(this@MainActivity, "You clicked me", Toast.LENGTH_LONG).show()

            }
        } else {
            Log.d("testLog", "RegistrationActivity registration failure")
        }
    }
}

