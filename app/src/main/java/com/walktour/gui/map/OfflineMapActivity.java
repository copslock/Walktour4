package com.walktour.gui.map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.adapter.OfflineMapAdapter;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.map.googlemap.constants.MapModel;
import com.walktour.gui.map.googlemap.constants.PrefConstants;
import com.walktour.gui.map.googlemap.kml.XMLparser.PredefMapsParser;
import com.walktour.gui.map.googlemap.utils.Ut;
import com.walktour.gui.newmap.BaseMapActivity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 离线地图选择界面
 * @author jianchao.wang
 *
 */

public class OfflineMapActivity extends BasicActivity implements PrefConstants{
    
    private ListView fileListView;
    
    OfflineMapAdapter offlineAdapter;
    
    private List<File> fileList = new ArrayList<File>();
    
    private String path = "";
    
    private String extension = "sqlitedb";
    
    public static final int FIND_DIROCTORY_FILE = 1;
    
    public static final int LOADING_OFFILEMAP = 2;
    
    private  final int EMPTY_FILE_DIALOG = 1000;
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FIND_DIROCTORY_FILE:
                    getFiles((String)msg.obj, extension, 2);
                    offlineAdapter.notifyDataSetChanged();
                    break;
                case LOADING_OFFILEMAP:
                    loadMap(new File((String)msg.obj));
                    break;
                default:
                    break;
            }
            return true;
        }
    });
    
    
    /**
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @param savedInstanceState
     * @see com.walktour.framework.ui.BasicActivity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offilinemap_activity);
        (initTextView(R.id.title_txt)).setText(R.string.map_offline);
        findViewById(R.id.pointer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OfflineMapActivity.this.finish();
			}
		});
        fileListView = (ListView) findViewById(R.id.file_list);
        
        path = AppFilePathUtil.getInstance().getSDCardBaseDirectory("maps");
        
        File file = new File(path);
        if(!file.exists()){
        	file.mkdir();
        }
        getFiles(path, extension, 2);
        offlineAdapter = new OfflineMapAdapter(this, fileList,mHandler);
        fileListView.setAdapter(offlineAdapter);
        if(fileList == null || fileList.size() == 0){
        	showDialog(EMPTY_FILE_DIALOG);
        }
    }
    
    
    
    /**
     * 遍历SD卡目录或者文件<BR>
     * 根据类型遍历目录文件，返回列表
     * @param path 目录路径
     * @param Extension 扩展名
     * @param isDiroctoryOrFile 1：目录 2：文件
     */
    public void getFiles(String path, String Extension, int isDiroctoryOrFile) { 
        File[] files = new File(path).listFiles();
        fileList.clear();
        for (File file : files) {
            if (file.isFile() && isDiroctoryOrFile ==2) {
                if (file.getPath()
                        .substring(file.getPath().length() - Extension.length())
                        .equals(Extension)){ //判断扩展名
                    fileList.add(file);
                }
            } else if (file.isDirectory() && file.getPath().indexOf("/.") == -1 && isDiroctoryOrFile == 1){
            	if(file.getName().equals("data") || file.getName().equals("cache")){
            		continue;
            	}
                fileList.add(file);
            } 
        }
    }
    
    public void loadMap(File file){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        List<MapModel> mapList = new ArrayList<MapModel>();
        if (file.getName()
                .toLowerCase()
                .endsWith(".mnm")
                || file.getName()
                        .toLowerCase()
                        .endsWith(".tar")
                || file.getName()
                        .toLowerCase()
                        .endsWith(".sqlitedb")) {
            String name = Ut.FileName2ID(file.getName());
            Editor ePref = pref.edit();
            ePref.putString(PREF_USERMAPS_ + name + "_baseurl", file.getAbsolutePath());
            ePref.commit();
            //如果是已经启用的地图
            /*if (pref.getBoolean("pref_usermaps_" + name + "_enabled", false)) {*/
            MapModel mapModel = new MapModel();
            mapModel.setMapid("usermap_" + name);
            mapModel.setName(pref.getString("pref_usermaps_"
                    + name + "_name",
                    file.getName()));
            mapList.add(mapModel);
            final SAXParserFactory fac = SAXParserFactory.newInstance();
            SAXParser parser = null;
            try {
                parser = fac.newSAXParser();
                if (parser != null) {
                    final InputStream in = getResources().openRawResource(R.raw.predefmaps);
                    parser.parse(in, new PredefMapsParser(mapList, pref));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String mapid = mapList.get(0).getMapid();
            Intent intent = new Intent();
            intent.putExtra("mapid", mapid);
            setResult(BaseMapActivity.OFFLINE_MAP_RESULT_CODE, intent);
            finish();
           
        }
    }



	/* (non-Javadoc)
	 * @see com.walktour.framework.ui.BasicActivity#onCreateDialog(int)
	 */
	@Override
    @SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		BasicDialog.Builder builder = new BasicDialog.Builder(OfflineMapActivity.this);
		switch (id) {
		case EMPTY_FILE_DIALOG:
			builder.setTitle(R.string.str_tip).setMessage(R.string.empty_offlinemap_file)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						OfflineMapActivity.this.finish();
					}
			});
			break;

		default:
			break;
		}
		return builder.create();
	}
    
    
    
}
