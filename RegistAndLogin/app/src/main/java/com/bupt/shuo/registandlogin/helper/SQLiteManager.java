package com.bupt.shuo.registandlogin.helper;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 应用所需的数据库操作
 */
public class SQLiteManager extends SQLiteOpenHelper {

    private static final String TAG = SQLiteManager.class.getSimpleName();

    // 所有的静态变量
    // 数据库版本
    private static final int DATABASE_VERSION = 1;

    // 数据库名称
    private static final String DATABASE_NAME = "birth_android_api";

    // 表名
    private static final String TABLE_LOGIN = "login";

    // 列名
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // 更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果表存在则删除表
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // 重新创建表
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // 名字
        values.put(KEY_EMAIL, email); // 邮箱
        values.put(KEY_UID, uid); // 唯一id,用于标示用户
        values.put(KEY_CREATED_AT, created_at); // 创建时间

        // 插入用户信息
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * 从数据库获取用户信息
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * 获取用户的登陆状态,如果表中没有数据说明用户没有登陆
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * 从数据库中删除所有信息
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // 删除所有列
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
