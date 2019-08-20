package com.walktour.control.config;

import android.content.Context;
import android.os.Environment;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.WalkStruct;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;
import com.walktour.gui.R;
import com.walktour.gui.applet.FileExplorer;
import com.walktour.model.BuildingModel;
import com.walktour.model.FloorModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ConfigIndoor {
    private static final String TAG = "ConfigIndoor";
    private static ConfigIndoor sInstance;
    private ApplicationModel appModel = ApplicationModel.getInstance();
    // private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ArrayList<String> buildStore;

    private ConfigIndoor(Context context) {
        String mobileDir = appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)
                ? AppFilePathUtil.getInstance().getAppFilesDirectory(context.getString(R.string.path_data), context.getString(R.string.path_indoor))
                : AppFilePathUtil.getInstance().getAppFilesDirectory(context.getString(R.string.path_data), context.getString(R.string.path_indoortest));
        String sdcardDir = appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)
                ? AppFilePathUtil.getInstance().getSDCardBaseDirectory(context.getString(R.string.path_data), context.getString(R.string.path_indoor))
                : AppFilePathUtil.getInstance().getSDCardBaseDirectory(context.getString(R.string.path_data), context.getString(R.string.path_indoortest));
        buildStore = new ArrayList<>();
        buildStore.add(mobileDir);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            buildStore.add(sdcardDir);
        }
    }

    /**
     * 返回唯一实例
     *
     * @param context 上下文
     * @return 唯一实例
     */
    public static ConfigIndoor getInstance(Context context) {
        if (sInstance == null)
            sInstance = new ConfigIndoor(context);
        return sInstance;
    }

//    public String[] getFileDirs() {
//        String[] files = new String[buildStore.size()];
//        for (int i = 0; i < files.length; i++) {
//            files[i] = buildStore.get(i);
//        }
//        return files;
//    }

