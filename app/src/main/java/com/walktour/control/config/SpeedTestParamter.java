/**
 * 
 */
package com.walktour.control.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jihong Xie SpeedTest服务器信息
 */
public class SpeedTestParamter {
	private String id;
	private String country;
	private String countrycode;

	private List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>();

	public static class  ServerInfo {
		private SpeedTestParamter parent;
		private String name;
		private String lat;
		private String lon;
		private String url;
		private String sponsor;
		private String sponsorurl;
		private String gid;
		private String url2;
		private String bigsamples;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public SpeedTestParamter getParent() {
			return parent;
		}

		public void setParent(SpeedTestParamter parent) {
			this.parent = parent;
		}

		public String getLat() {
			return lat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}

		public String getLon() {
			return lon;
		}

		public void setLon(String lon) {
			this.lon = lon;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getSponsor() {
			return sponsor;
		}

		public void setSponsor(String sponsor) {
			this.sponsor = sponsor;
		}

		public String getSponsorurl() {
			return sponsorurl;
		}

		public void setSponsorurl(String sponsorurl) {
			this.sponsorurl = sponsorurl;
		}

		public String getGid() {
			return gid;
		}

		public void setGid(String gid) {
			this.gid = gid;
		}

		public String getUrl2() {
			return url2;
		}

		public void setUrl2(String url2) {
			this.url2 = url2;
		}

		public String getBigsamples() {
			return bigsamples;
		}

		public void setBigsamples(String bigsamples) {
			this.bigsamples = bigsamples;
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setServerInfoList(List<ServerInfo> serverInfoList) {
		this.serverInfoList = serverInfoList;
	}

	public List<ServerInfo> getServerInfoList() {
		return serverInfoList;
	}
}
