package com.angelstudio.newsapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)

    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private lateinit var fab: FloatingActionButton





    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        fab = binding.floatingActionButton
        fab.alpha=(0.5f)




        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return
        // Set up Action Bar
        navController = navHostFragment.navController
        // Setup bottom navigation view
        binding.bottomNav.setupWithNavController(navController)
        setupActionBarWithNavController(navController)

        val selectedTheme =preferences.getBoolean(this.getString(R.string.theme_setting),false)
        if(selectedTheme == true){
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    private fun applyWindowInsets() {
        val root = binding.root
        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom
        val safeAreaTypes = WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.displayCutout()

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val safeArea = insets.getInsets(safeAreaTypes)
            view.updatePadding(
                left = initialLeft + safeArea.left,
                top = initialTop + safeArea.top,
                right = initialRight + safeArea.right,
                bottom = initialBottom + safeArea.bottom
            )
            // The root owns these insets; Material children must not apply them again.
            WindowInsetsCompat.Builder(insets)
                .setInsets(safeAreaTypes, Insets.NONE)
                .setInsetsIgnoringVisibility(safeAreaTypes, Insets.NONE)
                .setDisplayCutout(null)
                .build()
        }
        ViewCompat.requestApplyInsets(root)
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
