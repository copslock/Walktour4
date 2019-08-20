package com.walktour.gui.task;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.WalkCommonPara;
import com.walktour.Utils.WalkStruct;
import com.walktour.Utils.WalkStruct.RabByStateServiceId;
import com.walktour.Utils.WalkStruct.TaskType;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.framework.view.EditTextWatcher;
import com.walktour.gui.R;
import com.walktour.gui.task.parsedata.TaskListDispose;
import com.walktour.gui.task.parsedata.model.base.TaskModel;
import com.walktour.gui.task.parsedata.model.task.multirab.TaskRabModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * [一句话功能简述]<BR>
 * [并发任务界面与并发列表]
 * 
 * @author zhihui.lian@dinglicom.com
 * @version [WalkTour Client, 2012-8-09]
 */

@SuppressLint({ "InflateParams", "UseSparseArrays" })
public class TaskMultiRAB extends BaseTaskActivity implements OnItemClickListener, OnItemLongClickListener {
	TaskListDispose taskd = null;
	TaskRabModel model = null;
	private int taskListId = -1;
	private boolean isNew = true;
	private MultiRABTaskAdapter multiRABTaskAdapter;// 并发列表适配器
	private listAdapter listAdapter; // 弹出框列表适配器
	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>(); // 记录checkbox的位置
	private TaskListDispose taskListDispose;// 任务解析对象
	private TaskRabModel rabModel;

	private EditText taskNameEditText;
	private Spinner pppruleSpinner;// 拨号规则Spinner
	private EditText repeatEditText;
	private EditText intervalEditText;
	private Button btn_ok;
	private List<TaskModel> array;
	private EditText dialDelayEditText;
	private ListView multirab_listview;
	private Button bt_reference;
	private Button bt_taskNew;
	private Button btn_cencle;
	private EditText singleParallelTimeout;
	private PopupWindow treePopupWindow;
	private ListView list01;
	private boolean isFinish = false;
	private ArrayList<TaskModel> cloneRabModel;
	private DisplayMetrics metric;

	private Spinner startMode_spn; // 启动方式
	private Spinner spn_refTask; // 参考业务
	private Spinner spn_startstate; // 启动状态

	private Object[] lock = new Object[0];

