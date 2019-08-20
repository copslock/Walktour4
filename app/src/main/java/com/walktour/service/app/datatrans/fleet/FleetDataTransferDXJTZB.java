package com.walktour.service.app.datatrans.fleet;

import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.service.app.DataTransService;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Fleet服务器上传数据2015电信招标专用
 * 
 * @author jianchao.wang
 *
 */
public class FleetDataTransferDXJTZB extends FleetDataTransferBase {

	FleetDataTransferDXJTZB(DataTransService service) {
		super("FleetDataTransferDXJTZB", service);
	}

	@Override
	protected String getTag() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"Protocol\":\"DXJTZB\",");
		if (super.mCurrentFileType == FileType.ORGRCU)
			sb.append("\"FileType\":\"ORGRCU\",");
		else
			sb.append("\"FileType\":\"Photo\",");
		if (super.mCurrentFile.getTestTypeId() == TestType.DT.getTestTypeId())
			sb.append("\"SourceDataType\":\"DT\",");
		else
			sb.append("\"SourceDataType\":\"ICQT\",");
		if (super.mCurrentFile.hasExtraParam("Tester")) {
			sb.append("\"Tester\":\"").append(super.mCurrentFile.getStringExtraParam("Tester")).append("\",");
		} else {
			sb.append("\"Tester\":\"").append("").append("\",");
		}
		if (super.mCurrentFileType == FileType.ORGRCU) {
			if (super.mCurrentFile.hasExtraParam("BgPicID")) {
				sb.append("\"BgPicID\":\"").append(super.mCurrentFile.getStringExtraParam("BgPicID")).append("\",");
			} else
				sb.append("\"BgPicID\":\"").append("").append("\",");
		}
		sb.append("\"Info\":\"TestInfo\"}");
		return sb.toString();
	}

	@Override
	protected void initCurrentFileTypes() {
		Set<FileType> fileTypes = new HashSet<>();
		fileTypes.add(FileType.ORGRCU);
		fileTypes.add(FileType.FloorPlan);
		super.mCurrentFile.setFileTypes(fileTypes);
		if (!super.mCurrentFile.hasExtraParam("BgPicID")) {
			File parent = new File(super.mCurrentFile.getParentPath());
			for (File file : parent.listFiles()) {
				if (file.isFile()) {
					String fileName = file.getName().toLowerCase(Locale.getDefault());
					if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".bmp")
							|| fileName.endsWith(".jpeg")) {
						super.mCurrentFile.addExtraParam("BgPicID", file.getName());
						return;
					}
				}
			}
		}
	}

	@Override
	protected UploadRcuParams createParams() {
		UploadRcuParams params = super.createParams();
		if (super.mCurrentFileType == FileType.FloorPlan)
			params.serverFileName = super.mCurrentFile.getStringExtraParam("BgPicID");
		return params;
	}

}
