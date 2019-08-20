package com.walktour.base.gui.fragment;

import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.walktour.base.R;
import com.walktour.base.R2;
import com.walktour.base.gui.component.DaggerFileChooseFragmentComponent;
import com.walktour.base.gui.model.FileChoose;
import com.walktour.base.gui.module.FileChooseFragmentModule;
import com.walktour.base.gui.presenter.BaseFragmentPresenter;
import com.walktour.base.gui.presenter.FileChooseFragmentPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnItemClick;

/**
 * 硬盘文件选择视图
 * Created by wangk on 2017/6/28.
 */

public class FileChooseFragment extends BaseListFragment<FileChoose> {
    /**
     * 日志标识
     */
    private static final String TAG = "FileChooseFragment";
    /**
     * 交互类
     */
    @Inject
    FileChooseFragmentPresenter mPresenter;
    /**
     * 文件列表
     */
    @BindView(R2.id.list_view)
    ListView mFileListView;
    /**
     * 父文件路径
     */
    @BindView(R2.id.parent_file_path)
    TextView mParentFilePath;

    public FileChooseFragment() {
        super(R.string.file_choose, R.layout.fragment_file_choose_list, R.layout.fragment_file_choose_list_row);
    }

    @Override
    public String getLogTAG() {
        return TAG;
    }

    @Override
    public BaseFragmentPresenter getPresenter() {
        return this.mPresenter;
    }

    @Override
    protected void setupFragmentComponent() {
        DaggerFileChooseFragmentComponent.builder().fileChooseFragmentModule(new FileChooseFragmentModule(this)).build().inject(this);
    }

    @Override
    protected void onCreateView() {
        //无须实现
    }

    @OnItemClick(R2.id.list_view)
    void onFileClick(int position) {
        this.mPresenter.selectFile(this.getItem(position));
    }

    @Override
    protected AbsListView getListView() {
        return this.mFileListView;
    }

    /**
     * 显示父文件路径
     *
     * @param filePath 文件路径
     */
    public void showParentFilePath(String filePath) {
        this.mParentFilePath.setText(filePath);
    }

    @Override
    protected BaseHolder createViewHolder() {
        return new ViewHolder();
    }

    /**
     * 存放控件 的ViewHolder
     */
    public class ViewHolder extends BaseHolder<FileChoose> {
        /**
         * 文件图片
         */
        @BindView(R2.id.file_type_view)
        ImageView mImage;
        /**
         * 文件名称
         */
        @BindView(R2.id.file_name)
        TextView mName;
        /**
         * 文件描述
         */
        @BindView(R2.id.file_description)
        TextView mDescription;

        @Override
        public void setData(int position, FileChoose data) {
            this.showFileTypeImage(data);
            this.mName.setText(data.getFileName());
            this.mDescription.setText(data.getFileDescription());
        }

        /**
         * 显示文件类型图片
         *
         * @param data 文件对象
         */
        private void showFileTypeImage(FileChoose data) {
            if (data.getFileType() == FileChoose.FILE_TYPE_FILE) {
                this.mImage.setBackgroundResource(R.drawable.file_choose_file);
            } else {
                this.mImage.setBackgroundResource(R.drawable.file_choose_directory);
            }
        }

    }

}
