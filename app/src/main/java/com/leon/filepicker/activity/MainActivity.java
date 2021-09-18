package com.leon.filepicker.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.leon.filepicker.R;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.ui.AntFilePickActivity;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.leon.lfilepickerlibrary.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_PERMISSIONS = 1;
    private final String TAG = "FilePickerLeon";
    private RadioGroup mRgIconType;
    private RadioGroup mRgBackArrawType;
    private int mIconType;
    private int mBackArrawType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSIONS);
        }
        initView();
        initListener();
    }


    private void initView() {
        mRgIconType = findViewById(R.id.rg_iconstyle);
        mRgBackArrawType = findViewById(R.id.rg_backarrawstyle);
    }

    private void initListener() {
        mRgIconType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_yellow:
                        mIconType = Constant.ICON_STYLE_YELLOW;
                        break;
                    case R.id.radio_green:
                        mIconType = Constant.ICON_STYLE_GREEN;
                        break;
                    case R.id.radio_blue:
                        mIconType = Constant.ICON_STYLE_BLUE;
                        break;
                }
            }
        });
        mRgBackArrawType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.arrawback_styleone:
                        mBackArrawType = Constant.BACKICON_STYLEONE;
                        break;
                    case R.id.arrawback_styletwo:
                        mBackArrawType = Constant.BACKICON_STYLETWO;
                        break;
                    case R.id.arrawback_stylethree:
                        mBackArrawType = Constant.BACKICON_STYLETHREE;
                        break;
                }
            }
        });
    }

    public void openFromActivity(View view) {
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        new LFilePicker()
                .withActivity(this)
                .withRequestCode(Consant.REQUESTCODE_FROM_ACTIVITY)
                .withTitle("文件选择")
                .withIconStyle(mIconType)
                .withBackIcon(mBackArrawType)
                .withMultiMode(false)
                .withMaxNum(2)
                .withStartPath(sdCard)//指定初始显示路径
                .withNotFoundBooks("至少选择一个文件")
                .withIsGreater(false)//过滤文件大小 小于指定大小的文件
//                .withFileSize(500 * 1024)//指定文件大小为500K
                .withFileFilter(new String[]{"txt", "doc","docx", "pdf", "xls", "xlsx"});
//                .start();
        String[] mimeTypes = {
                FileUtils.MIME_TYPE_DOC,
                FileUtils.MIME_TYPE_DOCX,
                FileUtils.MIME_TYPE_XLS,
                FileUtils.MIME_TYPE_XLSX,
                FileUtils.MIME_TYPE_PPTX
        };
//        FileUtils.queryLatestUsedFiles(this, mimeTypes);
        new LFilePicker()
                .withActivity(this)
                .withRequestCode(Consant.REQUESTCODE_FROM_ACTIVITY)
                .withTitle("文件选择")
//                .withIconStyle(mIconType)
//                .withBackIcon(mBackArrawType)
                .withMultiMode(false)
                .withMaxNum(2)
//                .withStartPath(sdCard)//指定初始显示路径
                .withNotFoundBooks("至少选择一个文件")
                .withIsGreater(false)//过滤文件大小 小于指定大小的文件
//                .withFileSize(500 * 1024)//指定文件大小为500K
                .withFileFilter(new String[]{"doc","docx", "pdf", "xls", "xlsx", "pptx", "ppt"})
                .startWithLauncher(result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        processActivityResult(result.getData());
                    }
                });
    }

    public void openFragmentActivity(View view) {
        startActivity(new Intent(this, FragmengActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Consant.REQUESTCODE_FROM_ACTIVITY) {
                processActivityResult(data);
            }
        }
    }

    private void processActivityResult(Intent data) {
        List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
        //for (String s : list) {
        //    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        //}
//                Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
        String path = data.getStringExtra("path");
        Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
        Log.i("LeonFilePicker", path);
        List<String> paths = data.getStringArrayListExtra(AntFilePickActivity.SELECT_PATHS);
        if (paths != null && !paths.isEmpty()) {
            Log.e("MainActivity", "选中的文件" + paths.get(0));
        }

        ClipData multiFiles = data.getClipData();
        if (multiFiles != null && multiFiles.getItemCount() > 0) {
            for (int i = 0; i < multiFiles.getItemCount(); i++) {
                Log.e("sf", multiFiles.getItemAt(i).getUri().toString());
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSIONS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    list.add(permissions[i]);
                }
            }
            int length = list.size();
            if (length != 0) {
                final String[] array = new String[length];
                list.toArray(array);
                new AlertDialog.Builder(this)
                        .setMessage("为了正常使用软件，必须允许这些权限!")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(array, CODE_PERMISSIONS);
                            }
                        })
                        .show();
            }
        }
    }
}
