package com.leon.lfilepickerlibrary.adapter

import android.content.Context
import java.io.File

/**
 *
 * @author powerdata
 * @since 9/15/21
 */
interface FileLoader {

    companion object {
        private const val TAG = "FileIndexLoader"
    }

    fun loadIndexFiles(context: Context): List<File>

}