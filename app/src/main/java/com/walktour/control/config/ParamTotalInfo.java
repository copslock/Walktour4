package com.walktour.control.config;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.base.util.AppFilePathUtil;
import com.walktour.control.bean.MyXMLWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressLint("SdCardPath")
public class ParamTotalInfo {
//	private static final String tag = "ParamTotalInfo";
	private MyXMLWriter writer ;
	private Document doc ;//从文件读取到内存
	/**参数统计ID，最大最小值统计*/
	public static final int TotalType_Params= 1;
	/**事件统计ID*/
	public static final int TotalType_Event	= 2;
	/**覆盖质量统计,该参数为覆盖质量的主参数*/
	public static final int TotalType_Quality_Main = 3;
	/**覆盖质量统计，该参数为覆盖质量的从属参数*/
	public static final int TotalType_Quality_Assist=4;
	/**其它类型的统计参数，其结果除了与类型1统计最大最小外，还需要做特殊处理，如覆盖率运算等*/
	public static final int TotalType_Other	= 5;
	
	private static ParamTotalInfo sInstance;
	/***
	 * 所有需要统计的事件，参数列表
	 * 可以通过该列表获得参数的特殊属性
	 */
	private SparseArray<ParamInfo> allTotalInfo;
    /**从当前点获取实际值的参数ID*/
	private int[] mCurrentPointFilterTotalParas;
    /**从当前点获取继承值的参数ID*/
    private int[] mCurrentPointNoFilterTotalParas;
    /**从范围点内获取实际值的参数ID*/
    private int[] mFilterTotalParas;
    /**从范围点内获取继承值的参数ID*/
    private int[] mNoFilterTotalParas;
	/**所有需要统计的事件ID列表*/
	private ArrayList<Integer> allTotalEvents;
	private ArrayList<Integer> totalQualityIds;
	/**分段参数信息列表*/
	private List<ParamInfo> paramInfoList;
	/**分段阀段列表*/
	private SparseArray<ParamInfo> idList;
	/**其实特殊参数统计，该类型的参数统计需要特殊处理*/
	
	private ArrayList<WalkStruct.ShowInfoType> appList;
	
	public synchronized static ParamTotalInfo getInstance(){
		if(sInstance ==null){
			sInstance =new ParamTotalInfo();
		}
		return sInstance;
	}
	
	private ParamTotalInfo(){
		writer = new MyXMLWriter(AppFilePathUtil.getInstance().getAppConfigFile( "config_param_total.xml"));
		appList = ApplicationModel.getInstance().getNetList();
		initialParameter();
	}
	
