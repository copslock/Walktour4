package com.walktour.service.app.datatrans.fleet;

import com.walktour.Utils.WalkStruct.FileType;
import com.walktour.Utils.WalkStruct.TestType;
import com.walktour.service.app.DataTransService;

import java.util.HashSet;
import java.util.Set;

/**
 * Fleet服务器上传数据通用处理
 * 
 * @author jianchao.wang
 *
 */
public class FleetDataTransferCommon extends FleetDataTransferBase {

	FleetDataTransferCommon(DataTransService service) {
		super("FleetDataTransferCommon", service);
	}

	@Override
	protected String getTag() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"Protocol\":\"Dingli\",");
		sb.append("\"FileType\":");
		switch (super.mCurrentFileType) {
		case ORGRCU:
			case ECTI:
            case DCF:
			case CU:
			sb.append("\"DATA\",");
			break;
		case PCAP:
			sb.append("\"PCap\",");
			break;
		case DTLOG:
			sb.append("\"DTLog\",");
			break;
		default:
			return "";
		}
		sb.append("\"SourceDataType\":");
		if (super.mCurrentFile.getTestTypeId() == TestType.DT.getTestTypeId())
			sb.append("\"DT\",");
		else
			sb.append("\"CQTDataIn\",");

		if (super.mCurrentFile.hasExtraParam("GroupID")) {
			String groupID = super.mCurrentFile.getStringExtraParam("GroupID");
			sb.append("\"GroupID\":\""+groupID+"\",");
		}
		sb.append("\"Info\":\"TestInfo\"}");


//		LogUtil.w("TESTEXXXX",sb.toString());
		return sb.toString();
	}

	@Override
	protected void initCurrentFileTypes() {
		if (super.mCurrentFile.getFileTypes().length == 0) {
			Set<FileType> fileTypes = new HashSet<>();
			fileTypes.add(FileType.ORGRCU);
			super.mCurrentFile.setFileTypes(fileTypes);
		}
	}

}
