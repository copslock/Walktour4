package com.walktour.gui.perceptiontest.notice.dao;

import android.content.Context;
import android.util.Log;

import com.walktour.greendao.MessageBeanDao;
import com.walktour.gui.perceptiontest.notice.bean.MessageBean;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @author Max
 * @data 2018/11/18
 */
public class MessageDaoUtil {
    private static final String TAG = MessageDaoUtil.class.getSimpleName();
    private DaoManager mManager;

    public MessageDaoUtil(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成message记录的插入，如果表未创建，先创建message表
     * @return
     */
    public boolean insertMessage(MessageBean message){
        boolean flag = false;
        List list=  querymessageByQueryBuilder(message.getId());
        if (list.size()==0){
//            flag = mManager.getDaoSession().getMessageBeanDao().insert(message) == -1 ? false : true;
             mManager.getDaoSession().getMessageBeanDao().save(message);
             flag=true;
        }
        Log.i(TAG, "insert message :" + flag + "-->" + message.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param messageList
     * @return
     */
    public boolean insertMultMessage(final List<MessageBean> messageList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (MessageBean message : messageList) {
                        mManager.getDaoSession().insertOrReplace(message);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param message
     * @return
     */
    public boolean updatemessage(MessageBean message){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(message);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param message
     * @return
     */
    public boolean deletemessage(MessageBean message){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(message);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(MessageBean.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<MessageBean> queryAllmessage(){
        return mManager.getDaoSession().loadAll(MessageBean.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public MessageBean querymessageById(long key){
        return mManager.getDaoSession().load(MessageBean.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<MessageBean> querymessageByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(MessageBean.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<MessageBean> querymessageByQueryBuilder(long id){
        QueryBuilder<MessageBean> queryBuilder = mManager.getDaoSession().queryBuilder(MessageBean.class);
        return queryBuilder.where(MessageBeanDao.Properties.Id.eq(id)).list();
    }
    /**
     * 查看已讀列表
     * @return
     */
    public List<MessageBean> querymessageByIsReaded(boolean isRead){
        QueryBuilder<MessageBean> queryBuilder = mManager.getDaoSession().queryBuilder(MessageBean.class);
        return queryBuilder.where(MessageBeanDao.Properties.IsRead.eq(isRead)).list();
    }
}
