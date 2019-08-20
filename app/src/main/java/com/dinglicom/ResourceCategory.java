package com.dinglicom;


/***
 * 资源库整合
 */
public class ResourceCategory {
    /**
     * 0为参数资源
     */
    public static int RESOURCETYPE_PARAM = 0;
    /**
     * 1为事件资源
     */
    public static int RESOURCETYPE_EVENT = 1;
    /**
     * 2为业务过程资源
     */
    public static int RESOURCETYPE_BUSINESS = 2;


    static {
        //加载资源库
        System.loadLibrary("ResourceCategory");
    }

    /**
     * 单例
     **/
    private static final ResourceCategory instance = new ResourceCategory();

    /**
     * 私有构造器,防止外部构造
     */
    private ResourceCategory()
    {
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static ResourceCategory getInstance()
    {
        return instance;
    }

    /**
     * 创建资源库句柄
     *
     * @param eResourceHandleType 资源类型
     * @param DataSetResourceDir  libDataSetResource.so所在路径
     * @param handle              需要返回的句柄值
     * @return 调用返回值
     */
    public native int CreateResourceHandle(int eResourceHandleType, String DataSetResourceDir, Integer handle);

    /***
     * 打开资源库
     * @param handle 资源库句柄
     * @param DBFileName 数据库名字
     * @param Password 密码
     * @return
     */
    public native int Open(int handle, String DBFileName, String Password);

    /**
     * 关闭资源库
     *
     * @param handle 句柄
     * @return
     */
    public native int Close(int handle);

    /**
     * 释放资源库
     *
     * @param handle 句柄
     * @return
     */
    public native int FreeResourceHandle(int handle);


    // 配置最大缓存个数，默认为100, -1表示全缓存
    public native int SetMaxCacheCount(int handle, int Value);

    // 其他功能属性配置
    public native int ConfigIntProperty(int handle, int nPropertyKey, long nPropertyValue);

    // ---------------------------------------------------------------------------------------
    // 1.2 业务过程、事件、参数、单位资源的分组接口公用 --------------------------------------
    // ---------------------------------------------------------------------------------------

    // 获取根节点
    public native int GetRootGroupCode(int handle, Integer nRootGroupCode);

    // 获取分组Code列表
    public native int[] GetGroupCodeList(int handle, int nGroupCode);

    // 获取分组名
    public native String GetGroupName(int handle, int nGroupCode);

    public native int GetGroupPriority(int handle, int nGroupCode, Integer nPriority);

    public native int GetParentGroupCode(int handle, int nGroupCode, Integer nParentGroupCode);

    public native boolean GroupCodeExist(int handle, int nGroupCode);

    // 增删改
    public native int UpdateGroupItem(int handle, int nGroupCode, int nParentGroupCode, int nPriority, String Name);

    public native int DeleteGroupItem(int handle, int nGroupCode);

    // ---------------------------------------------------------------------------------------
    // 1.3 业务过程、事件、参数资源Code所属分组接口-------------------------------------------
    // ---------------------------------------------------------------------------------------

    // 主码所属分组操作
    public native int UpdateCodeGroupItem(int handle, int nCode, int nParentGroupID, int nPriority);

    public native int DeleteCodeGroupItem(int handle, int nCode);

    // 一个组的列表
    public native int[] GetCodeListByGroupCode(int handle, int nGroupCode);

    // 取得参数所属分组
    public native int GetGroupCodeByCode(int handle, int nCode, Integer nGroupCode);

    public native int GetPriorityByCode(int handle, int nCode, Integer nPriority);

    // ---------------------------------------------------------------------------------------
    // 1.4 业务过程、事件、参数、单位资源的自身表接口公用-------------------------------------
    // ---------------------------------------------------------------------------------------

    // 判断Code是否有不可修改的标准资源
    public native boolean IsDataSetResourceCode
    (int handle, int nCode);

    // 判断Code是否存在
    public native boolean CodeExist(int handle, int nCode);

    // 取值
    public native int[] GetCodeList(int handle, String Condition);

    public native int GetIntValue(int handle, int nCode, int nCol, Integer nValue);

    public native int GetInt64Value(int handle, int nCode, int nCol, Integer nValue);

    public native int GetFloatValue(int handle, int nCode, int nCol, Integer nValue);

    public native byte[] GetBufferValue(int handle, int nCode, int nCol);

    // 增删改
    public native int DeleteItem(int handle, int nCode);

    // ---------------------------------------------------------------------------------------
    // 2 参数资源特有接口 --------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    //struct tag_ParamItemRec
    //{
    //	int NetType;
    //	bool IsVisible;
    //	bool IsStatisticsParam;
    //
    //	int ParamValueType;
    //	int FilterType;
    //	int StatisticsType;
    //	int PaintShape;
    //
    //	int PaintSize;
    //	int DefaultColor;
    //
    //	//附加
    //	int SystemUnit;	// 参数值的进制显示
    //	int IntPlaceholder;	//整数的固定占位显示
    //	int DecimalDigits;	//显示小数位个数
    //};
    // 参数修改结构以字符串传入，以@@分隔，bool类型传0/1
    public native int UpdateParamItem(int handle, int nParamCode, String AliasName, String ParamItemStruct);

    //struct tag_ParamStandardInfoRec
    //{
    //	int UnitCode;
    //	int Scale;
    //	Int64 MinValue;
    //	Int64 MaxValue;
    //	int ValueType;	//参数值类型
    //	int IsValueTransition;	//是否要值转换
    //};
    // 标准信息，结构体传入同上
    public native int UpdateCustomParamStandardInfoItem(int handle, int nParamCode, String Name, String ParamStandardInfoStruct);

    public native int DeleteCustomParamStandardInfoItem(int handle, int nParamCode);

    public native boolean ParamStandardInfoExist(int handle, int nParamCode);

    /***
     * 返回格式：UnitCode@@Scale@@MinValue@@MaxValue@@ValueType@@IsValueTransition
     * @param handle
     * @param nParamCode
     * @return
     */
    public native String GetParamStandardInfo(int handle, int nParamCode);

    /***
     * 获取参数标准名称
     * @param handle
     * @param nParamCode
     * @return
     */
    public native String GetParamStandardName(int handle, int nParamCode);
    // 获取参数值的转义内容
    public native String GetParamValueParseMeaning(int handle, int nParamCode, long llValue);
    // 分段阀值增删改
    public native int UpdateThresholdItem(int handle, int nParamCode, int nThresholdCode, int nDisplayUnitCode, int nOrderMode, int nExtraColor, String Name);

    // pBuffer的格式为：ThresholdCount(int) + tag_ThresholdItemVisitRec[0]...+ tag_ThresholdItemVisitRec[ThresholdCount -1]
    //C_API int UpdateThresholdBuffer(void *pHandle, int nParamCode, int nThresholdCode,
    //								const unidst::tag_ThresholdItemVisitRec *pBuffer, int nSize);

    public native int DeleteThresholdItem(int handle, int nParamCode, int nThresholdCode);

    public native boolean ParamThresholdCodeExist(int handle, int nParamCode, int nThresholdCode);

    // 列出一个参数的所有ThresholdCode列表

    /***
     * 列出一个参数的所有阈值
     * @param handle 句柄
     * @param nParamCode 参数Key
     * @return 阈值集合
     */
    public native int[] GetThresholdCodeList(int handle, int nParamCode);

    /***
     * 获取每个阈值的名称
     * @param handle 句柄
     * @param nParamCode 参数key
     * @param nThresholdCode 阈值
     * @return 阈值名称
     */
    public native String GetThresholdName(int handle, int nParamCode, int nThresholdCode);

    //暂时忽略这个接口
    public native int GetThresholdInfo(int handle, int nParamCode, int nThresholdCode, Integer nDisplayUnitCode, Integer nOrderMode);

    /***
     * 获取阈值的分段个数
     * @param handle 句柄
     * @param nParamCode 参数Key
     * @param nThresholdCode 阈值code
     * @param nValue 阈值个数
     * @return
     */
    public native int GetThresholdCount(int handle, int nParamCode, int nThresholdCode, Integer nValue);

    /***
     * 获取阈值一个分段的颜色
     * @param handle 句柄
     * @param nParamCode 参数Key
     * @param nThresholdCode 阈值code
     * @param nIndex 阈值第几个分段
     * @param nColor 返回颜色
     * @return
     */
    public native int GetThresholdItemColor(int handle, int nParamCode, int nThresholdCode, int nIndex, Integer nColor);

    /**
     * 获取阈值一个分段的信息
     * @param handle
     * @param nParamCode
     * @param nThresholdCode
     * @param nIndex
     * @param nllLeftValue
     * @param nllRightValue
     * @param nbLeftInclude
     * @param nbRightInclude
     * @return
     */
    public native int GetThresholdItemInfo(int handle, int nParamCode, int nThresholdCode, int nIndex, Long nllLeftValue, Long nllRightValue, Boolean nbLeftInclude, Boolean nbRightInclude);

    /***
     * 获取阈值一个分段的备注信息
     * @param handle 句柄
     * @param nParamCode 参数key
     * @param nThresholdCode 阈值code
     * @param nIndex 第几个分段
     * @return 分段备注信息
     */
    public native byte[] GetThresholdItemNote(int handle, int nParamCode, int nThresholdCode, int nIndex);

    // 查找值所在区间ItemIndex
    public native int GetThresholdItemIndexByValue(int handle, int nParamCode, int nThresholdCode, double Value, Integer nItemIndex);

    // 单位转换
    public native int TranslateUnit(int handle, int nFromUnit, int nToUnit, double dFromeValue, Double dToValue);

    // 获取单位列表
    public native int[] GetSameGroupUnitCodeList(int handle, int nGroupCode);

    /***
     * 取得单位
     * @param handle 句柄
     * @param nUnitCode 从unitcode获取实际的单位
     * @return 单位
     */
    public native String GetUnitName(int handle, int nUnitCode);

    // 取得单位种类Code
    public native int[] GetUnitGroupCodeList(int handle);

    // 取得单位分类名
    public native String GetUnitGroupName(int handle, int nUnitGroupCode);

    // 取得nUnitCode所属分组nUnitGroupCode
    public native int GetUnitGroupCode(int handle, int nUnitCode, Integer nUnitGroupCode);

    // ---------------------------------------------------------------------------------------
    // 3 事件资源特有接口 --------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    //struct tag_EventItemRec
    //{
    //	int TextColor;
    //	bool IsVisible;
    //	char AliasName[g_Parameter_CharArrayMaxLength];
    //	char IconFileName[g_Parameter_CharArrayMaxLength];
    //};
    // 事件属性更新
    public native int UpdateEventItem(int handle, int nEventCode, String EventItemStruct, byte[] ExtendBuffer);

    // 事件标准信息
    public native int UpdateCustomEventStandardInfoItem(int handle, int nEventCode, String Name);

    public native int DeleteCustomEventStandardInfoItem(int handle, int nEventCode);

    public native String GetEventStandardName(int handle, int nEventCode);

    public native boolean EventStandardInfoExist(int handle, int nEventCode);

    // ---------------------------------------------------------------------------------------
    // 4 业务过程资源特有接口 ----------------------------------------------------------------
    // ---------------------------------------------------------------------------------------

    // 业务过程属性更新
    public native int UpdateSPItem(int handle, int nSPCode, int nTextColor, boolean IsVisible, String AliasName);

    // 业务过程标准信息
    public native String GetSPStandardName(int handle, int nSPCode);

    public native boolean SPStandardInfoExist(int handle, int nSPCode);

    // 随机离散颜色
    public native int GetRandomDiscreteColorCount(int handle, Integer nCount);

    public native int GetRandomDiscreteColor(int handle, int nIndex, Integer nColor);

    // 事务
    public native int StartTransaction(int handle);

    public native int CommitTransaction(int handle);

    public native int RollbackTransaction(int handle);
}
