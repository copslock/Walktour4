package com.walktour.gui.perceptiontest.surveytask.data.dao;

import android.content.Context;

import com.walktour.greendao.DaoMaster;
import com.walktour.greendao.DaoSession;
import com.walktour.greendao.SurveyTaskDao;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyCell;
import com.walktour.gui.perceptiontest.surveytask.claiming.model.SurveyTask;

import java.util.List;

/**
 * Created by Yi.Lin on 2018/11/21.
 */
public class DBManager {
    private static final String TAG = "DBManager";
    private static final String DB_NAME = "survey_task.db";
    private final DaoSession mDaoSession;
    private static DBManager sInstance;

    private DBManager(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        this.mDaoSession = daoMaster.newSession();
    }

    public static DBManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void insertSurveyTask(SurveyTask task) {
        mDaoSession.getSurveyTaskDao().insert(task);
    }

    public void insertSurveyCell(SurveyCell surveyCell) {
        mDaoSession.getSurveyCellDao().insert(surveyCell);
    }

    public boolean needReloadData(){
        List<SurveyTask> surveyTasks = mDaoSession.getSurveyTaskDao()
                .queryBuilder()
                .build()
                .list();
        return surveyTasks == null || surveyTasks.isEmpty();
    }

    public List<SurveyTask> queryList(int state) {
        List<SurveyTask> surveyTasks = mDaoSession.getSurveyTaskDao()
                .queryBuilder()
                .where(SurveyTaskDao.Properties.State.eq(state))
                .build()
                .list();
        return surveyTasks;
    }

    public void update(SurveyTask task) {
        mDaoSession.getSurveyTaskDao().update(task);
    }


}
