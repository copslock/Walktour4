package com.walktour.control.config;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.SparseArray;

import com.dinglicom.ResourceCategory;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.StringUtil;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.Utils.WalkStruct.NetType;
import com.walktour.Utils.WalkStruct.ShowInfoType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.MyPhoneState;
import com.walktour.control.bean.MyXMLWriter;
import com.walktour.gui.R;
import com.walktour.gui.setting.SysChart;
import com.walktour.gui.setting.SysMap;
import com.walktour.model.Especial;
import com.walktour.model.EspecialEnum;
import com.walktour.model.EspecialRow;
import com.walktour.model.Parameter;
import com.walktour.model.Threshold;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * @功能　参数设置，设置解码参数配置。
 * @参数类型 GSM,GPRS,EDGE,HSDPA,UMTS,CDMA
 * @显示　GPS轨迹颜色，地图，图表。
 * @author qihang.li@dinglicom.com
 * */
public class ParameterSetting{
	
	private static final String TAG =  "ParameterSetting";
	//从文件读取参数
	private MyXMLWriter writer 	;
	private Document doc 		=null;//从文件读取到内存
	
	private int gpsColor;//gps颜色 
	private boolean isDisplayLegen = true;//是否显示图例
	private boolean isMarkAccutely = true;//是否打点微调
	private String dtDefaultMap;  //DT 默认地图设置
	private Parameter mapParameter;//地图显示的参数
	private ArrayList<Parameter> paraList = new ArrayList<Parameter>();//所有参数的列表
	private HashMap<String,String> paraHash = new HashMap<String,String>();//所有参数的ID和ShortName对应
	private String[] parameterNames = new String[0];
	private int[]	 parameterIds;
	private Parameter[] distributionParams;
	private ArrayList<WalkStruct.ShowInfoType> appList;
	private Map<String ,int[]> mPageParaMap = new HashMap<>();
	
	public final int GSM_PUBLIC_PARAM[] = new int[]{UnifyParaID.G_Ser_LAC,UnifyParaID.G_Ser_RxLevFull,
													UnifyParaID.G_Ser_Cell_ID,UnifyParaID.G_Ser_MCC,UnifyParaID.G_Ser_MNC,
													UnifyParaID.G_Ser_BCCH,UnifyParaID.G_Ser_BSIC};
	
	public final int WCDMA_PUBLIC_PARAM[] = new int[]{	UnifyParaID.W_Ser_LAC,UnifyParaID.W_Ser_Total_RSCP,
														UnifyParaID.W_Ser_RNC_ID,UnifyParaID.W_Ser_MCC,UnifyParaID.W_Ser_MNC,
														UnifyParaID.W_Ser_DL_UARFCN,UnifyParaID.W_Ser_Max_PSC};
	
	public final int EVDO_PUBLIC_PARAM[] = new int[]{	UnifyParaID.E_UATI,UnifyParaID.E_Carrier1_TotalSINR,
														UnifyParaID.E_EV_Frequenc,UnifyParaID.E_ServingSectorPN};
	
	public final int TD_SCDMA_PUBLIC_PARAM[] = new int[]{	UnifyParaID.TD_Ser_LAC,UnifyParaID.TD_Ser_PCCPCHRSCP,
															UnifyParaID.TD_Ser_CellID,UnifyParaID.TD_Ser_MCC,
															UnifyParaID.TD_Ser_MNC,UnifyParaID.TD_Ser_CPI};
	
	public final int LTE_PUBLIC_PARAM[] = new int[]{UnifyParaID.L_SRV_TAC,UnifyParaID.L_SRV_RSRP,UnifyParaID.L_SRV_ECGI,
													UnifyParaID.L_SRV_MCC,UnifyParaID.L_SRV_MNC,UnifyParaID.L_SRV_EARFCN,
													UnifyParaID.L_SRV_PCI,UnifyParaID.L_SRV_eNodeBID,UnifyParaID.L_SRV_CellID};
	
	public final int CDMA_PUBLIC_PARAM[] = new int[]	{	UnifyParaID.C_NID,UnifyParaID.C_BID,UnifyParaID.C_TotalEcIo,
															UnifyParaID.C_Frequency,UnifyParaID.C_SID,UnifyParaID.C_ReferencePN};
	
	/**
	 * 轨迹形状
	 */
	private int locusShape = 0;
	
	/**
	 * 轨迹大小
	 */
	private int locusSize = 0;
	
	
	/**初始化地图读取*/
	private ParameterSetting(){
		try{
			writer 	= new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_map_chart.xml"));
			initialParameter();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * MapProperty类的静态值
	 * */
	private static ParameterSetting sInstance;
	
	public synchronized static ParameterSetting getInstance(){
		if(sInstance ==null){
			sInstance = new ParameterSetting();
		}
		return sInstance;
	}
	
	
	/**
	 * 把图表设置恢复为默认值(重新写入配置文件)
	 * @param context
	 */
	public static void resetToDefaultFromFile(Context context, boolean replace) {
//		File desFile = AppFilePathUtil.getInstance().getAppConfigFile("config_map_chart.xml");
//		UtilsMethod.writeRawResource(context, R.raw.config_map_chart,desFile);

		if (replace) {
			PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SysMap.LOCUS_SHARE, 0).apply();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SysMap.LOCUS_SHARE_SIZE, 1).apply();
		}
	}
	
	public static int[] mergerIntegerArray(int a[], int b[]){
	    int[] f = new int[a.length+b.length];
	    for(int i = 0 ; i < f.length; i++)
	     if(i < a.length)
	      f[i] = a[i];
	     else
	      f[i]= b[i-a.length];
	    return f;
	}

	/**
	 * 在xml 的doc对象中读取参数列表
	 * @return
	 */
	private NodeList getParamListByDoc(){
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("Parameters").item(0);
		return elmentChartLine.getElementsByTagName("Parameter");
	}
	
	/**
	 * 获得当前指定参数的分段阀值列表信息
	 * @param el
	 * @return	使用时需要判断当前分段长度，如果长度为0表示没有分段信息
	 */
	private NodeList getThresholdByDoc(Element el){
		NodeList list_thres  = el.getElementsByTagName("Thresholds");
		
		if(list_thres != null && list_thres.getLength() > 0){
			Element eleThres = (Element)list_thres.item(0);
			
			return eleThres.getElementsByTagName("Threshold");
		}
		
		return list_thres;
	}
	
	/**
	 * 当值发生变化时修改阀值比较串信息
	 */
	private void changeCodeSignByThreshold(Element el){
		NodeList list_thres  = el.getElementsByTagName("Thresholds");
		
		if(list_thres != null && list_thres.getLength() > 0){
			Element eleThres = (Element)list_thres.item(0);
			StringBuffer codeSign = new StringBuffer();
			for(Threshold thr : mapParameter.getThresholdList()){
				if(codeSign.length() > 0){
					codeSign.append("|");
				}
				codeSign.append(thr.getValue2Write());
			}
			eleThres.getAttributes().getNamedItem("CodeSign").setNodeValue(codeSign.toString());
		}
	}

