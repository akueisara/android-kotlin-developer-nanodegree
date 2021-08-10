package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "AuthenticationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.loginButton.setOnClickListener { launchSignInFlow() }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult? ->
        // Handle the FirebaseAuthUIAuthenticationResult
        result?.let {
            onSignInResult(result)
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // If the user was authenticated, send him to RemindersActivity
            startActivity(Intent(this, RemindersActivity::class.java))
            finish()
        } else {
            // Sign in failed
            if (response == null) {
                // User cancelled
                Toast.makeText(this, R.string.sign_in_cancelled, Toast.LENGTH_SHORT).show()
                return
            }
            if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                // No internet connection
                Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
                return
            }

            if (response.error?.errorCode == ErrorCodes.ERROR_USER_DISABLED) {
                Toast.makeText(this, R.string.account_disabled, Toast.LENGTH_SHORT).show()
                return
            }
            // Unknown error
            val errorMessage = getString(R.string.unknown_error, " ${response.error}")
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            Log.e(TAG, errorMessage)
        }
    }

    // Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            // This is where you can provide more ways for users to register and
            // sign in.
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.map)
            .setTheme(R.style.FirebaseTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
