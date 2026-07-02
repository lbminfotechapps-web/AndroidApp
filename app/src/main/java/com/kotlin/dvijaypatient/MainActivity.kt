package com.kotlin.dvijaypatient

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.kotlin.dvijaypatient.databinding.ActivityMainBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.ui.fragment.login.LoginFragment
import com.kotlin.dvijaypatient.ui.fragment.profile.ProfileFragment

class MainActivity : AppCompatActivity() {
     lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout
    var isLoggedIn:Boolean=false
    private lateinit var sharedPref: SharedPreferences
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (cameraGranted) {
                // TODO: Start camera
            }

            if (fineLocationGranted || coarseLocationGranted) {
                // TODO: Start location updates
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate view binding
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Set up the app bar
        //setSupportActionBar(activityMainBinding.appBarHome.toolbar)

        // Initialize toolbar, sidebar, navController
        init()

        Log.e("MainActivity", "MainActivity started")
    }

    private fun init() {

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        drawer = activityMainBinding.drawerLayout
        navView = activityMainBinding.navView

        sharedPref = getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        isLoggedIn = sharedPref.getBoolean("is_login", false)

        val user_name = sharedPref.getString("user_name", "")
        Log.e("user_name", user_name.toString())
        Log.e("is_login", isLoggedIn.toString())

        val tvUserName = navView.findViewById<TextView>(R.id.tv_navUserName)
        tvUserName.text = user_name

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_home) as NavHostFragment

        navController = navHostFragment.navController

        // ✅ Drawer navigation works without toolbar
        NavigationUI.setupWithNavController(navView, navController)

        setupBackPressHandling()
       // openFragment()
        activityMainBinding.appBarHome.ivLogout.setOnClickListener {
            showLogoutDialog()
        }

        activityMainBinding.appBarHome.tvProfile.setOnClickListener {
            openProfile()
        }



        if (isLoggedIn){
            navController.navigate(R.id.nav_home) // ID from nav_graph


            Log.e("usrName11",user_name.toString())
            val initials = user_name
                ?.replace("MR.", "", ignoreCase = true)
                ?.replace("MRS.", "", ignoreCase = true)
                ?.trim()
                ?.split(" ")
                ?.filter { it.isNotBlank() }
                ?.let { parts ->
                    when {
                        parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}"
                        parts.size == 1 -> "${parts.first().first()}"
                        else -> ""
                    }
                }
                ?.uppercase()
                ?: ""

            activityMainBinding.appBarHome.tvProfile.text = initials
            activityMainBinding.appBarHome.tvHeader.text = getGreeting()

        }else{

            navController.navigate(R.id.nav_login) // ID from nav_graph
        }

    }

    fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun performLogout() {

        val mainActivity = this // if inside MainActivity

        // 🔹 Clear SharedPreferences
        val pref = getSharedPreferences(ClassGlobal.PREFERENCES, MODE_PRIVATE)
        pref.edit().clear().apply()

        // 🔹 Load Login Fragment
        val fragment = LoginFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_home, fragment)
            .commit()

        // 🔹 Clear back stack (IMPORTANT)
       supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
    private fun openProfile() {
        if (navController.currentDestination?.id != R.id.nav_profile) {
            navController.navigate(R.id.nav_profile)
        }
    }
    /*private fun openFragment(){
        activityMainBinding.navMedicine.setOnClickListener {
            navController.navigate(R.id.nav_medicine) // ID from nav_graph
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }



        activityMainBinding.navHome.setOnClickListener {
            navController.navigate(R.id.nav_home) // ID from nav_graph
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }
        activityMainBinding.navProfile.setOnClickListener {
            navController.navigate(R.id.nav_profile) // ID from nav_graph
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }
        activityMainBinding.navLogin.setOnClickListener {
            handleLoginLogout()
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }
       *//* if (isLoggedIn){
            activityMainBinding.navLogin.setText("Logout")
            activityMainBinding.navAttendanceList.visibility=View.VISIBLE
            activityMainBinding.navView.visibility=View.VISIBLE

            activityMainBinding.navReqList.visibility=View.VISIBLE
            activityMainBinding.viewReq.visibility=View.VISIBLE

            activityMainBinding.navHome.visibility=View.VISIBLE
            navController.navigate(R.id.nav_home) // ID from nav_graph
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }else{
            activityMainBinding.navLogin.setText("Login")
            activityMainBinding.navAttendanceList.visibility=View.GONE
            activityMainBinding.navView.visibility=View.GONE

            activityMainBinding.navHome.visibility=View.GONE

            activityMainBinding.navReqList.visibility=View.GONE
            activityMainBinding.viewReq.visibility=View.GONE

            navController.navigate(R.id.nav_login) // ID from nav_graph
            drawer.closeDrawer(GravityCompat.START) // close drawer after click
        }*//*
    }*/


    // 🔹 This is mandatory for the hamburger icon to open the drawer
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupBackPressHandling() {
        when {
            Build.VERSION.SDK_INT >= 34 -> {
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() = onBack()
                })
            }
            Build.VERSION.SDK_INT >= 33 -> {
                window.onBackInvokedDispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT
                ) { onBack() }
            }
            else -> {
                onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() = onBack()
                })
            }
        }
    }


    fun onBack() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START) // Close drawer if open
            return
        }

        val currentDestinationId = navController.currentDestination?.id
        Log.e("currentDestinationId", currentDestinationId.toString())

        if (currentDestinationId == R.id.nav_home) {
            showExitDialog()
        } else {
            navController.navigateUp()
        }
    }

    private fun showExitDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> finishAffinity()
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }

        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .setCancelable(false)
            .setTitle("Exit")
            .show()
    }




}
