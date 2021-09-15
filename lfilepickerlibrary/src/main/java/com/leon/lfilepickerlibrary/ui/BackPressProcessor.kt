package com.leon.lfilepickerlibrary.ui

import android.app.Activity

/**
 *
 * @author powerdata
 * @since 9/15/21
 */
interface BackPressProcessor {

    fun processBackPress(activity: Activity): Boolean

}