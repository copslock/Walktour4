package com.walktour.gui.applet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ImageUtil;
import com.walktour.Utils.SharePreferencesUtil;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkMessage;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalktourConst;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.config.MapSetByATTved;
import com.walktour.control.instance.DataManagerFileList;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.map.MapActivity;
import com.walktour.gui.newmap.basestation.BaseDataParser;
import com.walktour.gui.newmap.basestation.service.BaseStationService;
import com.walktour.gui.setting.SysBuildingManager;
import com.walktour.gui.setting.SysFloorMap;
import com.walktour.gui.weifuwu.sharepush.ShareCommons;
import com.walktour.gui.weifuwu.sharepush.ShareNextActivity;
import com.walktour.gui.weifuwu.sharepush.ShareSendActivity;
import com.walktour.service.ApplicationInitService;

import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * ????????????????????????????????????????????????????????????????????????????????????????????????
 *
 * @author qihang.li
 */

@SuppressLint("SdCardPath")
public class FileExplorer extends BasicActivity {
    private static final String tag = "FileExplorer.walktour";

    /**
     * activity?????????????????????
     */
    public static final String ACTION_LOAD_NORMAL_FILE = "com.walktour.load.normal.file";

    public static final String KEY_FILE = "file";
    /**
     * ??????????????????
     */
    public static final String KEY_FILE_SIZE = "file_size";
    /**
     * ??????????????????
     */
    public static final String KEY_FILE_FILTER = "file_filter";
    /**
     * ??????????????????
     */
    public static final String KEY_INIT_DIR = "init_path";
    /**
     * activity????????????
     */
    public static final String KEY_RESULT = "result";
    /**
     * ?????????????????????
     */
    public static final String KEY_ACTION = "action";
    /**
     * ????????????????????????
     */
    public static final String KEY_EXTRA = "extra";
    /**
     * ?????????????????????
     */
    public static final String KEY_CLIP = "clip";
    /**
     * ???????????????????????????
     */
    public static final String KEY_REMOVE = "remove";
    /**
     * ????????????
     */
    public static final String KEY_DIR = "chooseDir";
    /**
     * ??????License??????
     */
    public static final String KEY_LICENSE = "license";
    /**
     * ??????url
     */
    public static final String KEY_URL = "importUrl";
    /**
     * ????????????
     */
    public static final String KEY_NORMAL = "normalFile";
    /**
     * ????????????
     */
    public static final String KEY_BASE_DATA = "mobileBase";
    /**
     * ???????????????????????????
     */
    public static final String KEY_MAP_TYPE = "mapType";
    /**
     * ??????License??????
     */
    public static final String KEY_IMPORT_BUILD = "build";
    /**
     * ????????????????????????
     */
    public static final int INDOOR_FILE_SIZE = (int) 10 * 1000 * 1000;
    /**
     * ????????????????????????????????????
     */
    public final static int REQUEST_CODE = 10086;
    /**
     * ?????????????????????????????????
     */
    public final static String RESPONSE_FILE_PATH = "file_path";

    private final static int DISPROGRESS = -1;

    private final static int SHOWPROGRESS = 0;

    private final static int SHOWMESSAGE = 1;

    // private final int DIR_LEVEL = 4;

    private final static int FILE_ERROR = 1000;
    /**
     * ????????????????????????????????????
     */
    private String actionOfBroadcast = "";

    private String extraName = "";
    /**
     * ??????????????????
     */
    private File file;
    /**
     * ??????ListView???????????????????????????
     */
    private ArrayList<String> items;
    /**
     * ?????????????????????????????????
     */
    private ArrayList<String> paths;
    /**
     * ????????????
     */
    private String current_path = "";
    /**
     * ???????????????
     */
    private String init_path = Environment.getExternalStorageDirectory().getPath() + "/";
    /**
     * ???????????????
     */
    private String app_path = "";
    /**
     * ??????????????????,??????????????????????????????
     */
    private String[] file_types = new String[]{};
    /**
     * ????????????????????????
     */
    private long fileSize = 10 * 1000 * 1000;
    /**
     * View:?????????
     */
    private ImageView imageView;

    private Bitmap bitmap;

    private RelativeLayout imageLayout;

    private Button btnLoad;

    private Button btnExpand;
    /**
     * ??????
     */
    private ListView listView;
    /**
     * ???????????????
     */
    private ControlBar toolBar;

    private Button btnReturn;
    /**
     * ??????????????????
     */

    private Button btnMakeDir;
    /**
     * ????????????
     */

    private Button btnPaste;
    /**
     * ??????
     */

