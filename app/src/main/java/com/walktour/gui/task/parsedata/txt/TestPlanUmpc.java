package com.walktour.gui.task.parsedata.txt;

import android.annotation.SuppressLint;

import com.walktour.Utils.UtilsMethod;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.ParallelServiceTestConfig;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

@SuppressLint("DefaultLocale")
public class TestPlanUmpc { 
	//读取文件有关
	private File file;
	private BufferedReader bfReader;
	private ArrayList<TaskModel> taskList = new ArrayList<TaskModel>();//测试任务类
	private int testTaskCount 	= 0;
	private int autoUpload 	  	= 0;
	public TestPlanUmpc(String filePath){
		this.file = new File(filePath);
		
		FileInputStream inStream =null;
		try {
			inStream = new FileInputStream(file);
			bfReader = new BufferedReader(  new InputStreamReader(inStream));
		 
				String line ;
				
				//文件从头到尾每行只读一次
				while( (line = bfReader.readLine() )!=null){
					if(line.contains("autoupload")){
						autoUpload = Integer.parseInt(line.split("=")[1]);
					}else if(line.contains("TestTaskCount" )){
						testTaskCount = Integer.parseInt( line.split("=")[1] );
						break;
					}
				}
				//读取所有测试任务的引用
				for(int i=0;i<this.testTaskCount;i++){
					//测试任务类：未指定测试类型 
					TaskModel taskModel = new TaskModel();
					line = bfReader.readLine();
					taskModel.setTag( "["+line.split("=")[1] +"]" );//对应test_task.txt中的TestTask(i)=****;
					line = bfReader.readLine();
					taskModel.setRepeat( Integer.parseInt( line.split("=")[1] ) );//记下该测试任务的重复次数
					taskList.add( taskModel );
				}
				/**
				 * 把文件中的测试任务生成模型列表
				 * 测试任务TestTasks对应test_task.xml中<TestTasks>以下的所有内容
				 */
				TestTasks tasks = new TestTasks(file,true);
				ArrayList<TaskModel> modelList =  tasks.getTestTasks();
				if(modelList.size()==0){ 
					taskList.clear();
				}
				//把模型的列表modelList复制到taskList中
				for(int i=0;i<modelList.size();i++){
					String modelTag = modelList.get(i).getTag();
					for(int j=0;j<taskList.size();j++){
						String taskTag = taskList.get(j).getTag();
						if( taskTag .equals( modelTag ) ){
							int repeat = taskList.get(j).getRepeat();
							/*//2012.3.20http的登陆次数作为原来的repeat
							if( modelList.get(i).getTaskType().equals( WalkStruct.TaskType.Http.name() ) ){
								TaskHttpModel httpModel =  (TaskHttpModel) modelList.get(i) ;
								repeat = httpModel.getLogonCount();
							}*/
							modelList.get(i).setRepeat( repeat );
							taskList.remove(j);
							taskList.add( modelList.get(i) );
						}
					}
				}
				
				/**
				 * 处理pad或者是统一平台任务没有解析
				 */
				for(int i=taskList.size()-1;i>=0;i--){
					if (taskList.get(i).getTaskName() == null){
						taskList.remove(i);
					}
				}
				
				
				for(int i=0;i<taskList.size();i++){
					if(taskList.get(i).getIsRab()==1){
						addDetailToRabModel(taskList.remove(i));
						i--;
					}
				}
				
		 
		}catch(Exception fe){
			fe.printStackTrace();
		}finally{
			try{
				if(null!=inStream){
				inStream.close();
				inStream=null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * [将详细任务的model保存到并发对象]<BR>
	 * [功能详细描述]
	 * @param model
	 */
	private void addDetailToRabModel(TaskModel model){
		for(int i=0;i<taskList.size();i++){
			if(taskList.get(i).getTaskType().equals(WalkStruct.TaskType.MultiRAB.name())&& taskList.get(i).getRabName().equals(model.getRabName())){
				TaskRabModel taskRabModel = ((TaskRabModel)taskList.get(i));
				String detailRabName=((TaskRabModel)taskList.get(i)).getTaskName()+"%"+model.getTaskName();//为并发提供名字拼接“并发名+%+子任务名”
				model.setTaskName(detailRabName);
				if (taskRabModel.getParallelServiceTestConfig().getRabStartMode() != 0){
					rabNewRuleFromIpad(model, taskRabModel); 
				}
				taskRabModel.addTaskList(model);
			}
		}
	}

	/**
	 * 并发新规则，下发特殊处理
	 * @param model
	 * @param taskRabModel
	 */
	@SuppressLint("DefaultLocale")
	public void rabNewRuleFromIpad(TaskModel model, TaskRabModel taskRabModel) {
		ArrayList<RabIpadMapModel> rabIpadMapModels = rabMapStrList(taskRabModel.getParallelServiceTestConfig().getRabStartMode() == ParallelServiceTestConfig.RAB_STATE_MODEL_EVENT_STATE ? taskRabModel.getRefTask() + ":" +  taskRabModel.getRefServiceIndex():taskRabModel.getRabRule());
		boolean flag = false;
		for (int j = 0; j < rabIpadMapModels.size() && !flag; j++) {
			String[] taskTypeArray = RabByNewRule.getTaskTypeArray(rabIpadMapModels.get(j).getIpadTaskType());
			if (taskTypeArray != null){
				for (int k = 0; k < taskTypeArray.length; k++) {
					if(model.getTaskType().toString().equalsIgnoreCase(taskTypeArray[k].toString())){
						String timeStr = UtilsMethod.htmlToStr(rabIpadMapModels.get(j).getValue());
						switch (taskRabModel.getParallelServiceTestConfig().getRabStartMode()) {
						case ParallelServiceTestConfig.RAB_STATE_MODEL_RELATIVE_TIME:
							model.setRabRelTime(timeStr);
							break;
						case ParallelServiceTestConfig.RAB_STATE_MODEL_ABSOLUTELY_TIME:
							model.setRabRuelTime(UtilsMethod.convertSecondToHHmm(Integer.valueOf(timeStr)));
							break;
						case ParallelServiceTestConfig.RAB_STATE_MODEL_EVENT_STATE:
							taskRabModel.getParallelServiceTestConfig().setReferenceService(model.getTaskType().toString());
							taskRabModel.getParallelServiceTestConfig().setStartState(WalkStruct.RabByStateServiceId.getEventIDByServiceIndex(model.getTaskType().toString(),taskRabModel.getRefServiceIndex()));
							break;
						default:
							break;
						}
						flag = true;
						break;
					}
				}
			}else {
				if( TaskType.valueOf(model.getTaskType()).getTypeName().toLowerCase().indexOf(UtilsMethod.htmlToStr(rabIpadMapModels.get(j).getIpadTaskType()).toLowerCase()) != -1){
					String timeStr = UtilsMethod.htmlToStr(rabIpadMapModels.get(j).getValue());
					switch (taskRabModel.getParallelServiceTestConfig().getRabStartMode()) {
					case ParallelServiceTestConfig.RAB_STATE_MODEL_RELATIVE_TIME:
						model.setRabRelTime(timeStr);
						break;
					case ParallelServiceTestConfig.RAB_STATE_MODEL_ABSOLUTELY_TIME:
						model.setRabRuelTime(UtilsMethod.convertSecondToHHmm(Integer.valueOf(timeStr)));
						break;
					default:
						break;
					}
					flag = true;
					break;
				}
			}
			
		}
	}
	
	/**
	 * 根据字符串转为Model
	 * @param rabMapStr
	 * @return
	 */
	private ArrayList<RabIpadMapModel> rabMapStrList(String rabMapStr){
		ArrayList<RabIpadMapModel> list = new ArrayList<TestPlanUmpc.RabIpadMapModel>();
		if(rabMapStr.length() != 0){
			String[] rabMapStrArray =  rabMapStr.split(",");
			for (int i = 0; i < rabMapStrArray.length; i++) {
				String[] subArray = rabMapStrArray[i].split(":");
				RabIpadMapModel rabIpadMapModel = new RabIpadMapModel();
				rabIpadMapModel.setIpadTaskType(subArray[0]);
				rabIpadMapModel.setValue(subArray[1]);
				list.add(rabIpadMapModel);
			} 
		}
		return list;
	}
	
	/*
	 * 招标并发新规则
	 * 组装匹配业务
	 */
	public static enum RabByNewRule{
		Call("call",TaskType.InitiativeCall.name(),TaskType.PassivityCall.name()),
		FTPUpload("FTPUpload",TaskType.FTPUpload.name()),
		FTPDownload("FTPDownload",TaskType.FTPDownload.name()),
		VideoPlay("VideoPlay",TaskType.HTTPVS.name()),
		SMSSend("sms",TaskType.SMSSend.name(),TaskType.SMSIncept.name(),TaskType.SMSSendReceive.name()),
		HTTPPage("HTTP",TaskType.Http.name(),TaskType.HttpDownload.name(),TaskType.HttpRefurbish.name()),
		HTTPUpload("httpUpload",TaskType.HttpUpload.name()),
		EmalSend("Email",TaskType.EmailSmtp.name(),TaskType.EmailPop3.name(),TaskType.EmailSmtpAndPOP.name());
		
		private String ipadTaskType;
		private String[] taskType;
		private RabByNewRule(String ipadTaskType,String... taskType){
			this.ipadTaskType = ipadTaskType;
			this.taskType = taskType;
		}
		
		
		public String getIpadTaskType() {
			return ipadTaskType;
		}

		public void setIpadTaskType(String ipadTaskType) {
			this.ipadTaskType = ipadTaskType;
		}


		public String[] getTaskType() {
			return taskType;
		}

		public void setTaskType(String[] taskType) {
			this.taskType = taskType;
		}



		public static String[] getTaskTypeArray(String ipadSendStr){
			String[] array = null;
			for (int i = 0; i < RabByNewRule.values().length; i++) {
				if(UtilsMethod.htmlToStr(ipadSendStr).equals(RabByNewRule.values()[i].getIpadTaskType())){
					array =  RabByNewRule.values()[i].getTaskType();
					break;
				}
			}
			return array;
		}
		
		
	}
	
	
	class RabIpadMapModel {
		private String ipadTaskType;
		private String value;

		public String getIpadTaskType() {
			return ipadTaskType;
		}

		public void setIpadTaskType(String ipadTaskType) {
			this.ipadTaskType = ipadTaskType;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
	
	
	/**
	 * @return 设备下的所有测试任务
	 * */
	public ArrayList<TaskModel> getTaskList(){
		return this.taskList;
	}
	
	/*是否测试完成自动上传*/
	public boolean isAutoUpload(){
		return autoUpload == 1;
	}
}
