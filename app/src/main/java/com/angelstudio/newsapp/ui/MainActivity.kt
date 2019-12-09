package com.angelstudio.newsapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.generated.callback.OnClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)

    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private lateinit var fab: FloatingActionButton





    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        fab = findViewById(R.id.floatingActionButton)
        fab.alpha=(0.5f)




        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        // Set up Action Bar
        navController = navHostFragment.navController
        // Setup bottom navigation view
        bottom_nav.setupWithNavController(navController)
        setupActionBarWithNavController(navController)

        val selectedTheme =preferences.getBoolean(this.getString(R.string.theme_setting),false)
        if(selectedTheme == true){
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)

    }
}
