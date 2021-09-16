package com.leon.lfilepickerlibrary.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leon.lfilepickerlibrary.R
import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog

class AntFilePickActivity : AppCompatActivity(), RefreshableView {

    companion object {
        const val SELECT_PATHS = "paths"
        const val REQUEST_STORAGE_PERMISSION = 0x70;
    }

    private val TAG = "AntFilePickerActivity"

    private lateinit var fragHost: NavHostFragment

    private var hasRequestPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LFileTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ant_file_picker)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestStoragePermission()
        }
        initToolBar()
        initBottomNavigationController()
    }

    private fun requestStoragePermission() {
        val permissions = arrayOf(
            permission.WRITE_EXTERNAL_STORAGE
        )
        val grantFlag = ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
        if(grantFlag != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERMISSION)
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshView()
            } else {
                if(!hasRequestPermission) {
                    showPermissionRequireTip()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionRequireTip() {
        AlertDialog.Builder(this)
            .setTitle("文件授权")
            .setMessage("选择文件需要读取SD卡权限，立即授权？")
            .setPositiveButton("授权") { dialog, _ ->
                dialog.dismiss()
                if(!hasRequestPermission) {
                    hasRequestPermission = true
                    requestStoragePermission()
                }
            }
            .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    override fun refreshView() {
        fragHost.childFragmentManager.fragments.forEach {
            if (it is RefreshableView) {
                it.refreshView()
            }
        }
    }
}