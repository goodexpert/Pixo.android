package com.example.apps.pixo.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.apps.pixo.R
import com.example.apps.pixo.ui.base.BaseActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_option_bar.*

class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val navController: NavController by lazy { Navigation.findNavController(this,
        R.id.nav_host_fragment
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.homeFragment) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_as_up)
                optionBar.visibility = View.GONE
            } else {
                optionBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