	private void initialParameter(){
		doc = writer.getDocument();
		Element e = doc.getDocumentElement();
		NodeList nodelist = e.getElementsByTagName("par");
		//LogUtil.w(tag, "---nodelist.size="+nodelist.getLength());
		List<Integer> allTotalParas = new ArrayList<>();
		allTotalEvents= new ArrayList<>();
		totalQualityIds = new ArrayList<>();
		allTotalInfo  = new SparseArray<>();
		paramInfoList = new ArrayList<>();
		
		idList = new SparseArray<>();
		
		for(int i = 0;i<nodelist.getLength();i++){
			ParamInfo pi = new ParamInfo();
			Node node = nodelist.item(i);
			NamedNodeMap map = node.getAttributes();
			pi.netType   = map.getNamedItem("nettype").getNodeValue();
			//1.当前参数是所属的网络是否有权限
			if(isShowNet(Integer.parseInt(pi.netType))){
				pi.id		 = Integer.parseInt(map.getNamedItem("id").getNodeValue(),16);
				pi.paramName = map.getNamedItem("name").getNodeValue();
				pi.totalType = Integer.parseInt(map.getNamedItem("totalType").getNodeValue());
				if(map.getNamedItem("showName") != null){
					pi.paramShowName = map.getNamedItem("showName").getNodeValue();
				}
				if(map.getNamedItem("scale") != null){
					pi.scale = Float.parseFloat(map.getNamedItem("scale").getNodeValue());
				}
				
				//2.当前参数参数与统计时，是否限制业务类型
				if(map.getNamedItem("tasklimit")!= null){
					String[] totalType = map.getNamedItem("tasklimit").getNodeValue().split(",");
					pi.limitTask = new ArrayList<WalkStruct.TaskType>();
					for(String type : totalType){
						try{
							pi.limitTask.add(TaskType.valueOf(type));	
						}catch(Exception ee){
							ee.printStackTrace();
						}
					}
					
					//3.是否数据业务限制，仅在有限制业务统计中有效
					if(map.getNamedItem("isDataLimit") != null
							&& map.getNamedItem("isDataLimit").getNodeValue().equals("1")){
						pi.isDataLimit = true;
					}
					
					//4.关键字名列表只有在限制业务中有用到
					if(map.getNamedItem("keyname") != null){
						pi.taskKeyName = map.getNamedItem("keyname").getNodeValue().split(",");
					}
				}
				
				//5.当前参数是否在分段图表中显示
				pi.showThreshold = map.getNamedItem("showThreshold").getNodeValue().equals("1");
				if(pi.showThreshold){
					NodeList threshold = ((Element)node).getElementsByTagName("threshold");
					
					NodeList sublist = ((Element)threshold.item(0)).getElementsByTagName("item");
					pi.paramItemList  = new ArrayList<ParamItem>();
					for(int j=0;j<sublist.getLength();j++){
						ParamItem pim = new ParamItem();
						Node subnode = sublist.item(j);
						NamedNodeMap submap = subnode.getAttributes();
						pim.itemname = submap.getNamedItem("name").getNodeValue();
						pim.showName = submap.getNamedItem("showName").getNodeValue();
						pim.color = Integer.parseInt(submap.getNamedItem("color").getNodeValue());
						pim.value = Integer.parseInt(submap.getNamedItem("value").getNodeValue());
						pi.paramItemList.add(pim);
					}
					paramInfoList.add(pi);
					idList.put(pi.id,pi);
				}
				
				//6.设置当前参数是否通过继承值参与统计,默认的或都值不为0都表示不用继承值进行统计
				pi.isFilter = map.getNamedItem("isFilter") == null || !map.getNamedItem("isFilter").getNodeValue().equals("0");
				//7.设置当前是否仅查询当前指定值，不为空且值为1表示仅查询当前值
				pi.currentPoint = map.getNamedItem("currentPoint") != null && map.getNamedItem("currentPoint").getNodeValue().equals("1");
				
				//8.根据参数统计类型，将相关参数添加到对应的列表中，如事件统计列表，参数统计列表，带辅助参数的统计列表
				if(pi.totalType == TotalType_Event){
					allTotalEvents.add(pi.id);
				}else if(pi.totalType == TotalType_Quality_Main){
					//pi.assistParamList = new ArrayList<Integer>();
					//totalQualityIds.add(pi.id);
					allTotalParas.add(pi.id); 
				}else if(pi.totalType == TotalType_Quality_Assist){
					if(map.getNamedItem("mainKeyID") != null){
						int mainKeyid = Integer.parseInt(map.getNamedItem("mainKeyID").getNodeValue(),16);
						if(allTotalInfo.get(mainKeyid) != null){
							if(allTotalInfo.get(mainKeyid).assistParamList == null){
								allTotalInfo.get(mainKeyid).assistParamList = new ArrayList<Integer>();
							}
							allTotalInfo.get(mainKeyid).assistParamList.add(pi.id);
						}
					}
					
					//totalQualityIds.add(pi.id);
					allTotalParas.add(pi.id); 
				}else {
					//此数据列表包含的统计类型为1,5
				    allTotalParas.add(pi.id); 
                }
				allTotalInfo.put(pi.id, pi);
			}
		}
        Set<Integer> currentPointFilterSet = new HashSet<>();//从当前点获取继承值key
        Set<Integer> currentPointNoFilterSet = new HashSet<>();//从当前点获取实际值key
        Set<Integer> filterSet = new HashSet<>();//获取继承值key
        Set<Integer> noFilterSet = new HashSet<>();//获取实际值key
        for (int id : allTotalParas) {
            ParamInfo info = allTotalInfo.get(id);
            if (info.currentPoint) {
                if (info.isFilter)
                    currentPointFilterSet.add(id);
                else
                    currentPointNoFilterSet.add(id);
            } else {
                if (info.isFilter)
                    filterSet.add(id);
                else
                    noFilterSet.add(id);
            }
        }
        mCurrentPointFilterTotalParas = new int[currentPointFilterSet.size()];
        int i = 0;
        for (int key : currentPointFilterSet) {
            mCurrentPointFilterTotalParas[i++] = key;
        }
        mCurrentPointNoFilterTotalParas = new int[currentPointNoFilterSet.size()];
        i = 0;
        for (int key : currentPointNoFilterSet) {
            mCurrentPointNoFilterTotalParas[i++] = key;
        }
        mFilterTotalParas = new int[filterSet.size()];
        i = 0;
        for (int key : filterSet) {
            mFilterTotalParas[i++] = key;
        }
        mNoFilterTotalParas = new int[noFilterSet.size()];
        i = 0;
        for (int key : noFilterSet) {
            mNoFilterTotalParas[i++] = key;
        }
	}
	
