package com.leon.lfilepickerlibrary.ui

/**
 *
 * @author powerdata
 * @since 9/16/21
 */
interface RefreshableView {

    companion object {
        private const val TAG = "RefreshableView"
    }

    fun refreshView()

}