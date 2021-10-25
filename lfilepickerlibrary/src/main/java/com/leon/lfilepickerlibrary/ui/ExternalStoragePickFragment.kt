package com.leon.lfilepickerlibrary.ui

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && !Environment.isExternalStorageManager()) {
            showGrantAccessFilePermission()
            return
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume();
        if(!isAdapterLoaded && checkFileManagePermission()) {
            this.loadListAdapter()
        }
    }

    private fun checkFileManagePermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()
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

    @TargetApi(Build.VERSION_CODES.R)
    private fun showGrantAccessFilePermission() {
        AlertDialog.Builder(requireActivity())
                .setTitle("文件访问授权")
                .setMessage("由于系统限制，浏览存储卡文件需要您授权")
                .setPositiveButton("前往授权") { dialog: DialogInterface?, which: Int ->
                    val grantIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    requireActivity().startActivity(grantIntent)
                }
                .setNegativeButton("取消") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .create()
                .show()
    }
}