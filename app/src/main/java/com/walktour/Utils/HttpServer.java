package com.walktour.Utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

import com.dinglicom.dataset.DatasetManager;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.FileOperater;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.config.ConfigRoutine;
import com.walktour.model.APNModel;
import com.walktour.service.OtsSocketUploadService;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
  
  
/** 
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server. 
 * <p> 
 * Please note the purpose of this application is demonstrate the usage of 
 * HttpCore APIs. It is NOT intended to demonstrate the most efficient way of 
 * building an HTTP file server. 
 */  
public class HttpServer {
	public static final int HttpServerPort = 8088;
	private final static String tag = "HttpServer";
	private  boolean httpServerRunnig = false;
	private  boolean httpServerRelease= false;
	public  boolean isCellCheck      = false;
	private static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private  DatasetManager setManager = null;
	private  SharedPreferences preferences;
	static ArrayList<String> headMsgList = new ArrayList<String>();
	static ArrayList<Integer> configList = new ArrayList<Integer>();
	static ArrayList<String> customSavePath = new ArrayList<String>();
	private  Thread requestThread = null;
	private   MyPhoneState myPhoneState = null;
	public  UpLoadStrI upLoadStrI;
	
	private static HttpServer httpServer = null;
	private Context context;
	private ConfigInfo configInfo;
	private static boolean isFristTimes = false;
	
	public static final String OtsFileCreate = "OtsFileCreate";
	public static final String OtsStopFile = "OtsStopFile";
	public static final String OtsFileExport = "OtsFileExport";
	
	
	public ConfigInfo getConfigInfoIn() {
		return new ConfigInfo();
	} 
	
	
	public ConfigInfo getConfigInfo() {
		return configInfo;
	}


	public static HttpServer getInstance(Context context) {
		if (httpServer == null) {
			isFristTimes = true;
			httpServer = new HttpServer(context);
		}
		return httpServer;
	}
	
	
	public HttpServer(Context context) {
		this.context = context;
	}
	
	
	/**
	 * 配置信息内部类
	 */
	public class ConfigInfo{
		public  int PARM_PORT = 6666;
		public  int L3_PORT   = 9999;
		public  int CELL_PORT = 7878;
		public  int inerval   = 1000;
		
		public ConfigInfo() {
			
		}
		
		public int getPARM_PORT() {
			return PARM_PORT;
		}
		public void setPARM_PORT(int pARM_PORT) {
			PARM_PORT = pARM_PORT;
		}
		public int getL3_PORT() {
			return L3_PORT;
		}
		public void setL3_PORT(int l3_PORT) {
			L3_PORT = l3_PORT;
		}
		public int getCELL_PORT() {
			return CELL_PORT;
		}
		public void setCELL_PORT(int cELL_PORT) {
			CELL_PORT = cELL_PORT;
		}
		public int getInerval() {
			return inerval;
		}
		public void setInerval(int inerval) {
			this.inerval = inerval;
		}
		
	}
	
	 
	
