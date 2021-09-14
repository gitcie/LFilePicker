package com.leon.lfilepickerlibrary.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leon.lfilepickerlibrary.R

class AntFilePickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LFileTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ant_file_picker)
        initToolBar()
        initBottomNavigationController()
        val asyncHandler = Handler()
        asyncHandler.postDelayed({
            findViewById<BottomNavigationView>(R.id.ant_tab_controller).apply {
//                closeAnimation(this)
//                postInvalidate()
            }
        }, 300)
    }

    private fun initToolBar() {
        findViewById<Toolbar>(R.id.ant_tool_bar)?.apply {
            setSupportActionBar(this)
        }
        supportActionBar?.title = "文件选择"
    }

    private fun initBottomNavigationController() {
//        val navigationView = findViewById<BottomNavigationView>(R.id.ant_tab_controller)
//        val navController = Navigation.findNavController(this, R.id.ant_host_fragment)
//        val tabIds: MutableSet<Int> = LinkedHashSet()
//        tabIds.add(R.id.navigation_latest)
//        tabIds.add(R.id.navigation_browse)
//        val listener = OnNavigateUpListener { false }
//        val barConfiguration = AppBarConfiguration.Builder(tabIds)
//            .setOpenableLayout(null)
//            .setFallbackOnNavigateUpListener(listener)
//            .build()
//        NavigationUI.setupActionBarWithNavController(this, navController, barConfiguration)
//        NavigationUI.setupWithNavController(navigationView, navController)

        val navView: BottomNavigationView = findViewById(R.id.ant_tab_controller)

        val navController = findNavController(R.id.ant_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_latest, R.id.navigation_browse))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    @SuppressLint("RestrictedApi")
    fun closeAnimation(view: BottomNavigationView) {
        val mMenuView = view.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until mMenuView.childCount) {
            val button = mMenuView.getChildAt(i) as BottomNavigationItemView
            val mLargeLabel = getField<TextView>(button.javaClass, button, "largeLabel")!!
            val mSmallLabel = getField<TextView>(button.javaClass, button, "smallLabel")!!
            val mSmallLabelSize = mSmallLabel.textSize
            setField(button.javaClass, button, "shiftAmount", 0f)
            setField(button.javaClass, button, "scaleUpFactor", 1f)
            setField(button.javaClass, button, "scaleDownFactor", 1f)
            mLargeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSmallLabelSize)
        }
        mMenuView.updateMenuView()
    }

    private fun <T> getField(targetClass: Class<*>, instance: Any, fieldName: String): T? {
        try {
            val field = targetClass.getDeclaredField(fieldName)
            field.isAccessible = true
            return field[instance] as T
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    private fun setField(targetClass: Class<*>, instance: Any, fieldName: String, value: Any) {
        try {
            val field = targetClass.getDeclaredField(fieldName)
            field.isAccessible = true
            field[instance] = value
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}