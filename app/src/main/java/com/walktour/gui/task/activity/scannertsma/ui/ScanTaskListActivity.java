package com.walktour.gui.task.activity.scannertsma.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.walktour.gui.task.activity.scannertsma.ScanTask5GOperateFactory;
import com.walktour.gui.task.activity.scannertsma.adapter.ScanTaskAdapter;
import com.walktour.gui.task.activity.scannertsma.model.ScanTaskModel;
import com.walktour.gui.task.activity.scannertsma.model.TestSchemaType;
import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.view.HeadListView;
import com.walktour.gui.R;

import java.util.ArrayList;


/**
 * 扫频仪任务列表配置界面
 *
 * @author jinfeng.xie
 */
public class ScanTaskListActivity extends BasicActivity implements OnItemClickListener, ScanTaskAdapter.OnItemClickListener {
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//    }
//
//    @Override
//    public void onItemClick(View view, int position) {
//
//    }

    public HeadListView mListView;

    private ScanTaskAdapter scanTaskAdapter = null;

    private ArrayList<ScanTaskModel> taskModelList = new ArrayList<ScanTaskModel>();

    private ProgressDialog ftpDialog;

    private ScanTask5GOperateFactory instance;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        instance = ScanTask5GOperateFactory.getInstance();
        setContentView(R.layout.scan_listview_main);
        mListView = (HeadListView) findViewById(R.id.ListView01);
        mListView.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        new getDateList().execute();    //获取分析数据
        super.onResume();
    }


    private void showDialog() {
        ftpDialog = new ProgressDialog(ScanTaskListActivity.this);
        ftpDialog.setCancelable(false);
        ftpDialog.setMessage("Loding");
        ftpDialog.show();
    }


    /**
     * 异步执行任务
     *
     * @author zhihui.lian
     */

    class getDateList extends AsyncTask<Object, Object, Object> {


        public getDateList() {
            super();
        }

        @Override
        protected Object doInBackground(Object... params) {
            if (taskModelList != null) {
                taskModelList.clear();
                taskModelList.addAll(instance.getTestModelList());
            }
            try {

            } catch (Exception e) {
//				ftpDialog.cancel();
            }
            return taskModelList;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (scanTaskAdapter == null) {
                scanTaskAdapter = new ScanTaskAdapter(ScanTaskListActivity.this, (ArrayList<ScanTaskModel>) result);
                mListView.setAdapter(scanTaskAdapter);
                mListView.setOnScrollListener(scanTaskAdapter);
//					mListView.setPinnedHeaderView(LayoutInflater.from(ScanTaskListActivity.this).inflate(R.layout.scan_item_section, mListView, false));
            } else {
                scanTaskAdapter.notifyDataSetChanged();            //更新数据
            }
            scanTaskAdapter.setOnItemClickListener(ScanTaskListActivity.this);
//				ftpDialog.cancel();				//隐藏加载条
        }


        /**
         * 开始准备ui工作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (scanTaskAdapter == null) {
//				showDialog();
            }
        }


    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {

    }

    ;


    /**
     * 实现Item监听器
     */
    @Override
    public void onItemClick(View view, int position) {
        ScanTaskModel taskModel = scanTaskAdapter.getItem(position);
        TestSchemaType taskType = TestSchemaType.valueOf(taskModel.getTaskType());
        Intent intent = getItentType(taskType.getSchemaTaskType());
        if (intent == null) {
            return;
        }
        intent.putExtra(ScanTask5GOperateFactory.TESTTYPE, taskModel.getTaskType());
        startActivity(intent);
    }

    /**
     * Intent根据模板类型跳转界面
     *
     * @param schemaTaskType
     * @return
     */
    private Intent getItentType(int schemaTaskType) {
        Intent intent = null;
        switch (schemaTaskType) {
            case TestSchemaType.CWTEST:
                intent = new Intent(ScanTaskListActivity.this, ScanTaskCwActivity.class);
                break;
            case TestSchemaType.COLORCODETEST:
                intent = new Intent(ScanTaskListActivity.this, ScanTaskColorCodeActivity.class);
                break;
            case TestSchemaType.PILOTTEST:
                intent = new Intent(ScanTaskListActivity.this, ScanTaskPilotActivity.class);
                break;
            case TestSchemaType.LTEPILOTTEST:
                intent = new Intent(ScanTaskListActivity.this, ScanTaskLtePilotActivity.class);
                break;
            case TestSchemaType.SPECTURM:
                intent = new Intent(ScanTaskListActivity.this, ScanTaskSpecturmActivity.class);
                break;
                case TestSchemaType.BLIND:
                    intent=new Intent(ScanTaskListActivity.this,ScanTaskBlindActivity.class);
            default:
                break;
        }
        return intent;
    }


}
