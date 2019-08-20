package com.walktour.workorder.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单列表，可以通过工单列表中的WorkType到工单详细中查找WorkType，得到工单详细信息。一个工单列表的项（WorkOrderInfo）对应多个一个工单详细（对应键为WorkID）
 * Author: ZhengLei
 *   Date: 2013-6-7 下午3:16:04
 */
public class WorkOrderList {
	private List<WorkOrderInfo> workOrderInfos;

	public WorkOrderList() {
		super();
		workOrderInfos = new ArrayList<WorkOrderInfo>();
	}

	public List<WorkOrderInfo> getWorkOrderInfos() {
		return workOrderInfos;
	}

	public void setWorkOrderInfos(List<WorkOrderInfo> workOrderInfos) {
		this.workOrderInfos = workOrderInfos;
	}

	@Override
	public String toString() {
		return "WorkOrderList [workOrderInfos=" + workOrderInfos + "]";
	}

	public class WorkOrderInfo {
		private int workId;
		private String workName;
		private int workArea;
		private int workType;
		private int projectId;
		private String projectName;
		private String planEndTime;
		private String senderAccount;
		private int provinceId;
		private int cityId;
		private int areaId;
		private int kmSum;
		private int netType;
		private int isReceived;
		
		public WorkOrderInfo() {
			super();
		}

		public int getWorkId() {
			return workId;
		}

		public void setWorkId(int workId) {
			this.workId = workId;
		}

		public String getWorkName() {
			return workName;
		}

		public void setWorkName(String workName) {
			this.workName = workName;
		}

		public int getWorkArea() {
			return workArea;
		}

		public void setWorkArea(int workArea) {
			this.workArea = workArea;
		}

		public int getWorkType() {
			return workType;
		}

		public void setWorkType(int workType) {
			this.workType = workType;
		}

		public int getProjectId() {
			return projectId;
		}

		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public String getPlanEndTime() {
			return planEndTime;
		}

		public void setPlanEndTime(String planEndTime) {
			this.planEndTime = planEndTime;
		}

		public String getSenderAccount() {
			return senderAccount;
		}

		public void setSenderAccount(String senderAccount) {
			this.senderAccount = senderAccount;
		}

		public int getProvinceId() {
			return provinceId;
		}

		public void setProvinceId(int provinceId) {
			this.provinceId = provinceId;
		}

		public int getCityId() {
			return cityId;
		}

		public void setCityId(int cityId) {
			this.cityId = cityId;
		}

		public int getAreaId() {
			return areaId;
		}

		public void setAreaId(int areaId) {
			this.areaId = areaId;
		}

		public int getKmSum() {
			return kmSum;
		}

		public void setKmSum(int kmSum) {
			this.kmSum = kmSum;
		}

		public int getNetType() {
			return netType;
		}

		public void setNetType(int netType) {
			this.netType = netType;
		}

		public int getIsReceived() {
			return isReceived;
		}

		public void setIsReceived(int isReceived) {
			this.isReceived = isReceived;
		}

		@Override
		public String toString() {
			return "WorkOrderInfo [workId=" + workId + ", workName=" + workName
					+ ", workArea=" + workArea + ", workType=" + workType
					+ ", projectId=" + projectId + ", projectName="
					+ projectName + ", planEndTime=" + planEndTime
					+ ", senderAccount=" + senderAccount + ", provinceId="
					+ provinceId + ", cityId=" + cityId + ", areaId=" + areaId
					+ ", kmSum=" + kmSum + ", netType=" + netType
					+ ", isReceived=" + isReceived + "]";
		}
	}

}
