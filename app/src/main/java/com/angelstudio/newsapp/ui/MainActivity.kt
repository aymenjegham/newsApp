package com.angelstudio.newsapp.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.angelstudio.newsapp.R
import com.yariksoffice.lingver.Lingver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*

class MainActivity : AppCompatActivity() {

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }




        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
            ?: return
        // Set up Action Bar
        val navController = host.navController
        // Setup bottom navigation view
        bottom_nav.setupWithNavController(navController)

        val selectedTheme =preferences.getBoolean(this.getString(R.string.theme_setting),false)
        if(selectedTheme == true){
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO)
        }





    }
}
