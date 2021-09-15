package com.leon.lfilepickerlibrary.ui

import android.app.Activity
import android.os.Bundle
import com.leon.lfilepickerlibrary.adapter.FileLoader
import com.leon.lfilepickerlibrary.adapter.LatestFileLoader

/**
 *
 * @author powerdata
 * @since 9/15/21
 */
class LatestPickFragment: AntFilePickFragment(), BackPressProcessor {

    companion object {
        private const val TAG = "LatestFilePickerFragment"
    }

    private lateinit var fileLoader: FileLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileLoader = LatestFileLoader(paramEntity.fileTypes)
    }

    override fun getFileLoader(): FileLoader {
        return fileLoader
    }

    override fun showNavigationPath(): Boolean {
        return false
    }

    override fun processBackPress(activity: Activity): Boolean {
        activity.finish()
        return true
    }
}