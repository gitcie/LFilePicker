package com.leon.lfilepickerlibrary.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.leon.lfilepickerlibrary.R;
import com.leon.lfilepickerlibrary.adapter.FileLoader;
import com.leon.lfilepickerlibrary.adapter.PathAdapter;
import com.leon.lfilepickerlibrary.filter.LFileFilter;
import com.leon.lfilepickerlibrary.model.ParamEntity;
import com.leon.lfilepickerlibrary.utils.FileUtils;
import com.leon.lfilepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AntFilePickFragment extends Fragment implements RefreshableView {

    private final String TAG = "LatestFileListFragment";
    public final static String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private EmptyRecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mTvPath;
    private TextView mTvBack;
    private Button mBtnAddBook;
    private String mPath;
    private List<File> mCurrentFileList = new ArrayList<>();
    private final ArrayList<String> mSelectedFiles = new ArrayList<>();//存放选中条目的数据地址
    private PathAdapter mPathAdapter;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private boolean mIsAllSelected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParamEntity = (ParamEntity) requireActivity().getIntent().getExtras().getSerializable("param");
        Bundle args = getArguments();
        if (args != null) {
            String browseType = args.getString("browse_type");
            Log.e(TAG, "browseType: " + browseType);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_file_picker, container, false);
        initView(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        updateAddButton();
        if (!checkSDState()) {
            Toast.makeText(getActivity(), R.string.lfile_NotFoundPath, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = mParamEntity.getPath();
        mPath = TextUtils.isEmpty(mPath) ? SD_CARD_PATH : mPath;
        setNavigationPath(mPath);

        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        FileLoader loader = getFileLoader();
        mCurrentFileList = loader.loadIndexFiles(requireActivity());
        mPathAdapter = new PathAdapter(mCurrentFileList, getActivity(), mFilter, mParamEntity.isMultiMode(), mParamEntity.isGreater(), mParamEntity.getFileSize());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mPathAdapter.setIconStyle(mParamEntity.getIconStyle());
        mRecyclerView.setAdapter(mPathAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
        initListener();
    }

    /**
     * 初始化控件
     */
    private void initView(View root) {
        mRecyclerView = root.findViewById(R.id.recylerview);
        mTvPath = root.findViewById(R.id.tv_path);
        if (!showNavigationPath()) {
            ConstraintSet constraints = new ConstraintSet();
            if(root instanceof ConstraintLayout){
                constraints.clone((ConstraintLayout) root);
                constraints.connect(mRecyclerView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraints.applyTo((ConstraintLayout) root);
            }
        }
        mTvBack = root.findViewById(R.id.tv_back);
        mBtnAddBook = root.findViewById(R.id.btn_addbook);
        mEmptyView = root.findViewById(R.id.empty_view);
        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        }
    }

    private void updateAddButton() {
        if (!mParamEntity.isMultiMode()) {
            mBtnAddBook.setVisibility(View.GONE);
        }
        if (!mParamEntity.isChooseMode()) {
            mBtnAddBook.setVisibility(View.VISIBLE);
            mBtnAddBook.setText(getString(R.string.lfile_OK));
            //文件夹模式默认为单选模式
            mParamEntity.setMultiMode(false);
        }
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private void setNavigationPath(String path) {
        mTvPath.setText(path.replace(SD_CARD_PATH, "我的手机"));
        mTvBack.setVisibility(path.equals(SD_CARD_PATH) ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * 添加点击事件处理
     */
    private void initListener() {
        // 返回目录上一级
        mTvBack.setOnClickListener(v -> navigateUpFolder());

        mPathAdapter.setOnItemClickListener(position -> {
            if (mParamEntity.isMultiMode()) {
                if (mCurrentFileList.get(position).isDirectory()) {
                    //如果当前是目录，则进入继续查看目录
                    checkInDirectory(position);
                    mPathAdapter.updateAllSelelcted(false);
                    mIsAllSelected = false;
                    mBtnAddBook.setText(getString(R.string.lfile_Selected));
                } else {
                    //如果已经选择则取消，否则添加进来
                    if (mSelectedFiles.contains(mCurrentFileList.get(position).getAbsolutePath())) {
                        mSelectedFiles.remove(mCurrentFileList.get(position).getAbsolutePath());
                    } else {
                        mSelectedFiles.add(mCurrentFileList.get(position).getAbsolutePath());
                    }
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mSelectedFiles.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mSelectedFiles.size() + " )");
                    }
                    //先判断是否达到最大数量，如果数量达到上限提示，否则继续添加
                    if (mParamEntity.getMaxNum() > 0 && mSelectedFiles.size() > mParamEntity.getMaxNum()) {
                        Toast.makeText(getActivity(), R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                //单选模式直接返回
                if (mCurrentFileList.get(position).isDirectory()) {
                    checkInDirectory(position);
                    return;
                }
                if (mParamEntity.isChooseMode()) {
                    //选择文件模式,需要添加文件路径，否则为文件夹模式，直接返回当前路径
                    mSelectedFiles.add(mCurrentFileList.get(position).getAbsolutePath());
                    chooseDone();
                } else {
                    Toast.makeText(getActivity(), R.string.lfile_ChooseTip, Toast.LENGTH_SHORT).show();
                }
            }

        });

        mBtnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParamEntity.isChooseMode() && mSelectedFiles.size() < 1) {
                    String info = mParamEntity.getNotFoundFiles();
                    if (TextUtils.isEmpty(info)) {
                        Toast.makeText(getActivity(), R.string.lfile_NotFoundBooks, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //返回
                    chooseDone();
                }
            }
        });
    }


    protected void navigateUpFolder() {
        String tempPath = new File(mPath).getParent();
        if (tempPath == null) {
            return;
        }
        mPath = tempPath;
        mCurrentFileList = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setFileList(mCurrentFileList);
        mPathAdapter.updateAllSelelcted(false);
        mIsAllSelected = false;
        mBtnAddBook.setText(getString(R.string.lfile_Selected));
        mRecyclerView.scrollToPosition(0);
        setNavigationPath(mPath);
        //清除添加集合中数据
        mSelectedFiles.clear();
        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        } else {
            mBtnAddBook.setText(R.string.lfile_Selected);
        }
    }

    /**
     * 点击进入目录
     */
    private void checkInDirectory(int position) {
        mPath = mCurrentFileList.get(position).getAbsolutePath();
        setNavigationPath(mPath);
        //更新数据源
        mCurrentFileList = FileUtils.getFileList(mPath, mFilter, mParamEntity.isGreater(), mParamEntity.getFileSize());
        mPathAdapter.setFileList(mCurrentFileList);
        mPathAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 完成提交
     */
    private void chooseDone() {
        //判断是否数量符合要求
        if (mParamEntity.isChooseMode()) {
            if (mParamEntity.getMaxNum() > 0 && mSelectedFiles.size() > mParamEntity.getMaxNum()) {
                Toast.makeText(getActivity(), R.string.lfile_OutSize, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra("paths", mSelectedFiles);
        intent.putExtra("path", mPath.trim());
        String authority = requireActivity().getPackageName() + ".FileProvider";
        if (mParamEntity.isMultiMode()) {
            ClipData multiUriData = null;
            for (int i = 0; i < mSelectedFiles.size(); i++) {
                String path = mSelectedFiles.get(i);
                Uri fileUri = FileProvider.getUriForFile(requireActivity(), authority, new File(path));
                ClipData.Item item = new ClipData.Item(fileUri);
                if (i == 0) {
                    multiUriData = new ClipData(new ClipDescription("多选", new String[]{}), item);
                } else {
                    multiUriData.addItem(item);
                }
            }
            if (multiUriData != null) {
                intent.setClipData(multiUriData);
            }
        } else {
            intent.setData(FileProvider.getUriForFile(requireActivity(), authority, new File(mSelectedFiles.get(0))));
        }
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_selecteall_cancel) {
            //将当前目录下所有文件选中或者取消
            mPathAdapter.updateAllSelelcted(!mIsAllSelected);
            mIsAllSelected = !mIsAllSelected;
            if (mIsAllSelected) {
                for (File mListFile : mCurrentFileList) {
                    //不包含再添加，避免重复添加
                    if (!mListFile.isDirectory() && !mSelectedFiles.contains(mListFile.getAbsolutePath())) {
                        mSelectedFiles.add(mListFile.getAbsolutePath());
                    }
                    if (mParamEntity.getAddText() != null) {
                        mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mSelectedFiles.size() + " )");
                    } else {
                        mBtnAddBook.setText(getString(R.string.lfile_Selected) + "( " + mSelectedFiles.size() + " )");
                    }
                }
            } else {
                mSelectedFiles.clear();
                mBtnAddBook.setText(getString(R.string.lfile_Selected));
            }
        }
        return true;
    }

    @Override
    public void refreshView() {
        FileLoader loader = getFileLoader();
        if(getActivity() != null) {
            mPathAdapter.setFileList(loader.loadIndexFiles(getActivity()));
            mPathAdapter.notifyDataSetChanged();
        }
    }

    protected ParamEntity getParamEntity() {
        return mParamEntity;
    }

    protected String currentPath() {
        return mPath;
    }

    protected boolean showNavigationPath() {
        return true;
    }

    abstract FileLoader getFileLoader();

}