//    /**
//     * 获取所有建筑物模型
//     *
//     * @return
//     */
//     public ArrayList<BuildingModel> getBuildings(){
//     return this.getBuildings(false);
//     }

    /**
     * 根据建筑物名称获取建筑物对象
     *
     * @param buildingName 建筑物名称
     * @return
     */
    public BuildingModel getBuilding(Context context, String buildingName) {
        List<BuildingModel> buildings = this.getBuildings(context, false);
        for (BuildingModel building : buildings) {
            if (building.getName().equals(buildingName)) {
                return building;
            }
        }
        return null;
    }

    /**
     * 所有建筑物模型
     *
     * @param isAHWorkOrder 是否安徽工单项目
     */
    public List<BuildingModel> getBuildings(Context context, boolean isAHWorkOrder) {
        // 动态数组
        List<BuildingModel> arrayList = new ArrayList<BuildingModel>();
        try {
            for (String path : buildStore) {
                LogUtil.i(TAG, "----path=" + path);
                // 第一级目录为省市，中间以下划线分开，如"广东_珠海"
                File[] firstDir = new File(path).listFiles();
                // 有室内专项权限时
                for (int j = 0; j < firstDir.length; j++) {
                    if (!isAHWorkOrder && firstDir[j].isDirectory() && firstDir[j].getName().contains("_")) {
                        String buildAddress = firstDir[j].getName();
                        if (appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                            buildAddress = firstDir[j].getName();
                        } else {
                            buildAddress = "000000";
                        }
                        LogUtil.i(TAG, "----buildAddres=" + buildAddress);
                        // 省市下的建筑目录
                        File[] dirBuilds = firstDir[j].listFiles();
                        for (int i = 0; i < dirBuilds.length; i++) {
                            if (dirBuilds[i].isDirectory()) {
                                // LogUtil.i(TAG,
                                // "----dirBuilds[i].getAbsolutePath()="+dirBuilds[i].getAbsolutePath());
                                arrayList.add(new BuildingModel(dirBuilds[i].getName(), dirBuilds[i].getAbsolutePath(),
                                        getFloorList(context, dirBuilds[i]), getBuildMap(context, dirBuilds[i]), buildAddress));
                            }
                        }
                    } else {
                        // 如果没有室内专项权限，那么再添加第一级目录下的子目录即为建筑目录，无省市信息
                        if (!appModel.getAppList().contains(WalkStruct.AppType.IndoorTest)) {
                            if (firstDir[j].isDirectory()) {
                                arrayList.add(new BuildingModel(firstDir[j].getName(), firstDir[j].getAbsolutePath(),
                                        getFloorList(context, firstDir[j]), getBuildMap(context, firstDir[j]), "000000"));
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return arrayList;
    }

    /**
     * 获取指定建筑物的外观图
     *
     * @param buildDir 建筑物目录
     * @return 如果建筑目录下有外观图，返回外观图路径，否则返回null
     */
    private String getBuildMap(Context context, File buildDir) {
        File[] files = buildDir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String exception_name = file.getName().toLowerCase(Locale.getDefault());
                String[] filterType = context.getResources().getStringArray(R.array.maptype_picture);
                for (int k = 0; k < filterType.length; k++) {
                    if (exception_name.endsWith(filterType[k])) {
                        return file.getAbsolutePath();
                    }
                }

            }
        }
        return null;
    }

    /**
     * 获取指定目录的建筑物下的指定楼层名称的楼层对象
     *
     * @param building  建筑物
     * @param floorName 楼层名称
     * @return
     */
    public FloorModel getFloor(Context context, File building, String floorName) {
        List<FloorModel> floors = this.getFloorList(context, building);
        for (FloorModel floor : floors) {
            if (floor.getName().equals(floorName))
                return floor;
        }
        return null;
    }

    /**
     * @return 获取指定目录的建筑物的所有楼层模型
     */
    public List<FloorModel> getFloorList(Context context, File driBuild) {
        String[] filterType = context.getResources().getStringArray(R.array.maptype_picture);
        List<FloorModel> floorModellist = new ArrayList<FloorModel>();
        try {
            String mapsetFilepath = driBuild.getAbsolutePath() + "/" + "mapset.xml";
            File mapsetFile = new File(mapsetFilepath);
            if (mapsetFile.exists()) {
                return parserMapset(context, mapsetFile, driBuild);
            }
            File[] floorsDir = driBuild.listFiles();
            for (int i = 0; i < floorsDir.length; i++) {
                if (floorsDir[i].isDirectory()) {
                    File[] mapfiles = floorsDir[i].listFiles();
                    // LogUtil.i(TAG, "----floorsDir:"+floorsDir[i].getAbsolutePath());
                    ArrayList<String> allMapPaths = new ArrayList<String>();
                    ArrayList<String> outsideMaps = new ArrayList<String>();
                    for (File ff : mapfiles) {
                        // 如果是存放楼层外观图的目录
                        if (ff.isDirectory() && ff.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith("camera")) {
                            File[] outViews = ff.listFiles();
                            for (File f : outViews) {
                                String extension_name = f.getName().toLowerCase(Locale.getDefault());
                                // 过滤文件，只添加图片文件
                                for (int k = 0; k < filterType.length; k++) {
                                    if (extension_name.endsWith(filterType[k])) {
                                        // LogUtil.i(TAG, "----ff:"+ff.getAbsolutePath());
                                        // 如果楼层外观图在限制范围之内
                                        if (f.length() < (3.3 * 1024 * 1024)) {
                                            outsideMaps.add(f.getAbsolutePath());
                                        }
                                    }
                                }
                            }
                        } else if (ff.isFile() && ff.length() > 0) {

                            String extendName = ff.getName().toLowerCase(Locale.getDefault());
                            // 过滤文件，只添加图片文件
                            for (int k = 0; k < filterType.length; k++) {
                                if (extendName.endsWith(filterType[k])) {
                                    // LogUtil.i(TAG, "----ff:"+ff.length());
                                    // 如果楼层图在限制范围之内
                                    if (ff.length() < FileExplorer.INDOOR_FILE_SIZE) {
                                        allMapPaths.add(ff.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                    // LogUtil.i(TAG, "----floorsDir["+i+"]
                    // "+floorsDir[i].getName()+"="+sdf.format(floorsDir[i].lastModified()));
                    FloorModel floorModel = new FloorModel(floorsDir[i].getName(), floorsDir[i].getAbsolutePath(), allMapPaths,
                            outsideMaps, driBuild.getAbsolutePath().substring(driBuild.getAbsolutePath().lastIndexOf("/") + 1));
                    floorModellist.add(floorModel);
                }
            }
        } catch (Exception e) {
            LogUtil.i(TAG, e.toString());
        }

        return floorModellist;
    }

    /**
     * 解析iBwave mappset.xml文件<BR>
     * [功能详细描述]
     *
     * @param mapsetFile
     * @param build
     * @return
     */
    public ArrayList<FloorModel> parserMapset(Context context, File mapsetFile, File build) {
        ArrayList<FloorModel> floorList = null;
        String[] filterType = context.getResources().getStringArray(R.array.maptype_picture);
        String floorName = null;
        String imageDir = null;
        String tabfilePath = null;
        ArrayList<String> allMapPaths = null;
        FloorModel floorModel = null;
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = fac.newDocumentBuilder();
            Document doc = db.parse(mapsetFile);
            NodeList buildings = doc.getElementsByTagName("Building");
            if (buildings != null) {
                floorList = new ArrayList<FloorModel>();
                for (int j = 0; j < buildings.getLength(); j++) {
                    Node buildNode = buildings.item(j);

                    if (buildNode.getNodeType() == Node.ELEMENT_NODE) {
                        String buildingName = buildNode.getAttributes().getNamedItem("Name").getNodeValue();
                        LogUtil.w(TAG, "---building name:" + buildingName);
                        Element buildEle = (Element) buildNode;

                        NodeList nodelist = buildEle.getElementsByTagName("LayoutPlan");
                        if (nodelist != null) {
                            for (int i = 0; i < nodelist.getLength(); i++) {
                                Node node = nodelist.item(i);
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    NamedNodeMap nodemap = node.getAttributes();
                                    Element el = (Element) node;
                                    floorName = nodemap.getNamedItem("Name").getNodeValue();
                                    String image = el.getElementsByTagName("Image").item(0).getFirstChild() != null
                                            ? el.getElementsByTagName("Image").item(0).getFirstChild().getNodeValue().trim() : "";
                                    if (!StringUtil.isNullOrEmpty(image)) {
                                        image = image.substring(image.lastIndexOf("\\") + 1, image.length());
                                        imageDir = build.getAbsolutePath() + "/" + floorName;
                                        allMapPaths = new ArrayList<String>();
                                        image = imageDir + "/" + image;
                                        allMapPaths.add(image);
                                    }

                                    String tabfile = el.getElementsByTagName("TabFile").item(0).getFirstChild() != null
                                            ? el.getElementsByTagName("TabFile").item(0).getFirstChild().getNodeValue().trim() : "";
                                    if (!StringUtil.isNullOrEmpty(tabfile)) {
                                        tabfile = tabfile.substring(tabfile.lastIndexOf("\\") + 1, tabfile.length());
                                        tabfilePath = build.getAbsolutePath() + "/" + floorName + "/" + tabfile;
                                    }
                                    ArrayList<String> outsideMaps = new ArrayList<String>();

                                    if (!StringUtil.isNullOrEmpty(imageDir)) {

                                        File[] mapfiles = new File(imageDir).listFiles();

                                        if (mapfiles != null) {

                                            for (File ff : mapfiles) {
                                                // 如果是存放楼层外观图的目录
                                                if (ff.isDirectory()
                                                        && ff.getAbsolutePath().toLowerCase(Locale.getDefault()).endsWith("camera")) {
                                                    File[] outViews = ff.listFiles();
                                                    for (File f : outViews) {
                                                        String extension_name = f.getName().toLowerCase(Locale.getDefault());
                                                        // 过滤文件，只添加图片文件
                                                        for (int k = 0; k < filterType.length; k++) {
                                                            if (extension_name.endsWith(filterType[k])) {
                                                                // LogUtil.i(TAG,
                                                                // "----ff:"+ff.getAbsolutePath());
                                                                // 如果楼层外观图在限制范围之内
                                                                if (f.length() < (3.3 * 1024 * 1024)) {
                                                                    outsideMaps.add(f.getAbsolutePath());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!StringUtil.isNullOrEmpty(tabfilePath) && !StringUtil.isNullOrEmpty(imageDir)) {
                                        // 判断楼层目录是否存在
                                        if (new File(imageDir).exists()) {
                                            floorModel = new FloorModel(floorName, imageDir, allMapPaths, outsideMaps, build.getName());
                                            floorModel.tabfilePath = tabfilePath;
                                            floorList.add(floorModel);
                                        }
                                        floorName = null;
                                        imageDir = null;
                                        allMapPaths = null;
                                        tabfilePath = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

		/*
         * try { XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		 * factory.setNamespaceAware(true); XmlPullParser parser =
		 * factory.newPullParser(); parser.setInput(new
		 * FileInputStream(mapsetFile),"utf-8"); int type = parser.getEventType();
		 * while (type != XmlPullParser.END_DOCUMENT) { switch (type) { case
		 * XmlPullParser.START_TAG: String TAG = parser.getName();
		 * if("Building".equals(TAG)){ buildName = parser.getAttributeValue(null,
		 * "Name"); if(build.getName().equals(buildName)){ floorList = new
		 * ArrayList<FloorModel>();
		 * 
		 * } }else if (floorList != null) { if ("LayoutPlan".equals(TAG)) {
		 * floorName = parser.getAttributeValue(null,"Name"); }
		 * if("Image".equals(TAG)){ String image = parser.nextText();
		 * if(!StringUtil.isNullOrEmpty(image)){ image =
		 * image.substring(image.lastIndexOf("\\") + 1, image.length()); imageDir =
		 * build.getAbsolutePath() + "/" + floorName; allMapPaths = new
		 * ArrayList<String>(); image = imageDir + "/" + image;
		 * allMapPaths.add(image); } } if("TabFile".equals(TAG)){ String tabfile =
		 * parser.nextText(); if(!StringUtil.isNullOrEmpty(tabfile)){ tabfile =
		 * tabfile.substring(tabfile.lastIndexOf("\\") +1, tabfile.length());
		 * tabfilePath = build.getAbsolutePath() + "/" + floorName +"/" + tabfile; }
		 * } } break; case XmlPullParser.END_TAG: String endTag = parser.getName();
		 * if ("LayoutPlan".equals(endTag) && buildName.equals(build.getName())) {
		 * if(!StringUtil.isNullOrEmpty(tabfilePath) &&
		 * !StringUtil.isNullOrEmpty(imageDir)){ //判断楼层目录是否存在 if(new
		 * File(imageDir).exists()){ floorModel = new FloorModel(floorName,
		 * imageDir, allMapPaths, outsideMaps, build.getName());
		 * floorModel.tabfilePath = tabfilePath; floorList.add(floorModel); }
		 * floorName = null; imageDir = null; allMapPaths = null; tabfilePath =
		 * null; } } break; default: break; } type = parser.next(); } } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
        return floorList;
    }

    /**
     * 重命名建筑物
     */
    public void setBuildingName(BuildingModel building, String newName) {
        File file = new File(building.getDirPath());
        File desFile = new File(file.getParent() + "/" + newName);
        file.renameTo(desFile);
    }

    /**
     * 重命名楼层
     */
    public void setFloorName(FloorModel floor, String newName) {
        File file = new File(floor.getDirPath());
        File desFile = new File(file.getParent() + "/" + newName);
        file.renameTo(desFile);
    }

    /**
     * 复制文件到
     *
     * @param dirFloor
     *          楼层目录
     * @param mapSrcPath
     *          地图文件源路径
     */
	/*
	 * public void setFloorMap(String dirFloor,String mapSrcPath){ String
	 * mapPrimalName = ""; try{ LogUtil.i(TAG, "----dirFloor:"+dirFloor);
	 * LogUtil.i(TAG, "----value:"+mapSrcPath); mapPrimalName =
	 * mapSrcPath.substring(mapSrcPath.lastIndexOf("/")+1,mapSrcPath.length());
	 * File file = new File( dirFloor+"/"+mapPrimalName); if( file.isFile() ){
	 * file.delete(); } }catch( Exception e){ LogUtil.i(TAG,
	 * e.getClass().toString() ); } FileOperater operater = new FileOperater();
	 * operater.copy( mapSrcPath, dirFloor+"/"+mapPrimalName); }
	 */

    /**
     * 复制文件到
     *
     * @param srcMapPath    源文件路径
     * @param targetMapPath 目的文件路径
     */
    public void setMap(String srcMapPath, String targetMapPath) {
        try {
            FileOperater operater = new FileOperater();
            operater.copy(srcMapPath, targetMapPath);
        } catch (Exception e) {
            LogUtil.w(TAG, e.getClass().toString());
        }
    }

    /**
     * 复制文件夹,将源文件下的所有文件拷贝至目的文件夹下面
     *
     * @param srcDir    源文件夹目录
     * @param targetDir 目的文件夹目录
     */
    public void copyDirectory(String srcDir, String targetDir) {
        try {
            FileOperater operater = new FileOperater();
            File taDir = new File(targetDir);
            taDir.mkdirs();
            File sourcefiles = new File(srcDir);
            if (sourcefiles.exists()) {
                File[] files = sourcefiles.listFiles();
                for (int i = 0; i < files.length; i++) {
                    // 如果是文件
                    if (files[i].isFile()) {
                        String targetfilepath = taDir.getAbsolutePath() + File.separator + files[i].getName();
                        operater.copy(files[i].getAbsolutePath(), targetfilepath);
                    }
                    // 如果是文件夹
                    if (files[i].isDirectory()) {
                        String dir1 = srcDir + "/" + files[i].getName();
                        String dir2 = targetDir + "/" + files[i].getName();
                        copyDirectory(dir1, dir2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }

    /**
     * 增加建筑物
     *
     * @param buildingName 建筑物名称
     */
    public boolean addBuilding(Context context, String buildingName) {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 在 /sdcard/walktour/data/indoor/中添加目录
            file = AppFilePathUtil.getInstance().getSDCardBaseFile(context.getString(R.string.path_data),context.getString(R.string.path_indoortest),buildingName);
        } else {
            // 在 /data/data/com.walktour.gui/files/data/indoor/中添加目录
            file = AppFilePathUtil.getInstance().getAppFilesFile(context.getString(R.string.path_data),context.getString(R.string.path_indoortest),buildingName);
        }
        if (file.exists() && file.isDirectory())
            return true;
        else if (!file.exists()) {
            file.mkdirs();
            return true;
        } else
            return false;
    }

    /**
     * 为指定建筑物添加楼层
     *
     * @param buildingDir 建筑物路径
     * @param floorName   楼层名称
     */
    public boolean addFloor(File buildingDir, String floorName) {
        File dir = new File(buildingDir.getAbsolutePath() + File.separator + floorName);
        if (!dir.exists())
            dir.mkdirs();
        if (dir.isDirectory()) {
            File outViewFloor = new File(dir.getAbsolutePath() + File.separator + "camera");
            if (!outViewFloor.exists())
                outViewFloor.mkdirs();
            return true;
        }
        return false;
    }

    /**
     * 为指定楼层添加地图
     *
     * @param driFloor   楼层目录
     * @param mapSrcPath 地图源路径
     */
    public void addFloorMap(String driFloor, String mapSrcPath) {
        FileOperater op = new FileOperater();
        String mapName = mapSrcPath.substring(mapSrcPath.lastIndexOf("/"), mapSrcPath.length());
        String mapDesPath = driFloor + "/" + mapName;
        op.copy(mapSrcPath, mapDesPath);
    }

    /**
     * @param path 需要更新的目录
     */
    public void update(String path) {
        File file = new File(path);
        if (file.isFile()) {
            // 删除文件

        } else if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                // 递归删除目录下的所有文件
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    delete(files[i].getAbsolutePath());
                }
                // 删除本目录
                file.delete();
            }
        }
    }

    /**
     * 删除目录以及目录下的所有文件并从数据库里删除
     *
     * @param path 指定的文件路径
     */
    public void delete(String path) {
        File file = new File(path);
        this.delete(file);
    }

    /**
     * 删除文件
     *
     * @param parent
     */
    private void delete(File parent) {
        if (parent.isDirectory()) {
            for (File file : parent.listFiles()) {
                this.delete(file);
            }
        }
        parent.delete();
    }

}// end ConfigIndoor