	/**
	 * 启动HTTP Server
	 * 监听指定端口	8080
	 * @param port
	 * @throws Exception
	 */
	public void startHttpServer(int port){
		try{
			Log.w(tag,"--runHttpServer:" + httpServerRunnig + "--Por:" + port);
			if(!httpServerRunnig){
				httpServerRunnig = true;
				if(!httpServerRelease){
					httpServerRelease = true;
					requestThread = new RequestListenerThread(port);
					requestThread.setDaemon(false);
					requestThread.start(); // start the webservice server
				}
				
			}
			setManager =  DatasetManager.getInstance(context);
			myPhoneState = MyPhoneState.getInstance();
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
  
	/**
	 * 停止HTTP服务
	 */
	public  void stopHttpServer(){
		if(httpServerRunnig){
			Log.w(tag,"--stopHttpServer--");
			httpServerRunnig = false;
			requestThread.interrupt();
		}
	}
	
	private  String filePath = "";
	private  String fileName = "";
	 class WebServiceHandler implements HttpRequestHandler {
		public WebServiceHandler() {
			super();
		}

		public void handle(final HttpRequest request,
				final HttpResponse response, final HttpContext _context)
				throws HttpException, IOException {

			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			String target = request.getRequestLine().getUri();
			Log.w(tag,"--httprequest:" + target);
			
			response.setStatusCode(HttpStatus.SC_OK);
			//if (method.equals("GET")) {
				if(target.indexOf("taskstatus") >= 0){
					//response.setStatusCode(HttpStatus.SC_OK);
					
					StringEntity entity = new StringEntity("<p name=\"code\">200</p><p name=\"logFile\">" + filePath + fileName + "</p>");
					response.setEntity(entity);
				}else if(target.indexOf("params") >= 0 ){
					//response.setStatusCode(HttpStatus.SC_OK);
					
					StringEntity entity = new StringEntity(UtilsWalktour.getParamsInfo(context));
					response.setEntity(entity);
				}else if(target.indexOf("device") >= 0){
					//response.setStatusCode(HttpStatus.SC_OK);
					
					StringEntity entity = new StringEntity(UtilsWalktour.getDeviceInfo(context));
					response.setEntity(entity);
				}else if (target.indexOf("Configtask") >= 0){
					if (method.equals("POST")){
						HttpEntity entry = ((BasicHttpEntityEnclosingRequest)request).getEntity();
						InputStream inputStream = entry.getContent(); 
						BufferedReader reader =new BufferedReader( new InputStreamReader(inputStream));
						String line = "" ;
						String parm = "";
						while ((line = reader.readLine()) != null) {
							Log.i("portInfo", line);
							parm += line;
						}
						parserPortInfo(parm);
					}
				}
			//} else if (method.equals("POST")) {
				else if(target.indexOf("starttask") >= 0){
					//路径从Walktour设置中获取
					if (method.equals("POST")){
						HttpEntity entry = ((BasicHttpEntityEnclosingRequest)request).getEntity();
						InputStream inputStream = entry.getContent(); 
						BufferedReader reader =new BufferedReader( new InputStreamReader(inputStream));
			            
						String param = "";
						String line = "";
			            while ((line = reader.readLine()) != null) { 
			            	param += line;
			            }
			            filePath = getFilePath(param);
					}
					
					//当前不在测试状态且初始化成功时
					if(!ApplicationModel.getInstance().isTestJobIsRun()
							&& ApplicationModel.getInstance().isTraceInitSucc()){
						ApplicationModel.getInstance().setTestJobIsRun(true);
						AlertWakeLock.acquire(context);
						fileName = sdFormat.format(System.currentTimeMillis());
						//setManager.createFile(filePath + fileName);
//						new Thread(new OperateFile(true,ConfigRoutine.getInstance().getStorgePathTask(context) + fileName + ".rcu" ,null)).start();
						OperateFile(true,ConfigRoutine.getInstance().getStorgePathTask() + fileName + ".rcu" ,null);
						context.startService(new Intent(context, OtsSocketUploadService.class));
						setResponse(response,HttpStatus.SC_OK,fileName);
					}else{
						setResponse(response,HttpStatus.SC_NO_CONTENT,"0");
					}
				}else if(target.indexOf("stoptask") >= 0){
					Log.i(tag, "ots stop task");
					if(ApplicationModel.getInstance().isTestJobIsRun()){
						ApplicationModel.getInstance().setTestJobIsRun(false);
						AlertWakeLock.release();
						//setManager.closeFile();
						OperateFile(false,fileName,response);
//						new Thread(new OperateFile(false,"",response)).start();
					}else{
						setResponse(response,HttpStatus.SC_NO_CONTENT,"0");
					}
				}else if(target.indexOf("StartCellCheck") >= 0){
					setResponse(response,HttpStatus.SC_OK,fileName);
				}else if(target.indexOf("StopCellCheck") >= 0){
					String filePath = Environment.getExternalStorageDirectory() + "/Walktour/data/task/" + fileName + "_Port2.otsparam";
					if(new File(filePath).exists()){
						setResponse(response,HttpStatus.SC_OK,fileName);
					}else{
						setResponse(response,HttpStatus.SC_NO_CONTENT,"0");
					}
				}
				
			/*} else {
				throw new MethodNotSupportedException(method
						+ " method not supported");
			}*/
		}
	}
	
	public  String readFileStr(String fileNamePath){
		Log.i(tag,"___________" + fileNamePath);
		String cellStr = "";
		try {
			File file =  new File(fileNamePath);
			if(file.exists()){
				FileInputStream fileOutputStream = new FileInputStream(file);
				byte[] b = new byte[fileOutputStream.available()];
				fileOutputStream.read(b);//将文件中的内容读取到字节数组中
				fileOutputStream.close();
				cellStr = new String(b);//再将字节数组中的内容转化成字符串形式
			}
			return cellStr;
		} catch (Exception e) {
			e.printStackTrace();
			return cellStr;
		}
	}
	
	
	
	
	 private  void OperateFile(boolean isCreate,final String fileName,final HttpResponse response){
			if(isCreate){
				isCellCheck = true;
				Intent intent = new Intent();
				intent.setAction(OtsFileCreate);
				intent.putExtra("FileName", fileName);
				context.sendBroadcast(intent);
				
			}else{
				new Thread(new Runnable() {
                    @Override
                    public void run() {
                    	HttpServer.this.upLoadStrI.stopRealtimeParm();
                    	try {
                    		Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
                    	Intent intent = new Intent();
                    	intent.setAction(OtsStopFile);
                    	context.sendBroadcast(intent);
                    	closeFileFlow(fileName, response);
                    }
                }).start();
			}
	}

	 /**
	  * 关闭文件，导出excel文档，导出小区核查流程
	  * @param fileName
	  * @param response
	  */
	private void closeFileFlow(String fileName, HttpResponse response) {
		String ddibFile = DatasetManager.getInstance(context).getDecodedIndexFileName(DatasetManager.PORT_2);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		headMsgList.clear();
		configList.clear();
		customSavePath.clear();
//		setManager.closeFile();
		isCellCheck = false;
		if(HttpServer.this.upLoadStrI != null){
			String fileStr = readFileStr(checkCellFile(fileName));
			HttpServer.this.upLoadStrI.upLoad(fileStr);
		}
		
		/**
		  * 增加自定义导出
		  */
		//String timeFormat = UtilsMethod.sdfhmsss.format(new Date());
		try {
			
			String savePath = filePath != null && !filePath.equals("") ? filePath 
					: preferences.getString(WalktourConst.SYS_SETTING_OTS_DATASTORAGE, AppFilePathUtil.getInstance().getSDCardBaseDirectory());  //获取配置路径
			int division_size = preferences.getInt(WalktourConst.SYS_SETTING_OTS_DIVISION_SIZE, 512);
			boolean division_enable = preferences.getBoolean(WalktourConst.SYS_SETTING_OTS_DIVISION_ENABLE, false);
			String signalDatePath = savePath + UtilsMethod.stringToDate(fileName, "yyyy-MM-dd") + File.separator + "Signal_" + myPhoneState.getIMEI(context) + "_" + fileName + ".csv";   //导出信令文件名
			String ParaDatePath = savePath + UtilsMethod.stringToDate(fileName, "yyyy-MM-dd") + File.separator + "Para_" + myPhoneState.getIMEI(context) + "_" + fileName +".csv";	   //导出参数文件名	 
			UtilsMethod.creatFile(signalDatePath);
			StringBuffer headMsg = new StringBuffer();
			headMsg.append("DEV.NAME"			+","	+ android.os.Build.MODEL 	+ "\n");
			headMsg.append("DEV.IMSI"			+","	+ myPhoneState.getIMSI(context) + "\n");
			headMsg.append("DEV.IMEI"			+","	+ myPhoneState.getIMEI(context) + "\n");
			headMsg.append("DEV.MAC"			+"," 	+ myPhoneState.getLocalMacAddress(context) + "\n");
			headMsg.append("DEV.AT_PORT" 		+","	+ "COM2" + "\n");
			headMsg.append("DEV.VERSION"		+","	+ myPhoneState.getAndroidVersion() + "\n");
			headMsg.append("DEV.MTMSI"			+","	+ "0" + "\n");
			APNModel apn = APNOperate.getInstance(context).getCurrentApn();
			if(apn != null)
				headMsg.append("DEV.APN"			+","  	+ apn.getName() + "\n");
			else
				headMsg.append("DEV.APN"			+",CMNET\n");
			headMsg.append("DEV.IPV4"			+"," 	+ myPhoneState.getLocalIpv4Address() + "\n");
			headMsg.append("DEV.IPV6"			+"," 	+ myPhoneState.getLocalIpv4Address() + "\n");
			headMsg.append("FIRMWARE.VERSION"	+","	+ myPhoneState.getBaseBandVersion()+ "\n");
			headMsg.append("VENDOR.ID"		+"," 	+ "0x01" + "\n\n");
//			headMsg.append("VENDOR.VER"			+"," 	+ UtilsMethod.getCurrentVersionName(context) + "\n\n");
			headMsgList.add("");  //信令没有表头
			headMsgList.add(headMsg.toString()); //参数有表头
			configList.add(0x01);   //导信令
			configList.add(0x7c);	//导参数
			customSavePath.add(signalDatePath);
			customSavePath.add(ParaDatePath);
			
			LogUtil.w(tag,"Ots--p:" + signalDatePath
					+ "--s:" + ParaDatePath
					+ "--f:" + ddibFile
					);
			LogUtil.i(tag, "--Ots export!");
			FileOperater operater = new FileOperater();
			String filePathName = Environment.getExternalStorageDirectory() + "/Walktour/data/task/" + new File(ddibFile).getName();
			operater.move(ddibFile, filePathName);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.setAction(OtsFileExport);
			intent.putExtra("FileName", filePathName);
			intent.putIntegerArrayListExtra("configList", configList);
			intent.putStringArrayListExtra("customSavePath", customSavePath);
			intent.putStringArrayListExtra("headMsgList", headMsgList);
			intent.putExtra("division_size", division_size);
			intent.putExtra("division_enable", division_enable);
			context.sendBroadcast(intent);
//			setManager.openPlayback(DatasetManager.PORT_4, filePathName);
//			setManager.customExportFile(
//					customSavePath,headMsgList,division_enable == true ? (division_size * 1000) : Integer.MAX_VALUE ,configList,0,setManager.getTotalPointCount(DatasetManager.PORT_4),null,0);   //指定分割大小，从配置文件获取
//			setManager.closePlayback(DatasetManager.PORT_4);
//					setManager.startDataSet(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpServer.this.upLoadStrI.stopUpload();
		context.stopService(new Intent(context, OtsSocketUploadService.class));
		
		try {
			setResponse(response,HttpStatus.SC_OK,fileName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
	
	
	private String checkCellFile(String fileName){
		String fileCell1 = Environment.getExternalStorageDirectory() + "/Walktour/data/task/" + fileName + "_Port2.otsparam";
		String fileCell2 = Environment.getExternalStorageDirectory() + "/Walktour/data/task/" + fileName + ".otsparam";
		if(new File(fileCell1).exists()){
			return fileCell1;
		}
		return fileCell2;
	}
	
	
	
	private  void setResponse(HttpResponse response,int httpStatus,String name) throws UnsupportedEncodingException{
		StringEntity entity = new StringEntity
				("<p name=\"code\">" + httpStatus + "</p><p name=\"id\">" + name +"</p>");
		response.setEntity(entity);
	}
	
	private  String getFilePath(String pathStr){
		String filePath = "";
		try{
			int start = pathStr.indexOf("<p name=\"ServiceTestDir\">",0);
			int end = pathStr.indexOf("</p>",start);
			if(start > 0 && end > start){
				filePath = pathStr.substring(start + 25,end);
				filePath = filePath + (filePath.endsWith("/") ? "" : "/");
				
				File file = new File(filePath);
				if(!file.exists()){
					Log.w(tag,"--file path not exists--");
					file.mkdirs();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		LogUtil.w(tag, "--httpPostPath:" + pathStr + "--end:" + filePath);
		return filePath;
	}
	
	
	/**
	 * 获取参数socket端口
	 * @param xml
	 * @return
	 */
	
	public  void parserPortInfo(String xml) {

		ByteArrayInputStream tInputStringStream = null;
		try {
			if (xml != null && !xml.trim().equals("")) {
				tInputStringStream = new ByteArrayInputStream(xml.getBytes());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(tInputStringStream, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("p")) {
						configInfo = new ConfigInfo();
						String attributeName = parser.getAttributeValue(null, "name");
						if (attributeName.equalsIgnoreCase("PARAM SOCKET PORT")) {
							configInfo.setPARM_PORT(Integer.parseInt(parser.nextText()));
						}
						if (attributeName.equalsIgnoreCase("L3 SOCKET PORT")) {
							configInfo.setL3_PORT(Integer.parseInt(parser.nextText()));
						}
						if (attributeName.equalsIgnoreCase("CellCheck SOCKET PORT")) {
							configInfo.setCELL_PORT(Integer.parseInt(parser.nextText()));
						}
						if (attributeName.equalsIgnoreCase("SOCKET INERVAL")) {
							configInfo.setInerval((int)(Double.parseDouble(parser.nextText())*1000));
						}
					}
					break;
				}
				eventType = parser.next();
			}
			tInputStringStream.close();

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
  
     class RequestListenerThread extends Thread {  
        private final ServerSocket serversocket;  
        private final HttpParams params;  
        private final HttpService httpService;  
  
        public RequestListenerThread(int port) throws IOException {  
            //    
            this.serversocket = new ServerSocket(port);  
  
            // Set up the HTTP protocol processor   
            HttpProcessor httpproc = new ImmutableHttpProcessor(  
                    new HttpResponseInterceptor[] {  
                            new ResponseDate(), new ResponseServer(),  
                            new ResponseContent(), new ResponseConnControl() });  
  
            this.params = new BasicHttpParams();  
            this.params  
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)  
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1000)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)  
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)  
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER,"HttpComponents/1.1");  
  
            // Set up request handlers   
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();  
            reqistry.register("*", new WebServiceHandler());  	//WebServiceHandler用来处理webservice请求。   
            //reqistry.register("*starttask", new WebServiceHandler());  	//WebServiceHandler用来处理webservice请求。   
            //reqistry.register("*taskstatus", new WebServiceHandler());  //WebServiceHandler用来处理webservice请求。   
            //reqistry.register("*stoptask", new WebServiceHandler());  	//WebServiceHandler用来处理webservice请求。   
            
            this.httpService = new HttpService(httpproc,  
                    new DefaultConnectionReuseStrategy(),  
                    new DefaultHttpResponseFactory());  
            httpService.setParams(this.params);  
            httpService.setHandlerResolver(reqistry);       //为http服务设置注册好的请求处理器。   
        }  
  
        @Override  
        public void run() {  
            while (!Thread.interrupted() && httpServerRunnig) {
            	Log.w(tag,"--RequestListenerThread run--");
                try {
                    // Set up HTTP connection   
                    Socket socket = this.serversocket.accept();  
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();  
                    conn.bind(socket, this.params);  
  
                    //getAndResposeSocket(socket);
                    
                    // Start worker thread   
                    Thread t = new WorkerThread(this.httpService, conn);  
                    t.setDaemon(true);  
                    t.start();  
                } catch (InterruptedIOException ex) {
                	Log.w(tag,"",ex);
                    break;  
                } catch (IOException e) {  
                    Log.w(tag,"",e);
                    break;  
                }  
            }  
            
            Log.w(tag,"--RequestListenerThread Finish--");
            try {
				serversocket.close();
				httpServerRelease = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            LogUtil.w(tag, "--Quit Request--");
        }  
    }  
  
     class WorkerThread extends Thread {  
        private final HttpService httpservice;  
        private final HttpServerConnection conn;  
  
        public WorkerThread(final HttpService httpservice,  
                final HttpServerConnection conn) {  
            super();  
            this.httpservice = httpservice;  
            this.conn = conn;  
        }  

        @Override  
        public void run() {  
            HttpContext context = new BasicHttpContext(null);  
            try {
                while (!Thread.interrupted() && this.conn.isOpen() && httpServerRunnig) {  
                    this.httpservice.handleRequest(this.conn, context);  
                }  
            } catch (ConnectionClosedException ex) {  
            	Log.w(tag,"--ConnectionClosedException--",ex);
            } catch (IOException ex) {  
            	Log.w(tag,"--IOException--",ex);
            } catch (HttpException ex) {  
            	Log.w(tag,"--HttpException--",ex);
            } finally {  
                try {  
                    this.conn.shutdown();  
                } catch (IOException ignore) {
                	Log.w(tag,"--IOException--",ignore);
                }  
            }  
        }
        
    }
    
    
     public void setUpLoadStrI(UpLoadStrI upLoadStrI){
    	 this.upLoadStrI = upLoadStrI;
     }
     
     
    /**
     * 接口回调方式
     */
	public interface UpLoadStrI{
    	 void upLoad(String cellStr);
    	 void stopRealtimeParm();
    	 void stopUpload();
    }
    
    
    
    private static void getAndResposeSocket(Socket socket) throws IOException{
    	int contentLength = 0;
    	BufferedReader reader =new BufferedReader( new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine(); 
        while (line != null) { 
            System.out.println(line); 
            line = reader.readLine(); 
            if ("".equals(line)) { 
                break; 
            } else if (line.indexOf("Content-Length") != -1) { 
                contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16)); 
            } 
        }
        //读BODY1可用  继续读取普通post（没有附件）提交的数据 
        System.out.println("begin read posted data......"); 
        char[] buf = null; 
        if (contentLength != 0) { 
            buf = new char[contentLength]; 
            reader.read(buf, 0, contentLength); 
            System.out.println("The data user posted: " + new String(buf)); 
        }
        
        //读BODY2可用
        /*byte[] buf = {}; 
        if (contentLength != 0) { 
            buf = new byte[contentLength]; 
            int size = 0; 
              
            while(size<contentLength){ 
                int c = reader.read(); 
                buf[size++] = (byte)c; 
                  
            } 
            System.out.println("The data user posted: " + new String(buf, 0, size)); 
        }*/
        
        /*System.out.println("current user close the session.");
        //直接out返回未试成功
        OutputStream out = socket.getOutputStream();
        String body = "<p name=\"code\">200</p><p name=\"id\">1011111111111</p>";
        out.write(body.getBytes());
        out.flush();*/
    }
    
    
    
}  