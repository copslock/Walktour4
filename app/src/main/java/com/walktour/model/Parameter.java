package com.walktour.model;

import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.model.Threshold.SignType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 参数实体类
 */
public class Parameter {
	//公共属性　
	private String key;              	//参数数据集编码ID
	private String showName;//显示名称，与解码库提供标准名称有所精简
	private String unit;//单位
	private int decimal;//小数位
	private boolean dynamicPara;//如下所有参数均可以在参数显示界面可设置，=0为不显示，=1为显示
	private boolean chartView;//设置是否在Chart窗口可选，=0代表该参数在参数选择列表框不可见，=1可见
	private boolean chartChecked;//设置Chart窗口默认选中的参数，=1的参数为默认配置参数
	private boolean mapView;//设置是否在地图覆盖参数中可选，=0代表该参数在参数选择列表框不可见，=1可见
	private boolean mapChecked;//设置Map窗口默认选中的参数，=1的参数为默认配置参数
	private boolean pdfView;//设置是否在分布参数窗口中可选，=0代表该参数在参数选择列表框不可见，=1可见
	private boolean pdfChecked;//设置分布参数窗口默认选中的参数，=1的参数为默认配置参数
	private boolean rtTotal;			//是否实时统计参数
	private int taskType;			//当前字段目前表示是否语音任务类型，1：语音类型，其它为数据类型
	private boolean singleLine;			//是否单行显示,1:显示单行，其它默认两字段一行显示
	
	private int color;                 	//在图表的曲线中显示的颜色
	private int scale = 1;             	//参数放大倍数
	private ShowInfoType nettype;				//网络类型nettype 0:ALL 1:WCDMA 2:GSM 3:CDMA 4:EVDO 5:TDSCDMA 6:LTE
	private int especialType = 1;//代表特殊显示参数：=1代表特殊参数表格，表格内容用子节点描述，一般为一行一个名称多个参数值（多个ID）；=2代表枚举参数类型，需读取子节点枚举关系，=3代表结构体中的单一参数类型，如IP，APN等；=4 代表需要额外计算的参数类型，如ECI转换为特定显示形式
	private Especial especial;
	private int tabIndex;//在第几页
	private String structure;//
	
	//阈值的属性　
	private int minimum;				//参数有效最小值
	private long maximum;				//参数有效最大值
	private String thresholdStr = "";	//当前阀值配置记录
	private ArrayList<Threshold>  thresholdList ;
	
	/**
	 * 设置当前参数阀值信息
	 * @param thresholds
	 */
	public void setThresholds(Threshold[] thresholds) {
		for (int i = 0; i < thresholds.length; i++) {
			thresholdList.add(thresholds[i]);
		}
	}
	
	/**
	 * 只有名字的参数
	 * @param id
	 * @param shortName
	 */
	public Parameter(String id,String showName){
		this.key = id;
		this.showName = showName;
		thresholdList = new ArrayList<Threshold>();
	}
	
	public Parameter(String key, String showName, String unit, int decimal,
			boolean dynamicPara, boolean chartView, boolean chartChecked,
			boolean mapView, boolean mapChecked, boolean pdfView,
			boolean pdfChecked, boolean rtTotal, int taskType,
			boolean singleLine, int scale, ShowInfoType nettype, int especialType,  int tabIndex,
			int minimum, long maximum) {
		super();
		this.key = key;
		this.showName = showName;
		this.unit = unit;
		this.decimal = decimal;
		this.dynamicPara = dynamicPara;
		this.chartView = chartView;
		this.chartChecked = chartChecked;
		this.mapView = mapView;
		this.mapChecked = mapChecked;
		this.pdfView = pdfView;
		this.pdfChecked = pdfChecked;
		this.rtTotal = rtTotal;
		this.taskType = taskType;
		this.singleLine = singleLine;
		this.scale = scale;
		this.nettype = nettype;
		this.especialType = especialType;
		this.tabIndex = tabIndex;
		if(minimum!=maximum){
			this.minimum=minimum;
			this.maximum=maximum;
		}
		thresholdList = new ArrayList<Threshold>();
	}

