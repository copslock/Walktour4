package com.dinglicom.ipack;

import com.dinglicom.DataSetLib;
import com.dinglicom.data.model.RecordDetail;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.base.util.LogUtil;
import com.walktour.model.UmpcTestInfo;
import com.walktour.service.iPackTerminal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * 连小背包测试过程中文件实时上传工具类
 * 
 * @author Tangwq
 *
 */
public class FileRealTimeUpload {

	private final String TAG = "FileRealTimeUpload";
	private boolean uploadState = false;	//当前是否正在上传中
	private HashMap<String, Boolean> uploadEnd = new HashMap<String, Boolean>();

	private UmpcTestInfo _umpcInfo = null;
	private HashMap<String,RecordDetail> detailList = null;

	/**
	 * 开始执行实时文件上传动作
	 */
	public void startRTFileUpload(UmpcTestInfo umpcInfo,HashMap<String,RecordDetail> details){
		uploadState = true;
		detailList = details;
		_umpcInfo = umpcInfo;

		for(String upType : umpcInfo.getSyncfile()){
			if(detailList.containsKey(upType)){
				if(detailList.get(upType).file_name.endsWith("ddib"))
					continue;
				new Thread(new doRTFileUplad(detailList.get(upType))).start();
			}
		}
	}
	
	/**
	 * 当收到IPACK端重新登陆成功的消息后，如果有实时上传流程
	 * 将指定上传类型的文件重调上传
	 */
	public void reStartRTFileUpload(){
		for(String upType : _umpcInfo.getSyncfile()){
			if(detailList.containsKey(upType)){
				if(detailList.get(upType).file_name.endsWith("ddib"))
					continue;
				RecordDetail detail = detailList.get(upType);
				sendRTFile(detail,new File( detail.file_path + detail.file_name));
			}
		}
	}
	
	
	/**
	 * 停止上传实时文件调用
	 */
	public void stopRTFileUplad(){
		uploadState = false;
	}
	
	public boolean isFileEnd(){
		Iterator<Entry<String, Boolean>> upEnd = uploadEnd.entrySet().iterator();
		while(upEnd.hasNext()){
			Entry<String, Boolean> bb = upEnd.next();
			if(bb.getValue() == Boolean.FALSE&&(!bb.getKey().endsWith("ddib"))){
				return false;
			}
		}
		return true;
	}

    class doRTFileUplad implements Runnable{
        File uploadFile 	= null;
        boolean fileExist	= false;
        long lastFileSize	= 0;
        RecordDetail model = null;
        public doRTFileUplad(RecordDetail detail){
            uploadFile = new File( detail.file_path + detail.file_name);
            uploadEnd.put(uploadFile.getName(), false);
            this.model = detail;

            LogUtil.w(TAG,"--doRTFileUplad:" + uploadFile.getAbsolutePath());
        }

		@Override
		public void run() {
			LogUtil.w(TAG,"--send rtfile 1start:" + model.file_guid + "--len:" + uploadFile.exists() + "--" + uploadFile.length());
			File filx=null;
			while(uploadState){
				try{
					Thread.sleep(500);
					if(fileExist){
						if(uploadFile.getName().endsWith("dcf")){
							if(DataSetLib.currentFileLength>0&& DataSetLib.currentFileType==DataSetLib.FileType.CPV_FileType_DCF) {
								if (lastFileSize != DataSetLib.currentFileLength) {
									LogUtil.w(TAG, "DataSetLib.currentFileLength1=" + DataSetLib.currentFileLength);
									lastFileSize = DataSetLib.currentFileLength;
									SetRTFileInfo(model, DataSetLib.currentFileLength, 0);
								}
							}
						}else {
							//发送实时文件大小
							if (lastFileSize != uploadFile.length()) {
								LogUtil.w(TAG,"uploadFile length2="+uploadFile.length());
								lastFileSize = uploadFile.length();
								SetRTFileInfo(model, uploadFile, 0);
							}
						}
					}else{
						if(uploadFile.exists()){
							LogUtil.w(TAG,"--File:" + uploadFile.getPath() + "--" + fileExist + "--" + uploadFile.length());
							if(uploadFile.getName().endsWith("dcf")){
								if(DataSetLib.currentFileLength>0&& DataSetLib.currentFileType==DataSetLib.FileType.CPV_FileType_DCF) {
									LogUtil.w(TAG, "DataSetLib.currentFileLength3=" + DataSetLib.currentFileLength);
									//发送开妈上传命令
									sendRTFile(model, uploadFile);
									lastFileSize = DataSetLib.currentFileLength;
									fileExist = true;
								}
							}else {
								LogUtil.w(TAG,"uploadFile length4="+uploadFile.length());
								//发送开妈上传命令
								sendRTFile(model, uploadFile);
								lastFileSize = uploadFile.length();
								fileExist = true;
							}
						}


					}
				}catch(Exception e){
					LogUtil.w(TAG,"doRTFileUplad",e);
				}
			}

			LogUtil.w(TAG,"--send rtfile end" + model.file_guid + "--len:" + uploadFile.exists() + "--" + uploadFile.length());
			if(uploadFile.getName().endsWith("dcf")) {
				if (DataSetLib.currentFileLength > 0 && DataSetLib.currentFileType == DataSetLib.FileType.CPV_FileType_DCF) {
					LogUtil.w(TAG, "DataSetLib.currentFileLength5=" + DataSetLib.currentFileLength);

						SetRTFileInfo(model, DataSetLib.currentFileLength, 1);

				}
			}else {
				LogUtil.w(TAG,"uploadFile length6="+uploadFile.length());
				//发送文件完成命令
				SetRTFileInfo(model, uploadFile, 1);
			}
			uploadEnd.put(uploadFile.getName(), true);
		}
    }

	private byte[] readBytesFromInputStream(InputStream br_right,
												  int length2) throws IOException {
		int readSize;
		byte[] bytes = null;
		bytes = new byte[length2];

		long length_tmp = length2;
		long index = 0;// start from zero
		while ((readSize = br_right.read(bytes, (int) index, (int) length_tmp)) != -1) {
			length_tmp -= readSize;
			if (length_tmp == 0) {
				break;
			}
			index = index + readSize;
		}
		return bytes;
	}

	private void SetRTFileInfo(RecordDetail model,File uploadFile,int endOf){
		LogUtil.w(TAG,"--File:" + uploadFile.getPath() + "--" + uploadFile.length() + "--endOf:" + endOf);
		iPackTerminal.SetRTFileInfo(String.format(IpackControl.getInstance().getSetRTFileInfoFormat(),
				model.file_guid,
				endOf,
				uploadFile.length()));
	}

	private void SetRTFileInfo(RecordDetail model,long fileLength,int endOf){
		iPackTerminal.SetRTFileInfo(String.format(IpackControl.getInstance().getSetRTFileInfoFormat(),
				model.file_guid,
				endOf,
				fileLength));
	}
	/**
	 * 往IPAD端发送文件的接口
	 * @param model
	 * @param uploadFile
	 */
	private void sendRTFile(RecordDetail model,File uploadFile){
		iPackTerminal.SendRTFile(String.format(IpackControl.getInstance().getSendRTFileFormat(), 
				model.file_guid,
				uploadFile.getParent() + "/",
				uploadFile.getName(),
				FileType.getFileType(model.file_type).getIpackTypeId(),
				UmpcTestInfo.eTransMode_ReTrans,
				_umpcInfo.getTestgroupinfo(),
				_umpcInfo.getTestmode(),
				_umpcInfo.getTaskno(),
				_umpcInfo.getAtuPort()
				));
	}
}
