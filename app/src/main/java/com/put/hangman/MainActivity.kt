package com.put.hangman

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*
import com.firebase.ui.auth.AuthMethodPickerLayout



class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 1
    private var providers: List<AuthUI.IdpConfig>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initProviders()

        showSignInOptions()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val response: IdpResponse? = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, user?.email, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, response?.error?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initProviders() {
        providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
    }

    private fun showSignInOptions() {
        val customLayout = AuthMethodPickerLayout.Builder(R.layout.activity_login)
            .setEmailButtonId(R.id.email_button)
            .setGoogleButtonId(R.id.google_button)
            .build()

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(providers!!)
                .setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
                .build(),
            REQUEST_CODE
        )
    }

    private fun logout() {
        AuthUI.getInstance().signOut(this@MainActivity).addOnCompleteListener {
            showSignInOptions()
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}