	/**
	 * @return 从文件读取GPS轨迹颜色
	 * */
	private void readGpsColorFromFile(){
		try{
			Node node = doc.getElementsByTagName("gpscolor").item(0); 
			this.gpsColor = Integer.parseInt( node.getAttributes().getNamedItem("value").getNodeValue() );
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * @return 从文件读取默认选择的Dt默认地图
	 * */
	private void readDtDefaultMapFromFile(){
		try{
			Node node = doc.getElementsByTagName("dtDefaultmap").item(0); 
			this.dtDefaultMap = node.getAttributes().getNamedItem("value").getNodeValue();
			if(this.dtDefaultMap == null || this.dtDefaultMap.trim().length() == 0){
				if(TextUtils.equals(Locale.getDefault().getLanguage(), "zh")){
					this.dtDefaultMap = "Baidu Map"; 
				}else{
					this.dtDefaultMap = "Google Map";
				}
				this.setDtDefaultMap(this.dtDefaultMap);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获得传入节点指定属性值，如果指定节点不存在返回默认值
	 * @param node_par
	 * @param attributeName
	 * @param defaults
	 * @return
	 */
	private String getAttributes(Node node_par,String attributeName,String defaults){
		return node_par.getAttributes().getNamedItem(attributeName) != null ? 
				node_par.getAttributes().getNamedItem(attributeName).getNodeValue()
				: defaults;
	}
	
	/**
	 * 读取参数列表
	 * */
	private void readParameterList (){
		
		//先清空所有内容（为了使readParameterList可重复使用）
		paraList.clear();
		
		//ArrayList<Parameter> list = new ArrayList<Parameter>();
		NodeList nodeList = getParamListByDoc();
		
		for(int i=0;i<nodeList.getLength();i++){
			Node node_par = nodeList.item(i);
			try{
				if(isShowNet(getAttributes(node_par, "NetType", ""))){
					String key =  getAttributes(node_par, "Key", "").trim();
//					BigInteger bi = new BigInteger(key, 16);//资源库的参数名
//					String showName = ResourceCategory.getInstance().GetParamStandardName(ApplicationModel.getInstance().getHandler_param(), bi.intValue());
//					bi=null;
					String showName = getAttributes(node_par, "ShowName", "");
					Parameter mParam = new Parameter(
							key, 
							showName, 
							getAttributes(node_par, "Unit", ""),
							Integer.parseInt(getAttributes(node_par,"Decimal","0")),
							getAttributes(node_par,"DynamicPara","").equals("1"), 
							getAttributes(node_par,"ChartView","").equals("1"), 
							getAttributes(node_par,"ChartCheck","").equals("1"), 
							getAttributes(node_par,"MapView","").equals("1"), 
							getAttributes(node_par,"MapCheck","").equals("1"), 
							getAttributes(node_par,"PDFView","").equals("1"), 
							getAttributes(node_par,"PDFCheck","").equals("1"), 
							getAttributes(node_par,"RTTotal","").equals("1"), 
							Integer.parseInt(getAttributes(node_par,"TaskType","1")), 
							getAttributes(node_par,"SingleLine","").equals("1"), 
							Integer.parseInt(getAttributes(node_par,"Scale","1")), 
							ShowInfoType.valueOf(getAttributes(node_par,"NetType","Normal")), 
							Integer.parseInt(getAttributes(node_par,"Especial","0")),
							Integer.parseInt(getAttributes(node_par,"TabIndex","1")),
							Integer.parseInt(getAttributes(node_par,"MinValue","0")), 
							Long.parseLong(getAttributes(node_par,"MaxValue","0")));
					Element el = (Element) node_par;
					
					//特殊结构1
					if (mParam.getEspecialType() == Especial.TYPE_ONE) {
						Especial especial = new Especial();
						NodeList nodeListColumnName =el.getElementsByTagName("ColumnName");
						if(nodeListColumnName != null && nodeListColumnName.getLength() > 0){
							Element elementColumnName = (Element)nodeListColumnName.item(0);
							NodeList nodeListColumn = elementColumnName.getElementsByTagName("Column");
							//System.out.println("ColumnName:" + getAttributes(elementColumnName, "Name", ""));
							especial.setTableTitle(getAttributes(elementColumnName, "Name", ""));
							String colWidth = getAttributes(elementColumnName, "ColumnWidth", "0");
							if (colWidth.equals("")) {
								colWidth = "0";
							}
							especial.setColumnWidth(Integer.parseInt(colWidth));
							if (nodeListColumn != null && nodeListColumn.getLength() > 0) {
								String[] columnTitles = new String[nodeListColumn.getLength()];
								for (int j = 0; j < nodeListColumn.getLength(); j++) {
									columnTitles[j] = nodeListColumn.item(j).getTextContent();
								}
								especial.setColumnTitles(columnTitles);
							}
						} 
						
						NodeList nodeListTableRow = el.getElementsByTagName("list");
						if (nodeListTableRow != null && nodeListTableRow.getLength() > 0) {
							EspecialRow[] tableRows = new EspecialRow[nodeListTableRow.getLength()];
							for (int j = 0; j < nodeListTableRow.getLength(); j++) {
								EspecialRow tableRow = new EspecialRow();
								Element elementTableRow = (Element)nodeListTableRow.item(j);
								String name = getAttributes(elementTableRow, "Name", "");
								String unit = getAttributes(elementTableRow, "Unit", "");
								int decimal = Integer.parseInt(getAttributes(elementTableRow, "Decimal", "0").trim());
								int scale = Integer.parseInt(getAttributes(elementTableRow, "Scale", "0").trim());
								int enums = Integer.parseInt(getAttributes(elementTableRow, "ENUM", "0").trim());
								
								tableRow.setName(name);
								tableRow.setUnit(unit);
								tableRow.setDecimal(decimal);
								tableRow.setScale(scale);
								tableRow.setEnums(enums);
								NodeList nodeListID = elementTableRow.getElementsByTagName("ID");
								if (nodeListID != null && nodeListID.getLength() > 0) {
									String[] keys = new String[nodeListID.getLength()];
									for (int k = 0; k < nodeListID.getLength(); k++) {
										keys[k] = nodeListID.item(k).getTextContent().trim();
									}
									tableRow.setKeys(keys);
								}
								
								//如果有对应描述转换
								NodeList enumsList = elementTableRow.getElementsByTagName("ENUMS");
								if(enumsList != null && enumsList.getLength() > 0){
									Element enumsColumnName = (Element)enumsList.item(0);
									NodeList enumListRows = enumsColumnName.getElementsByTagName("Enum");
									if(enumListRows != null && enumListRows.getLength() > 0){
										SparseArray<String> enumMap = new SparseArray<String>();
										for(int k = 0; k < enumListRows.getLength(); k++){
											Element enumsTableRow = (Element)enumListRows.item(k);
											int enumKey = Integer.parseInt(getAttributes(enumsTableRow, "Value", "0").trim());
											String enumValue = getAttributes(enumsTableRow, "Detail", "").trim();
											
											enumMap.put(enumKey, enumValue);
										}
										tableRow.setEnumSet(enumMap);
									}
								}
								tableRows[j] = tableRow;
							}
							especial.setTableRows(tableRows);
						}
						mParam.setEspecial(especial);
					} else if (mParam.getEspecialType() == Especial.TYPE_TWO) {
						Especial especial = new Especial();
						NodeList nodeListEnum =el.getElementsByTagName("Enum");
						if(nodeListEnum != null && nodeListEnum.getLength() > 0){
							EspecialEnum[] especialEnums = new EspecialEnum[nodeListEnum.getLength()];
							for (int j = 0; j < nodeListEnum.getLength(); j++) {
								EspecialEnum especialEnum = new EspecialEnum();
								Element elementEnum = (Element)nodeListEnum.item(j);
								int value = Integer.parseInt(getAttributes(elementEnum, "Value", "0"));
								String detail = getAttributes(elementEnum, "Detail", "");
								especialEnum.setValue(value);
								especialEnum.setDetail(detail);
								especialEnums[j] = especialEnum;
							}
							especial.setEspecialEnums(especialEnums);
						}
						mParam.setEspecial(especial);
					} else if (mParam.getEspecialType() == Especial.TYPE_THREE) {
						mParam.setStructure(getAttributes(node_par, "Structure", ""));
					} else if (mParam.getEspecialType() == Especial.TYPE_FOUR) {
						
					}else if (mParam.getEspecialType() == Especial.TYPE_SEVEN) {
						Especial especial = new Especial();
						NodeList nodeListTableRow = el.getElementsByTagName("list");
						if (nodeListTableRow != null && nodeListTableRow.getLength() > 0) {
							EspecialRow[] tableRows = new EspecialRow[nodeListTableRow.getLength()];
							for (int j = 0; j < nodeListTableRow.getLength(); j++) {
								EspecialRow tableRow = new EspecialRow();
								Element elementTableRow = (Element)nodeListTableRow.item(j);
								String name = getAttributes(elementTableRow, "Name", "");
								String unit = getAttributes(elementTableRow, "Unit", "");
								int decimal = Integer.parseInt(getAttributes(elementTableRow, "Decimal", "0").trim());
								int scale = Integer.parseInt(getAttributes(elementTableRow, "Scale", "0").trim());
								
								tableRow.setUnit(unit);
								tableRow.setDecimal(decimal);
								tableRow.setScale(scale);
								tableRow.setName(name);
								NodeList nodeListID = elementTableRow.getElementsByTagName("ID");
								if (nodeListID != null && nodeListID.getLength() > 0) {
									String[] keys = new String[nodeListID.getLength()];
									for (int k = 0; k < nodeListID.getLength(); k++) {
										keys[k] = nodeListID.item(k).getTextContent().trim();
									}
									tableRow.setKeys(keys);
								}
								tableRows[j] = tableRow;
							}
							especial.setTableRows(tableRows);
						}
						mParam.setEspecial(especial);
					}
					
					//阀值
					NodeList list_thres  = el.getElementsByTagName("Thresholds");
					if(list_thres != null && list_thres.getLength() > 0){
						Element eleThres = (Element)list_thres.item(0);
						String codeSign = eleThres.getAttributes().getNamedItem("CodeSign").getNodeValue();
						
						//LogUtil.w(TAG,"--Thresholds codeSign:" + codeSign + "--len:" + list_thres.getLength());
						NodeList list_thre = eleThres.getElementsByTagName("Threshold");
						
						Threshold[] thresholds = new Threshold[list_thre.getLength()];
						for(int l=0;l<list_thre.getLength();l++){
							
							String range = list_thre.item(l).getAttributes().getNamedItem("Range").getNodeValue();
							
							String colors = list_thre.item(l).getAttributes().getNamedItem("Color").getNodeValue();
							//int value = 0;//Integer.parseInt( list_thre.item(l).getAttributes().getNamedItem("value").getNodeValue() );
							//int color = 0;//Integer.parseInt( list_thre.item(l).getAttributes().getNamedItem("Color").getNodeValue() );
							thresholds[l] = new Threshold(range, colors);
							thresholds[l].setScale(mParam.getScale());
							//LogUtil.w(TAG,"--Thresholds range:" + range + "--color:" + colors);
						}
						
						mParam.setThresholds(thresholds);
						mParam.setThresholdStr(codeSign);
					} else {
						mParam.setThresholds(new Threshold[0]);
						mParam.setThresholdStr("");
					}
					
					//设置分布统计参数信息，如果该值不为空且为1，2表示第几个分布参数，目前仅支持两个分布参数
					String distribution = getAttributes(node_par,"Distribution","");
					if(distribution.equals("1")){
						distributionParams[0] = mParam;
					}else if(distribution.equals("2")){
						distributionParams[1] = mParam;
					}
					
					//LogUtil.w(TAG,"--Thresholds param:" + mParam.toString());

					this.paraList.add(mParam);
					this.paraHash.put(key, showName);
				}
			}catch(Exception e){
				LogUtil.w(TAG, "readparametersetting",e);
			}
		}
	}
	
	/**
	 * 判断当前参数所以的网络类型是否显示
	 * @param nettype
	 * @return
	 */
	private boolean isShowNet(String nettype){
		boolean isShow = false;
		for(WalkStruct.ShowInfoType showNet : appList){
			if(nettype.equals(NetType.ENDC.name())||nettype.equals(NetType.CatM.name())||nettype.equals(NetType.Normal.name()) || showNet.name().equalsIgnoreCase(nettype) || nettype.equals(ShowInfoType.WLAN.name())){
				isShow = true;
				break;
			}
		}
		return isShow;
	}
	
	/**
	 * 从文件读取地图要显示的参数
	 * */
	private Parameter readMapPara(){
		Node node = doc.getElementsByTagName("mapparameter").item(0);
		String paraName = node.getAttributes().getNamedItem("name").getNodeValue();
		for(int i =0;i<this.paraList.size();i++){
			if( paraName != null && paraName.equals( this.paraList.get(i).getId() ) ){
				return this.paraList.get(i);
			}
		}
		
		if(paraList.size()>0){
			setMapParameter(paraList.get(0).getId());
			return paraList.get(0);
		}
		return null;
	}
	
	/**
	 * 读取所有参数名
	 * */
	private void initParameterNames(){
		try{
			parameterNames 	= new String[this.paraList.size()];
			//parameterIds	= new int[this.paraList.size()];
			ArrayList<Integer>	enabledIds = new ArrayList<Integer>();
			
			for(int i=0;i<this.paraList.size();i++){
				parameterNames[i] = this.paraList.get(i).getId();
				
				//twq20131122 有部分参数值是在业务过程中添加到显示结果中的，此部分参数不通过数据集接口去查
				/*if(paraList.get(i).isDontSearchByDataSet()){
					parameterIds[i] = 0x7F05FF00;
				}else{
					parameterIds[i] =Integer.parseInt(this.paraList.get(i).getName(), 16) ;
				}*/
				
				//twq20140211 仅需要显示的参数出现在查询列表中
//				if(paraList.get(i).isDisplayInTable() && !paraList.get(i).isDontSearchByDataSet()){
//					enabledIds.add(Integer.parseInt(this.paraList.get(i).getId(), 16));
//				}
				if (paraList.get(i).isDynamicPara() && StringUtil.isInteger(this.paraList.get(i).getId())) {
					enabledIds.add(Integer.parseInt(this.paraList.get(i).getId(), 16));
				}
			}
			
			parameterIds = new int[enabledIds.size()];
			for(int i=0; i < enabledIds.size(); i++){
				parameterIds[i] = enabledIds.get(i);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 初次运行时载入所有参数配置
	 * */
	public void initialParameter(){
		doc = writer.getDocument();
		appList =  ApplicationModel.getInstance().getNetList();
		//初始化分布页面的参数列表
		distributionParams = new Parameter[2];
		
		//读取GPS颜色
		readGpsColorFromFile();
		
		//读取DT默认地图
		readDtDefaultMapFromFile();
		
		//读取参数列表
		readParameterList ();
		
		this.isDisplayLegen =  doc.getElementsByTagName("lengen").item(0)
								.getAttributes().getNamedItem("value").getNodeValue().equals("1");
		this.isMarkAccutely = doc.getElementsByTagName("mark").item(0)
								.getAttributes().getNamedItem("value").getNodeValue().equals("1");
		
		//获取地图显示的参数；
		mapParameter = readMapPara();
		
		//获取所有参数名
		initParameterNames();
	}
	
	/**
	 * 初始化地图轨迹样式<BR>
	 * [功能详细描述]
	 * @param context
	 */
	public void initMapLocusShape(Context context){
	    locusShape = PreferenceManager.getDefaultSharedPreferences(context).getInt(SysMap.LOCUS_SHARE, 0);
	    locusSize = PreferenceManager.getDefaultSharedPreferences(context).getInt(SysMap.LOCUS_SHARE_SIZE, 1);
	}
	
	/**
	 * @return 所有参数
	 * */
	public ArrayList<Parameter> getParameters(){
		return this.paraList;
	}
	
	/**
	 * @return 所有参数,第一个为空
	 * */
	public ArrayList<Parameter> getParametersFirstNull(Context context){
		ArrayList<Parameter> result = new ArrayList<Parameter>();
		result.add( new Parameter("null",context.getString(R.string.str_check_non)) );
		result.addAll(paraList);
		return result;
	}
	
	/**
	 *   @return 所有参数名
	 * */
	public String[] getParameterNames(){
		return this.parameterNames;
	}
	
	/**反回地图图表参数所需要显示的参数ID值*/
	public int[] getParameterIds(){
		return this.parameterIds;
	}
	
	public int[] getChartParameterIds(Context context){
	    NetType nettype = MyPhoneState.getInstance().getCurrentNetType(context);
	    int[] chartParameterIds = null;
        switch (nettype) {
            case GSM:
                chartParameterIds = mergerIntegerArray(GSM_PUBLIC_PARAM, this.parameterIds);
                break;
            case EVDO:
                chartParameterIds = mergerIntegerArray(EVDO_PUBLIC_PARAM, this.parameterIds);
                break;
            case CDMA:
                chartParameterIds = mergerIntegerArray(CDMA_PUBLIC_PARAM, this.parameterIds);
                break;
            case WCDMA:
                chartParameterIds = mergerIntegerArray(WCDMA_PUBLIC_PARAM, this.parameterIds);
            case TDSCDMA:
                chartParameterIds = mergerIntegerArray(TD_SCDMA_PUBLIC_PARAM, this.parameterIds);
                break;
            case LTE:
                chartParameterIds = mergerIntegerArray(LTE_PUBLIC_PARAM, this.parameterIds);
                break;
			case ENDC:
				chartParameterIds = mergerIntegerArray(LTE_PUBLIC_PARAM, this.parameterIds);
				break;
            default:
                if(this.parameterIds != null){
                    chartParameterIds = this.parameterIds;
                }else {
                    chartParameterIds = new int[]{};
                }
                break;
        }
	    return chartParameterIds;
	}
	
	/**
	 * @return 所有参数名的简称
	 * */
	public String[] getParameterShortNames(){
		String [] result = new String[this.paraList.size()]; 
		for(int i=0;i<this.paraList.size();i++){
			result[i] = this.paraList.get(i).getShowName();
		}
		return result;
	}
	
	/**
	 * @param id 参数ID
	 * @return 根据参数ID返回参数名
	 */
	public String getParamShortName(String id){
		return this.paraHash.get(id);
	}
	
	/**
	 * 根据网络类型返回参数数组<BR>
	 * type = 0 为公共参数 
	 * @param type 网络类型 2 为 2G ,3为3G,4为4G
	 * @return 参数数组
	 */
	public ArrayList<Parameter> getParamerterByNetType(int type){
	    ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
	    for(int i=0;i<this.paraList.size();i++){
            Parameter parameter = paraList.get(i);
            if (!parameter.isMapView()) {
            	continue;
            }
            switch (type) {
                case 0:
                    if(parameter.getNettype().getNetGroup() == NetType.Normal){
                        parameterList.add(parameter);
                    }
                    break;
                case 2:
                    if(parameter.getNettype().getNetGroup() == NetType.GSM 
                    	|| parameter.getNettype().getNetGroup() == NetType.CDMA
                    	|| parameter.getNettype().getNetGroup() == NetType.Normal){
                        parameterList.add(parameter);
                    }
                    break;
                case 3:
                	if(parameter.getNettype().getNetGroup() == NetType.WCDMA
	            		|| parameter.getNettype().getNetGroup() == NetType.CDMA 
	            		|| parameter.getNettype().getNetGroup() == NetType.EVDO
	                	|| parameter.getNettype().getNetGroup() == NetType.TDSCDMA 
	                	|| parameter.getNettype().getNetGroup() == NetType.Normal){
	                    parameterList.add(parameter);
                	}
                	break;
                case 4:
//				case 5:
                	if(parameter.getNettype().getNetGroup() == NetType.LTE 
                		|| parameter.getNettype().getNetGroup() == NetType.LTETDD
            			|| parameter.getNettype().getNetGroup() == NetType.EVDO 
            			|| parameter.getNettype().getNetGroup() == NetType.Normal
							|| parameter.getNettype().getNetGroup() == NetType.NBIoT
							|| parameter.getNettype().getNetGroup() == NetType.CatM
					||parameter.getNettype().getNetGroup() == NetType.ENDC){	//CDMA网络的和LTE合成一个参数选项，供用户自己选择
                		parameterList.add(parameter);
                	}
                    break;
                default:
                    break;
            }

        }
	    return parameterList;
	}
	
	/**
	 * 判断该参数是否是公共参数<BR>
	 * [功能详细描述]
	 * @param param
	 * @return
	 */
	public boolean isPublicParamters(String param){
	    for (Parameter parameter : paraList) {
            if(parameter.getNettype().getNetGroup() == NetType.Normal && parameter.getShowName().equals(param)){
                return true;
            }
        }
	    return false;
	}
	
	/**
	 * 获得所有已选中的参数<BR>
	 * 用于显示轨迹
	 * @return
	 */
	public List<Parameter> getCheckedParamerters(){
	    List<Parameter> parameterList = new ArrayList<Parameter>();
       for(int i=0;i<this.paraList.size();i++){
            Parameter parameter = paraList.get(i);
            if(parameter.isMapChecked()){
                parameterList.add(parameter);
            }
       }
       return parameterList;
	}
	
	   
    /**
     * 获得所有已选中的参数<BR>
     * 用于显示轨迹
     * @return
     */
    public List<Parameter> getCheckedParamertersByNet(int type){
        List<Parameter> parameterCheckeds = getParamerterByNetType(type);
        List<Parameter> parameterList = new ArrayList<Parameter>();
        for (int i = 0; i < parameterCheckeds.size(); i++) {
            Parameter parameter = parameterCheckeds.get(i);
            if (parameter.isMapChecked()) {
                parameterList.add(parameter);
            }
        }
        return parameterList;
    }
	
	
	/**
	 * @return 地图配置参数
	 * */
	public Parameter getMapParameter(){
		return this.mapParameter;
	}

	/**
	 * 设置当前地图要显示什么参数
	 * @param name　参数名
	 * */
	public void setMapParameter(String name){
		doc.getElementsByTagName("mapparameter").item(0).getAttributes()
			.getNamedItem("name").setNodeValue(name);
		writer.writeToFile(doc);//写入文件
		//doc = writer.getDocument();//重新读取文件
		
		//重新读取当前地图显示参数
		mapParameter = this.readMapPara();
	}
	
	public void setMapParamterChecked(String key,boolean checked){
	    
       NodeList nl = getParamListByDoc();
        for(int i=0;i<nl.getLength();i++){
            Node node = nl.item(i);
            if(key.equals(getAttributes(node, "Key", "")) ){
            	if(node.getAttributes().getNamedItem("MapCheck") == null){
//            		LogUtil.w(TAG,"--key:" + key + "--check:" + checked);
            	}else{
            		node.getAttributes().getNamedItem("MapCheck").setNodeValue(checked ? "1": "0") ;
            	}
                writer.writeToFile(doc);//写入文件
            }
        }
	    
       //重新读取当前地图显示参数
       mapParameter = this.readMapPara();
	}
	
	/**
	 * 设置地图显示的参数值
	 * @param threshold 
	 * 0:阀值1 
	 * 1:阀值2 
	 * 2:阀值3  
	 * 3:阀值4 
	 * @param value 阀值的值
	 * */
	public void setMapParameterValue(int threshold,int value){
		//修改内存中的值
		ArrayList<Threshold> listThreshold = this.mapParameter.getThresholdList();
		if(threshold >= 0 && threshold < listThreshold.size()){
			listThreshold.get(threshold).setValue(value,mapParameter.isAscending());
			if(threshold + 1 < listThreshold.size()){
				listThreshold.get(threshold + 1).setThreshold2Value(value,mapParameter.isAscending());
			}
		}
		this.mapParameter.setThresholdList(listThreshold);
		//修改文件中的值
		NodeList nl = getParamListByDoc();
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			if(this.mapParameter.getId().equals( getAttributes(node, "Key", "") ) ){
				Element parElement = (Element) node;
				NodeList threList = getThresholdByDoc(parElement);
				
				if(threList.getLength() > 0){
					changeCodeSignByThreshold(parElement);
					
					Node threNode = threList.item(threshold);
					
					threNode.getAttributes().getNamedItem("Range").setNodeValue(listThreshold.get(threshold).getValue2Write()) ;
					if(threshold + 1 < threList.getLength()){
						threNode = threList.item(threshold + 1);
						threNode.getAttributes().getNamedItem("Range").setNodeValue(listThreshold.get(threshold + 1).getValue2Write()) ;
					}
					writer.writeToFile(doc);//写入文件
					//doc = writer.getDocument();//重新读取文件
				}else{
//					LogUtil.w(TAG,"--save threList value error:" + mapParameter.getId() + "--i:" + threshold + "--v:" + value);
				}
			}
		}
	}
	
	/**
	 * 添加指定阀值分段信息
	 * @param value
	 */
	public void addThresholdValue(int value, String color, int scale){
		List<Threshold> list = this.mapParameter.getThresholdList();
		boolean isAscending = this.mapParameter.isAscending();
		boolean isMinEquals = this.mapParameter.isMinEquals();
		for (int i = 0; i < list.size() - 1; i++) {
			int leftValue = list.get(i).getValue(isAscending);
			int rightValue = list.get(i + 1).getValue(isAscending);
			if (isAscending) {
				if (value > leftValue && value < rightValue) {
					String valueStr = isMinEquals ? ("(" + leftValue + "," + value + "]") : ("[" + leftValue + "," + value + ")");
					String valueStrNext = isMinEquals ? ("(" + value + "," + rightValue + "]") : ("[" + value + "," + rightValue + ")");
					Threshold threshold = new Threshold(valueStr, color);
					threshold.setScale(scale);
					this.mapParameter.getThresholdList().get(i + 1).setValueStr(valueStrNext);
					this.mapParameter.getThresholdList().add(i + 1, threshold);
				}
			} else {
				if (value < leftValue && value > rightValue) {
					String valueStr = isMinEquals ? ("(" + value + "," + leftValue + "]") : ("[" + value + "," + leftValue + ")");
					String valueStrNext = isMinEquals ? ("(" + rightValue + "," + value + "]") : ("[" + rightValue + "," + value + ")");
					Threshold threshold = new Threshold(valueStr, color);
					threshold.setScale(scale);
					this.mapParameter.getThresholdList().get(i + 1).setValueStr(valueStrNext);
					this.mapParameter.getThresholdList().add(i + 1, threshold);
				}
			}
		}
	}
	
	/**
	 * 删除指定序号的阀值分段信息
	 * @param witch
	 */
	public void delThresholdValue(int witch){
		List<Threshold> list = this.mapParameter.getThresholdList();
		boolean isAscending = this.mapParameter.isAscending();
		boolean isMinEquals = this.mapParameter.isMinEquals();
		int leftValue = list.get(witch- 1).getValue(isAscending);
		int rightValue = list.get(witch + 1).getValue(isAscending);
		String valueStrNext = isMinEquals ? ("(" + leftValue + "," + rightValue + "]") : ("[" + leftValue + "," + rightValue + ")");
		this.mapParameter.getThresholdList().get(witch + 1).setValueStr(valueStrNext);
		this.mapParameter.getThresholdList().remove(witch);
	}
	
	/**
	 * 保存对阀值的修改
	 */
	public void saveMapParameterThreshold() {
		
		NodeList nodeList = getParamListByDoc();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node_par = nodeList.item(i);
			if (getAttributes(node_par, "Key", "").equals(this.mapParameter.getId())) {
				Element parameter = (Element)node_par;
				NodeList thresholdRootList = parameter.getElementsByTagName("Thresholds");
				if (thresholdRootList.getLength() > 0){
					parameter.removeChild(thresholdRootList.item(0));
				}
				String codeSign = "";
				Node nodeThresholdRoot = null;
				Element elementThresholdRoot = null;
				List<Threshold> thresholds = this.mapParameter.getThresholdList();
				if (thresholds.size() > 0) {
					nodeThresholdRoot = doc.createElement("Thresholds");
					elementThresholdRoot = (Element)nodeThresholdRoot;
					for (int j = 0; j < thresholds.size(); j++) {
						codeSign += thresholds.get(j).getValueStr() + "|";
						Node nodeThreshold = doc.createElement("Threshold");
						Element elementThreshold = (Element)nodeThreshold;
						elementThreshold.setAttribute("Range", thresholds.get(j).getValue2Write());
						elementThreshold.setAttribute("Color", thresholds.get(j).getColor2Write());
						elementThresholdRoot.appendChild(elementThreshold);
					}
					if (!codeSign.equals("")) {
						codeSign = codeSign.substring(0, codeSign.length() - 1);
					}
					elementThresholdRoot.setAttribute("CodeSign", codeSign);
					parameter.appendChild(elementThresholdRoot);
				}
				writer.writeToFile(doc);// 写入文件
			}
		}
	}
	
	/**
	 * 保存单个parameter 是否在表格中显示
	 * @param parameter
	 */
	public void saveMapParameter(Parameter parameter) {

		NodeList nodeList = getParamListByDoc();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node_par = nodeList.item(i);
			if (getAttributes(node_par, "Key", "").equals(parameter.getId())) {
				node_par.getAttributes().getNamedItem("DynamicPara").setNodeValue(parameter.isDynamicPara() ? 1 + "" : 0 + "");
				writer.writeToFile(doc);// 写入文件
			}
		}
	}
	
	/**
	 * 保存多个parameter 是否在表格中显示
	 * @param parameters
	 */
	public void saveMapParameters(List<Parameter> parameters) {
		NodeList nodeList = getParamListByDoc();
		for (int i = 0; i < nodeList.getLength(); i++) {
//			saveMapParameter(parameters.get(i));
			for (int j = 0; j < parameters.size(); j++) {
				Node node_par = nodeList.item(i);
				if (getAttributes(node_par, "Key", "").equals(parameters.get(j).getId())) {
					node_par.getAttributes().getNamedItem("DynamicPara").setNodeValue(parameters.get(j).isDynamicPara() ? 1 + "" : 0 + "");
				}
			}
		}
		writer.writeToFile(doc);// 写入文件
	}
	
	/**
	 * 顺序保存多个parameter
	 * @param parameters
	 */
	public void saveMapParametersByOrder(List<Parameter> parameters) {
		if (parameters == null || parameters.size() == 0) {
			return;
		}
		System.out.println("parameters--------------------------------:" + parameters.size());
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("Parameters").item(0);
		NodeList nodeList = getParamListByDoc();
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
//			if(contain(parameters, node) ){
				elmentChartLine.removeChild(node);
//			}
		}
		for (int i = 0; i < parameters.size(); i++) {
			Node nodeParameter = doc.createElement("Parameter");
			Element elementParameter = (Element)nodeParameter;
			elementParameter.setAttribute("Key", parameters.get(i).getId());
			elementParameter.setAttribute("ShowName", parameters.get(i).getShowName());
			elementParameter.setAttribute("NetType", parameters.get(i).getNettype().name());
			elementParameter.setAttribute("MinValue", parameters.get(i).getMinimum() + "");
			elementParameter.setAttribute("MaxValue", parameters.get(i).getMaximum() + "");
			elementParameter.setAttribute("Unit", parameters.get(i).getUnit());
			elementParameter.setAttribute("Decimal", parameters.get(i).getDecimal() + "");
			elementParameter.setAttribute("Scale", parameters.get(i).getScale() + "");
			elementParameter.setAttribute("DynamicPara", parameters.get(i).isDynamicPara() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("ChartView", parameters.get(i).isChartView() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("ChartCheck", parameters.get(i).isChartChecked() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("MapView", parameters.get(i).isMapView() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("MapCheck", parameters.get(i).isMapChecked() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("PDFView", parameters.get(i).isPdfView() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("PDFCheck", parameters.get(i).isPdfChecked() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("RTTotal", parameters.get(i).isRtTotal() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("TaskType", parameters.get(i).getTaskType() + "");
			elementParameter.setAttribute("SingleLine", parameters.get(i).isSingleLine() ? 1 + "" : 0 + "");
			elementParameter.setAttribute("Especial", parameters.get(i).getEspecialType() + "");
			if(parameters.get(i).getEspecialType() == Especial.TYPE_THREE){
				elementParameter.setAttribute("Structure", parameters.get(i).getStructure() + "");
			}
			elementParameter.setAttribute("TabIndex", parameters.get(i).getTabIndex() + "");
			//特殊表格
			if (parameters.get(i).getEspecialType() == Especial.TYPE_ONE && parameters.get(i).getEspecial() != null) {
				Especial especial = parameters.get(i).getEspecial();
				Element columnNodeRoot = doc.createElement("ColumnName");
				columnNodeRoot.setAttribute("Name", especial.getTableTitle());
				columnNodeRoot.setAttribute("ColumnWidth", especial.getColumnWidth() + "");
				for (int j = 0; j < especial.getColumnTitles().length; j++) {
					Element columnNode = doc.createElement("Column");
					columnNode.setTextContent(especial.getColumnTitles()[j]);
					columnNodeRoot.appendChild(columnNode);
				}
				elementParameter.appendChild(columnNodeRoot);
				for (int j = 0; j < especial.getTableRows().length; j++) {
					Element tableRow = doc.createElement("list");
					tableRow.setAttribute("Name", especial.getTableRows()[j].getName());
					tableRow.setAttribute("Unit", especial.getTableRows()[j].getUnit());
					tableRow.setAttribute("Decimal", especial.getTableRows()[j].getDecimal() + "");
					tableRow.setAttribute("Scale", especial.getTableRows()[j].getScale() + "");
					for (int k = 0; k < especial.getTableRows()[j].getKeys().length; k++) {
						Element id = doc.createElement("ID");
						id.setTextContent(especial.getTableRows()[j].getKeys()[k]);
						tableRow.appendChild(id);
					}
					elementParameter.appendChild(tableRow);
				}
				
			} else if (parameters.get(i).getEspecialType() == Especial.TYPE_TWO && parameters.get(i).getEspecial() != null){
				Especial especial = parameters.get(i).getEspecial();
				for (int j = 0; j < especial.getEspecialEnums().length; j++) {
					Element elementEnum = doc.createElement("Enum");
					elementEnum.setAttribute("Value", especial.getEspecialEnums()[j].getValue() + "");
					elementEnum.setAttribute("Detail", especial.getEspecialEnums()[j].getDetail() + "");
					elementParameter.appendChild(elementEnum);
				}
			} else if (parameters.get(i).getEspecialType() == Especial.TYPE_THREE) {
				elementParameter.setAttribute("Structure", parameters.get(i).getStructure() + "");
			}
			//阀值信息
			List<Threshold> thresholds = parameters.get(i).getThresholdList();
			Node nodeThresholdRoot = null;
			Element elementThresholdRoot = null;
			if (thresholds.size() > 0) {
				nodeThresholdRoot = doc.createElement("Thresholds");
				elementThresholdRoot = (Element)nodeThresholdRoot;
				elementThresholdRoot.setAttribute("CodeSign", parameters.get(i).getThresholdStr());
				elementParameter.appendChild(elementThresholdRoot);
			}
			for (int j = 0; j < thresholds.size(); j++) {
				Node nodeThreshold = doc.createElement("Threshold");
				Element elementThreshold = (Element)nodeThreshold;
//				Log.d("DDD", "写入文件：Range-----:" + thresholds.get(j).getValue2Write() + "-----------Color-----:" + thresholds.get(j).getColor2Write());
				elementThreshold.setAttribute("Range", thresholds.get(j).getValue2Write());
				elementThreshold.setAttribute("Color", thresholds.get(j).getColor2Write());
				if(elementThresholdRoot != null)
					elementThresholdRoot.appendChild(elementThreshold);
			}
			elmentChartLine.appendChild(elementParameter);
			}
			writer.writeToFile(doc);//写入文件
	}
	
//	private boolean contain(List<Parameter> parameters, Node node) {
//		for (int i = 0; i < parameters.size(); i++) {
//			if (parameters.get(i).getId().equals(getAttributes(node, "Key", ""))) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * 删除所有节点
	 * @param element
	 * @param nodeList
	 */
//	private void removeAllNode(Element element, NodeList nodeList) {
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			element.removeChild(nodeList.item(i));
//		}
//	}
	
	/**
	 * 设置地图显示的参数值
	 * @param threshold 0:阀值1  1:阀值2	 2:阀值3  3:阀值４
	 * @param color 阀值对应的颜色
	 * */
	public void setMapParameterColor(int threshold,int color){
		//修改内存中的值
		ArrayList<Threshold> listThreshold = this.mapParameter.getThresholdList();
		if(threshold >= 0 && threshold < listThreshold.size()){
			listThreshold.get(threshold).setColor(color);
		}
		
		this.mapParameter.setThresholdList(listThreshold);
		//修改文件中的值
		NodeList nl = getParamListByDoc();
		for(int i=0;i<nl.getLength();i++){
			Node node = nl.item(i);
			if(this.mapParameter.getId().equals( getAttributes(node, "Key", "") ) ){
				Element parElement = (Element) node;
				NodeList threList = getThresholdByDoc(parElement);
				
				if(threList.getLength() > 0){
					Node threNode = threList.item(threshold);
					threNode.getAttributes().getNamedItem("Color").setNodeValue(listThreshold.get(threshold).getColor2Write()) ;
					
					writer.writeToFile(doc);//写入文件
					//doc = writer.getDocument();
				}else{
//					LogUtil.w(TAG,"--save threList color error:" + mapParameter.getId() + "--i:" + threshold + "--c:" + color);
				}
			}
		}
	}
	
	
	/**
	 * @return 当前曲线图要显示的参数对象,最多４个
	 * */
	public Parameter[] getChartLineParemeters(){
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("chartline").item(0);
		NodeList nodeList = elmentChartLine.getElementsByTagName("line");
		
		ArrayList<Parameter> arrayList = new ArrayList<Parameter>();
		//载入曲线列表
		for(int i =0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			String name = node .getAttributes().getNamedItem("name").getNodeValue();
			//如果不是空参数并且存在于参数对象总表中,表法当前参数对应的网络有权限
			Parameter param = getParameterById(name);
			
			if( !name.equals("null") && param != null) {
				int color = Integer.parseInt( node.getAttributes().getNamedItem("color").getNodeValue() );
				param.setColor(color);
				arrayList.add( param );
			}
		}
		
		Parameter[] result = new Parameter[arrayList.size()];
		arrayList.toArray(result);
		return result;
	}
	
	
	/**
	 * 设置曲线参数列表
	 * */
	public void setChartLineParameters( ArrayList< HashMap<String ,Object > > parameterList ){
		
		int listLength = parameterList.size();
		
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("chartline").item(0);
		NodeList nodeList = elmentChartLine.getElementsByTagName("line");
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			if( i > (listLength-1)  ){
				node.getAttributes().getNamedItem("name").setNodeValue("null");
				node.getAttributes().getNamedItem("color").setNodeValue("-1");
			}else{
				HashMap<String,Object> map =  parameterList.get(i);
				String parameterName = (String) map.get(SysChart.KEY_PARA );
				int parameterColor = (Integer) map.get(SysChart.KEY_COLOR );
				node.getAttributes().getNamedItem("name").setNodeValue( parameterName  );
				node.getAttributes().getNamedItem("color").setNodeValue( String.valueOf( parameterColor ) );
//				LogUtil.w("Walktour", "set line"+i+1+":"+parameterName+","+"color:"+parameterColor);
			}
		}//end for
		
		writer.writeToFile(doc);
	}//end setChartLineParameters
	
	/**
	 * @return 当前曲线图要显示的参数的真实参数名
	 * */
	public String[] getChartLineParameterNames(){
		Parameter[] chartParameters = getChartLineParemeters();
		String [] result = new String[chartParameters.length];
		for(int i=0;i<result.length;i++){
			result[i]=  chartParameters[i].getId();
		}
		
		return result;
	}
	
	
	/**
	 * @return 当前选中要显示在趋势图的参数颜色，
	 * */
	public int[] getChartLineColors(){
		Parameter[] chartParameters = getChartLineParemeters();
		int [] result = new int[chartParameters.length];
		for(int i=0;i<result.length;i++){
			result[i]=  chartParameters[i].getColor();
		}
		
		return result;
	}
	
	
	/**
	 * @return 当前表格要显示的参数
	 * */
	public Parameter[] getTableParameters(){
		ArrayList<Parameter > paras = new ArrayList<Parameter>();
		for(int i=0;i<this.paraList.size();i++){
//			if(this.paraList.get(i).isDynamicPara() ){
//				paras.add(this.paraList.get(i) );
//			}
			if(this.paraList.get(i).isChartView() ){
				paras.add(this.paraList.get(i) );
			}
		}
		Parameter [] result = new Parameter [paras.size()];
		paras.toArray(result);
		return result;
	}
	
	public Parameter[] getTableParametersByNetworkType(String networkType) {
//		LogUtil.d(TAG,"----getTableParametersByNetworkType----start----");
		ArrayList<Parameter > paras = new ArrayList<Parameter>();
		for(int i=0;i<this.paraList.size();i++){
			Parameter parameter = this.paraList.get(i);
			if(parameter.getNettype().getNetGroup().name().equals(networkType) && parameter.isDynamicPara()){
//				LogUtil.d(TAG,"----param:"+parameter.getShowName());
				paras.add(parameter );
			}
			getSubTypeParm(networkType,paras,i);
		}
		Parameter [] result = new Parameter [paras.size()];
		paras.toArray(result);
//		LogUtil.d(TAG,"----getTableParametersByNetworkType----end----");
		return result;
	}
	
	/**
	 * 增加统计参数等查询
	 * @param parameter
	 * @return
	 */
	private boolean isQuery(Parameter parameter){
		return parameter.isDynamicPara() || parameter.isChartView() || parameter.isMapView() || parameter.isPdfView() || parameter.isRtTotal();
	}
	
	
	/**
	 * 根据网络动态动态查询配置参数
	 */
	
	public int[] getPageParaByNetworkType(String networkType) {
		if(StringUtil.isNullOrEmpty(networkType))
			return new int[0];
		if(this.mPageParaMap.containsKey(networkType))
			return this.mPageParaMap.get(networkType);
		ArrayList<String > paras = new ArrayList<>();
		for(int i=0;i<this.paraList.size();i++){
			if(this.paraList.get(i).getNettype().getNetGroup().name().equals(networkType) && isQuery(this.paraList.get(i))){
				setSubParamID(paras,i);
			}	
			getSubTypeParmByid(networkType,paras,i);
		}
		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < paras.size(); i++) {
			try{
				set.add(Integer.valueOf(paras.get(i),16));
			}catch(Exception e){
				LogUtil.w(TAG,"getPageParaByNetworkType",e);
			}
		}
		int[] result = new int[set.size()];
		int pos = 0;
		for(int value:set){
			result[pos++] = value;
		}
		this.mPageParaMap.put(networkType,result);
		return result;
	}
	
	
	/**
	 * 根据主网络类型,获取子网络参数（如根据LTE获取LTECA）
	 * @param networkType
	 * @return
	 */
	private void getSubTypeParmByid(String networkType,ArrayList<String> paras,int position){
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.LTECA)) {
			if (networkType.equals(CurrentNetState.LTE.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.LTECA.name())&& isQuery(this.paraList.get(position))) {
					setSubParamID(paras, position);
				}
			}
		}
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.LTE4T4R)) {
			if (networkType.equals(CurrentNetState.LTE.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.LTE4T4R.name())&& isQuery(this.paraList.get(position))) {
					setSubParamID(paras, position);
				}
			}
		}

		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.HspaPlus)) {
			if (networkType.equals(CurrentNetState.WCDMA.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.HspaPlus.name())&& isQuery(this.paraList.get(position))) {
					setSubParamID(paras, position);
				}
			}
		}
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.TDHspaPlus)) {
			if (networkType.equals(CurrentNetState.TDSCDMA.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.TDHspaPlus.name())&& isQuery(this.paraList.get(position))) {
					setSubParamID(paras, position);
				}
			}
		}
	}

	/**
	 * 设置特殊表格参数
	 * @param paras
	 * @param position
	 */
	public void setSubParamID(ArrayList<String> paras, int position) {
		if(this.paraList.get(position).getEspecialType() == Especial.TYPE_ONE){
			for (int i = 0; i < this.paraList.get(position).getEspecial().getTableRows().length; i++) {
				for (int j = 0; j < this.paraList.get(position).getEspecial().getTableRows()[i].getKeys().length; j++) {
					if(!paraList.get(position).getEspecial().getTableRows()[i].getKeys()[j].equals("FFFFFFFF")){
						paras.add(this.paraList.get(position).getEspecial().getTableRows()[i].getKeys()[j]);
					}
				}
			}
		}else{
			paras.add(this.paraList.get(position).getKey());
		}
	}
	
	
	/**
	 * 根据主网络类型,获取子网络参数（如根据LTE获取LTECA）
	 * @param networkType
	 * @return
	 */
	private void getSubTypeParm(String networkType,ArrayList<Parameter> paras,int position){
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.LTECA)) {
			if (networkType.equals(CurrentNetState.LTE.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.LTECA.name())&& this.paraList.get(position).isDynamicPara()) {

						paras.add(this.paraList.get(position));
					}
			}
		}
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.LTE4T4R)) {
			if (networkType.equals(CurrentNetState.LTE.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.LTE4T4R.name())&& this.paraList.get(position).isDynamicPara()) {
						paras.add(this.paraList.get(position));
					}
			}
		}
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.HspaPlus)) {
			if (networkType.equals(CurrentNetState.WCDMA.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.HspaPlus.name())&& this.paraList.get(position).isDynamicPara()) {
					paras.add(this.paraList.get(position));
				}
			}
		}
		if (ApplicationModel.getInstance().getNetList().contains(ShowInfoType.TDHspaPlus)) {
			if (networkType.equals(CurrentNetState.TDSCDMA.name())) {
				if (this.paraList.get(position).getNettype().getNetGroup().name().equals(ShowInfoType.TDHspaPlus.name())&& this.paraList.get(position).isDynamicPara()) {
					paras.add(this.paraList.get(position));
				}
			}
		}
		if(this.paraList.get(position).getNettype().name().equals(ShowInfoType.WLAN.name()) && this.paraList.get(position).isDynamicPara() && ApplicationModel.getInstance().isWifiOpen()){
					paras.add(this.paraList.get(position));
		}
	}
	
	
	
	public List<Parameter> getParametersByNetworkType(String networkType) {
		ArrayList<Parameter > paras = new ArrayList<Parameter>();
		for(int i=0;i<this.paraList.size();i++){
			if(this.paraList.get(i).getNettype().getNetGroup().name().equals(networkType)){
				paras.add(this.paraList.get(i) );
			}
		}
		return paras;
	}
	
	/**
	 * @return 当前表格要显示的参数名
	 * */
	public String[] getTableParameterNames(){
		ArrayList<String > names = new ArrayList<String>();
		for(int i=0;i<this.paraList.size();i++){
			if(this.paraList.get(i).isDynamicPara() ){
				names.add(this.paraList.get(i).getId() );
			}
		}
		String [] result = new String [names.size()];
		names.toArray(result);
		return result;
	}
	
	/**
	 * @return 参数列表中各个值是否显示在表格中，这里方便设置使用
	 * */
	public boolean[] getTableBooleans (){
		boolean [] result = new boolean [this.paraList.size()];
		for(int i=0;i<this.paraList.size();i++){
			result[i] = this.paraList.get(i).isChartView();
		}
		return result;
	}
	
	/**
	 * 设置各个参数是否要显示在表格中
	 * 此处为图表设置点击确认时调用
	 * */
	public void setTableBooleans(boolean [] checked){
		//修改内存中的值
		for(int i=0;i<this.paraList.size();i++){
			this.paraList.get(i).setChartView(checked[i] );
		}
		
		//修改文件中的值
		NodeList nodeList = getParamListByDoc();
		for(int i=0;i<nodeList.getLength();i++){
			Node node = nodeList.item(i);
			//node.getAttributes().getNamedItem("table").setNodeValue( checked[i]?"1":"0" );
			node.getAttributes().getNamedItem("DynamicPara").setNodeValue(
					getIsShowInTable(getAttributes(node, "Key", "")));
		}
		writer.writeToFile(doc);//写入文件
		//doc = writer.getDocument();
		
		//重新初始化当需要显示的参数列表
		initParameterNames();
	}
	
	/**
	 * 获取指定参数名是否显示在图表中的状态
	 * @param name
	 * @return
	 */
	private String getIsShowInTable(String name){
		String isShow = "0";
		for(Parameter para : this.paraList){
			if(para.getId().equals(name)){
				isShow = para.isDynamicPara() ? "1" : "0";
				break;
			}
		}
		return isShow;
	}
	
	/**
	 * 设置曲线的参数　
	 * @param line　选择哪一条曲线,0,1,2,3分别对应曲线1,曲线2,曲线3,曲线4
	 * @param parameter 参数名 
	 * */
