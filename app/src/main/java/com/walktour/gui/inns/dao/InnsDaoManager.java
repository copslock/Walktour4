package com.walktour.gui.inns.dao;


import android.content.Context;

import com.walktour.base.util.LogUtil;
import com.walktour.greendao.DaoMaster;
import com.walktour.greendao.DaoSession;
import com.walktour.greendao.InnsFtpParamsDao;
import com.walktour.greendao.InnsVoLTEParamsDao;
import com.walktour.gui.inns.dao.model.InnsFtpParams;
import com.walktour.gui.inns.dao.model.InnsVoLTEParams;

/**
 * Created by yi.lin on 2017/12/1.
 *
 * 寅时数据库管理类
 */

public class InnsDaoManager {

    /**
     * 日志标识
     */
    private static final String TAG = "InnsDaoManager";


    /**
     * 唯一实例
     */
    private static InnsDaoManager sInstance;
    /**
     * 数据库名称
     */
    private static final String DB_NAME = "inns.db";

    /**
     * 数据操作事务类
     */
    private DaoSession mDaoSession;

    private InnsDaoManager(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        this.mDaoSession = daoMaster.newSession();
    }

    /**
     * 返回唯一实例
     *
     * @param context 上下文
     * @return 唯一实例
     */
    public static InnsDaoManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InnsDaoManager(context.getApplicationContext());
        }
        return sInstance;
    }


    /**
     * 保存InnsFtpParams对象到表
     * @param innsFtpParams
     */
    public void save(InnsFtpParams innsFtpParams){
        LogUtil.d(TAG, "----save----InnsFtpParams----");
        try {
            InnsFtpParamsDao dao = this.mDaoSession.getInnsFtpParamsDao();
            dao.save(innsFtpParams);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 保存InnsVoLTEParams对象到表
     * @param innsVoLTEParams
     */
    public void save(InnsVoLTEParams innsVoLTEParams){
        LogUtil.d(TAG, "----save----InnsVoLTEParams----");
        try {
            InnsVoLTEParamsDao dao = this.mDaoSession.getInnsVoLTEParamsDao();
            dao.save(innsVoLTEParams);
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
    }


    public InnsFtpParams getInnsFtpParamsByFileNameDetail(String fileNameDetail){
        LogUtil.d(TAG, "----getInnsFtpParamsByFileNameDetail----fileNameDetail = " + fileNameDetail);
        InnsFtpParams innsFtpParams = new InnsFtpParams();
        try {
            InnsFtpParamsDao dao = this.mDaoSession.getInnsFtpParamsDao();
            innsFtpParams = dao.queryBuilder().where(InnsFtpParamsDao.Properties.FileNameDetail.eq(fileNameDetail)).unique();
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return innsFtpParams;
    }


    public InnsVoLTEParams getInnsVoLTEParamsByFileNameDetail(String fileNameDetail){
        LogUtil.d(TAG, "----getInnsFtpParamsByFileNameDetail----fileNameDetail = " + fileNameDetail);
        InnsVoLTEParams innsVoLTEParams = new InnsVoLTEParams();
        try {
            InnsVoLTEParamsDao dao = this.mDaoSession.getInnsVoLTEParamsDao();
            innsVoLTEParams = dao.queryBuilder().where(InnsVoLTEParamsDao.Properties.FileNameDetail.eq(fileNameDetail)).unique();
            this.mDaoSession.clear();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage(), e);
        }
        return innsVoLTEParams;
    }

}