    private Button btnQuite;
    /**
     * ??????
     */

    private Button btnChooseDir;
    /**
     * ??????????????????
     */

    private Button btnShare;
    /**
     * ??????
     */
    // ?????????
    private ProgressDialog progressDialog;

    // ??????????????????
    private int ITEM_POSITION;

    // ???????????????
    private String[] clipBoard = null;

    private boolean isMoving = false;

    private boolean isLoadingLicense = false; // ????????????Lisense

    private boolean isLoadingZip = false;
    private boolean isLoadingNormalFile = false; // ????????????????????????

    /**
     * ????????????????????????
     */
    private boolean isLoadingBaseData = false;
    /**
     * ?????????????????????????????????
     */
    private int mapType = 0;

    private boolean isLoadingUrl = false;

    private boolean isChoosingDir = false;

    public final static int SHOWLeadinDataPROGRESS = 0;

    public final static int SHOWLicenseDataPROGRESS = 2;

    /**
     * ?????????????????????
     */
    private BaseStationService bsService;

    //?????????
    private String mRootPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_image);

        listView = (ListView) findViewById(R.id.ListView01);

        ImageButton ibBack = (ImageButton) findViewById(R.id.pointer);
        TextView tvTitle = (TextView) findViewById(R.id.title_txt);

        tvTitle.setText(R.string.file_explorer);
        ibBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithoutAnim();
            }
        });

        this.getBundle(); // ??????????????????

        app_path = getFilesDir().getAbsolutePath();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            init_path = getFilesDir().getAbsolutePath();
            Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_storge_sdcardnon), Toast.LENGTH_LONG).show();
        }
        this.getFileDir(init_path);
        Intent bsService = new Intent(this, BaseStationService.class);
        bindService(bsService, conn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bsService = ((BaseStationService.BaseStationBinder) service).getService();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        imageView = initImageView(R.id.ImageView01);
        imageView.setImageBitmap(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finishWithoutAnim();
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        unbindService(conn);
        super.onDestroy();

        imageView.setImageBitmap(null);
        // ??????bitmap???????????????????????????
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

    }

    /**
     * ?????????Activity??????????????????????????????
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        // ?????????????????????????????????
        try {
            // ????????????
            init_path = (bundle.getString(FileExplorer.KEY_INIT_DIR) == null) ? init_path : bundle
                    .getString(FileExplorer.KEY_INIT_DIR);
            mRootPath = init_path;
            if (mRootPath.length() > 1 && mRootPath.endsWith("/")) {
                mRootPath = mRootPath.substring(0, mRootPath.length() - 1);
            }
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_INIT_DIR");
            LogUtil.w(tag, " opening " + init_path);
        }

        try {
            // ??????????????????
            file_types = bundle.getStringArray(FileExplorer.KEY_FILE_FILTER) == null ? file_types : bundle
                    .getStringArray(FileExplorer.KEY_FILE_FILTER);
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_FILE_FILTER");
        }

        try {
            // ??????????????????
            fileSize = bundle.getLong(KEY_FILE_SIZE);
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_FILE_FILTER");
        }

        try {
            // ????????????License
            isLoadingLicense = bundle.getBoolean(FileExplorer.KEY_LICENSE);
        } catch (Exception e) {
            isLoadingLicense = false;
            LogUtil.w(tag, "there is no bundle for KEY_FILE_FILTER");
        }

        try {
            isLoadingNormalFile = bundle.getBoolean(KEY_NORMAL);
        } catch (Exception e) {
            isLoadingNormalFile = false;
        }

        try {
            isLoadingZip = bundle.getBoolean(FileExplorer.KEY_IMPORT_BUILD);
        } catch (Exception e) {
            isLoadingZip = false;
        }
        try {
            // ??????????????????
            isLoadingBaseData = bundle.getBoolean(FileExplorer.KEY_BASE_DATA);
            mapType = bundle.getInt(FileExplorer.KEY_MAP_TYPE);
        } catch (Exception e) {
            isLoadingBaseData = false;
            LogUtil.w(tag, "there is no bundle for KEY_BASE");
        }
        try {
            // ??????????????????url
            isLoadingUrl = bundle.getBoolean(FileExplorer.KEY_URL);
        } catch (Exception e) {
            isLoadingUrl = false;
            LogUtil.w(tag, "there is no bundle for KEY_URL");
        }
        try {
            // ?????????action
            actionOfBroadcast = bundle.getString(FileExplorer.KEY_ACTION) == null ? actionOfBroadcast : bundle
                    .getString(FileExplorer.KEY_ACTION);
            extraName = bundle.getString(FileExplorer.KEY_EXTRA) == null ? extraName : bundle
                    .getString(FileExplorer.KEY_EXTRA);
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_ACTION");
        }

        try {
            // ???????????????
            clipBoard = bundle.getStringArray(FileExplorer.KEY_CLIP) == null ? clipBoard : bundle
                    .getStringArray(FileExplorer.KEY_CLIP);
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_CLIP");
        }

        try {
            // ???????????????????????????
            isMoving = bundle.getBoolean(FileExplorer.KEY_REMOVE);
        } catch (Exception e) {
            isMoving = false;
            LogUtil.w(tag, "there is no bundle for KEY_REMOVE");
        }

        try {
            // ????????????
            isChoosingDir = bundle.getBoolean(FileExplorer.KEY_DIR);
        } catch (Exception e) {
            LogUtil.w(tag, "there is no bundle for KEY_DIR");
        }
    }


    /**
     * ????????????,??????????????????????????????,?????????Action??????????????????Extra?????????????????????Activity??????????????????
     *
     * @param filePath ?????????????????????
     */
    private void sendPathBroadcast(String filePath) {
        if (actionOfBroadcast.equals(MapActivity.ACTION_LOAD_MIF_MAP)) {
            SharePreferencesUtil.getInstance(this).saveString(WalktourConst.MIF_MAP_DIR, filePath);
        }
        Intent intent = new Intent();
        intent.putExtra(this.extraName, filePath);
        intent.setAction(this.actionOfBroadcast);
        sendBroadcast(intent);
    }

    /**
     * ???????????????????????????
     *
     * @param file ??????
     * @return ?????????file???????????????, ???????????????????????????????????????????????????
     */
    private boolean isFilted(File file) {
        // ????????????????????????????????????
        if (file.isDirectory()) {
            return false;
        }
        // ?????????????????????????????????????????????????????????
        if (!file.isDirectory() && !file.isFile()) {
            Log.i(">>>>>>>>", file.getName() + "    ");
            return true;
        } else {
            String path = file.getAbsolutePath();
            if (file_types.length == 0) {
                return false;
            }
            for (String x : file_types) {
                if (path.substring(path.lastIndexOf(".") + 1, path.length()).trim().equalsIgnoreCase(x.trim())) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param dir_path ???????????????????????????
     */
    private void getFileDir(String dir_path) {

        /* ?????????????????????SDCARD???????????????SDCARD?????????????????????,??????????????????????????? */
        File sdcardDir = new File(AppFilePathUtil.getInstance().getSDCardDirectory());
        File pathDir = new File(dir_path);
        try {
            if (sdcardDir.getCanonicalPath().equals(pathDir.getCanonicalPath())
                    && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_storge_sdcardnon), Toast.LENGTH_LONG)
                        .show();
                dir_path = getFilesDir().getAbsolutePath();
            }
        } catch (IOException e1) {
            dir_path = getFilesDir().getAbsolutePath();
            e1.printStackTrace();
        }

        File f = new File(dir_path);
        File[] files = f.listFiles();

        if (files == null) {
            return;
        }

        try {
            this.current_path = f.getAbsolutePath();
            LogUtil.w(tag, "---current path,getCanonicalPath():" + f.getCanonicalPath());
            LogUtil.w(tag, "---current path,getAbsolutePath():" + f.getAbsolutePath());
            LogUtil.w(tag, "---current path,getPath():" + f.getPath());

        } catch (IOException e) {
            e.printStackTrace();
            this.finishWithoutAnim();
        }
        setTitle(new File(this.current_path).getAbsolutePath());
        this.items = new ArrayList<String>();
        this.paths = new ArrayList<String>();

        if (!current_path.equals("/")) {// ?????????????????????????????????

            if (current_path.equals(app_path)) {// ???????????????????????????????????????????????????"/"
                this.items.add(".." + getString(R.string.up_directory));
                this.paths.add("/");
            } else {
                this.items.add(".." + getString(R.string.up_directory));
                File currentDir = new File(this.current_path);
                this.paths.add(currentDir.getParentFile().getAbsolutePath());
            }
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                // LogUtil.w("is filted", String.valueOf( this.isFilted(file) ) );
                boolean isNeedToFilte = false;// ????????????????????????
                if (isLoadingNormalFile) {
                    if (this.isFilted(file)) {
                        isNeedToFilte = true;
                    }
                    if (fileSize > 0 ? (file.length() > fileSize) : false) {
                        Log.i(">>>>>>>>>>>", file.getName() + "     ");
                        isNeedToFilte = true;
                    }
                } else if (this.isFilted(file)) {// ?????????????????????????????????????????????
                    isNeedToFilte = true;
                }
                if (!isNeedToFilte) {
                    this.items.add(file.getName());
                    this.paths.add(file.getAbsolutePath());
                }
            }
        } else {// ??????????????????????????????

            this.items.add(getString(R.string.phone));
            this.items.add(getString(R.string.sdcard));
            this.paths.add(app_path);
            this.paths.add(Environment.getExternalStorageDirectory().getPath() + "/");
        }

        this.genFileList();
        this.genToolBar();
    }

    /**
     * ???????????????
     */
    private void getImageView() {
        File file = new File(paths.get(ITEM_POSITION));

        try {
            // ??????bitmap???????????????????????????
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
            ShareCommons.CURRENT_CQT_PIC_NAME = file.getAbsolutePath();
            bitmap = createBitmap(file.getPath());
            imageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            LogUtil.v("readImage---->", e.toString());
        }
    }

    private final int IMG_MAX_PIXELS = 480 * 800;

    public Bitmap createBitmap(String filepath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // ?????????????????????????????????????????????????????????????????????
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, opts);
        opts.inSampleSize = UtilsMethod.computeSuitedSampleSize(opts, IMG_MAX_PIXELS);
        LogUtil.w(tag, "----opts.inSampleSize=" + opts.inSampleSize);
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, opts);
        bitmap = ImageUtil.rotaingImageView(filepath, bitmap);
        LogUtil.i("TabDataParser", "---mbmpTest width=" + bitmap.getWidth() + "---mbmpTest height=" + bitmap.getHeight());
        return bitmap;
    }


    private FileExplorerAdapter listItemAdapter;

    /**
     * ??????????????????????????????????????????????????????????????? ???????????????,???????????????????????????sd????????????????????? ?????????????????????????????????
     *
     * @author Administrator
     */
    class UnZip extends Thread {
        private String zipFileName;

        private String outputDirectory;

        private boolean isRevStructure = false;

        /**
         * ???????????????????????????
         *
         * @param zipFileName ????????????????????????
         */
        public UnZip(String zipFileName) {
            this.zipFileName = zipFileName;
            isRevStructure = zipFileName.endsWith(".ibwc");
            String targetDir = ApplicationModel.getInstance().getAppList().contains(WalkStruct.AppType.IndoorTest) ?
                    AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_data), getString(R.string.path_indoor))
                    : AppFilePathUtil.getInstance().getSDCardBaseDirectory(getString(R.string.path_data), getString(R.string.path_indoortest));
            this.outputDirectory = targetDir;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void run() {
            super.run();

            try {
                BufferedInputStream bi;
                ZipFile zf = new ZipFile(zipFileName, "GBK");// ????????????
                Enumeration e = zf.getEntries();
                Message msg;
                /** -----????????????????????????????????????---- **/
                ArrayList<Integer> dirNum = new ArrayList<Integer>();
                boolean firstCheck = false;// ?????????????????????????????????????????????"_"???????????????????????????????????????????????????????????????????????????????????????
                while (e.hasMoreElements()) {
                    org.apache.tools.zip.ZipEntry ze2 = (org.apache.tools.zip.ZipEntry) e.nextElement();
                    String entryName = ze2.getName();
                    if (entryName.indexOf("/") > -1 && !isRevStructure) {
                        String[] arrs = entryName.split("/");
                        if (arrs[0].indexOf("_") > -1) {
                            firstCheck = true;
                            // ??????????????????????????????4??????????????????????????????
                            if (arrs.length > 4) {
                                firstCheck = false;
                                msg = mHandler.obtainMessage(FILE_ERROR);
                                mHandler.sendMessage(msg);
                                return;
                            }
                            dirNum.add(arrs.length);
                            LogUtil.w(tag, "--arrs.length=" + arrs.length);
                        } else {
                            // ???????????????"_",?????????????????????
                            firstCheck = false;
                            msg = mHandler.obtainMessage(FILE_ERROR);
                            mHandler.sendMessage(msg);
                            return;
                        }
                    }
                }
                // ???????????????
                if (firstCheck) {
                    int max = 0;
                    for (int i = 0; i < dirNum.size(); i++) {
                        max = max < dirNum.get(i) ? dirNum.get(i) : max;
                    }
                    LogUtil.w(tag, "----dir max=" + max);
                    if (max != 4) {
                        msg = mHandler.obtainMessage(FILE_ERROR);
                        mHandler.sendMessage(msg);
                        return;
                    }
                }
                e = zf.getEntries();
                msg = mHandler.obtainMessage(SHOWPROGRESS, getString(R.string.str_importing));
                mHandler.sendMessage(msg);
                while (e.hasMoreElements()) {
                    org.apache.tools.zip.ZipEntry ze = (org.apache.tools.zip.ZipEntry) e.nextElement();
                    String entryName = ze.getName();
                    String path = outputDirectory + "/" + entryName;
                    if (ze.isDirectory()) {
                        LogUtil.i(tag, "--Create zip Directory - " + entryName);
                        File decompressDirFile = new File(path);
                        if (!decompressDirFile.exists()) {
                            decompressDirFile.mkdirs();
                        }
                    } else {
                        LogUtil.i(tag, "--Create zip file " + entryName);
                        String primalName = entryName.substring(entryName.lastIndexOf("/") + 1);
                        // ?????????????????????"lcpm_"?????? ?????????REV??????
                        if (!primalName.startsWith("lcpm_") && !isRevStructure) {
                            primalName = "lcpm_" + primalName;
                        }
                        // strs = entryName.split("/");
                        String fileDir = path.substring(0, path.lastIndexOf("/"));
                        File fileDirFile = new File(fileDir);
                        if (!fileDirFile.exists()) {
                            fileDirFile.mkdirs();
                        }
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDirFile.getAbsolutePath()
                                + "/" + primalName));
                        LogUtil.w(tag, "---ppppp=" + fileDirFile.getAbsolutePath() + "/" + primalName);
                        bi = new BufferedInputStream(zf.getInputStream(ze));
                        byte[] readContent = new byte[1024 * 8];
                        int readCount = 0;
                        while ((readCount = bi.read(readContent)) > 0) {
                            bos.write(readContent, 0, readCount);
                        }
                        bi.close();
                        bos.flush();
                        bos.close();
                    }
                }
                zf.close();
                /** ?????????REV?????????????????????????????????mapset.xml??????????????????????????????????????? */
                if (isRevStructure) {
                    renameByMapsetFile(outputDirectory + "mapset.xml");
                }
                SysBuildingManager.getInstance(getApplicationContext()).syncDB(getApplicationContext());
                msg = mHandler.obtainMessage(DISPROGRESS);
                mHandler.sendMessage(msg);
                Intent intent = new Intent(WalkMessage.ACTION_IMPORT_ZIP);
                sendBroadcast(intent);
                finishWithoutAnim();
            } catch (Exception e) {
                // ????????????????????????????????????????????????
                Message msg = mHandler.obtainMessage(DISPROGRESS);
                mHandler.sendMessage(msg);
                msg = mHandler.obtainMessage(FILE_ERROR);
                mHandler.sendMessage(msg);
                LogUtil.w(tag, "--e=" + e.getMessage());
                return;
            }
        }
    }

    /**
     * ????????????mapset.xml???????????????????????????????????????
     *
     * @param filename
     * @author tangwq
     */
    private void renameByMapsetFile(String filename) {
        LogUtil.w(tag, "---revFile:" + filename);
        MapSetByATTved mapSet = new MapSetByATTved(filename);
        mapSet.renameGuidToName(filename.substring(0, filename.lastIndexOf("/")) + "/");
    }

    /**
     * ??????????????????
     */
    private void genFileList() {
        // ???????????????
        btnExpand = initButton(R.id.Button01);
        btnLoad = initButton(R.id.Button02);
        imageLayout = initRelativeLayout(R.id.RelativeLayout02);
        imageLayout.setVisibility(View.GONE);
        imageView = initImageView(R.id.ImageView01);
        imageView.setImageBitmap(null);
        // ??????bitmap???????????????????????????
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        if (listItemAdapter == null) {
            listItemAdapter = new FileExplorerAdapter(this);
            listView.setAdapter(listItemAdapter);
        } else {
            listItemAdapter.notifyDataSetChanged();
        }

        // ??????item???????????????
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ITEM_POSITION = arg2;

                // ???????????????SD?????????SD???????????????
                File f = new File(paths.get(ITEM_POSITION));
                File s = new File(AppFilePathUtil.getInstance().getSDCardDirectory());
                try {
                    if (f.getCanonicalPath().startsWith(s.getCanonicalPath())
                            && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.sys_alarm_storge_sdcardnon), Toast.LENGTH_LONG)
                                .show();
                    } else {
                        File file = new File(paths.get(ITEM_POSITION));
                        if (file.isDirectory()) {
                            getFileDir(file.getPath());
                        } else {
                            if (isLoadingLicense || isLoadingBaseData) {
                                // ??????License
                                new LicenseLoader(file.getAbsolutePath()).start();
                            } else if (isLoadingZip) {
                                new UnZip(file.getAbsolutePath()).start();
                            } else if (isLoadingNormalFile || isLoadingUrl) {
                                // ????????????
                                sendPathBroadcast(file.getAbsolutePath());
                                Intent intent = new Intent();
                                intent.putExtra(RESPONSE_FILE_PATH, file.getAbsolutePath());
                                setResult(REQUEST_CODE, intent);
                                FileExplorer.this.finishWithoutAnim();
                            } else if (isChoosingDir) {
                                // do nothing
                            } else if (file.isFile()) {
                                // ???????????????
                                setTitle(paths.get(ITEM_POSITION));
                                imageLayout.setVisibility(View.VISIBLE);
                                btnShare.setVisibility(View.VISIBLE);
                                getImageView();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // ???????????????????????????
        btnLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageBitmap(null);

                // ??????bitmap???????????????????????????
                if (bitmap != null) {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                // ???????????????????????????
                if (actionOfBroadcast.equals(SysFloorMap.ACTION_LOADFILE)) {
                    File f = new File(paths.get(ITEM_POSITION));
                    LogUtil.w(tag, "-----fileexplorer path:" + paths.get(ITEM_POSITION));
                    if (f.isFile()) {
                        long length = f.length();
                        LogUtil.w(tag, "-----fileexplorer length:" + length / (1000 * 1000));
                        // ?????????????????????????????????????????????????????????
                        if (length > INDOOR_FILE_SIZE) {
                            Toast.makeText(FileExplorer.this, R.string.main_indoor_largefile, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                // ????????????
                sendPathBroadcast(paths.get(ITEM_POSITION));
                FileExplorer.this.finishWithoutAnim();
            }

        });

        btnExpand.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.GONE) {
                    listView.setVisibility(View.VISIBLE);
                    toolBar.setVisibility(View.VISIBLE);
                    btnExpand.setText(R.string.str_image_expand);
                    // ??????
                    RelativeLayout.LayoutParams params = (LayoutParams) imageLayout.getLayoutParams();
                    params.height = convertDIP2PX(getApplicationContext(), 160);
                    imageLayout.setLayoutParams(params);

                } else if (listView.getVisibility() == View.VISIBLE) {
                    listView.setVisibility(View.GONE);
                    toolBar.setVisibility(View.GONE);
                    btnExpand.setText(R.string.str_image_exit);
                    // ??????
                    RelativeLayout.LayoutParams params = (LayoutParams) imageLayout.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.FILL_PARENT;
                    imageLayout.setLayoutParams(params);
                }
            }

        });

    }

    /**
     * ???????????????
     */
    class FileExplorerAdapter extends BaseAdapter {

        // ?????????
        private Context context;

        // ?????????
        public FileExplorerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_style5, null);
                viewHolder = new ViewHolder();
                // ??????item???indexText?????????
                viewHolder.ItemTitle = (TextView) convertView.findViewById(R.id.ItemTitle);
                viewHolder.fileImg = (ImageView) convertView.findViewById(R.id.ItemImage);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            file = new File(paths.get(position));

            if (file.isFile()) {
                viewHolder.fileImg.setImageResource(R.drawable.file);
            } else if (file.isDirectory()) {
                viewHolder.fileImg.setImageResource(R.drawable.folder);
            }

            viewHolder.ItemTitle.setText(items.get(position));
            return convertView;
        }

        private class ViewHolder {

            TextView ItemTitle;

            ImageView fileImg;
        }

    }// end method


    /**
     * ????????????????????????
     */
    private void genToolBar() {
        toolBar = (ControlBar) findViewById(R.id.ControlBar);
        toolBar.setButtonsListener(barClickListener);
        toolBar.setVisibility((current_path.equals("/")) ? View.GONE : View.VISIBLE);
        btnQuite = toolBar.getButton(0);
        btnQuite.setText(R.string.str_cancle);
        btnQuite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_undo, 0, 0);
        btnMakeDir = toolBar.getButton(1);
        btnMakeDir.setText(R.string.makedir);
        btnMakeDir.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_new, 0, 0);
        btnReturn = toolBar.getButton(2);
        btnReturn.setText(getString(R.string.up_directory));
        btnReturn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_muilt, 0, 0);
        btnPaste = toolBar.getButton(3);
        btnPaste.setText(isMoving ? R.string.movehere : R.string.copyhere);
        btnPaste.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_edit, 0, 0);
        btnPaste.setVisibility((clipBoard == null) ? View.GONE : View.VISIBLE);
        btnChooseDir = toolBar.getButton(4);
        btnChooseDir.setText(R.string.str_ok);
        btnChooseDir.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_select, 0, 0);
        btnChooseDir.setVisibility(isChoosingDir ? View.VISIBLE : View.GONE);

        btnShare = toolBar.getButton(5);
        btnShare.setText(R.string.share_project_share);
        btnShare.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.controlbar_select, 0, 0);
        btnShare.setVisibility(View.GONE);

        if (isLoadingBaseData) {
            btnShare.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ?????????????????? ????????????????????????
     */
    private class Paster extends Thread {

        @Override
        public void run() {
            // ???????????????
            Message msg = mHandler.obtainMessage(SHOWPROGRESS, "");
            msg.sendToTarget();

            for (int i = 0; i < clipBoard.length; i++) {
                File file = new File(clipBoard[i]);
                if (file.isFile()) {
                    msg = mHandler.obtainMessage(SHOWMESSAGE, (isMoving ? getString(R.string.moving)
                            : getString(R.string.copying)) + file.getAbsolutePath());
                    msg.sendToTarget();
                    try {
                        // ?????????????????????????????????cp????????????????????????cp??????Java?????????
                        FileOperater operater = new FileOperater();
                        if (isMoving) {
                            operater.move(file.getAbsolutePath(), current_path + "/" + file.getName());
                        } else {
                            operater.copy(file.getAbsolutePath(), current_path + "/" + file.getName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    msg = mHandler.obtainMessage(SHOWMESSAGE, file.getAbsolutePath() + "is not file");
                    msg.sendToTarget();
                }
            }

            // ???????????????????????????
            msg = mHandler.obtainMessage(SHOWMESSAGE, getString(R.string.refreshdata));
            msg.sendToTarget();
            DataManagerFileList.getInstance(getApplicationContext()).refreshFilePath(current_path + File.separator);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            clipBoard = null;

            // ???????????????
            mHandler.obtainMessage(DISPROGRESS);
            finishWithoutAnim();
        }
    }

    /**
     * License????????????
     */
    private class LicenseLoader extends Thread {
        private String licensePath = "";

        public LicenseLoader(String licensePath) {
            this.licensePath = licensePath;
        }

        public void run() {
            if (isLoadingBaseData) {
                mHandler.obtainMessage(SHOWLeadinDataPROGRESS, getString(R.string.main_menu_license_loading)).sendToTarget();
                dealFileData();
            } else {
                mHandler.obtainMessage(SHOWLicenseDataPROGRESS, getString(R.string.main_menu_license_loading)).sendToTarget();
                copyLicense();
            }
            // copyFile();
            finishWithoutAnim();
        }

        private void dealFileData() {
            bsService.dealFileData(licensePath, mapType, mHandler);
        }

        /**
         * ??????License?????????
         */
        private void copyLicense() {
            // ?????????license
            File file = new File(licensePath);
            if (file.isFile()) {
                UtilsMethod.runCommand("rm " + getApplicationContext().getFilesDir() + "/license.bin");
                FileOperater operater = new FileOperater();
                operater.copy(file.getAbsolutePath(), getApplicationContext().getFilesDir() + "/license.bin");
                UtilsMethod.runCommand("chmod 666 " + getApplicationContext().getFilesDir() + "/license.bin");
            }
            try {
                stopService(new Intent(FileExplorer.this, ApplicationInitService.class));
                ApplicationModel.getInstance().setEnvironmentInit(false);
                Thread.sleep(1000);
                startService(new Intent(FileExplorer.this, ApplicationInitService.class));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mHandler.obtainMessage(DISPROGRESS).sendToTarget();
        }
    }

    private void showLicenseProgressDialog(String message, boolean cancleable) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancleable);
        progressDialog.show();
    }

    private void showLeadinDataProgressDialog(String message, boolean cancleable) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(R.string.str_tip);
        progressDialog.setMessage(getResources().getString(R.string.importing_map));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(100);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private static class MyHandler extends Handler {
        private WeakReference<FileExplorer> reference;

        public MyHandler(FileExplorer fe) {
            this.reference = new WeakReference<>(fe);
        }

        @Override
        public void handleMessage(Message msg) {
            FileExplorer ex = this.reference.get();
            switch (msg.what) {
                case BaseStationService.SHOW_PROGRESS:
                    ex.progressDialog.setProgress((Integer) msg.obj);
                    break;
                case SHOWLeadinDataPROGRESS:
                    if (msg.obj instanceof String) {
                        ex.showLeadinDataProgressDialog((String) msg.obj, true);
                    } else if (msg.obj instanceof Integer) {
                        ex.progressDialog.setProgress((Integer) msg.obj);
                    }
                    break;
                case SHOWLicenseDataPROGRESS:
                    ex.showLicenseProgressDialog((String) msg.obj, true);
                    break;
                /*
                 * case SHOWPROGRESS: showProgressDialog((String) msg.obj, true); break;
                 */
                case DISPROGRESS:
                    if (ex.progressDialog != null) {
                        ex.progressDialog.dismiss();
                    }
                    break;
                case SHOWMESSAGE:
                    ex.progressDialog.setMessage((String) msg.obj);
                    break;
                case FILE_ERROR:
                    new BasicDialog.Builder(ex).setTitle(R.string.main_indoor_alarm)
                            .setMessage(R.string.main_indoor_importerror)
                            .setNeutralButton(R.string.str_ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;

            }
        }

    }

    private Handler mHandler = new MyHandler(this);

    /**
     * ???????????????????????????
     */
    private OnClickListener barClickListener = new OnClickListener() {
        @SuppressLint("InflateParams")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                /* ?????? */
                case R.id.Button01:
                    finishWithoutAnim();
                    break;

                /* ???????????? */
                case R.id.Button02:
                    // ???XML??????????????????????????????:EditText
                    LayoutInflater factory = LayoutInflater.from(FileExplorer.this);
                    final View textEntryView = factory.inflate(R.layout.alert_dialog_edittext, null);
                    final EditText alert_EditText = (EditText) textEntryView.findViewById(R.id.alert_textEditText);
                    new BasicDialog.Builder(FileExplorer.this).setIcon(android.R.drawable.ic_menu_add)
                            .setTitle(R.string.makedir).setView(textEntryView)
                            .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String dirName = alert_EditText.getText().toString().trim();
                                    try {
                                        File file = new File(current_path + "/" + dirName);
                                        file.mkdir();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    getFileDir(current_path);
                                }
                            }).setNegativeButton(R.string.str_cancle).show();
                    break;

                // ????????????
                case R.id.Button03:
                    getFileDir(paths.get(0));
                    break;

                /* ???????????? */
                case R.id.Button04:
                    // ??????????????????
                    new Paster().start();
                    break;

                // ????????????
                case R.id.Button05:
                    sendPathBroadcast(current_path);
                    finishWithoutAnim();
                    break;
                // ??????
                case R.id.Button06:
                    Bundle bundle = new Bundle();
                    if (isLoadingBaseData) {
                        bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_STATION);
                        bundle.putString("station_path", current_path);
                        // ???????????????
                        jumpActivity(ShareNextActivity.class, bundle);
                    } else {
                        bundle.putInt(ShareCommons.SHARE_FROM_KEY, ShareCommons.SHARE_FROM_CQT_PIC);
                        // ???????????????
                        jumpActivity(ShareSendActivity.class, bundle);
                    }

                    finishWithoutAnim();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listView != null) {
                // ???????????????????????????,????????????
                if (listView.getVisibility() == View.GONE) {
                    listView.setVisibility(View.VISIBLE);
                    toolBar.setVisibility(View.VISIBLE);
                    btnExpand.setText(R.string.str_image_expand);
                    // ??????
                    RelativeLayout.LayoutParams params = (LayoutParams) imageLayout.getLayoutParams();
                    params.height = convertDIP2PX(this, 160);
                    imageLayout.setLayoutParams(params);
                } else {

                    if (current_path.equals(mRootPath)) {
                        this.finishWithoutAnim();
                    } else {
                        // ?????????????????????????????????
                        if (!current_path.equals("/")) {
                            getFileDir(paths.get(0));
                            return true;
                        } else {
                            this.finishWithoutAnim();
                        }
                    }

                }
            } else {
                this.finishWithoutAnim();
            }

            if (actionOfBroadcast.equals(MapActivity.ACTION_LOAD_SCAN_MAP) || actionOfBroadcast.equals(MapActivity.ACTION_LOAD_MIF_MAP)
                    || actionOfBroadcast.equals(MapActivity.ACTION_LOAD_TAB_MAP)) {
                Intent intent2 = new Intent(MapActivity.SWITCH_MAP);
                sendBroadcast(intent2);
            }
        }
        return false;
    }

    // ??????dip???px
    public static int convertDIP2PX(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

}