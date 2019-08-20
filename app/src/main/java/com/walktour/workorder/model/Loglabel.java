package com.walktour.workorder.model;

public class Loglabel {
	private static Loglabel instance = null;
	private Loglabel(){};
	/**
	 * 做成单例，因为要在不同的类中写log标签
	 */
	public synchronized static Loglabel newInstance() {
		if(instance == null) {
			instance = new Loglabel();
		}
		return instance;
	}
	
	public String product_name="Walktour";
	public String prodcut_version="1.0";
	public String fileformat_version="1.0";
	public String device_name="1102";
	public String number_of_supported_systems="2";
	public String supported_systems="1,5";
	public String device_id="12312312312";  //imei
	public String service_type="normal";
	public String scene="indoor";
	public String province_code="1";
	public String city_code="0551";
	public String photo_md5="";
	public String work_order_id="991";
	public String work_order_sub="12312";
	public String work_test_type="1";
	public String work_test_scene="1";
	public String work_test_mode="3";
	public String produc_id="10";
	public String terminal_os="Android 4.0";
	public String start_time="2013-10-15 13:12:29";
	public String stop_time_millseconds="13:12:29.878";
	public String stop_time="2013-10-15 13:12:29";

	
	public String format() {
		StringBuilder builder = new StringBuilder();
		String splitter="@@";
		builder.append("product_name="+product_name+splitter);
		builder.append("prodcut_version="+prodcut_version+splitter);
		builder.append("fileformat_version="+fileformat_version+splitter);
		builder.append("device_name="+device_name+splitter);
		builder.append("number_of_supported_systems="+number_of_supported_systems+splitter);
		builder.append("supported_systems="+supported_systems+splitter);
		builder.append("device_id="+device_id+splitter);
		builder.append("service_type="+product_name+splitter);
		builder.append("scene="+scene+splitter);
		builder.append("province_code="+province_code+splitter);
		builder.append("city_code="+city_code+splitter);
		if (!photo_md5.equals(""))
			builder.append("photo_md5="+photo_md5+splitter);
		builder.append("work_order_id="+work_order_id+splitter);
		builder.append("work_order_sub="+work_order_sub+splitter);
		builder.append("work_test_type="+work_test_type+splitter);
		builder.append("work_test_scene="+work_test_scene+splitter);
		builder.append("work_test_mode="+work_test_mode+splitter);
		builder.append("product_id="+produc_id+splitter);
		builder.append("terminal_os="+terminal_os+splitter);
		builder.append("start_time="+start_time+splitter);
		builder.append("stop_time="+stop_time+splitter);
		builder.append("stop_time_millseconds="+stop_time_millseconds);
		return builder.toString();
		
	}
}
