package com.leon.lfilepickerlibrary.adapter

import android.content.Context
import android.os.Environment
import com.leon.lfilepickerlibrary.utils.FileUtils
import java.io.File
import java.io.FileFilter

/**
 *
 * @author powerdata
 * @since 9/15/21
 */
class ExternalStorageFileLoader(private val filter: FileFilter, private val targetSize: Long): FileLoader {

    companion object {
        private const val TAG = "ExtraStorageFileLoader"
    }

    override fun loadIndexFiles(context: Context): List<File> {
        val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
        return FileUtils.getFileList(sdcardPath, filter, false, targetSize)
    }
}