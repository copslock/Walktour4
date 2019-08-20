package com.walktour.Utils;

import com.walktour.base.util.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * 流量统计工具类
 * 在Android系统中流量的信息会保存在系统文件{@link #DEV_FILE}中，在手机
 * 产生网络流量的时候该文件按设备的名称保存上行和下行的流量值。格式如下：
 * 
 *Inter-| Receive          |  Transmit
 *face |bytes    packets errs |bytes    packets errs 
 *dev1: 0       0      0     0       0          0
 *dev2:0    0      0     0		0		  0
 *dev3: 0  0      0     0       0         0
 *dev4:      70       4    0    79       5     0
 *devn代表产生流量的设备名
 * 
 * 根据手机的不同标识GPRS流量的设备名称为{@link #gprs}
 * 标识wifi流量的设备名称为{@link #wifi }
 * 此流量统计服务就会每隔一段时间去读取系统流量文件来分类统计gprs和wifi的流量
 * 注意：有些设备在建立数据网络连接的时候才会在系统文件{@link #DEV_FILE }中有记录
 * 		 当关闭网络连接时系统文件{@link #DEV_FILE}中会删除该设备的记录。
 * @author maosen.zhang
 *
 */
public class TrafficStatisticsUtils {
	public static String TAG = "TrafficStatisticsUtils";

	/** 总的GPRS上传流量 */
	private long totalGprsTxTraffic = 0;
	/** 总的GPRS下载流量 */
	private long totalGprsRxTraffic = 0;
	
	/** 总的WIFI上传流量 */
	private long totalWifiTxTraffic = 0;
	/** 总的WIFI下载流量 */
	private long totalWifiRxTraffic = 0;
	
	
	/** 下载流量所在列 */
	private int rxDataIndex = -1;
	/** 上传流量所在列 */
	private int txDataIndex = -1;
	
	private String[] title1;
	private String[] title2;

	final public String DEV_FILE = "/proc/self/net/dev";// 系统流量文件
	
	/** 产生GPRS流量的设备名称 */
	private String[] gprs = new String[]{"ppp0","rmnet0","rmnet1","ccmni0","svnet0","pdp0","pdp_ip0"};
	
	/** 中间变量存储统计的时间间隔内的GPRS上传流量值  
	 * 其中该时间间隔内的上传流量等于该次读取 的上传流量值减去上次读取的上传流量值
	 * {@link #gprsRxTraffic}，{@link #wifiTxTraffic}， {@link#wifiRxTraffic}
	 * 算法相同
	 */
	private long[] gprsTxTraffic = new long[]{0,0,0,0,0,0,0}; 
	/** 统计的时间间隔内的GPRS下载流量值 */
	private long[] gprsRxTraffic = new long[]{0,0,0,0,0,0,0}; 
	
	/** 保存上次读取时设备的总（GPRS上传）流量值 */
	private long[] gprsTxLastTimeTotalTraffic = new long[]{-1,-1,-1,-1,-1,-1,-1};
	/** 保存上次读取时设备的总（GPRS下载）流量值 */
	private long[] gprsRxLastTimeTotalTraffic = new long[]{-1,-1,-1,-1,-1,-1,-1};
	
	
	
	/** 产生WiFi流量的设备名称 */
	private String[] wifi = new String[]{"tiwlan0","wlan0", "eth0"};
	/** 统计的时间间隔内的WIFI上传流量值 */
	private long[] wifiTxTraffic = new long[]{0,0,0}; 
	/** 统计的时间间隔内的WIFI上传流量值 */
	private long[] wifiRxTraffic = new long[]{0,0,0}; 
	
	/** 保存上次读取时设备的总（WIFI上传）流量值 */
	private long[] wifiTxLastTimeTotalTraffic = new long[]{-1,-1,-1};
	/** 保存上次读取时设备的总（WIFI下载）流量值 */
	private long[] wifiRxLastTimeTotalTraffic = new long[]{-1,-1,-1};
	
	/** 是否开始统计流量 */
	private boolean isInitOK = false;
	
	
	/**
	 * 初始化开始统计流量
	 */
	public synchronized void initTraffic(){
		readSystemDevTraffic();
		isInitOK = true;
	}
	
	/**
	 * 统计归零
	 */
	public synchronized void unInitTraffic(){
		initTrafficData();
		gprsTxLastTimeTotalTraffic = new long[]{-1,-1,-1,-1,-1,-1,-1};
		gprsRxLastTimeTotalTraffic = new long[]{-1,-1,-1,-1,-1,-1,-1};
		wifiTxLastTimeTotalTraffic = new long[]{-1,-1,-1};
		wifiRxLastTimeTotalTraffic = new long[]{-1,-1,-1};
		
		totalGprsTxTraffic = 0;
		totalGprsRxTraffic = 0;
		totalWifiTxTraffic = 0;
		totalWifiRxTraffic = 0;
		
		isInitOK = false;
	}
	
	/**
	 * 获取GPRS的流量
	 * @return GPRS的流量
	 */
	public synchronized long getGPRSTraffic(){
		readSystemDevTraffic();
		return totalGprsTxTraffic+totalGprsRxTraffic;
	}
	
	/**
	 * 获取WIFI的流量
	 * @return WIFI的流量
	 */
	public long getWIFITraffic(){
		readSystemDevTraffic();
		return totalWifiTxTraffic+totalWifiRxTraffic;
	}
	
	/**
	 * 是否已经开始统计流量
	 * @return	是否已经开始统计流量
	 */
	public boolean hasInit(){
		return isInitOK;
	}

	/**
	 * 读取系统设备的流量信息
	 */
	private void readSystemDevTraffic() {
		initTrafficData();
		FileReader fstream = null;
		try {
			fstream = new FileReader(DEV_FILE);
			BufferedReader in = new BufferedReader(fstream, 500);
			String line;
			List<String> gprsDataTitle = new ArrayList<String>();
			List<String> wifiDataTitle = new ArrayList<String>();
			while ((line = in.readLine()) != null) {
				analyzeTrafficInfo(line,gprsDataTitle,wifiDataTitle);
			}	
			saveTraffic();
			initLastTimeTotalData(gprsDataTitle, wifiDataTitle);
		} catch (IOException e) {
			LogUtil.w(TAG, e.getMessage(),e);
		}finally{
			if(fstream != null){
				try {
					fstream.close();
				} catch (IOException e) {}
			}
		}
	}	
	
	/**
	 * 初始化流量值
	 * 每次统计之前将所有中间变量中存储的流量值设置为0
	 */
	private void initTrafficData(){
		gprsTxTraffic = new long[]{0,0,0,0,0,0,0}; 
		gprsRxTraffic = new long[]{0,0,0,0,0,0,0}; 
		
		wifiTxTraffic = new long[]{0,0,0}; 
		wifiRxTraffic = new long[]{0,0,0}; 
	}
	
	/**
	 * 将统计时间间隔中没有统计到的WIFI和GPRS设备流量置为0
	 * 上次读取的信息中ppp0的上下行分别不为0，然后用户关闭了数据网络
	 * 这时ppp0这行信息在系统文件中被删除
	 * 然后下次在读取系统流量文件后如果发现ppp0被删除则清空ppp0的上下行的流量值
	 * @param gprsDataTitle
	 * @param wifiDataTitle
	 */
	private void initLastTimeTotalData(List<String> gprsDataTitle,List<String> wifiDataTitle){
		for(int i=0;i<gprs.length;i++){
			if(!gprsDataTitle.contains(gprs[i])){
				gprsTxLastTimeTotalTraffic[i] = 0;
				gprsRxLastTimeTotalTraffic[i] = 0;
				//LogUtil.w(TAG, "remove GPRS line name=="+gprs[i]);
			}
		}
		
		for(int i=0;i<wifi.length;i++){
			if(!wifiDataTitle.contains(wifi[i])){
				wifiTxLastTimeTotalTraffic[i] = 0;
				wifiRxLastTimeTotalTraffic[i] = 0;
				//LogUtil.w(TAG, "remove Wifi line name=="+wifi[i]);
			}
		}
	}
	
	/**
	 * 分析系统的流量文件
	 * 根据传入的数据找到下载流量值和上传流量值所在的列
	 * 并计算设备的上下行流量
	 * @param line
	 * @param gprsDataTitle	入参，每次分析后的GPRS设备名称会存入该值
	 * @param wifiDataTitle 入参，每次分析后的WIFI设备名称会存入该值
	 */
	private void analyzeTrafficInfo(String line,List<String> gprsDataTitle,List<String> wifiDataTitle){
		if(rxDataIndex == -1 || txDataIndex == -1){
			analyzeTitle(line);
			return;
		}
		String[] segs;
		String[] netdata;
		segs = line.trim().split(":");
		int index;
		if ((index = isGPRSTrafficData(segs[0])) != -1) {
			netdata = segs[1].split(" ");
			netdata = trimArray(netdata);
			calculateGPRSData(index, netdata);
			gprsDataTitle.add(segs[0].trim());
		} else if ((index = isWifiTrafficData(segs[0])) != -1) {
			netdata = segs[1].split(" ");
			netdata = trimArray(netdata);
			calculateWifiData(index, netdata);
			wifiDataTitle.add(segs[0].trim());
		}
	}
	
	/**
	 * 分析系统流量文件的标题信息找出传流量值和下载流量值所在的列
	 * 标题信息如下：
	 *Inter-| Receive             |  Transmit
	 *face  |bytes    packets errs |bytes    packets errs 
	 *
	 *根据该信息找出上传流量值（Transmit-->bytes）所在的列
	 *和下载流量值（Receive-->bytes）所在列
	 * @param line	系统流量文件的一行内容
	 */
	private void analyzeTitle(String line){
		//标题信息是两行所以title1，title2分别存储这两行信息
		if(title1 == null){
			title1 = line.split("\\|");
		}else if(title2 == null){
			title2 = line.split("\\|");
			
			//两行标题已经找到，分析并找出上传流量值和下载流量值所在的列
			int offSet = 0;
			for (int i = title1.length - 2;i < title1.length;i++) {
				String _t1 = title1[i];
				String _t2 = title2[i];
				String[] _title2Array = _t2.split(" ");
				_title2Array = trimArray(_title2Array);
				int index = indexOf("bytes", _title2Array);
				if(index == -1){
					break;
				}
				if(_t1.toLowerCase().trim().equals("receive")){
					rxDataIndex = index + offSet;
					offSet = _title2Array.length;
				}else if(_t1.toLowerCase().trim().equals("transmit")){
					txDataIndex = index + offSet;
					offSet = _title2Array.length;
				}
			}
		}else{
			LogUtil.w(TAG, "analyze title fail");
		}
	}
	
	
	/**
	 * 找出GPRS的总上传和下载流量值并计算统计间隔内的流量
	 * @param index		被计算的数据是{@link #gprs}[index]的设备
	 * @param netdata	所有的流量信息，包含设备上下行流量
	 */
	private void calculateGPRSData(int index,String[] netdata){
		//首先取得该设备的上下行总流量
		long currentRxTotal = Long.parseLong(netdata[rxDataIndex].trim());
		long currentTxTotal = Long.parseLong(netdata[txDataIndex].trim());
		
		//然后获取上次存储的上下行总流量
		long gprsRxLastSize = gprsRxLastTimeTotalTraffic[index];
		long gprsTxLastSize = gprsTxLastTimeTotalTraffic[index];
		
		if(gprsRxLastSize == -1 || gprsTxLastSize == -1){//第一次读取数据
			gprsRxTraffic[index] =  0;
			gprsTxTraffic[index] =  0;
		}else{
			if(currentRxTotal < gprsRxLastSize || currentTxTotal < gprsTxLastSize){
				//当前的上下行总流量 < 上次的上下行总流量则代表网络连接被关闭然后产生了cRxTotal和cTxTotal的流量
				gprsRxTraffic[index] =  currentRxTotal;
				gprsTxTraffic[index] =  currentTxTotal;
			}else{
				//当前的上下行总流量  > 上次的上下行总流量则该段时间间隔内的流量为两者之差
				gprsRxTraffic[index] =  currentRxTotal - gprsRxLastSize;
				gprsTxTraffic[index] =  currentTxTotal - gprsTxLastSize;
			}
		}
		
		
		//将上次的上下行总流量置为当前的上下行总流量
		gprsRxLastTimeTotalTraffic[index] = currentRxTotal;
		gprsTxLastTimeTotalTraffic[index] = currentTxTotal;
	}
	
	
	/**
	 * 找出WIFI的总上传和下载流量值并计算统计间隔内的流量
	 * @param index		被计算的数据是{@link #WIFI}[index]的设备
	 * @param netdata	所有的流量信息，包含设备上下行流量
	 */
	private void calculateWifiData(int index,String[] netdata){
		long currentRxTotal = Long.parseLong(netdata[rxDataIndex].trim());
		long currentTxTotal = Long.parseLong(netdata[txDataIndex].trim());
		
		long wifiRxLastSize = wifiRxLastTimeTotalTraffic[index];
		long wifiTxLastSize = wifiTxLastTimeTotalTraffic[index];
		
		if(wifiRxLastSize == -1 || wifiTxLastSize == -1){//第一次读取数据
			wifiRxTraffic[index] =  0;
			wifiTxTraffic[index] =  0;
		}else{
			if(currentRxTotal < wifiRxLastSize || currentTxTotal < wifiTxLastSize){
				wifiRxTraffic[index] =  currentRxTotal;
				wifiTxTraffic[index] =  currentTxTotal;
			}else{
				wifiRxTraffic[index] =  currentRxTotal - wifiRxLastSize;
				wifiTxTraffic[index] =  currentTxTotal - wifiTxLastSize;
			}
		}
		
		wifiRxLastTimeTotalTraffic[index] = currentRxTotal;
		wifiTxLastTimeTotalTraffic[index] = currentTxTotal;
	}
	
	
	/**
	 * 保存统计后的流量值,
	 */
	private void saveTraffic(){
		totalGprsRxTraffic += getGprsRx();
		totalGprsTxTraffic += getGprsTx();
		totalWifiRxTraffic += getWifiRx();
		totalWifiTxTraffic += getWifiTx();
	}
	
	/**
	 * 获取总的（即所有GPRS设备产生的）GPRS上传流量
	 * @return
	 */
	private long getGprsTx(){
		long totalTx = 0;
		for (long tx : gprsTxTraffic) {
			totalTx += tx;
		}
		return totalTx;
	}
	
	/**
	 * 获取总的（即所有GPRS设备产生的）GPRS下载流量
	 * @return
	 */
	private long getGprsRx(){
		long totalRx = 0;
		for (long rx : gprsRxTraffic) {
			totalRx += rx;
		}
		return totalRx;
	}
	
	/**
	 * 获取总的（即所有WIFI设备产生的）WIFI上传流量
	 * @return
	 */
	private long getWifiTx(){
		long totalTx = 0;
		for (long tx : wifiTxTraffic) {
			totalTx += tx;
		}
		return totalTx;
	}
	
	/**
	 * 获取总的（即所有WIFI设备产生的）WIFI下载流量
	 * @return
	 */
	private long getWifiRx(){
		long totalRx = 0;
		for (long rx : wifiRxTraffic) {
			totalRx += rx;
		}
		return totalRx;
	}
	
	/**
	 * 根据设备名称找出该设备在{@link #gprs}}数组中的位置
	 * @param seg	设备名称
	 * @return	seg设备在{@link #gprs}}数组中的位置
	 * 			-1代表不在{@link #gprs}}数组中
	 */
	private int isGPRSTrafficData(String seg){
		int index = -1;
		for (int i=0;i<gprs.length;i++) {
			if(seg.startsWith(gprs[i])){
				index = i; 
				break;
			}
		}
		return index;
	}
	
	/**
	 * 根据设备名称找出该设备在{@link #wifi}}数组中的位置
	 * @param seg	设备名称
	 * @return	seg设备在{@link #wifi}}数组中的位置
	 * 			-1代表不在{@link #wifi}}数组中
	 */
	private int isWifiTrafficData(String seg){
		int index = -1;
		for (int i=0;i<wifi.length;i++) {
			if(seg.startsWith(wifi[i])){
				index = i; 
				break;
			}
		}
		return index;
	}
	
	/**
	 * 将数组中值为空格的项清理掉
	 * @param array
	 * @return
	 */
	private String[] trimArray(String[] array){
		List<String> list = new ArrayList<String>();
		for (String str : array) {
			if(!str.trim().equals("")){
				list.add(str);
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * 找出待查找字符串在数组中的位置
	 * @param findStr
	 * @param strArray
	 * @return
	 */
	private int indexOf(String findStr,String[] strArray){
		int index = -1;
		for(int i=0;i<strArray.length;i++){
			if(findStr.trim().equals(strArray[i].trim())){
				index = i;
				break;
			}
		}
		return index;
	}
}
