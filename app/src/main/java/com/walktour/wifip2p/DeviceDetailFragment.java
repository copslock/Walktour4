package com.walktour.wifip2p;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.gui.R;
import com.walktour.wifip2p.DeviceListFragment.DeviceActionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
@SuppressLint("NewApi")
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    ArrayList<DataProcessThread> dataProcessThreadList = new ArrayList<DataProcessThread>();
    DataProcessThread  clientDataProcess=null;
    ClientRecvThread   clientRecvThread=null;
    ServerThread       serverThread=null;
    private Context mContext;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @SuppressLint("InflateParams")
		@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.wifip2p_device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                        );
                ((DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                       // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                       // intent.setType("image/*");
                       // startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    	Log.d("p2p","start connect ," + info.isGroupOwner + "" + info.groupFormed);
                    	if (info.isGroupOwner && info.groupFormed) {
                    		
                    	   if (dataProcessThreadList.size() > 0 ) {
                    		   for (int i=0;i<dataProcessThreadList.size();i++) {
                    			   DataProcessThread dataProcess = dataProcessThreadList.get(i);
                    			   if (device == null) {
                    				   Log.d("p2p"," device is null");
                    				   Toast.makeText(mContext, mContext.getString(R.string.select_peer_first), Toast.LENGTH_LONG).show();
                    				   return;
                    			   }
                    			   if (dataProcess.getDevice() == null ) {
                    				   Log.d("p2p"," getdevice is null");
                    				   return;
                    			   }
                    			   Log.d("p2p","peer device = " + device.deviceName + " dataprocess device = " + dataProcess.getDevice());
                    			   if (dataProcess.getDevice().equals(device.deviceName) && dataProcess.state()) {
                    				    Log.d("p2p"," match begin to send file");
                    				    dataProcess.sendFile(Environment.getExternalStorageDirectory().getPath() + "/Walktour/task/", WiFiDirectActivity.taskFileName);
                    				   break;
                    			   }
                    		   }
                    			
                    	 }else {
                    		 Log.d("p2p","dataProcessThreadList size is 0");
                    	 }
                    	   
                       }else if (info.groupFormed) {
                    	   Log.d("p2p","is client send file to server");
                    	   if (clientRecvThread != null)
                    	       new Thread(new Runnable() {
                                   @Override
                                   public void run() {
                                       clientRecvThread.sendFile(Environment.getExternalStorageDirectory().getPath() + "/Walktour/task/", WiFiDirectActivity.taskFileName);
                                   }
                               }).start();
                       }
                    }
                });

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
        	//启动一个服务进程等待客户端的请求，客户端知道Owner的地址，此时onwer还不知道客户端的地址
            //new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
             //       .execute();
        	if (serverThread != null) {
        		serverThread.stopIt();
        	}
        	serverThread = new ServerThread();
        	serverThread.start();
        	
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
            //是客户端的
        	Log.d("p2p","client begin to  connect .... ");
            if (clientRecvThread != null) {
            	Log.d("p2p","stop old thread firstly ");
            	clientRecvThread.stopIt();
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            clientRecvThread = new ClientRecvThread(info.groupOwnerAddress.getHostAddress(),8988,this.getActivity());
            clientRecvThread.start();
        }
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	//如果已经连接了，则断开连接，下次重新建立连接关系
    	//((DeviceActionListener) getActivity()).disconnect();
    	//this.getView().setVisibility(View.GONE);
    
    	if (serverThread!=null) {
    		serverThread.stopIt();
    	}
    	Log.d("p2p","onDestroyView");
    }
	
    /* client<---->server
     * id+len+data
     * id:100 login,data:device name
     * id:101 send file name,data:file name
     * id:102 send file content, data: file content
     * 
     * server<----->client (after client login)
     * id:101 send file name,data:file name
     * id:102 send file content, data: file content
     * 
     */
    private class ServerThread extends Thread{
    	
    	private boolean needStop=false;
    	private ServerSocket serverSocket=null;
    	public void stopIt() {
    		needStop=true;
    		try{
    		if (serverSocket != null) {
    			serverSocket.close();
    		}
    		}catch(IOException e) {
    			
    		}
    	}
    	@Override
		public void run() {
    		 try {
                 serverSocket = new ServerSocket(8988);
                 Log.d("p2p", "Server: Socket opened");
//                 byte[] buffer = new byte[4096];
                 while(!needStop) {
                   Socket client = serverSocket.accept();
                  
                   Log.d("p2p", "Server: connection done");
                   //dataProcessThreadList.clear();
//                   String peerAddr = client.getInetAddress().getHostAddress();
                   for (int i=0;i<dataProcessThreadList.size();i++) {
                	   DataProcessThread data = dataProcessThreadList.get(i);
                	   data.stopIt();
                   }
                   dataProcessThreadList.clear();
                   DataProcessThread dataProcess = new DataProcessThread(client,mContext);
                   dataProcessThreadList.add(dataProcess);
                   Log.d("p2p", "dataProcessThreadList size = " + dataProcessThreadList.size());
                   dataProcess.start();
                 }
             } catch (IOException e) {
                 Log.e("p2p", e.getMessage());
                 //return null;
             }
    	}
    };
    
  //client thread
