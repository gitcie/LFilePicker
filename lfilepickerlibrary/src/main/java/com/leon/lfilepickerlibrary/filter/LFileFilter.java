package com.leon.lfilepickerlibrary.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * 作者：Leon
 * 时间：2017/3/24 13:43
 */
public class LFileFilter implements FileFilter {
    private final String[] mTypes;

    public LFileFilter(String[] types) {
        this.mTypes = types;
    }

    @Override
    public boolean accept(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.startsWith(".")) { //不显示隐藏文件夹
            return false;
        }
        if (file.isDirectory()) {
            return  true;
        }
        if (mTypes != null && mTypes.length > 0) {
            for (String mType : mTypes) {
                if (fileName.endsWith(mType.toLowerCase())) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
