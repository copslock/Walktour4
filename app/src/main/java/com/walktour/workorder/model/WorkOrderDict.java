package com.walktour.workorder.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单字典，可以通过工单字典中的CodeId到工单列表中查找WorkType，得到工单列表详细信息。一个工单字典的项（WorkType）对应多个工单列表
 * Author: ZhengLei
 *   Date: 2013-6-7 上午11:32:51
 */
public class WorkOrderDict {
	private List<WorkType> workTypes;
	
	public WorkOrderDict() {
		super();
		workTypes = new ArrayList<WorkType>();
	}

	public List<WorkType> getWorkTypes() {
		return workTypes;
	}

	public void setWorkTypes(List<WorkType> workTypes) {
		this.workTypes = workTypes;
	}
	
	@Override
	public String toString() {
		return "WorkOrderDict [workTypes=" + workTypes + "]";
	}

	public class WorkType {
		private int codeId;
		private String enName;
		private String cnName;
		private int count;
		public int getCodeId() {
			return codeId;
		}
		public void setCodeId(int codeId) {
			this.codeId = codeId;
		}
		public String getEnName() {
			return enName;
		}
		public void setEnName(String enName) {
			this.enName = enName;
		}
		public String getCnName() {
			return cnName;
		}
		public void setCnName(String cnName) {
			this.cnName = cnName;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		
		@Override
		public String toString() {
			return "WorkType [codeId=" + codeId + ", enName=" + enName
					+ ", cnName=" + cnName + ", count=" + count + "]";
		}
		
	}

}