	public String getKey() {
		if (this.key == null) {
			return "";
		}
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getDecimal() {
		return decimal;
	}

	public void setDecimal(int decimal) {
		this.decimal = decimal;
	}

	public boolean isDynamicPara() {
		return dynamicPara;
	}

	public void setDynamicPara(boolean dynamicPara) {
		this.dynamicPara = dynamicPara;
	}

	public boolean isChartView() {
		return chartView;
	}

	public void setChartView(boolean chartView) {
		this.chartView = chartView;
	}

	public boolean isChartChecked() {
		return chartChecked;
	}

	public void setChartChecked(boolean chartChecked) {
		this.chartChecked = chartChecked;
	}

	public boolean isMapView() {
		return mapView;
	}

	public void setMapView(boolean mapView) {
		this.mapView = mapView;
	}

	public boolean isMapChecked() {
		return mapChecked;
	}

	public void setMapChecked(boolean mapChecked) {
		this.mapChecked = mapChecked;
	}

	public boolean isPdfView() {
		return pdfView;
	}

	public void setPdfView(boolean pdfView) {
		this.pdfView = pdfView;
	}

	public boolean isPdfChecked() {
		return pdfChecked;
	}

	public void setPdfChecked(boolean pdfChecked) {
		this.pdfChecked = pdfChecked;
	}

	public void setNettype(ShowInfoType nettype) {
		this.nettype = nettype;
	}

	public Especial getEspecial() {
		return especial;
	}

	public void setEspecial(Especial especial) {
		this.especial = especial;
	}
	
	
	/**
	 * 设置当前为升序或降序排序，true为升,false为降
	 * @param isAscending
	 */
	public void setAscending(){
		Collections.reverse(thresholdList);
	}
	
	public int getEspecialType() {
		return especialType;
	}

	public void setEspecialType(int especialType) {
		this.especialType = especialType;
	}

	/**
	 * 阀值是否以升序排序，true为升序，false为降序
	 * @return
	 */
	public boolean isAscending(){
		if(thresholdList.size() <= 0 
				|| thresholdList.get(0).getValue(false) < thresholdList.get(thresholdList.size() - 1).getValue(false)) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 设置当前为小于等于还是大于等于
	 * @param isMinEquals	
	 */
	public void setMinEquals(){
		for(Threshold threshold:thresholdList){
			threshold.changeSign();
		}
	}
	
	/**
	 * 当前是否小于等于
	 * @return
	 */
	public boolean isMinEquals(){
		if(thresholdList.size() > 0){
			SignType signType = null;
			if(thresholdList.get(0).getValue(false) < thresholdList.get(thresholdList.size() - 1).getValue(false)){
				signType = thresholdList.get(0).getSignType();
			}else{
				signType = thresholdList.get(thresholdList.size() - 1).getSignType();
			}
			
			//最小值的运算符为<=表示，当前符号设置为<=;如果最小值的符号为<,那么当前符号设置为>=;例：<=(X<=-110;-110>X<=-95;);>=(X<-110;-110 <= X <-95)
			return !(signType == SignType.LESS);
		}
		return true;
	}
	
	public String getId(){
		if (this.key == null) {
			return "";
		}
		return this.key;
	}
	
	public void setId(String name){
		this.key = name;
	}
	
	public int  getColor(){
		return this.color;
	}
	
	public void  setColor(int color){
		this.color = color;
	}
	
	public int getMinimum(){
		return this.minimum;
	}
	
	public void setMinimum(int value){
		this.minimum = value;
	}
	
	public long getMaximum(){
		return this.maximum;
	}
	
	public void setMaximum(long value){
		this.maximum = value;
	}
	
	public void setThresholdList(ArrayList<Threshold> thresholds){
		this.thresholdList = thresholds;
	}
	
	public ArrayList<Threshold> getThresholdList(){
		return this.thresholdList;
	}

	public synchronized ShowInfoType getNettype() {
		return nettype;
	}

	public synchronized int getScale(){
	    return scale;
	}
	public synchronized void setScale(int scale){
	    this.scale = scale;
	}

	public boolean isRtTotal() {
		return rtTotal;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public void setRtTotal(boolean rtTotal) {
		this.rtTotal = rtTotal;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public boolean isSingleLine() {
		return singleLine;
	}

	public void setSingleLine(boolean singleLine) {
		this.singleLine = singleLine;
	}

	public String getThresholdStr() {
		return thresholdStr;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public void setThresholdStr(String thresholdStr) {
		this.thresholdStr = thresholdStr;
	}

	@Override
	public String toString() {
		return this.showName;
	}
	
	
}