	private ApplicationModel appModel = ApplicationModel.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskd = TaskListDispose.getInstance();
		array = taskd.getTaskListArray();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("taskListId")) {
			taskListId = bundle.getInt("taskListId");
			model = (TaskRabModel) taskd.getTaskListArray().get(taskListId);
			abstModel = model;
			isNew = false;
		}
		taskListDispose = TaskListDispose.getInstance();
		showView();
		initEstRabModel();
		initRabModel();
		metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
	}

	@SuppressWarnings("unchecked")
	private Handler mhHandler = new Handler(new Handler.Callback() {// 接收adapter发送过来的消息

				@Override
				public boolean handleMessage(Message msg) {
					if (msg.what == 1) {
						new Thread(new SetListViewHeightBasedOnChildren()).start(); // 处理listview嵌套
						ArrayList<TaskModel> taskModels = (ArrayList<TaskModel>) msg.obj;
						synchronized (lock) {
							rabModel.addSubTaskModels(taskModels);
						}
					}
					return true;
				}
			});

	Boolean isFristCome = true;
	private LinearLayout rabLayoutV2G;

	private void showView() {
		// 绑定Layout里面的ListView
		setContentView(R.layout.task_multirab);
		(initTextView(R.id.title_txt)).setText(R.string.act_task_multirab);// 设置标题
		(initTextView(R.id.task_sort_title_txt)).setText(R.string.rab_rabRule_str);// 设置标题
		(initImageView(R.id.pointer)).setOnClickListener( // 标题栏返回按钮处理
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (rabModel.getTaskModel().size() <= 1 && model != null) { // 如果并发里头没有并发子任务直接移除创建的临时对象
							array.remove(taskListId);
							taskd.setTaskListArray(array);
						}
						TaskMultiRAB.this.finish();
					}
				});
		TextView tv_taskName = initTextView(R.id.txt_taskname);
		TextView tv_taskCount = initTextView(R.id.txt_taskCount);
		TextView tv_intervalTime = initTextView(R.id.txt_intervaltime);
		TextView tv_pppRule = initTextView(R.id.txt_pppRule);
		TextView tv_dialDelay = initTextView(R.id.txt_dialDelay);
		taskNameEditText = initEditText(R.id.edit_taskname);// 编辑任务名
		repeatEditText = initEditText(R.id.edit_taskCount);// 重复次数
		intervalEditText = initEditText(R.id.edit_intervalTime);// 呼叫间隔时间
		dialDelayEditText = initEditText(R.id.edit_dialDelay);// 语音业务延时
		singleParallelTimeout = initEditText(R.id.single_parallel_timeout_edt);// 单次并发超时
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		btn_cencle = (Button) findViewById(R.id.btn_cencle);
		btn_cencle.setOnClickListener(this);
		tv_taskName.setText(getString(R.string.task_taskName));
		tv_taskCount.setText(getString(R.string.task_multirab_taskcount));
		tv_intervalTime.setText(getString(R.string.task_multirab_intervaltime));
		tv_pppRule.setText(getString(R.string.task_multirab_ppprule));
		tv_dialDelay.setText(getString(R.string.task_multirab_dialdelay));

		bt_reference = initButton(R.id.reference_task); // 引用
		bt_reference.setOnClickListener(this);
		bt_taskNew = initButton(R.id.new_task);// 新建
		bt_taskNew.setOnClickListener(this);

		multirab_listview = (ListView) findViewById(R.id.multirab_listview);
		multirab_listview.setEmptyView(findViewById(R.id.emptyList));// set一个空view,加入listview提示语
		pppruleSpinner = (Spinner) findViewById(R.id.edit_pppRule);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.array_task_disconnect));
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		pppruleSpinner.setAdapter(adapter);
		// *************************华丽分割线*****************************
		startMode_spn = (Spinner) findViewById(R.id.spn_startMode);
		ArrayAdapter<String> startModeAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				getResources().getStringArray(R.array.taskrab_byMode_array));
		startModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		startMode_spn.setAdapter(startModeAdapter);
		rabLayoutV2G = (LinearLayout) findViewById(R.id.rab_time_layout_V2G);
		startMode_spn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 == 3) {
					rabLayoutV2G.setVisibility(View.VISIBLE);
				} else {
					rabLayoutV2G.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		spn_refTask = (Spinner) findViewById(R.id.spn_refTask);
		ArrayAdapter<String> refTaskAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_custom_layout,
				RabByStateServiceId.getRabTaskName());
		refTaskAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spn_refTask.setAdapter(refTaskAdapter);

		spn_startstate = (Spinner) findViewById(R.id.spn_startstate);
		spn_refTask.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String[] taskIDNameArray = RabByStateServiceId.getRabTaskNameByID(
						RabByStateServiceId.getRabTaskName()[arg2], TaskMultiRAB.this);
				ArrayAdapter<String> taskIDAdapter = new ArrayAdapter<String>(TaskMultiRAB.this,
						R.layout.simple_spinner_custom_layout, taskIDNameArray);
				taskIDAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				spn_startstate.setAdapter(taskIDAdapter);
				if (model != null && isFristCome == true) {
//					spn_startstate.setSelection(RabByStateServiceId.getIndexByServiceEventID(
//							model.getParallelServiceTestConfig().getReferenceService(), model.getParallelServiceTestConfig().getStartState()));
					spn_startstate.setSelection(model.getParallelServiceTestConfig().getStartState());
					isFristCome = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				System.out.println("----onNothingSelected----");
			}
		});

		// **********************************************************
		btn_ok.setText(" " + getString(R.string.str_save) + " ");
		btn_cencle.setText(getString(R.string.str_cancle));

		if (model != null) {
			taskNameEditText.setText(model.getTaskName());
			repeatEditText.setText(model.getRepeat() + "");
			intervalEditText.setText(model.getInterVal() + "");
			dialDelayEditText.setText(model.getVoiceDelay() + "");
			singleParallelTimeout.setText(String.valueOf(model.getSingleParallelTimeout()));
			pppruleSpinner.setSelection(model.getDisConnect());
			startMode_spn.setSelection(model.getParallelServiceTestConfig().getRabStartMode());
			spn_refTask.setSelection(RabByStateServiceId.getTaskIndexByService(model.getParallelServiceTestConfig().getReferenceService()));
			spn_startstate.setSelection(model.getParallelServiceTestConfig().getStartState());
		} else {
			taskNameEditText.setText("Parallel Service");
			repeatEditText.setText("10");
			intervalEditText.setText("15");
			dialDelayEditText.setText("5");
			singleParallelTimeout.setText("0");
		}
	}

	/**
	 * 
	 * [获取并发对象]<BR>
	 * [功能详细描述]
	 */
	private void initRabModel() {
		rabModel = new TaskRabModel();
		List<TaskModel> objs = taskListDispose.getTaskListArray();
		if (model != null) {
			for (int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getTaskID() == model.getTaskID()){
					rabModel = ((TaskRabModel) objs.get(i));
					taskListId = i;
					break;
				}
			}
		}
	}

	/**
	 * 
	 * [初始化最原始的并发对象]<BR>
	 * [功能详细描述] 重写主要保证克隆之前的对象不被重复调用，所有重新获取了并发对象
	 */
	@SuppressWarnings("unchecked")
	private void initEstRabModel() {
		TaskRabModel estRabModel = new TaskRabModel();
		ArrayList<TaskModel> objs = (ArrayList<TaskModel>) taskListDispose.getTaskListArray();
		ArrayList<TaskModel> objss = (ArrayList<TaskModel>) objs.clone();
		if (model != null) {
			for (int i = 0; i < objss.size(); i++) {
				if(objss.get(i).getTaskID() == model.getTaskID()){
					estRabModel = ((TaskRabModel) objss.get(i));
					taskListId = i;
					break;
				}
			}
		}
		try {
			cloneRabModel = (ArrayList<TaskModel>) estRabModel.getTaskModel().clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重写返回按钮捕捉事件，做返回移除创建的临时对象
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (appModel.isTestJobIsRun()) {
				TaskMultiRAB.this.finish();
				return true;
			}
			BasicDialog.Builder builder = new BasicDialog.Builder(this);
			builder.setTitle(R.string.task_dialog_title);
			builder.setMessage(R.string.task_dialog_content);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isFinish = true;
					saveTestTask();
					if (model != null) {
						isNew = false;
					}
				}
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// if(rabModel.getTaskModel().size()<=1&&model!=null){
					// //如果并发里头没有并发子任务直接移除创建的临时对象
					// array.remove(taskListId);
					// taskd.setTaskListArray(array);
					// }
					if (cloneRabModel.size() == 1 || cloneRabModel.size() == 0 && model != null) {
						if (array.size() > taskListId && taskListId >= 0) {
							array.remove(taskListId);
						}
						taskd.setTaskListArray(array);
						TaskMultiRAB.this.finish();
					} else { // 将初始对象设置回未修改，解决未保存
						rabModel.setTaskModelList(cloneRabModel);
						// System.out.println("cloneRabModel>>>>>>>>>>>>>>>"+cloneRabModel.size());
						TaskMultiRAB.this.finish();
					}
				}
			});
			builder.show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * 保存任务<BR>
	 * [功能详细描述]
	 */
	public void saveTestTask() {
		if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullName, Toast.LENGTH_SHORT).show();
			taskNameEditText.setError(getString(R.string.task_alert_nullName));
			return;
		} else if (repeatEditText.getText().toString().trim().length() == 0
				|| repeatEditText.getText().toString().trim().equals("0")) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullRepeat, Toast.LENGTH_SHORT).show();
			repeatEditText.setError(getString(R.string.task_alert_nullRepeat));
			return;
		} else if (intervalEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInterval, Toast.LENGTH_SHORT);
			intervalEditText.setError(getString(R.string.task_alert_nullInterval));
			return;
		}
		if (intervalEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nulKeepTime, Toast.LENGTH_SHORT).show();
			return;
		} else if (intervalEditText.getText().toString().trim().equals("0")
				|| intervalEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullTimeout, Toast.LENGTH_SHORT).show();
			intervalEditText.setError(getString(R.string.task_alert_nullTimeout));
			return;
		} else if (dialDelayEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nulldialDelay, Toast.LENGTH_SHORT).show();
			dialDelayEditText.setError(getString(R.string.task_alert_nulldialDelay));
			return;
		} else if (singleParallelTimeout.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplicationContext(), R.string.task_alert_nullInut, Toast.LENGTH_SHORT).show();
			singleParallelTimeout.setError(getString(R.string.task_alert_nullInut));
			return;
		}
		if (model == null) {
			model = new TaskRabModel();
			taskd.setCurrentTaskIdAndSequence(model);
		}
		model.setTaskName(taskNameEditText.getText().toString().trim());
		model.setTaskType(WalkStruct.TaskType.MultiRAB.name());
		model.setRepeat(Integer.parseInt(repeatEditText.getText().toString().trim()));
		model.setInterVal(Integer.parseInt(intervalEditText.getText().toString().trim()));
		model.setSingleParallelTimeout(Integer.parseInt(singleParallelTimeout.getText().toString().trim()));
		model.setVoiceDelay(dialDelayEditText.getText().toString().trim().equals("") ? 1 : Integer
				.parseInt(dialDelayEditText.getText().toString().trim()));
		model.setDisConnect(pppruleSpinner.getSelectedItemPosition());
		model.getParallelServiceTestConfig().setRabStartMode(startMode_spn.getSelectedItemPosition());
		model.getParallelServiceTestConfig().setReferenceService(RabByStateServiceId.valueOf(
				RabByStateServiceId.getRabTaskName()[spn_refTask.getSelectedItemPosition()]).getTaskType());
		model.getParallelServiceTestConfig().setStartState(spn_startstate.getSelectedItemPosition());
		model.setEnable(1);

		if (isNew) {
			array.add(array.size(), model);// 可选中的个数
		} else {
			for (int i = 0; i < array.size(); i++) {
				if(array.get(i).getTaskID().equals(model.getTaskID())){
					taskListId = i;
					break;
				}
			}
			array.remove(taskListId);
			array.add(taskListId, model);
		}
		taskd.setTaskListArray(array);

		if (isFinish) {
			if (rabModel.getTaskModel().size() >= 2) {
				Toast.makeText(getApplicationContext(),
						isNew ? R.string.task_alert_newSucess : R.string.task_alert_updateSucess, Toast.LENGTH_SHORT)
						.show();
				TaskMultiRAB.this.finish();
			} else {
				Toast.makeText(getApplicationContext(), R.string.task_multirab_joblist_twotip, Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

	/**
	 * 添加EditText输入监听限制<BR>
	 * [功能详细描述]
	 */
	public void addEditTextWatcher() {

		taskNameEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (taskNameEditText.getText().toString().trim().length() == 0) { // 任务名为空
					taskNameEditText.setError(getString(R.string.task_alert_nullName));
				}

			}
		});

		intervalEditText.addTextChangedListener(new EditTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (intervalEditText.getText().toString().trim().equals("0")
						|| intervalEditText.getText().toString().trim().length() == 0) {
					intervalEditText.setError(getString(R.string.task_alert_input) + " "
							+ getString(R.string.task_keepTime));
					return;
				}
			}
		});

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onResume()
	 */

	@Override
	protected void onResume() {
		super.onResume();
		if (isNew) {
			taskNameEditText.setEnabled(true);
		}
		multiRABTaskAdapter = new MultiRABTaskAdapter(TaskMultiRAB.this, rabModel.getTaskModel(), this.taskListDispose,
				mhHandler);
		multirab_listview.setAdapter(multiRABTaskAdapter);
		multiRABTaskAdapter.notifyDataSetChanged();
		new Thread(new SetListViewHeightBasedOnChildren()).start();
		multirab_listview.setOnItemClickListener(this);
		multirab_listview.setOnItemLongClickListener(this);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @see com.walktour.framework.ui.BasicActivity#onPause()
	 */

	@Override
	protected void onPause() {
		super.onPause();
		if (treePopupWindow != null) {
			treePopupWindow.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * ListView处理<BR>
	 * 该方法解决ScroolView 中嵌套ListView的问题
	 * 设置完ListView的Adapter后，根据ListView的子项目重新计算ListView的高度，
	 * 然后把高度再作为LayoutParams设置给ListView 修改并发不能滑屏
	 */

	class SetListViewHeightBasedOnChildren implements Runnable {

		@Override
		public void run() {
			ListAdapter hotlistAdapter = multirab_listview.getAdapter();
			if (hotlistAdapter == null) {
				return;
			}
			int hottotalHeight = 0;
			for (int i = 0; i < hotlistAdapter.getCount(); i++) {
				View listItem = hotlistAdapter.getView(i, null, multirab_listview);
				listItem.measure(0, 0);
				hottotalHeight += listItem.getMeasuredHeight();
			}
			final ViewGroup.LayoutParams hotparams = multirab_listview.getLayoutParams();
			hotparams.height = hottotalHeight
					+ (multirab_listview.getDividerHeight() * (hotlistAdapter.getCount() - 1));
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					multirab_listview.setLayoutParams(hotparams);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reference_task: // 引用任务
			isFinish = false;
			saveTestTask();// 首先获取保存基础类型，获取并发对象
			initRabModel();
			if (model != null) {
				isNew = false;
				refTask();
			}
			break;
		case R.id.new_task:// 新建任务
			isFinish = false;
			saveTestTask();// 同理
			initRabModel();
			if (model != null) {
				isNew = false;
				showNewPopView();
			}
			break;
		case R.id.btn_ok:
			isFinish = true;
			saveTestTask();
			initRabModel();
			if (model != null) {
				isNew = false;
			}
			break;
		case R.id.btn_cencle:
			if (cloneRabModel.size() == 1 || cloneRabModel.size() == 0 && model != null) {
				array.remove(taskListId);
				taskd.setTaskListArray(array);
				TaskMultiRAB.this.finish();
			} else { // 将初始对象设置回未修改，解决未保存
				rabModel.setTaskModelList(cloneRabModel);
				TaskMultiRAB.this.finish();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * [新建任务]<BR>
	 * [功能详细描述]
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void showNewPopView() {

		// LinearLayout morePopView =
		// (LinearLayout)findViewById(R.id.more_popup);
		View treePopView = LayoutInflater.from(this).inflate(R.layout.task_tree_menu, null);
		// 绑定Layout里面的ListView
		list01 = (ListView) treePopView.findViewById(R.id.ListView02);
		list01.setVisibility(View.VISIBLE);
		list01.setFocusable(true);
		list01.requestFocus();
		list01.setFocusableInTouchMode(true);

		// 所有业务权限
		ArrayList<WalkStruct.TaskType> taskFromLicense = ApplicationModel.getInstance().getTaskList();
		// 移除自己本身

		ArrayList<WalkStruct.TaskType> taskFromLicenses = null;

		taskFromLicenses = (ArrayList<WalkStruct.TaskType>) taskFromLicense.clone();

		for (int i = 0; i < taskFromLicenses.size(); i++) {// 去掉并发的任务与非数据任务，wap任务
			if (WalkStruct.TaskType.MultiRAB.equals(taskFromLicenses.get(i))
					|| taskFromLicenses.get(i).getDataType() == WalkStruct.TaskType.NON
					|| taskFromLicenses.get(i).getDataType() == WalkStruct.TaskType.WAP) {
				taskFromLicenses.remove(i);
				i--;
			}
		}
		ArrayList<WalkStruct.TaskType> rabTaskType = new ArrayList<WalkStruct.TaskType>();

		// 新建子任务时候限制不能出现重复的业务
		for (int i = 0; i < rabModel.getTaskModel().size(); i++) {
			rabTaskType.add(TaskType.valueOf( rabModel.getTaskModel().get(i).getTaskType()));
		}

		for (int i = taskFromLicenses.size() - 1; i >= 0; i--) {
			TaskType type = taskFromLicenses.get(i);
			for (int j = 0; j < rabTaskType.size(); j++) {
				if (rabTaskType.get(j).getTypeName().equals(taskFromLicenses.get(i).getTypeName())) {
					taskFromLicenses.remove(type);
				}
			}
		}
		// 添加有权限的业务到列表中显示
		final ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < taskFromLicenses.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			TaskType taskType = taskFromLicenses.get(i);
			map.put("ItemTitle", taskType.getShowName(TaskMultiRAB.this));
			map.put("ItemKey", taskType);
			if (!taskType.getShowName(TaskMultiRAB.this).equals("unknown")) {
				listItem.add(map);
			}
		}

		for (int i = 0; i < listItem.size() - 1; i++) {
			for (int j = listItem.size() - 1; j > i; j--) {
				if (listItem.get(j).get("ItemTitle").equals(listItem.get(i).get("ItemTitle"))) {
					listItem.remove(j);
				}
			}
		}

		// 生成适配器的Item和动态数组对应的元素
		final SimpleAdapter adapter = new SimpleAdapter(this, listItem,// ListItem的数据源
																		// `1
				R.layout.listview_item_style6,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemTitle" }, new int[] { R.id.ItemTitle });

		// 添加并且显示
		adapter.notifyDataSetChanged();
		list01.setAdapter(adapter);

		// list02添加点击 事件
		list01.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TaskType taskType = (TaskType) listItem.get(arg2).get("ItemKey");
				Intent intent = getIntentByTaskType(taskType);
				if (intent != null) {
					intent.putExtra("RAB", TaskMultiRAB.RABTAG);// 为并发传递标记
					intent.putExtra("multiRabName", rabModel.getTaskID());// 传递任务名称
					intent.putExtra("RabDelayTimeType", startMode_spn.getSelectedItemPosition());
					startActivity(intent);
				}
			}
		});
		// 设置选择
		list01.setSelected(true);
		treePopupWindow = new PopupWindow(treePopView, (int) (190 * metric.density), (int) (350 * metric.density), true);
		treePopupWindow.setFocusable(true);
		treePopupWindow.setTouchable(true);
		treePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		treePopupWindow.showAsDropDown(bt_taskNew, 30, 10);
		treePopupWindow.showAsDropDown(bt_taskNew, 30, 10);

	}

	private Intent getIntentByTaskType(TaskType taskType) {
		Intent intent = null;
		switch (taskType) {
		case EmptyTask:
			intent = new Intent(this, TaskEmpty.class);
			break;
		case InitiativeCall:
			intent = new Intent(this, TaskMOCCall.class);
			break;
		case PassivityCall:
			intent = new Intent(this, TaskMTCCall.class);
			break;
		case InitiativeVideoCall:
			intent = new Intent(this, TaskInitiativeVideoCall.class);
			break;
		case PassivityVideoCall:
			intent = new Intent(this, TaskPassivityVideoCall.class);
			break;
		case Ping:
			intent = new Intent(this, TaskPing.class);
			break;
		case Attach:
			intent = new Intent(this, TaskAttach.class);
			break;
		case PDP:
			intent = new Intent(this, TaskPDP.class);
			break;
		case FTPUpload:
			intent = new Intent(this, TaskFtpUpload.class);
			break;
		case FTPDownload:
			intent = new Intent(this, TaskFtpDownload.class);
			break;
		case Http:
			intent = new Intent(this, TaskHttpPage.class);
			break;
		case HttpRefurbish:
			intent = new Intent(this, TaskHttpPage.class);
			break;
		case HttpDownload:
			intent = new Intent(this, TaskHttpDownload.class);
			break;
		case EmailPop3:
			intent = new Intent(this, TaskEmailPop3.class);
			break;
		case EmailSmtp:
			intent = new Intent(this, TaskEmailSmtp.class);
			break;
		case SMSIncept:
			intent = new Intent(this, TaskSmsIncept.class);
			break;
		case SMSSend:
			intent = new Intent(this, TaskSmsSend.class);
			break;
		case SMSSendReceive:
			intent = new Intent(this, TaskSmsSendReceive.class);
			break;
		case MMSIncept:
			intent = new Intent(this, TaskMmsReceive.class);
			break;
		case MMSSend:
			intent = new Intent(this, TaskMmsSend.class);
			break;
		case MMSSendReceive:
			intent = new Intent(this, TaskMmsSendReceive.class);
			break;
		case WapLogin:
			intent = new Intent(this, TaskWapLogin.class);
			break;
		case WapRefurbish:
			intent = new Intent(this, TaskWapRefurbish.class);
			break;
		case WapDownload:
			intent = new Intent(this, TaskWapDownload.class);
			break;
		case EmailSmtpAndPOP:
			intent = new Intent(this, TaskEmailSmtpAndPop3.class);
			break;
		case Stream: // 流媒体处理 Jihong Xie 2012-07-19
			intent = new Intent(this, TaskStreaming.class);
			break;
		case DNSLookUp:// DNS
			intent = new Intent(this, TaskDNSLookup.class);
			break;
		case SpeedTest:// Speed Test Jihong.Xie
			intent = new Intent(this, TaskSpeedTest.class);
			break;
		case HttpUpload:
			intent = new Intent(this, TaskHttpUpload.class);
			break;
		case VOIP: // VoIP处理
			intent = new Intent(this, TaskVoIP.class);
			break;
		case HTTPVS: // httpvs
			intent = new Intent(this, TaskVideoPlay.class);
			break;
		case Facebook:
			intent = new Intent(this, TaskFacebookActivity.class);
			break;
		case TraceRoute:
			intent = new Intent(this, TaskTraceRouteActivity.class);
			break;
		case PBM:
			intent = new Intent(this, TaskPBMActivity.class);
			break;
		case WeiBo:
			intent = new Intent(this, TaskWeiBoActivity.class);
			break;
		case WeChat:
//			intent = new Intent(this, TaskWeChatActivity.class);
			intent = new Intent(this, TaskOttWeChatActivity.class);
			break;
		case UDP:
			intent = new Intent(this, TaskUDPActivity.class);
			break;
		case REBOOT:
			intent = new Intent(this, TaskReboot.class);
			break;
		case WeCallMoc:
			intent = new Intent(this, TaskOttWxMocActivity.class);
			break;
		case WeCallMtc:
            intent = new Intent(this, TaskOttWxMtcActivity.class);
		    break;
        case SkypeChat:
            intent = new Intent(this, TaskOttSkypeActivity.class);
            break;
        case SinaWeibo:
            intent = new Intent(this, TaskOttSinaActivity.class);
            break;
        case QQ:
            intent = new Intent(this, TaskOttQQActivity.class);
            break;
        case WhatsAppChat:
            intent = new Intent(this, TaskOttWhatsAppActivity.class);
            break;
        case WhatsAppMoc:
            intent = new Intent(this, TaskOttWhatsAppMocActivity.class);
            break;
        case WhatsAppMtc:
            intent = new Intent(this, TaskOttWhatsAppMtcActivity.class);
            break;
        case Facebook_Ott:
            intent = new Intent(this, TaskOttFacebookActivity.class);
            break;
        case Instagram_Ott:
            intent = new Intent(this, TaskOttInstagramActivity.class);
            break;
		default:
			break;
		}

		return intent;
	}

	/**
	 * [引用任务]<BR>
	 * [功能详细描述]
	 */
	@SuppressWarnings("deprecation")
	private void refTask() {
		/**
		 * 根据屏幕密度缩放
		 */
		DisplayMetrics metric = new DisplayMetrics();
		TaskMultiRAB.this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		LayoutInflater factory = LayoutInflater.from(TaskMultiRAB.this);
		View alert_view = factory.inflate(R.layout.alert_dialog_rablist_item, null);
		ListView alertList = (ListView) alert_view.findViewById(R.id.ListView_task);
		List<TaskModel> refTaskList = taskListDispose.getCurrentTaskList();// 引用数据

		for (int i = 0; i < refTaskList.size(); i++) {
			TaskModel model = refTaskList.get(i);
			if (WalkStruct.TaskType.MultiRAB.name().equals(model.getTaskType())) {// 去除引用任务里头的并发任务
				refTaskList.remove(i);
				i = i - 1;// 移除一个model之后i对应的减1
			}
		}
		// 在并发里头已经引用过的任务，引用任务列表做相应的移除
		ArrayList<TaskModel> taskTempa = new ArrayList<TaskModel>();

		// 去除wap业务与非数据业务
		for (int j = 0; j < refTaskList.size(); j++) {
			if (refTaskList.get(j).getTypeProperty() == WalkCommonPara.TypeProperty_None
					|| refTaskList.get(j).getTypeProperty() == WalkCommonPara.TypeProperty_Wap) {
				taskTempa.add(refTaskList.get(j));
			}
		}
		refTaskList.removeAll(taskTempa);

		// 去除引用过相同类型的业务。例如。并发列表有语音 引用列表就不在出现语音的选择 保证同种类型业务在并发里头只出现一个
		ArrayList<TaskModel> taskTemp = new ArrayList<TaskModel>();
		for (int i = refTaskList.size() - 1; i >= 0; i--) {
			for (int j = 0; j < rabModel.getTaskModel().size(); j++) {
				if (TaskType
						.valueOf(refTaskList.get(i).getTaskType())
						.getTypeName()
						.equals(TaskType.valueOf(rabModel.getTaskModel().get(j).getTaskType())
								.getTypeName())) {
					taskTemp.add(refTaskList.get(i));
				}
			}
		}
		refTaskList.removeAll(taskTemp);
		final List<TaskModel> showList = refTaskList;
		listAdapter = new listAdapter(refTaskList);
		alertList.setAdapter(listAdapter);
		alertList.setEmptyView(alert_view.findViewById(R.id.task_emptyList));
		listAdapter.notifyDataSetChanged();
		BasicDialog.Builder builder = new BasicDialog.Builder(this);
		builder.setView(alert_view, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				(int) (280 * metric.density)));
		builder.setTitle(R.string.reference_task);
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				TaskModel taskDetail = new TaskModel();
				Iterator<Integer> iterator = state.keySet().iterator();
				ArrayList<TaskModel> detailList = new ArrayList<TaskModel>();
				try {
					// 将引用勾选的子任务，添加到一个新建的list中。方便判断同种类型的业务
					while (iterator.hasNext()) {
						int pos = iterator.next();
						String refMultiRabName = taskNameEditText.getText().toString() + "%"
								+ showList.get(pos).getTaskName();// 得到引用的名字修改成携带并发的名
						taskDetail = (TaskModel) (showList.get(pos).deepClone());
						taskDetail.setTaskName(refMultiRabName);
						detailList.add(taskDetail);
					}
					// 判断同种类型的业务，如果列表里头有相同类型的业务，则不添加到并发列表，做提示操作
					for (int i = 0; i < detailList.size(); i++) {
						int count = 0;
						for (int j = 0; j < detailList.size(); j++) {
							if (TaskType
									.valueOf(detailList.get(i).getTaskType())
									.getTypeName()
									.equals(TaskType.valueOf(detailList.get(j).getTaskType())
											.getTypeName())) {
								count++;
								if (count == 2) {
									Toast.makeText(TaskMultiRAB.this,
											getString(R.string.task_mulitirab_selete_ps_task), Toast.LENGTH_SHORT)
											.show();
									return;
								}
							}
						}

					}

					for (TaskModel taskModel : detailList) {
						rabModel.addTaskList(taskModel);
					}
					// 将符合条件的引用业务添加到并发业务里，原先的业务不变。在原业务基础上追加
					// rabModel.getTaskModel().addAll(detailList);
					// rabModel.setTaskModelList(detailList);
//					ArrayList<TaskModel> arrays = taskListDispose.getTaskListArray();
//					arrays.remove(taskListId);
//					arrays.add(taskListId, rabModel);
//					taskListDispose.setTaskListArray(arrays);
					multiRABTaskAdapter = new MultiRABTaskAdapter(TaskMultiRAB.this, rabModel.getTaskModel(),
							taskListDispose, mhHandler);
					multirab_listview.setAdapter(multiRABTaskAdapter);
					multiRABTaskAdapter.notifyDataSetChanged();
					new Thread(new SetListViewHeightBasedOnChildren()).start();
					state.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		builder.setNegativeButton(R.string.str_cancle);
		alertList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			}
		});
		builder.show();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String rabTaskName = (rabModel.getTaskModel().get(arg2)).getTaskID();
		this.edit(rabTaskName);// 调用编辑任务方法
	}

	/**
	 * 编辑并发列表任务
	 * 
	 * @param taskName
	 *            任务名
	 * */
	private void edit(String taskName) {
		ArrayList<TaskModel> modelList = rabModel.getTaskModel();
		int position = -1;
		WalkStruct.TaskType taskType = null;
		for (int i = 0; i < modelList.size(); i++) {
			TaskModel taskModel = modelList.get(i);
			if (taskModel.getTaskID().equals(taskName)) {
				position = i;
				taskType = WalkStruct.TaskType.valueOf(taskModel.getTaskType());
				break;
			}
		}

		if (position == -1) {
			return;
		}

		Intent intent = getIntentByTaskType(taskType);
		if (intent != null) {
			Bundle bundle = new Bundle();
			bundle.putInt("taskListId", position);
			intent.putExtras(bundle);
			intent.putExtra("RAB", RABTAG);// 为并发传递标记
			intent.putExtra("RabTaskName", taskName);// 传递任务名称
			intent.putExtra("RabDelayTimeType", startMode_spn.getSelectedItemPosition());
			intent.putExtra("multiRabName", rabModel.getTaskID());
			startActivity(intent);
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 */

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		if (appModel.isTestJobIsRun()) {
			return false;
		}
		BasicDialog.Builder builder = new BasicDialog.Builder(TaskMultiRAB.this);
		String[] items = new String[] { getResources().getString(R.string.edit),
				getResources().getString(R.string.up), getResources().getString(R.string.down),
				getResources().getString(R.string.delete), getResources().getString(R.string.delete_all) };
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				// 编辑
				case 0:
					String taskName = rabModel.getTaskModel().get(arg2).getTaskName();
					edit(taskName);
					break;
				// 上移
				case 1:
					taskListDispose.rabMove(1, arg2, rabModel.getTaskModel());
					break;
				// 下移
				case 2:
					taskListDispose.rabMove(2, arg2, rabModel.getTaskModel());
					break;
				// 删除
				case 3:
					taskListDispose.rabMove(3, arg2, rabModel.getTaskModel());
					break;
				// 删除全部
				case 4:
					taskListDispose.rabMove(4, arg2, rabModel.getTaskModel());
					break;
				default:
					break;
				}
				// multiRABTaskAdapter=new
				// MultiRABTaskAdapter(TaskMultiRAB.this,rabModel.getTaskModel(),taskListDispose,mhHandler);
				// multirab_listview.setAdapter(multiRABTaskAdapter);
				multiRABTaskAdapter.notifyDataSetChanged();
				new Thread(new SetListViewHeightBasedOnChildren()).start();
			}
		});
		builder.show();
		return true;
	}

	/**
	 * 
	 * [dialog里listview的适配器]<BR>
	 * [功能详细描述]
	 */
	class listAdapter extends BaseAdapter {

		private List<TaskModel> taskList;
		private HashMap<Integer, String> enable = new HashMap<Integer, String>(); // 记录checkbox的位置

		/**
		 * [传入list作为有参构造]
		 */
		public listAdapter(List<TaskModel> taskList) {
			this.taskList = taskList;
		}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @return
		 * @see android.widget.Adapter#getCount()
		 */

		@Override
		public int getCount() {
			if (taskList != null) {
				return taskList.size();
			}
			return 0;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param position
		 * @return
		 * @see android.widget.Adapter#getItem(int)
		 */

		@Override
		public Object getItem(int position) {
			return taskList.get(position);
		}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param position
		 * @return
		 * @see android.widget.Adapter#getItemId(int)
		 */

		@Override
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater factory = LayoutInflater.from(TaskMultiRAB.this);
			ViewHolder holder;
			if (convertView == null) {
				convertView = factory.inflate(R.layout.alert_dialog_rablistview, null);
				holder = new ViewHolder();
				holder.tv_task = (TextView) convertView.findViewById(R.id.tv_rab_task);
				holder.ck_task = (CheckBox) convertView.findViewById(R.id.ck_rab_task);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ArrayList<TaskModel> rabLists = rabModel.getTaskModel();
			if (rabLists.contains(taskList.get(position))) {
				enable.put(position, "TAG");
				holder.ck_task.setEnabled(false);
			}
			holder.ck_task.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				/**
				 * 
				 * 将已勾选的checkbox的position保存起来，防止listview滚动的时候checkbox错乱<BR>
				 */
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {

						state.put(position, isChecked); //
						// taskCheckList.put(position, taskList.get(position));
					} else {
						state.remove(position);
						// taskCheckList.remove(position);
					}
				}
			});
			holder.ck_task.setChecked(state.get(position) == null ? false : true);
			holder.ck_task.setEnabled(enable.get(position) != null ? false : true);
			holder.tv_task.setText(taskList.get(position).getTaskName());
			/**
			 * 这里设置checkbox勾选状态的时候，就根据设置的位置显示与否
			 */
			return convertView;
		}

		public class ViewHolder {

			TextView tv_task;// 任务名

			CheckBox ck_task;// 勾选框

		}
	}
}
