package com.leon.filepicker.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.leon.lfilepickerlibrary.LFilePicker
import com.leon.lfilepickerlibrary.ui.AntFilePickActivity

/**
 *
 * @author powerdata
 * @since 9/16/21
 */
class FilePickApp: Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private const val TAG = "FilePickApp"
    }



    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if(activity is MainActivity) {
            val activityForResult = StartActivityForResult()
            val callback = ActivityResultCallback<ActivityResult> { result ->
                result?.apply {
                    if (resultCode == Activity.RESULT_OK) {
                        result.data?.apply {
                            processActivityResult(this)
                        }
                    }
                }
            }
            LFilePicker.pickLauncher = activity.registerForActivityResult(activityForResult, callback)
        }
    }

    private fun processActivityResult(data: Intent) {
        val path = data.getStringExtra("path")
        Toast.makeText(applicationContext, "选中的路径为$path", Toast.LENGTH_SHORT).show()
        Log.i("LeonFilePicker", path!!)
        val paths: List<String>? = data.getStringArrayListExtra(AntFilePickActivity.SELECT_PATHS)
        if (paths != null && !paths.isEmpty()) {
            Log.e("MainActivity", "选中的文件" + paths[0])
        }
        val multiFiles = data.clipData
        if (multiFiles != null && multiFiles.itemCount > 0) {
            for (i in 0 until multiFiles.itemCount) {
                Log.e("sf", multiFiles.getItemAt(i).uri.toString())
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}