package com.leon.filepicker.activity

import android.app.Application
import com.leon.lfilepickerlibrary.AntFilePicker

/**
 *
 * @author powerdata
 * @since 9/16/21
 */
class FilePickApp: Application() {

    companion object {
        private const val TAG = "FilePickApp"
    }

    override fun onCreate() {
        super.onCreate()
        AntFilePicker.instance.init(this)
    }

}