/**
 * @author tangwq
 */
package com.walktour.model;

/**
 * AT&T公司楼层结构描述信息
 * @author tangwq
 *
 */
public class MapSetModel {
	private String buildingName;
	private String name;
	private String guid;
	private String image;
	private String tabFile;
	private String transmitterFile;
	private float height;
	
	
	/**
	 * @return the buildingName
	 */
	public String getBuildingName() {
		return buildingName;
	}
	/**
	 * @param buildingName the buildingName to set
	 */
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}
	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the tabFile
	 */
	public String getTabFile() {
		return tabFile;
	}
	/**
	 * @param tabFile the tabFile to set
	 */
	public void setTabFile(String tabFile) {
		this.tabFile = tabFile;
	}
	/**
	 * @return the transmitterFile
	 */
	public String getTransmitterFile() {
		return transmitterFile;
	}
	/**
	 * @param transmitterFile the transmitterFile to set
	 */
	public void setTransmitterFile(String transmitterFile) {
		this.transmitterFile = transmitterFile;
	}
	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}
}
