package com.leon.lfilepickerlibrary

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.leon.lfilepickerlibrary.model.FilePickConfig
import com.leon.lfilepickerlibrary.ui.AntFilePickActivity

/**
 *
 * @author powerdata
 * @since 9/17/21
 */
class AntFilePicker private constructor(): Application.ActivityLifecycleCallbacks, ActivityResultCallback<ActivityResult>{

    companion object {
        private const val TAG = "AntFilePicker"
        val instance: AntFilePicker by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AntFilePicker()
        }
    }

    private var filePickConfig = FilePickConfig()

    private val pickerLaunchers: MutableMap<Activity, ActivityResultLauncher<Intent>> by lazy {
        mutableMapOf()
    }

    private var activityResultCallback: ActivityResultCallback<ActivityResult>? = null

    fun init(application: Application, config: FilePickConfig? = null) {
        config?.apply {
            filePickConfig = this
        }
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getFilePickConfig(): FilePickConfig {
        return filePickConfig
    }

    fun launchPicker(activity: Activity, intent: Intent, callback: ActivityResultCallback<ActivityResult>) {
        activityResultCallback = callback
        pickerLaunchers[activity]?.apply {
            launch(intent)
        }
    }

    private fun createPickLauncher(activity: FragmentActivity): ActivityResultLauncher<Intent> {
        val activityForResult = ActivityResultContracts.StartActivityForResult()
        return activity.registerForActivityResult(activityForResult, this)
    }

    override fun onActivityResult(result: ActivityResult?) {
        activityResultCallback?.onActivityResult(result)
        activityResultCallback = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is FragmentActivity && !(activity is AntFilePickActivity)) {
            val pickerLauncher = createPickLauncher(activity)
            pickerLaunchers[activity] = pickerLauncher
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        pickerLaunchers[activity]?.apply {
            pickerLaunchers.remove(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}
}