//    Thread serverThread = new Thread() {
//    	@Override
//		public void run() {
//    		 try {
//                 ServerSocket serverSocket = new ServerSocket(8988);
//                 Log.d("p2p", "Server: Socket opened");
//                 byte[] buffer = new byte[4096];
//                 while(true) {
//                   Socket client = serverSocket.accept();
//                  
//                   Log.d("p2p", "Server: connection done");
//                   //dataProcessThreadList.clear();
//                   String peerAddr = client.getInetAddress().getHostAddress();
//                   for (int i=0;i<dataProcessThreadList.size();i++) {
//                	   DataProcessThread data = dataProcessThreadList.get(i);
//                	   data.stopIt();
//                   }
//                   dataProcessThreadList.clear();
//                   DataProcessThread dataProcess = new DataProcessThread(client,mContext);
//                   dataProcessThreadList.add(dataProcess);
//                   Log.d("p2p", "dataProcessThreadList size = " + dataProcessThreadList.size());
//                   dataProcess.start();
//                   
//                   
//                   
////                 final File f = new File(Environment.getExternalStorageDirectory() + "/"
////                         + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
////                         + ".jpg");
////
////                 File dirs = new File(f.getParent());
////                 if (!dirs.exists())
////                     dirs.mkdirs();
////                 f.createNewFile();
//
////                 Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
////                 InputStream inputstream = client.getInputStream();
////                 copyFile(inputstream, new FileOutputStream(f));
// //                 serverSocket.close();
//                 }
//                 //return f.getAbsolutePath();
//             } catch (IOException e) {
//                 Log.e("p2p", e.getMessage());
//                 //return null;
//             }
//         }
//    	
//    };
//
    
//    //client thread
//    Thread clientThread = new Thread() {
//    	@Override
//		public void run() {
//    		  Socket socket = new Socket();
//              int port = 8988;
//              int SOCKET_TIMEOUT = 5000;
//              try {
//            	  try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//                  Log.d("p2p", "Opening client socket - ");
//                  socket.bind(null);
//                  
//                  String deviceName = DeviceListFragment.getSelfDevice().deviceName; //local
//                  socket.connect((new InetSocketAddress(info.groupOwnerAddress.getHostAddress(), port)), SOCKET_TIMEOUT);
//                  DataProcessThread dataProcess = new DataProcessThread(socket);
//                  dataProcess.login(deviceName);
//                  dataProcess.start();
//                  clientDataProcess = dataProcess;
//                  Log.d("p2p", "Opening client socket end ");
////                  
////                  
////                  Log.d(WiFiDirectActivity.TAG, "Client socket - " + socket.isConnected());
////                  OutputStream stream = socket.getOutputStream();
////                  stream.write(deviceName.getBytes()); //send device name to group owner
////                  stream.flush();
////                  Log.d(WiFiDirectActivity.TAG, "Client: Data written");
//              } catch (IOException e) {
//                  Log.e("p2p", e.getMessage());
//              } 
////              finally {
////                  if (socket != null) {
////                      if (socket.isConnected()) {
////                          try {
////                              socket.close();
////                          } catch (IOException e) {
////                              // Give up
////                              e.printStackTrace();
////                          }
////                      }
////                  }
////              }
////
//          }

    	
//    };
    
    
    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());
        Log.d("p2p","showDetails,deviename " + device.deviceName);

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}
