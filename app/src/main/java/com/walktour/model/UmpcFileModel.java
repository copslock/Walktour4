package com.walktour.model;

import com.dinglicom.ipack.IpackControl;
import com.walktour.Utils.WalkStruct.FileType;

/**
 * UMPC测试过程中生成的文件对象相关信息
 * @author Tangwq
 *
 */
public class UmpcFileModel {
	
	private String iFileType ;
	private String fileName;
	private String fileType;
	private String umpcUUID;
	
	public UmpcFileModel(String name,String type,String uuid){
		fileName = name;
		fileType= type;
		umpcUUID= uuid;
	}

	public String getIFileType(){
		if(fileType.equals(FileType.RCU.getFileTypeName())){
//			iFileType = IpackControl.IpackFileType.RCU.getTypeID();
		}else if(fileType.equals(FileType.DTLOG.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.DTLOG.getTypeID();
		}else if(fileType.equals(FileType.DDIB.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.DDIB.getTypeID();
		}else if(fileType.equals(FileType.PCAP.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.PCAP.getTypeID();
		}else if(fileType.equals(FileType.DCF.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.DCF.getTypeID();
		}else if(fileType.equals(FileType.ORGRCU.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.ORGRCU.getTypeID();
		}else if(fileType.equals(FileType.ECTI.getFileTypeName())){
			iFileType = IpackControl.IpackFileType.ECTI.getTypeID();
		}
		return iFileType;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public String getFileType() {
		if(fileType.equals(FileType.DTLOG.getFileTypeName())){
			return FileType.DTLOG.getFileTypeName();
		}
		return fileType;
	}

	public String getUmpcUUID() {
		return umpcUUID;
	}
}