	/**
	 * 判断当前参数所以的网络类型是否显示
	 * @param nettype
	 * @return
	 */
	private boolean isShowNet(int nettype){
		boolean isShow = false;
		for(WalkStruct.ShowInfoType showNet : appList){
			if(nettype == 0 || showNet.getNetType() == nettype){
				isShow = true;
				break;
			}
		}
		return isShow;
	}
	
	/**
	 * 返回图表显示参数列表
	 * @return
	 */
	public List<ParamInfo> getParamList(){
		return paramInfoList;
	}
	
	/**
	 * 获得分段参数ID列表
	 * @return
	 */
	public SparseArray<ParamInfo> getParaIdList(){
		return idList;
	}
	
	/***
	 * 返回从范围点内获取继承值的参数ID
	 * @return
	 */
	public int[] getFilterTotalParas(){
		return mFilterTotalParas;
	}
    /***
     * 返回从范围点内获取实际值的参数ID
     * @return
     */
    public int[] getNoFilterTotalParas(){
        return mNoFilterTotalParas;
    }
    /***
     * 返回从当前点获取继承值的参数ID
     * @return
     */
    public int[] getCurrentPointFilterTotalParas(){
        return mCurrentPointFilterTotalParas;
    }
    /***
     * 返回从当前点获取实际值的参数ID
     * @return
     */
    public int[] getCurrentPointNoFilterTotalParas(){
        return mCurrentPointNoFilterTotalParas;
    }

	/**
	 * 返回所有需要统计的事件的ID列表
	 * @return
	 */
	public ArrayList<Integer> getAllTotalEvents(){
		return allTotalEvents;
	}
	
	/**
	 * 所有需要统计的事件，参数列表
	 * 可以通过该列表获得参数的特殊属性 
	 * @return
	 */
	public SparseArray<ParamInfo> getAllTotalInfo(){
		return allTotalInfo;
	}

    /**
     * @return 覆盖质量统计id数组
     */
    public ArrayList<Integer> getTotalCoverageQualityIds() {
        return totalQualityIds;
    }

    /**
     * @param totalCoverageQualityIds 覆盖质量统计id数组
     */
    public void setTotalCoverageQualityIds(
            ArrayList<Integer> totalCoverageQualityIds) {
        this.totalQualityIds = totalCoverageQualityIds;
    }
}
