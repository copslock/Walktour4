package com.walktour.service.app.datatrans.fleet;
import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.Utils.ZipUtil;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.config.Deviceinfo;
import com.walktour.service.app.DataTransService;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Fleet服务器上传数据带MOS文件的处理
 *
 * @author jianchao.wang
 *
 */
public class FleetDataTransferMOS extends FleetDataTransferBase {

	private String PROTOCOL_YUNNAN="YunNanPolqa";
	FleetDataTransferMOS(DataTransService service) {
		super("FleetDataTransferMOS", service);
	}

	@Override
	protected String getTag() {
		StringBuilder sb = new StringBuilder();
//		sb.append("{\"Protocol\":\"ThailandMOS\",");
		String protocol="";
		if(Deviceinfo.getInstance().getDevicemodel().equals("ZTENX569J")){
			protocol=PROTOCOL_YUNNAN;
		}else{
			protocol="Dingli";
		}
		sb.append("{\"Protocol\":\""+ protocol+"\",");
		sb.append("\"FileType\":");
		switch (super.mCurrentFileType) {
			case ORGRCU:
			case ECTI:
			case DCF:
				sb.append("\"DATA\",");
				break;
			case PCAP:
				sb.append("\"PCap\",");
				break;
			case DTLOG:
				sb.append("\"DTLog\",");
				break;
			case MOSZIP:
				sb.append("\"MOSZIP\",");
				break;
			case MIXZIP:
				sb.append("\"MIXZIP\",");
				break;
			default:
				return "";
		}
		sb.append("\"SourceDataType\":");
		if (super.mCurrentFile.getTestTypeId() == TestType.DT.getTestTypeId()) {
			sb.append("\"DT\"");
		}else {
			sb.append("\"ICQT\"");
		}
		if(super.mCurrentFileType.equals(FileType.ECTI)){
			sb.append("}");
		}else {
			sb.append(",");
			String fileName = super.formatFileName() + FileType.ORGRCU.getExtendName();
			if (super.mCurrentFileType == FileType.MIXZIP || super.mCurrentFileType == FileType.MOSZIP) {
				sb.append("\"SourceFileName\":\"").append(fileName).append("\",");
				if (super.mCurrentFileType == FileType.MIXZIP) {
					sb.append("\"DataNextStep\":\"UnzipMIX\"}");
				} else if (super.mCurrentFileType == FileType.MOSZIP) {
					sb.append("\"DataNextStep\":\"UnzipMOS\"}");
				}
			} else {
				sb.append("\"DataNextStep\":\"SRCLnk,MosAlgo\"}");
			}
		}
		LogUtil.d(TAG,"TAG="+sb.toString());
		return sb.toString();
	}

	@Override
	protected void initCurrentFileTypes() {
		LogUtil.d(TAG, "----initCurrentFileTypes:" + super.mCurrentFile.getFileTypes().length + "----");
		for (FileType fileType : super.mCurrentFile.getFileTypes()) {
			LogUtil.d(TAG,"----fileType:" + fileType.getFileTypeName() + "---------");
		}
		super.mCurrentFile.setLastSuccess(true);
		if (super.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<>();
			fileTypes.add(FileType.MOSZIP);
			fileTypes.add(FileType.ORGRCU);
			super.mCurrentFile.setFileTypes(fileTypes);
		} else {
			if (this.mCurrentFile.hasFileTypes(FileType.MOSZIP)) {
				if (this.mCurrentFile.hasExtraParam("MOSFilesPath")) {
					String mosFilesPath = this.mCurrentFile.getStringExtraParam("MOSFilesPath");
					LogUtil.i(TAG,"----hasExtraParam  MOSFilesPath" + mosFilesPath + "---------");
					this.zipMOSFiles(mosFilesPath);
				} else if(this.mCurrentFile.getFile(FileType.MOSZIP) == null){
					//对于崩溃生成的数据文件关联的MOS，在Walktour/voice/下遍历所有日期目录，寻找相关的文件名目录
					String mos = AppFilePathUtil.getInstance().getSDCardBaseDirectory("voice");
					File parentFile = new File(mos);
					LogUtil.i(TAG,"------parentFile:" + parentFile + "---------");
					if(parentFile.isDirectory()){
						for(File dateFile:parentFile.listFiles()){
							LogUtil.i(TAG,"------dateFile:" + dateFile + "---------");
							for(File mosFile:dateFile.listFiles()){
								LogUtil.i(TAG,"------mosFile:" + mosFile + "---------");
								if(this.mCurrentFile.getName().startsWith(mosFile.getName())){
									if (zipMOSFiles(mosFile.getAbsolutePath()))
										return;
								}
							}
						}
					}
					this.mCurrentFile.setLastSuccess(false);
				}
			}

			if (this.mCurrentFile.hasFileTypes(FileType.MIXZIP)) {//包含标注文件
				if(this.mCurrentFile.getFile(FileType.MIXZIP) == null){//标注不存在,则打包生成
					String mix = AppFilePathUtil.getInstance().getSDCardBaseDirectory("tag",this.mCurrentFile.getName());
					File parentFile = new File(mix);
					LogUtil.i(TAG,"------parentFile:" + parentFile + "---------");
					if(parentFile.exists()&&parentFile.isDirectory()){
					    //可以直接压缩
                        if (zipMIXFiles(parentFile.getAbsolutePath())) {
                            return;
                        }
//						for(File dateFile:parentFile.listFiles()){
//							LogUtil.i(TAG,"------dateFile:" + dateFile + "---------");
//							for(File mosFile:dateFile.listFiles()){//压缩里面的所有文件
//								LogUtil.i(TAG,"------mosFile:" + mosFile + "---------");
//								if (zipMIXFiles(mosFile.getAbsolutePath()))
//									return;
//							}
//						}
					}
					this.mCurrentFile.setLastSuccess(false);
				}
			}
		}
	}

