package com.walktour.gui.perceptiontest.surveytask.data;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walktour.base.util.LogUtil;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;
import com.walktour.gui.perceptiontest.surveytask.data.dao.DBManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Yi.Lin on 2018/11/20.
 * 数据提供器
 */

public class DataProvider {

    private static DataProvider sInstance;

    private DataProvider()
    {
    }

    public static DataProvider getInstance() {
        if (null == sInstance) {
            synchronized (DataProvider.class) {
                if (null == sInstance) {
                    sInstance = new DataProvider();
                }
            }
        }
        return sInstance;
    }

    public void initFakeSurveyTaskData(Context context) {

        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(context.getAssets().open("survey_tasks.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            String jsonStr = builder.toString();
            List<SurveyTask> list = new Gson().fromJson(jsonStr, new TypeToken<List<SurveyTask>>() {
            }.getType());
            for (SurveyTask surveyTask : list) {
                LogUtil.e("DataProvider","" + surveyTask.toString());
                DBManager.getInstance(context).insertSurveyTask(surveyTask);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取勘测任务列表
     *
     * @param state 任务状态
     * @return
     */
    public List<SurveyTask> getSurveyTaskList(Context context, int state) {
        return DBManager.getInstance(context).queryList(state);
    }

}