/*	public void setChartLineParameter(int line,String parameter){
		
		if(line<0 || line >4){return;}
			
		//修改文件中的值
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("chartline").item(0);
		NodeList nodeList = elmentChartLine.getElementsByTagName("line");
		Node node = nodeList.item(line);
		node.getAttributes().getNamedItem("name").setNodeValue(parameter);
		
		writer.writeToFile(doc);//写入文件
		doc = writer.getDocument();
		
	}*/
	
	/**
	 * 曲线的参数　
	 * @param line　选择哪一条曲线,0,1,2,3分别对应曲线1,曲线2,曲线3,曲线4
	 * @param parameter 参数名 
	 * */
/*	public String getLineParameterName(int line){
		
		if(line<0 || line >4){return "null";}
			
		//修改文件中的值
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("chartline").item(0);
		NodeList nodeList = elmentChartLine.getElementsByTagName("line");
		Node node = nodeList.item(line);
		return node.getAttributes().getNamedItem("name").getNodeValue();
		
	}*/
	
	/**
	 * 设置曲线的参数　
	 * @param line　选择哪一条曲线,0,1,2,3分别对应颜色1,颜色2,颜色3,颜色4
	 * @param color 颜色值 
	 * */
	/*public void setChartLineColor(int line,int color){
		
		if(line<0 || line >4){return;}
		
		//修改内存中的值
		this.chartLineColors[line] = color;
		
		//修改文件中的值
		Element elmentChartLine = (Element) doc.getDocumentElement().getElementsByTagName("chartline").item(0);
		NodeList nodeList = elmentChartLine.getElementsByTagName("line");
		Node node = nodeList.item(line);
		node.getAttributes().getNamedItem("color").setNodeValue(String.valueOf(color));
		writer.writeToFile(doc);//写入文件
		//doc = writer.getDocument();
	}*/
	
	
	/**
	 * @return 返回GPS轨迹颜色
	 * */
	public int getGpsColor(){
		return this.gpsColor;
	}
	
	/**
	 * @return 返回选择的DT默认地图
	 * */
	public String getDtDefaultMap(){
		return this.dtDefaultMap;
	}
	
	/**
	 * @param color　要设置的GPS轨迹颜色值
	 * */
	public void setGpsColor(int color){
		this.gpsColor = color;
		
		doc.getElementsByTagName("gpscolor").item(0).getAttributes().getNamedItem("value")
			.setNodeValue( String.valueOf(color) );
		writer.writeToFile(doc);
		//doc = writer.getDocument();
	}
	
	/**
	 * @param name 要设置的默认值
	 * */
	public void setDtDefaultMap(String name){
		this.dtDefaultMap = name;
		
		doc.getElementsByTagName("dtDefaultmap").item(0).getAttributes().getNamedItem("value")
		.setNodeValue( name );
		writer.writeToFile(doc);
		//doc = writer.getDocument();
	}
	
	/**
	 * @return 当前GPS颜色在当前列表中的位置，方便在Spinner中引用selection
	 * */
	public int getGPSColorPosition(){
		Node node = doc.getElementsByTagName("gpscolor").item(0);
		switch( Integer.parseInt( node.getAttributes().getNamedItem("value").getNodeValue() ) ){
		case Color.GRAY:return 0;
		case Color.WHITE:return 1;
		case Color.DKGRAY:return 2;
		case Color.RED:return 3;
		case Color.YELLOW:return 4;
		case Color.GREEN:return 5;
		case Color.CYAN:return 6;
		case Color.BLUE:return 7;
		case Color.MAGENTA:return 8; 
		default: return 0;
		}
		
	}
	
	public int getPositionOfColor(int color){
		switch( color ){
		case Color.GRAY:return 0;
		case Color.WHITE:return 1;
		case Color.DKGRAY:return 2;
		case Color.RED:return 3;
		case Color.YELLOW:return 4;
		case Color.GREEN:return 5;
		case Color.CYAN:return 6;
		case Color.BLUE:return 7;
		case Color.MAGENTA:return 8; 
		default: return 0;
		}
	}
	
	/**
	 * 给定参数名称,返回参数信息对象<BR>
	 * [功能详细描述]
	 * @param shortName
	 * @return
	 */
	public Parameter getParameterByShortName(String shortName){
	    for (Parameter parameter : paraList) {
            if(parameter.getShowName().equals(shortName)){
                return parameter;
            }
        }
	    return null;
	}
	
	/**
	 * 通过参数ID获得相应参数对象
	 * @param paramId
	 * @return
	 */
	public Parameter getParameterById(String paramId){
		for (int i = 0; i < paraList.size(); i++) {
			Parameter parameter = paraList.get(i);
			if (parameter.getId().equals(paramId)) {
				return parameter;
			}
		}
		return null;
	}
	
	/**
	 * @return 给定参数名的位置
	 * @param paraName 参数名
	 * */
	public int getPositionOfParameter( String paraName){
		for(int i =0;i<parameterNames.length;i++){
			if(parameterNames[i].equals(paraName) ){
				return i ;
			}
		}
		return 0;
	}
	
	public int getColorOfPosition(int position){
		switch( position ){
		case 0:return Color.GRAY;
		case 1:return Color.WHITE;
		case 2:return Color.DKGRAY;
		case 3:return Color.RED;
		case 4:return Color.YELLOW;
		case 5:return Color.GREEN;
		case 6:return Color.CYAN;
		case 7:return Color.BLUE;
		case 8:return Color.MAGENTA;
		default:return Color.WHITE;
		}
	}

	/**
	 * 获取事件点的颜色值
	 * @param value 事件值
	 * @return      颜色值
	 */
	public int getGpsEventColor(double value){
		
    	if(mapParameter.getThresholdList().size() > 1){
    		for(int i=0;i<mapParameter.getThresholdList().size() - 1;i++){
    			//if(value <= mapParameter.getThresholdList().get(i).getValue()){
    			if(mapParameter.getThresholdList().get(i).getValueResult(value)){
    				return mapParameter.getThresholdList().get(i).getColor();
    			}
    		}
    		return mapParameter.getThresholdList().get(mapParameter.getThresholdList().size() - 1).getColor();
    	}
  		//当没有阀值设置时,应该随机产生15(可变)种颜色,每产生一个不同值则配一个颜色,直接所有颜色都分配完再重新替换
  		//return this.gpsColor;
  		return dynamicColors.getDynamicColor(value);
    }
    public int getGpsEventColor(Context context,double value){
        if(value == -9999){
            return Color.BLACK;
        } else if(mapParameter.getThresholdList().size() > 1){
    		for(int i=0;i<mapParameter.getThresholdList().size() - 1;i++){
    			//if(value <= mapParameter.getThresholdList().get(i).getValue()){
    			if(mapParameter.getThresholdList().get(i).getValueResult(value)){
    				return mapParameter.getThresholdList().get(i).getColor();
    			}
    		}
    		return mapParameter.getThresholdList().get(mapParameter.getThresholdList().size() - 1).getColor();
    	}else{
    		//当没有阀值设置时,应该随机产生15(可变)种颜色,每产生一个不同值则配一个颜色,直至所有颜色都分配完再重新替换
    		return context.getResources().getColor(dynamicColors.getDynamicColor(context,value));
    	}
    }
    
    
    public int getGpsEventColor(Context context,Parameter paramter,double value){
        if(value == -9999){
            return Color.GRAY;
        } else if(paramter.getThresholdList().size() > 1){
            for(int i=0;i<paramter.getThresholdList().size() - 1;i++){
                //if(value <= paramter.getThresholdList().get(i).getValue()){
            	if(paramter.getThresholdList().get(i).getValueResult(value)){
                    return paramter.getThresholdList().get(i).getColor();
                }
            }
            return paramter.getThresholdList().get(paramter.getThresholdList().size() - 1).getColor();
        }else{
            //当没有阀值设置时,应该随机产生15(可变)种颜色,每产生一个不同值则配一个颜色,直至所有颜色都分配完再重新替换
            return context.getResources().getColor(dynamicColors.getDynamicColor(context,value));
        }
    }
    
    private LegendColors dynamicColors = LegendColors.getInstance();
    
    /**是否显示图例*/
    public boolean isDisplayLegen(){
    	return this.isDisplayLegen;
    }
    
    /**设置 是否要显示图例*/
    public void setDisplayLegen(boolean disPlay){
    	this.isDisplayLegen = disPlay;
    	doc.getElementsByTagName("lengen").item(0)
		.getAttributes().getNamedItem("value").setNodeValue(disPlay?"1":"0");
    	writer.writeToFile( doc );
    }
    
    public boolean isMarkAccurately(){
    	if(ApplicationModel.getInstance().isSingleStationTest()&&ApplicationModel.getInstance().getSelectScene()== WalkStruct.SceneType.SingleSite)
    		return false;
    	return  this.isMarkAccutely;
    }
    
    /**设置 是否要精确打点*/
    public void setMarkAccurately(boolean accurately){
    	this.isMarkAccutely = accurately;
    	doc.getElementsByTagName("mark").item(0)
		.getAttributes().getNamedItem("value").setNodeValue(accurately?"1":"0");
    	writer.writeToFile(doc);
    }


    /**
     * @return the locusShape
     */
    public int getLocusShape() {
        return locusShape;
    }

    /**
     * @param locusShape the locusShape to set
     */
    public void setLocusShape(int locusShape) {
        this.locusShape = locusShape;
    }

    /**
     * @return the locusSize
     */
    public int getLocusSize() {
        return locusSize;
    }

    /**
     * @param locusSize the locusSize to set
     */
    public void setLocusSize(int locusSize) {
        this.locusSize = locusSize;
    }
    
    /**
     * 获得分布参数界面当前设置参数列表
     * @return
     */
    public Parameter[] getDistributionParams(){
    	return distributionParams;
    }
    
    /**
     * 分布参数修改时,将相应的参数对象存到分布参数列表中
     * 
     * @param sort	修改的分布参数序号0表示第一个,1表示第二个..
     * @param param	当前修改的参数对象
     */
    public void updateDistributionParams(int sort,Parameter param){
    	if(sort >= 0 && sort < 2){
    		setParameterAttribute(distributionParams[sort],"Distribution","0");
    		setParameterAttribute(param,"Distribution",String.valueOf(sort + 1));
    		writer.writeToFile(doc);
    		
    		distributionParams[sort] = param;
    	}
    }
    
    /**
	 * 保存单个parameter
	 * @param parameter
	 */
	public void setParameterAttribute(Parameter parameter,String attributeName,String value) {
		if(parameter != null){
			NodeList nodeList = getParamListByDoc();
	
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node_par = nodeList.item(i);
				
				if (getAttributes(node_par, "Key", "").equals(parameter.getId())) {
					if(node_par.getAttributes().getNamedItem(attributeName) == null){
						node_par.appendChild(doc.createElement(attributeName));
					}
					if(node_par.getAttributes().getNamedItem(attributeName) != null){
						node_par.getAttributes().getNamedItem(attributeName).setNodeValue(value);
					}
					break;
				}
			}
		}
	}
}