	/**
	 * 如果当前要上传MOS文件，则判断指定文件目录下是否有相关的压缩文件，如果没有则进行压缩
	 */
	private boolean zipMOSFiles(String filePath) {
		LogUtil.d(TAG, "------zipMOSFiles------filePath:" + filePath);
		File mosPath = new File(filePath);
		if (!mosPath.exists()){
			LogUtil.d(TAG,"-------mosPath not exists----------");
			return false;
		}
		File zipFile = new File(this.mCurrentFile.getParentPath() + File.separator + this.mCurrentFile.getName(FileType.MOSZIP));
		Set<File> files = new HashSet<>();
		if (mosPath.isDirectory()) {
			for (File file : mosPath.listFiles()) {
				if (file.getName().endsWith(".wav")) {
					LogUtil.d(TAG,"-----wav files add:" + file.getName() + "---------");
					files.add(file);
				}
			}
		}
		LogUtil.d(TAG,"-----wav files size:" + files.size() + "---------");
		if (!files.isEmpty()) {
			try {
				ZipUtil.zip(files, zipFile, "");
				this.deleteDir(mosPath);
				this.mService.saveDBFilePath(this.mCurrentFile, FileType.MOSZIP, zipFile.getParentFile().getAbsolutePath(), zipFile.getName());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

    /**
     * 如果当前要上传标注文件，则判断指定文件目录下是否有相关的压缩文件，如果没有则进行压缩
     */
    private boolean zipMIXFiles(String filePath) {
        LogUtil.d(TAG, "------zipMIXFiles------filePath:" + filePath);
        File mosPath = new File(filePath);
        if (!mosPath.exists()){
            LogUtil.d(TAG,"-------mixPath not exists----------");
            return false;
        }
        //zip直接在tag目录下
        File zipFile = new File(AppFilePathUtil.getInstance().getSDCardBaseDirectory("tag") + this.mCurrentFile.getName(FileType.MIXZIP));
        Set<File> files = new HashSet<>();
        if (mosPath.isDirectory()) {
            for (File file : mosPath.listFiles()) {
//                if (file.getName().endsWith(".wav")) {
                    LogUtil.d(TAG,"-----wav files add:" + file.getName() + "---------");
                    files.add(file);
//                }
            }
        }
        LogUtil.d(TAG,"-----mix files size:" + files.size() + "---------");
        if (!files.isEmpty()) {
            try {
                ZipUtil.zip(files, zipFile, "");
                this.deleteDir(mosPath);
//                this.mService.saveDBFilePath(this.mCurrentFile, FileType.MIXZIP, zipFile.getParentFile().getAbsolutePath(), zipFile.getName());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 *
	 * @param dir
	 *          将要删除的文件目录
	 * @return 是否删除
	 */
	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			File[] Files = dir.listFiles();
			for (File file : Files) {
				boolean success = deleteDir(file);
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

}
