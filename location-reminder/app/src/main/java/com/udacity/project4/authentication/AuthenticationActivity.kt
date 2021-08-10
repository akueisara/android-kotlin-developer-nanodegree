package com.udacity.project4.authentication

import android.os.Bundle
import android.util.Log
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
        binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(this, R.layout.activity_authentication)

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
            // Successfully signed in
            // TODO: If the user was authenticated, send him to RemindersActivity
        } else {
            // Sign in failed
            if (response == null) {
                // User cancelled
                return
            }
            if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                // No internet connection
                return
            }
            // Unknown error
            Log.e(TAG, "Sign-in error: ", response.error)
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
            // TODO: a bonus is to customize the sign in flow to look nice using :
            //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
//            .setAuthMethodPickerLayout(customLayout)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
