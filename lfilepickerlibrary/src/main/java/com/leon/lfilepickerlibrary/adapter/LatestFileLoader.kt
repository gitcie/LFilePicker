package com.leon.lfilepickerlibrary.adapter

import android.content.Context
import com.leon.lfilepickerlibrary.utils.FileUtils
import java.io.File

/**
 * @author powerdata
 * @since 9/15/21
 */
class LatestFileLoader(private val suffixFilter: Array<String>?): FileLoader {

    companion object {
        private const val TAG = "LatestFileLoader"
    }

    override fun loadIndexFiles(context: Context): List<File> {
        val mimeTypes = suffixFilter?.let {
            FileUtils.convertSuffixToMimeType(*it)
        }
        val resolverFiles = FileUtils.queryLatestUsedFiles(context, mimeTypes)
        return resolverFiles.map {
            File(it.path)
        }
    }
}