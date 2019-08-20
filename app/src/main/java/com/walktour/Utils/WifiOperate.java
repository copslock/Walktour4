/*
 * 文件名: WifiOperate.java
 * 版    权：  Copyright DingliCom Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: tangwq
 * 创建时间:2012-10-11
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.walktour.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.model.UmpcEnvModel;

/**
 * [WIFI相关操作]<BR>
 * [处理连接UMPC时通过命令绑定WIFI的相关操作]
 * @author tangwq
 * @version [WalkTour Client V100R001C03, 2012-10-11] 
 */
public class WifiOperate {
	private static final String tag = "WifiOperate";
    
    /**
     * 获得eth0绑定的本地IP
     * @return
     */
    public static String getLocalWifiIP(Deviceinfo deviceInfo){
        String result = UtilsMethod.getLinuxCommandResult("netcfg");
        String localIp="";
        if(result != null){
            String[] nets = result.split("\n");
            for(int i=0;i<nets.length;i++){
                if(nets[i].indexOf(deviceInfo.getWifiDevice())>=0){
                    String eth0 = nets[i];
                    String[] ls = eth0.split(" ");
                    for(int j=0;j<ls.length;j++){
                        if(!ls[j].startsWith("255") && UtilsMethod.getTargetStrTimes(ls[j],'.') == 3){
                            localIp = ls[j];
                            if(localIp.indexOf("/")>0){
                                localIp = localIp.substring(0,localIp.indexOf("/"));
                            }
                            if(localIp.indexOf("\\")>0){
                                localIp = localIp.substring(0,localIp.indexOf("\\"));
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        return localIp;
    }
    
    /**
     * 执行绑定WIFI动作
     * @author tangwq
     */
    public static void toBandWifi(Deviceinfo deviceInfo,UmpcEnvModel umpcEnvModel){
        LogUtil.w("--WifiOperate","---model:"+deviceInfo.getDevicemodel()
                +"--"+UtilsMethod.getSDKVersionNumber());
        if(deviceInfo.getDevicemodel().toUpperCase().equals("G11")
                || deviceInfo.getDevicemodel().toUpperCase().equals("G18")){
            doConnectTargetWIFIByG11(umpcEnvModel);             //执行连接指定WIFI
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("ME860")){
            doConnectTargetWIFIByME860(umpcEnvModel);
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("MT870")
                || deviceInfo.getDevicemodel().toUpperCase().equals("XT882")){
            doConnectTargetWIFIByMT870(umpcEnvModel);
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("G19")){
            doConnectTargetWIFIByG19(umpcEnvModel);
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("G22")){
            doConnectTargetWIFIByG22(umpcEnvModel);
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("XT910")){
            if(UtilsMethod.getSDKVersionNumber()>=15){
                doConnectTargetWIFIByXT910(umpcEnvModel);
            }else{
                doConnectTargetWIFIByG22(umpcEnvModel);
            }
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("XT800+")
                || deviceInfo.getDevicemodel().toUpperCase().equals("XT800")){
            doConnectTargetWIFIByXT800A(umpcEnvModel);
        }else if(deviceInfo.getDevicemodel().toUpperCase().equals("I747")){
            doConnectTargetWIFIByS3I747(umpcEnvModel);
        }else if(deviceInfo.getWifiDataOnly().equals("1")){
            doConnectTargetWIFIByS3I9305(umpcEnvModel);
        }
    }
    
    public static boolean checkWifiConnected(Context context){
    	//在S4中，WIFI 3G共存后，该连接状态为false，不可取
	    /*boolean isConnected = false;
	    ConnectivityManager cwjManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null){
            isConnected = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        }*/
        
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiManager.isWifiEnabled() && wifiInfo.getIpAddress() != 0;
	}
	
	/**
     * 检查当前网络是否已连接
     * @return
     */
    public static boolean checkNetWorkIsConnected(Context context){
    	boolean isConnected = false;
    	try{
	    	ConnectivityManager cwjManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	/*if (cwjManager.getActiveNetworkInfo() != null){ 
	    		isConnected = cwjManager.getActiveNetworkInfo().isConnected();*/
	    	if (cwjManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){ 
                isConnected = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	    	}else{
	    		isConnected = false;
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    		isConnected = false;
    	}
    	return isConnected;
    }
    
    /**wifi断开重新连接线程运行状态*/
	private static boolean wifiReConStateRun = false;
	
	/**如果当前为I9305WIFI接入点无法正常连接，执行命令将WIFI关闭再打开*/
	public static void setDataWifiEnable(Context context,String preferState)throws InterruptedException{
		if(!wifiReConStateRun){
			wifiReConStateRun = true;
			int redoTimes = 0;
			while(redoTimes < 5){
				LogUtil.w(tag, "--setDataWifiEnable--" + redoTimes++);
			    UtilsMethod.runRootCommand("svc wifi disable");
			    waitWifiState(context,false);
			    UtilsMethod.runRootCommand("svc data disable");
			    waitDataState(context,false);
			    UtilsMethod.runRootCommand("svc data enable");
			    waitDataState(context,true);
		        UtilsMethod.runRootCommand("svc data prefer");
		        Thread.sleep(1000);
		        UtilsMethod.runRootCommand("svc wifi enable");
		        waitWifiState(context,true);

		        //当前值为2时表示当前需要设置wifi优选，走下面流程
		        if(preferState.equals("2")){
			        UtilsMethod.runRootCommand("svc wifi prefer");
			        Thread.sleep(1000);
		        }

				UtilsMethod.runRootCommand("svc data disable");
				Thread.sleep(1000);
				UtilsMethod.runRootCommand("svc data enable");
				waitDataState(context,true);

			    if(checkWifiConnected(context) && checkNetWorkIsConnected(context)){
			    	break;
			    }
			}
	        wifiReConStateRun = false;
		}
	}
	
	/**数据优先流程，重置wifi开关*/
	public static void setWifiEnableDataByDataPrefer(Context context,String preferState){
		if(!wifiReConStateRun){
			wifiReConStateRun = true;
			UtilsMethod.runRootCommand("svc wifi disable");
			waitWifiState(context, false);
			UtilsMethod.runRootCommand("svc wifi enable");
			waitWifiState(context, true);
			if(preferState.equals("2")){
				UtilsMethod.runRootCommand("svc wifi prefer");
			}
			wifiReConStateRun = false;
		}
	}
	
	/**等待WIFI 执行状态*/
	private static synchronized void waitWifiState(Context context,boolean state){
		int waitTimeOut = 20000;	//等待超时15秒
		int timeOut = 0;
		try{
			boolean isWifiConnected = checkWifiConnected(context);
			while((isWifiConnected && !state || !isWifiConnected && state)&& timeOut < waitTimeOut){
				LogUtil.w(tag, "--wait wifi " + state + "--" +timeOut);
				Thread.sleep(1000);
				timeOut += 1000;
				isWifiConnected = checkWifiConnected(context);
				
				if(state && isWifiConnected){
					LogUtil.w(tag, "--wait wifi true keep 1S");
					Thread.sleep(1000);
					isWifiConnected = checkWifiConnected(context);
				}else if(!state && !isWifiConnected){
					Thread.sleep(1000);
					isWifiConnected = checkWifiConnected(context);
				}
			}
		}catch(Exception e){
			LogUtil.w(tag, "",e);
		}
	}
	
	/**等待数据拨号 执行状态*/
	private static synchronized void waitDataState(Context context,boolean state){
		int waitTimeOut = 15000;	//等待超时15秒
		int timeOut = 0;
		try{
			boolean isNetWorkConnect = checkNetWorkIsConnected(context);
			while((!state && isNetWorkConnect || state && !isNetWorkConnect) && timeOut < waitTimeOut){
				LogUtil.w(tag, "--wait data " + state + "--" +timeOut);
				Thread.sleep(1000);
				timeOut += 1000;
				isNetWorkConnect = checkNetWorkIsConnected(context);
				
				if(state && isNetWorkConnect){
					LogUtil.w(tag, "--wait data true keep 2S");
					Thread.sleep(2000);
					isNetWorkConnect = checkNetWorkIsConnected(context);
				}else if(!state && !isNetWorkConnect){
					Thread.sleep(1000);
					isNetWorkConnect = checkNetWorkIsConnected(context);
				}
			}
		}catch(Exception e){
			LogUtil.w(tag, "",e);
		}
	}
	
    /**
     * G11,G18通过bcm4329库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByG11(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod bcm4329");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            UtilsMethod.runRootCommand("insmod /system/lib/modules/bcm4329.ko");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/wpa_supplicant/eth0");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg eth0 dhcp");
            }else{
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf&");
                Thread.sleep(8000);
                
                UtilsMethod.runRootCommand("dhcpcd eth0");
                //UtilsMethod.runRootCommand("netcfg eth0 dhcp");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * G19通过bcm4330库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByG19(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod bcm4330.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/modules/bcm4330.ko \"firmware_path=/system/etc/firmware/fw_bcm4330b2.bin nvram_path=/system/etc/calibration\"");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg eth0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf&");
                Thread.sleep(7000);
                UtilsMethod.runRootCommand("netcfg eth0 dhcp");
                //UtilsMethod.runRootCommand("dhcpcd eth0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * G22通过tiwlan_drv库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByG22(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod tiwlan_drv.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/modules/tiwlan_drv.ko");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig tiwlan0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig tiwlan0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg tiwlan0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dtiwlan0 -itiwlan0 -c/data/local/wpa.conf&");
                Thread.sleep(6000);
                UtilsMethod.runRootCommand("dhcpcd tiwlan0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * G22通过tiwlan_drv库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByXT910(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod wl12xx_sdio.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/modules/wl12xx_sdio.ko");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg wlan0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dnl80211 -iwlan0 -c/data/local/wpa.conf&");
                Thread.sleep(6000);
                UtilsMethod.runRootCommand("dhcpcd wlan0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * ME860通过dhd.ko库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByME860(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod dhd.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/dhd.ko firmware_path=/system/etc/firmware/wifi/sdio-ag-cdc-full11n-minioctl-roml-pno-wme-aoe-pktfilter-keepalive-wapi.bin nvram_path=/system/etc/firmware/wifi/nvram.txt");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg eth0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf&");
                Thread.sleep(6000);
                //UtilsMethod.runRootCommand("netcfg eth0 dhcp");
                UtilsMethod.runRootCommand("dhcpcd eth0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * ME860通过dhd.ko库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByMT870(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod dhd.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/dhd.ko \"firmware_path=/system/etc/firmware/wifi/sdio-ag-cdc-full11n-minioctl-roml-pno-wme-aoe-pktfilter-keepalive-wapi.bin nvram_path=/system/etc/firmware/wifi/nvram.txt\"");
            Thread.sleep(1000*10);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig eth0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg eth0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf&");
                Thread.sleep(8000);
                //UtilsMethod.runRootCommand("netcfg eth0 dhcp");
                UtilsMethod.runRootCommand("dhcpcd eth0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev eth0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * XT800+通过tiwlan_drv.ko库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByXT800A(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod tiwlan_drv.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            UtilsMethod.runRootCommand("insmod /system/lib/modules/tiwlan_drv.ko");
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig tiwlan0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig tiwlan0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg tiwlan0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                UtilsMethod.runRootCommand("ifconfig tiwlan0 up");
                //Thread.sleep(1000);
                //new RunCommandThread("wpa_supplicant -Dtiwlan0 -itiwlan0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dtiwlan0 -itiwlan0 -c/data/local/wpa.conf&");
                Thread.sleep(6000);
                UtilsMethod.runRootCommand("dhcpcd tiwlan0");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * ME860通过dhd.ko库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByS3I747(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod dhd.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /system/lib/modules/dhd.ko \"firmware_path=/system/etc/wifi/bcmdhd_sta.bin nvram_path=/system/etc/wifi/nvram_net.txt\"");
            Thread.sleep(1000*5);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg wlan0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dnl80211 -iwlan0 -c/data/local/wpa.conf&");
                Thread.sleep(8000);
                //UtilsMethod.runRootCommand("netcfg eth0 dhcp");
                UtilsMethod.runRootCommand("dhcpcd wlan0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev wlan0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * ME860通过dhd.ko库绑定指定的WIFI热点
     */
    private static void doConnectTargetWIFIByS3I9305(UmpcEnvModel umpcEnvModel){
        try {
            UtilsMethod.runRootCommand("rmmod dhd.ko");
            //UtilsMethod.runRootCommand("pkill wpa_supplicant");
            UtilsMethod.killProcessByPname("wpa_supplicant", true);
            Thread.sleep(1000);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp");
            
            UtilsMethod.runRootCommand("insmod /lib/modules/dhd.ko \"firmware_path=/system/etc/wifi/bcmdhd_sta.bin nvram_path=/system/etc/wifi/nvram_net.txt\"");
            Thread.sleep(1000*5);
            UtilsMethod.runRootCommand("chmod 777 /data/local/tmp/*");
            if(umpcEnvModel.getWifiCiphermode() == 0){  //OPEN 无密码方式
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 mode Managed");
                UtilsMethod.runRootCommand("/data/local/iwconfig wlan0 essid "+umpcEnvModel.getWifiName());
                UtilsMethod.runRootCommand("netcfg wlan0 dhcp");
            }else{
                UtilsMethod.runRootCommand("start wlan_loader");
                //new RunCommandThread("wpa_supplicant -Dwext -ieth0 -c/data/local/wpa.conf").start();
                UtilsMethod.runRootCommand("wpa_supplicant -Dnl80211 -iwlan0 -c/data/local/wpa.conf&");
                Thread.sleep(8000);
                //UtilsMethod.runRootCommand("netcfg eth0 dhcp");
                UtilsMethod.runRootCommand("dhcpcd wlan0");
            }
            //Thread.sleep(1000);
            //UtilsMethod.runRootCommand("route add default gw 192.168.0.1 dev wlan0");
            //Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
