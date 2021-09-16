package com.leon.lfilepickerlibrary;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;

import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.ui.AntFilePickActivity;

/**
 * 作者：Leon
 * 时间：2017/3/20 16:57
 */
public class LFilePicker {

    public static ActivityResultLauncher<Intent> pickLauncher = null;

    private FragmentActivity mActivity;
    private Fragment mFragment;
    private Fragment mSupportFragment;
    private String mTitle;
    private String mTitleColor;
    private int theme = R.style.LFileTheme;
    private int mTitleStyle = R.style.LFileToolbarTextStyle;
    private String mBackgroundColor;
    private int mBackStyle;
    private int mRequestCode;
    private boolean mMultiMode = true;

    //true为文件选择模式，false为文件夹选择模式，默认为true
    private boolean mChooseMode = true;
    private String mAddText;
    private int mIconStyle;
    private String[] mFileTypes;
    private String mNotFoundFiles;
    private int mMaxNum;
    private String mStartPath;
    private boolean mIsGreater = true;//是否大于
    private long mFileSize = 100 * 1024 * 1024;

    /**
     * 绑定Activity
     *
     * @param activity
     * @return
     */
    public LFilePicker withActivity(FragmentActivity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * 绑定Fragment
     *
     * @param fragment
     * @return
     */
    public LFilePicker withFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    /**
     * 绑定v4包Fragment
     *
     * @param supportFragment
     * @return
     */
    public LFilePicker withSupportFragment(Fragment supportFragment) {
        this.mSupportFragment = supportFragment;
        return this;
    }


    /**
     * 设置主标题
     *
     * @param title
     * @return
     */
    public LFilePicker withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置标题颜色
     *
     * @param color
     * @return
     */
    @Deprecated
    public LFilePicker withTitleColor(String color) {
        this.mTitleColor = color;
        return this;
    }

    /**
     * 设置主题
     *
     * @param theme
     * @return
     */
    public LFilePicker withTheme(@StyleRes int theme) {
        this.theme = theme;
        return this;
    }

    /**
     * 设置标题的颜色和字体大小
     *
     * @param style
     * @return
     */
    public LFilePicker withTitleStyle(@StyleRes int style) {
        this.mTitleStyle = style;
        return this;
    }

    /**
     * 设置背景色
     *
     * @param color
     * @return
     */
    public LFilePicker withBackgroundColor(String color) {
        this.mBackgroundColor = color;
        return this;
    }

    /**
     * 请求码
     *
     * @param requestCode
     * @return
     */
    public LFilePicker withRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置返回图标
     *
     * @param backStyle
     * @return
     */
    public LFilePicker withBackIcon(int backStyle) {
        this.mBackStyle = backStyle;
        return this;
    }

    /**
     * 设置选择模式，默认为true,多选；false为单选
     *
     * @param isMulti
     * @return
     */
    public LFilePicker withMultiMode(boolean isMulti) {
        this.mMultiMode = isMulti;
        return this;
    }

    /**
     * 设置多选时按钮文字
     *
     * @param text
     * @return
     */
    public LFilePicker withAddText(String text) {
        this.mAddText = text;
        return this;
    }

    /**
     * 设置文件夹图标风格
     *
     * @param style
     * @return
     */
    public LFilePicker withIconStyle(int style) {
        this.mIconStyle = style;
        return this;
    }

    public LFilePicker withFileFilter(String[] arrs) {
        this.mFileTypes = arrs;
        return this;
    }

    /**
     * 没有选中文件时的提示信息
     *
     * @param notFoundFiles
     * @return
     */
    public LFilePicker withNotFoundBooks(String notFoundFiles) {
        this.mNotFoundFiles = notFoundFiles;
        return this;
    }

    /**
     * 设置最大选中数量
     *
     * @param num
     * @return
     */
    public LFilePicker withMaxNum(int num) {
        this.mMaxNum = num;
        return this;
    }

    /**
     * 设置初始显示路径
     *
     * @param path
     * @return
     */
    public LFilePicker withStartPath(String path) {
        this.mStartPath = path;
        return this;
    }

    public LFilePicker withChooseFile() {
        this.mChooseMode = true;
        return this;
    }

    public LFilePicker withChooseFolder() {
        this.mChooseMode = false;
        return this;
    }

    /**
     * 设置文件大小过滤方式：大于指定大小或者小于指定大小
     *
     * @param isGreater true：大于 ；false：小于，同时包含指定大小在内
     * @return
     */
    public LFilePicker withIsGreater(boolean isGreater) {
        this.mIsGreater = isGreater;
        return this;
    }

    /**
     * 设置过滤文件大小
     *
     * @param fileSize
     * @return
     */
    public LFilePicker withFileSize(long fileSize) {
        this.mFileSize = fileSize;
        return this;
    }

    public void start() {
        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity or Fragment by withActivity or withFragment or withSupportFragment method");
        }
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);

        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }

    public void startWithLauncher(ActivityResultCallback<ActivityResult> callback) {
//        StartActivityForResult activityForResult = new StartActivityForResult();
//        ActivityResultLauncher<Intent> launcher = mActivity.registerForActivityResult(activityForResult, callback);
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);
        if (pickLauncher != null) {
            pickLauncher.launch(intent);
        }
    }

    private Intent initIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, AntFilePickActivity.class);
        } else if (mFragment != null) {
            intent = new Intent(mFragment.getActivity(), AntFilePickActivity.class);
        } else {
            intent = new Intent(mSupportFragment.getActivity(), AntFilePickActivity.class);
        }
        return intent;
    }

    @NonNull
    private Bundle getBundle() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.setTitle(mTitle);
        paramEntity.setTheme(theme);
        paramEntity.setTitleColor(mTitleColor);
        paramEntity.setTitleStyle(mTitleStyle);
        paramEntity.setBackgroundColor(mBackgroundColor);
        paramEntity.setBackIcon(mBackStyle);
        paramEntity.setMultiMode(mMultiMode);
        paramEntity.setAddText(mAddText);
        paramEntity.setIconStyle(mIconStyle);
        paramEntity.setFileTypes(mFileTypes);
        paramEntity.setNotFoundFiles(mNotFoundFiles);
        paramEntity.setMaxNum(mMaxNum);
        paramEntity.setChooseMode(mChooseMode);
        paramEntity.setPath(mStartPath);
        paramEntity.setFileSize(mFileSize);
        paramEntity.setGreater(mIsGreater);
        Bundle bundle = new Bundle();
        bundle.putSerializable("param", paramEntity);
        return bundle;
    }
}
