package com.leon.lfilepickerlibrary.ui

import android.app.Activity
import android.os.Bundle
import com.leon.lfilepickerlibrary.adapter.ExternalStorageFileLoader
import com.leon.lfilepickerlibrary.adapter.FileLoader
import com.leon.lfilepickerlibrary.filter.LFileFilter

/**
 *
 * @author powerdata
 * @since 9/15/21
 */
class ExternalStoragePickFragment: AntFilePickFragment(), BackPressProcessor {

    companion object {
        private const val TAG = "ExternalStoragePickFragment"
    }

    private lateinit var fileLoader: FileLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = LFileFilter(paramEntity.fileTypes)
        fileLoader = ExternalStorageFileLoader(filter, paramEntity.fileSize)
    }

    override fun getFileLoader(): FileLoader {
        return fileLoader
    }

    override fun processBackPress(activity: Activity): Boolean {
        if (currentPath().equals(SD_CARD_PATH)) {
            activity.finish()
        } else {
            navigateUpFolder()
        }
        return true
    }
}