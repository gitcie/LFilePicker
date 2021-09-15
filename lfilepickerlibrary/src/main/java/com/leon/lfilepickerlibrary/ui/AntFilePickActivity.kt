package com.leon.lfilepickerlibrary.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leon.lfilepickerlibrary.R

class AntFilePickActivity : AppCompatActivity() {

    companion object {
        const val SELECT_PATHS = "paths"
    }

    private val TAG = "AntFilePickerActivity"

    private lateinit var fragHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LFileTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ant_file_picker)
        initToolBar()
        initBottomNavigationController()
    }

    private fun initToolBar() {
        findViewById<Toolbar>(R.id.ant_tool_bar)?.apply {
            setSupportActionBar(this)
        }
        supportActionBar?.title = "文件选择"
    }

    private fun initBottomNavigationController() {
        val navView: BottomNavigationView = findViewById(R.id.ant_tab_controller)
        fragHost = supportFragmentManager.findFragmentById(R.id.ant_host_fragment) as NavHostFragment
        val navController = findNavController(R.id.ant_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_latest, R.id.navigation_browse))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            var processed = false
            fragHost.childFragmentManager.fragments.apply {
                for (frag in this) {
                    if (frag is BackPressProcessor) {
                        processed = frag.processBackPress(this@AntFilePickActivity)
                        if(processed) {
                            break
                        }
                    }
                }
            }
            if (processed) {
                true
            } else {
                super.onKeyDown(keyCode, event)
            }
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}