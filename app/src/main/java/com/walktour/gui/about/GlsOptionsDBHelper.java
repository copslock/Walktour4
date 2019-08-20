package com.walktour.gui.about;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 用户登录数据库操作类
 * 
 * @author zhihui.lian 功能:增、删、改等
 */
public class GlsOptionsDBHelper {
	private static final boolean D = true;
	private static final String TAG = "---OptionsDBHelper---";// LogCat

	private static final String DB_NAME = "glsdb.db";// 数据库名
	private static final String DB_TABLE = "accounts";// 数据库表名
	private static final int DB_VERSION = 1;// 数据库版本号

	private Context mContext;
	private DBHelper dbHelper;
	private SQLiteDatabase database;

	public GlsOptionsDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public boolean isOpen() {
		if (database.isOpen()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @功能 打开数据库 空间不够存储的时候设为只读
	 * @throws SQLiteException
	 **/
	public boolean open() throws SQLiteException {
		dbHelper = new DBHelper(mContext, DB_NAME, null, DB_VERSION);
		try {
			database = dbHelper.getWritableDatabase();
			return true;
		} catch (SQLiteException e) {
			database = dbHelper.getReadableDatabase();
			return false;
		}
	}

	/**
	 * @功能 关闭数据库
	 * 
	 **/
	public void close() {

		if (database != null) {
			database.close();
			database = null;
		}
		Log.e(TAG, "close()");
	}

	/**
	 * @功能 更新选中账号记录
	 * @param appInfo
	 * @return
	 **/
	public void updateState(String name) {
		if (name != null) {
			database.execSQL("UPDATE " + DB_TABLE + " SET "
					+ DBHelper.KEY_LAST_ACC + " = 1 WHERE "
					+ DBHelper.KEY_USERNAME + " = " + "\"" + name + "\";");
			database.execSQL("UPDATE " + DB_TABLE + " SET "
					+ DBHelper.KEY_LAST_ACC + " = 0 WHERE "
					+ DBHelper.KEY_USERNAME + " <> " + "\"" + name + "\";");// 将其它的置为0
		} else {
			database.execSQL("UPDATE " + DB_TABLE + " SET "
					+ DBHelper.KEY_LAST_ACC + " = 0 ;");// 将其它的置为0
		}

	}

	/**
	 * @功能 向表中添加一条数据
	 * @param appInfo
	 * @return
	 **/
	public long insert(UserAccount userAccount) {
		Log.i("D", "insert a record to database");

		ContentValues contentValues = new ContentValues();

		contentValues.put(DBHelper.KEY_USERNAME, userAccount.getUserName());
		contentValues.put(DBHelper.KEY_PASSWORD, userAccount.getPassWord());
		contentValues.put(DBHelper.KEY_REMB_PWD, (userAccount.getRembPwd()) ? 1
				: 0);
		contentValues.put(DBHelper.KEY_LAST_ACC, (userAccount.getLastAcc()) ? 1
				: 0);

		return database.insert(DB_TABLE, null, contentValues);
	}

	/**
	 * @功能 向表中删除一条数据
	 * @param appInfo
	 * @return
	 **/
	public long deleteOne(String name) {

		return database.delete(DB_TABLE, DBHelper.KEY_USERNAME + "=" + "\""
				+ name + "\"", null);
	}

	/**
	 * @功能 删除数据表
	 * @return
	 **/
	public long deleteAllData() {

		return database.delete(DB_TABLE, null, null);
	}

	/**
	 * @功能 获得上一次登录的账号
	 * @param
	 * @return
	 **/
	public UserAccount queryLastAcc() {
		UserAccount userAccount = null;
		Cursor result = database
				.query(DB_TABLE, null, DBHelper.KEY_LAST_ACC + "=" + "\""
						+ String.valueOf(1) + "\"", null, null, null, null);
		Log.e(TAG, "queryOneData()---result:" + result.getColumnCount());
		if (result.getCount() > 0) {
			userAccount = ConvertAll(result)[0];
		}
		result.close();
		return userAccount;
	}

	/**
	 * @功能 根据包名查询一条数据
	 * @param
	 * @return
	 **/
	public UserAccount queryOne(String name) {
		UserAccount userAccount;
		Cursor result = database.query(DB_TABLE, null, DBHelper.KEY_USERNAME
				+ "=" + "\"" + name + "\"", null, null, null, null);
		Log.e(TAG, "queryOneData()---result:" + result.getColumnCount());
		userAccount = ConvertAll(result)[0];
		result.close();
		return userAccount;
	}

	/**
	 * @功能 查询全部数据记录数量
	 * @return
	 **/
	public int queryDataCount() {
		int count = 0;
		Cursor result = database.query(DB_TABLE, null, null, null, null, null,
				null);
		count = result.getCount();
		result.close();
		return count;
	}

	/**
	 * @功能 查询全部数据
	 * @return
	 **/
	public UserAccount[] queryAllData() {
		UserAccount[] userAccounts;
		Cursor result = database.query(DB_TABLE, null, null, null, null, null,
				null);
		userAccounts = ConvertAll(result);
		result.close();
		return userAccounts;
	}

	/**
	 * @功能 根据包名更新一条数据
	 * @param
	 * @return long
	 **/
	public long updateOne(String name, UserAccount userAccount) {

		Log.i("D", "update a record with username = " + name);

		ContentValues contentValues = new ContentValues();

		contentValues.put(DBHelper.KEY_USERNAME, userAccount.getUserName());
		contentValues.put(DBHelper.KEY_PASSWORD, userAccount.getPassWord());
		contentValues.put(DBHelper.KEY_REMB_PWD, (userAccount.getRembPwd()) ? 1
				: 0);
		contentValues.put(DBHelper.KEY_LAST_ACC, (userAccount.getLastAcc()) ? 1
				: 0);

		return database.update(DB_TABLE, contentValues, DBHelper.KEY_USERNAME
				+ "=" + "\"" + name + "\"", null);
	}

	/**
	 * @功能 根据包名查询一条数据是否存在
	 * @param
	 * @return
	 **/
	public boolean isOneExist(String name) {
		Cursor result = database.query(DB_TABLE, null, DBHelper.KEY_USERNAME
				+ "=" + "\"" + name + "\"", null, null, null, null);
		Log.i("D", "check a record with username = " + name
				+ " is exist or not");
		if (result.getCount() == 0 || !result.moveToFirst()) {
			Log.i("D", "the record is not exist");
			result.close();
			return false;
		} else {
			Log.i("D", "the record is exist");
			result.close();
			return true;
		}
	}

	private UserAccount[] ConvertAll(Cursor cursor) {
		int resultCounts = cursor.getCount();
		if (D)
			Log.e(TAG, "ConvertToAppInfo()---resultCounts:" + resultCounts);
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}
		UserAccount[] userAccounts = new UserAccount[resultCounts];
		Log.i(TAG, "AppInfo length:" + userAccounts.length);
		for (int i = 0; i < resultCounts; i++) {
			userAccounts[i] = new UserAccount();
			userAccounts[i].setUserName(cursor.getString(cursor
					.getColumnIndex(DBHelper.KEY_USERNAME)));
			userAccounts[i].setPassWord(cursor.getString(cursor
					.getColumnIndex(DBHelper.KEY_PASSWORD)));
			userAccounts[i].setRembPwd(((byte) (cursor.getInt(cursor
					.getColumnIndex(DBHelper.KEY_REMB_PWD))) == 1) ? true
					: false);
			userAccounts[i].setLastAcc(((byte) (cursor.getInt(cursor
					.getColumnIndex(DBHelper.KEY_LAST_ACC))) == 1) ? true
					: false);
			cursor.moveToNext();
		}
		cursor.close();
		return userAccounts;
	}

	public class UserAccount {

		private String mUserName = "", mPassWord = "";
		private boolean mLastAcc = false, mRembPwd = true;

		/**
		 * @Name OptionsDBHelper.UserAccounts
		 * @Description TODO
		 * @Date 2013-8-2 下午1:01:42
		 **/
		public UserAccount() {
			// TODO Auto-generated constructor stub
		}

		public UserAccount(String username, String password, boolean lastacc,
				boolean rempwd, boolean autolog) {
			this.mUserName = username;
			this.mPassWord = password;
			this.mRembPwd = rempwd;
			this.mLastAcc = lastacc;
		}

		public void setUserName(String un) {
			this.mUserName = un;
		}

		public String getUserName() {
			return this.mUserName;
		}

		public void setPassWord(String pwd) {
			this.mPassWord = pwd;
		}

		public String getPassWord() {
			return this.mPassWord;
		}

		public void setRembPwd(boolean rem) {
			this.mRembPwd = rem;
		}

		public boolean getRembPwd() {
			return this.mRembPwd;
		}

		public void setLastAcc(boolean la) {
			this.mLastAcc = la;
		}

		public boolean getLastAcc() {
			return this.mLastAcc;
		}

		@Override
		public String toString() {
			String str = "账号:" + this.mUserName + "," + "密码:" + this.mPassWord
					+ "," + "记住密码:" + this.mRembPwd + "," + "是否为上次账号:"
					+ this.mLastAcc;
			return str;
		}
	}

	public class DBHelper extends SQLiteOpenHelper {

		public static final boolean D = true;
		public static final String TAG = "---DBHelper---";// LogCat

		private static final String DB_TABLE = "accounts";// 数据库表名

		private static final int DB_VERSION = 1;// 数据库版本号

		private static final String KEY_USERNAME = "UserName";
		private static final String KEY_PASSWORD = "PassWord";
		private static final String KEY_REMB_PWD = "RembPwd";
		private static final String KEY_LAST_ACC = "LastAcc";

		private static final String CREATE_TABLE_SQL = "CREATE TABLE "
				+ DB_TABLE + " (" + KEY_USERNAME
				+ " VARCHAR(32) NOT NULL PRIMARY KEY, " + KEY_PASSWORD
				+ " VARCHAR(32) NOT NULL, " + KEY_REMB_PWD
				+ " BIT(1) DEFAULT 1, " + KEY_LAST_ACC + " BIT(1) DEFAULT 0);";

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 第一个使用数据库时自动建表
			db.execSQL(CREATE_TABLE_SQL);
		}

		/**
		 * 
		 * 函数在数据库需要升级时被调用， 一般用来删除旧的数据库表，并将数据转移到新版本的数据库表中
		 * 
		 **/
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SQL);
			onCreate(db);
			if (D)
				Log.i(TAG, "Upgrade");
		}
	}

}
