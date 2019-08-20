package com.walktour.gui.mos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.base.util.StringUtil;
import com.walktour.base.util.ToastUtil;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.gui.report.ReportFactory;
import com.walktour.gui.report.ReportPreviewActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 联通 Mos 离线算分功能
 *
 * @author zhicheng.chen
 * @date 2019/3/19
 */
public class MosOfflineActivity extends FragmentActivity implements MosCallback<String> {

    private final String EXTRA_8K_DIR = "8k_dir";
    private final String EXTRA_48K_DIR = "48k_dir";

    private final String ACTION_FETCH_8K_DIR = "ACTION_FETCH_8K_DIR";
    private final String ACTION_FETCH_48K_DIR = "ACTION_FETCH_48K_DIR";

    @BindView(R.id.title_txt)
    TextView mTvTitle;
    @BindView(R.id.tv_8k_dir)
    TextView mTv8kDir;
    @BindView(R.id.tv_48k_dir)
    TextView mTv48kDir;
    @BindView(R.id.tv_valid_file)
    TextView mTvValidFile;
    @BindView(R.id.tv_mos_result)
    TextView mTvMosResult;

    private int file8kCount = 0, file48kCount = 0;

    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_FETCH_8K_DIR.equals(action)) {
                String dir8k = intent.getStringExtra(EXTRA_8K_DIR);
                mTv8kDir.setText(dir8k);
                File file = new File(dir8k);
                String[] filePath = file.list();
                for (String path : filePath) {
                    if (path.endsWith("wav") || path.endsWith("WAV")) {
                        ++file8kCount;
                    }
                }
            } else if (ACTION_FETCH_48K_DIR.equals(action)) {
                String dir48k = intent.getStringExtra(EXTRA_48K_DIR);
                mTv48kDir.setText(dir48k);
                File file = new File(dir48k);
                String[] filePath = file.list();
                for (String path : filePath) {
                    if (path.endsWith("wav") || path.endsWith("WAV")) {
                        ++file48kCount;
                    }
                }

            }

            String validFileStr = "8K共 <font color='blue'>%d</font> 个；48K共 <font color='blue'>%d</font> 个";
            String format = String.format(validFileStr, file8kCount, file48kCount);
            Spanned html = Html.fromHtml(format);
            mTvValidFile.setText(html);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mos_offline);
        ButterKnife.bind(this);

        mTvTitle.setText("Mos离线测试");

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FETCH_8K_DIR);
        filter.addAction(ACTION_FETCH_48K_DIR);
        registerReceiver(mReciver, filter);

        readFromCache();
    }

    private void readFromCache() {
        String dir8K = SharePreferencesUtil.getInstance(this).getString(EXTRA_8K_DIR, "");
        String dir48K = SharePreferencesUtil.getInstance(this).getString(EXTRA_48K_DIR, "");

        mTv8kDir.setText(dir8K);
        mTv48kDir.setText(dir48K);

        File file = new File(dir8K);
        if (file.exists()) {
            String[] filePath = file.list();
            if (filePath != null) {
                for (String path : filePath) {
                    if (path.endsWith("wav") || path.endsWith("WAV")) {
                        ++file8kCount;
                    }
                }
            }
        }

        File file2 = new File(dir48K);
        if (file2.exists()) {
            String[] filePath2 = file2.list();
            if (filePath2 != null) {
                for (String path : filePath2) {
                    if (path.endsWith("wav") || path.endsWith("WAV")) {
                        ++file48kCount;
                    }
                }
            }
        }

        String validFileStr = "8K共 <font color='blue'>%d</font> 个；48K共 <font color='blue'>%d</font> 个";
        String format = String.format(validFileStr, file8kCount, file48kCount);
        Spanned html = Html.fromHtml(format);
        mTvValidFile.setText(html);
    }

    @OnClick(R.id.btn_8k_dir)
    void click8kButton() {
        Intent intent = new Intent(this, FileExplorer.class);
        intent.putExtra(FileExplorer.KEY_ACTION, ACTION_FETCH_8K_DIR);
        intent.putExtra(FileExplorer.KEY_EXTRA, EXTRA_8K_DIR);
        intent.putExtra(FileExplorer.KEY_DIR, true);
        startActivity(intent);
    }

    @OnClick(R.id.btn_48k_dir)
    void click48kButton() {
        Intent intent = new Intent(this, FileExplorer.class);
        intent.putExtra(FileExplorer.KEY_ACTION, ACTION_FETCH_48K_DIR);
        intent.putExtra(FileExplorer.KEY_EXTRA, EXTRA_48K_DIR);
        intent.putExtra(FileExplorer.KEY_DIR, true);
        startActivity(intent);
    }


    @OnClick(R.id.btn_begin)
    void clickBeginCaculate() {
        String dir8K = mTv8kDir.getText().toString();
        String dir48K = mTv48kDir.getText().toString();
        if (StringUtil.isEmpty(dir8K)) {
            ToastUtil.showShort(this, "请先设置8k语料目录");
            return;
        } else if (StringUtil.isEmpty(dir48K)) {
            ToastUtil.showShort(this, "请先设置48k语料目录");
            return;
        }
        SharePreferencesUtil.getInstance(this).saveString(EXTRA_8K_DIR, dir8K);
        SharePreferencesUtil.getInstance(this).saveString(EXTRA_48K_DIR, dir48K);
        MosOfflineDialog dialog = new MosOfflineDialog();
        Bundle b = new Bundle();
        b.putString("8k_dir", dir8K);
        b.putString("48k_dir", dir48K);
        dialog.setArguments(b);
        dialog.show(getSupportFragmentManager(), "caculate");
    }

    @OnClick(R.id.tv_mos_result)
    void clickMosResult() {
        if (mTvMosResult.getText().toString().trim().equals("")) {
            ToastUtil.showShort(this, "请先开始算分");
        } else {
            ReportFactory.getInstance(this).createHtmlFile(mTvMosResult.getText().toString().trim());
            Intent intent = new Intent(this, ReportPreviewActivity.class);
            intent.putExtra(ReportPreviewActivity.EXTRA_FILE_PATH, mTvMosResult.getText().toString().trim());
            startActivity(intent);
        }
    }

    @OnClick(R.id.pointer)
    void clickBackButton() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciver);
    }

    @Override
    public void handleResult(String path) {
        mTvMosResult.setText(path);
    }
}
