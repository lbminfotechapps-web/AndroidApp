package com.kotlin.dvijaypatient

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.ui.fragment.login.LoginFragment

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // For Android 12+ splash
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_login", false)


        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("is_login", isLoggedIn)

        startActivity(intent)
        finish()
    }
    private fun showLoginFragment() {
        // Replace the fragment directly into the FragmentContainerView
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, loginFragment)
            .commit()
    }

}
