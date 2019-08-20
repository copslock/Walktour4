package com.walktour.license;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.base.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InitPower {
	private ApplicationModel appModel = ApplicationModel.getInstance();
	private final String tag = "InitPower";
	private SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());

	public int checkUserLicense(String appPath,String deviceId){
		int checkPowerSuccess = 0;
		String licensePath = appPath + "/license.dat";
		String sign = UtilsMethod.ReadFile(licensePath);
		if(sign != null && !sign.equals("")){	//如果授权文件不存在或者验证失败，则结束当前应用
			try{
				sign = new String(Base64.decode(sign));
			}catch(Exception e){
				e.printStackTrace();
				sign = "";
			}
			String[] checkInfo = sign.split("L@=##=@C");
			if(checkInfo.length == 3 ){
				String signs = checkInfo[0];
				String myKey = checkInfo[1];
				String power = checkInfo[2];
				String palinText = deviceId +  new String (Base64.decode(power));
				boolean checkPass = SignProvider.verify(myKey.getBytes(), palinText, signs.getBytes());
				if(checkPass){
					String myPower = new String(Base64.decode(power));
					String[] powers = myPower.split(";");
					if(powers.length == 4 || powers.length == 3){
						String timeLimit = powers[0];
						String[] appPowers = powers[1].split(",");
						String[] netPowers = powers[2].split(",");
						String[] jobPowers =null;
						if(powers.length == 4)
							jobPowers = powers[3].split(",");
						if(checkTimeLimit(timeLimit)){
							if(appPowers.length > 0 && appPowers.length <=10){
								if(initPowerList(appPowers,netPowers,jobPowers)){
									int activeTime = checkRemainTime(timeLimit);
									
									LogUtil.w(tag, "============activeTime 1:" + activeTime + "============");
                                    appModel.setActiveDate(timeLimit);
									appModel.setActiveTime(activeTime);
									//初始化权限列表成功
									appModel.setCheckPowerSuccess(true);
									checkPowerSuccess = WalkCommonPara.POWER_LINCESE_SUCCESS;
								}else{	//初始化用户权限失败
									checkPowerSuccess = WalkCommonPara.POWER_INIT_USERPOWER_FAILD;
								}
							}else{	//拥有应用权限无效
								checkPowerSuccess = WalkCommonPara.POWER_APPPOWER_FAILD;
							}
						}else{	//License鉴权超时
							checkPowerSuccess = WalkCommonPara.POWER_LICENSE_TIME_OUT;
						}
					}else{	//权限内容分组失败
						checkPowerSuccess = WalkCommonPara.POWER_POWER_GROUP_FAILD;
					}
				}else{	//文件内容监权失败
					checkPowerSuccess = WalkCommonPara.POWER_CONTENT_CHECK_FAILD;
				}
			}else{	//文件内容分组出错
				checkPowerSuccess = WalkCommonPara.POWER_CONTENT_GROUP_FAILD;
			}
		}else{	//授权文件不存在
			checkPowerSuccess = WalkCommonPara.POWER_LICENSE_NULL;
		}
		return checkPowerSuccess;
	}
	
	
	
	/* 检查当前license有效剩余天数 */
	private  int checkRemainTime(String timeLiimit) {
		int day = 0;
		try {
			long timeLimit = sdt.parse(timeLiimit).getTime();
			long nowTime = System.currentTimeMillis();
			day = (int) ((timeLimit - nowTime) / 3600000) / 24; // 共计小时数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}
	
	
	
	/*检查当前系统时间是否超出试用期限*/
	private boolean checkTimeLimit(String timeLiimit){
		Date currentDate = new Date();
		boolean isInTimes = false;
		try{
			if(currentDate.before(sdt.parse(timeLiimit))){
				isInTimes = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isInTimes;
	}
	
    /*初始化用户权限列表*/
    private boolean initPowerList (String[] appList,String[] netList,String[] taskList){
        appModel.addAppList(appList);
        appModel.addNetList(netList);
        appModel.addTaskList(taskList);
        return true;
    }
}
