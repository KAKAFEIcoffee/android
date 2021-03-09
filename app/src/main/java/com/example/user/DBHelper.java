/*******************************************************/
/**********继承SQLiteOpenHelper，生成DBhelper类**********/
/*******************************************************/
package com.example.user;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "user.db";
    public final static int VERSION = 1;
    private static DBHelper instance = null;
    private SQLiteDatabase db;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private void openDatabase() {
        if(db == null) {
            db = getWritableDatabase();
        }
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /** 第一次安装程序后创建数据库 */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists tb_user(_id integer primary key autoincrement,name text,sound_and int,setting int,picture text,sound_wav text)");
        //id 编号 name 昵称 sound_and 语音控制(11代表switch_sound和switch_sound_movie均打开) setting 设置(1111代表spinner_back,spinner_forward,spinner_speed,spinner_speed_均选1)
        //picture 头像(地址) sound_wav 录音文件(地址)
    }

    /** 版本升级时，先删除原有的数据库，再重新创建数据库 */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists tb_user");
        onCreate(db);
    }

    /** 添加一条数据 */
    public long saveLamp(Userdata user) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("_id", user.get_id());
        value.put("name", user.getname());
        value.put("sound_and", user.getsound_and());
        value.put("setting", user.getsetting());
        value.put("picture", user.getpicture());
        value.put("sound_wav", user.getsound_wav());
        return db.insert("tb_user", null, value);
    }

    /** 根据id删除数据 */
    public int deleteLamp(int id) {
        openDatabase();
        return db.delete("tb_user", "_id=?", new String[] { String.valueOf(id) });
    }

    /** 根据id更新数据 */
    public int updateLamp(Userdata user, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("name", user.getname());
        value.put("sound_and", user.getsound_and());
        value.put("setting", user.getsetting());
        value.put("picture", user.getpicture());
        value.put("sound_wav", user.getsound_wav());
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }
    public int updateLamp_name(String name, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("name", name);
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }
    public int updateLamp_sound_and(int sound_and, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("sound_and", sound_and);
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }
    public int updateLamp_setting(int setting, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("setting", setting);
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }
    public int updateLamp_picture(String picture, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("picture", picture);
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }
    public int updateLamp_sound_wav(String sound_wav, int id) {
        openDatabase();
        ContentValues value = new ContentValues();
        value.put("sound_wav", sound_wav);
        return db.update("tb_user", value, "_id=?", new String[] { String.valueOf(id) });
    }


    /** 查询所有数据 */
    public List<Userdata> getLampList() {
        openDatabase();
        Cursor cursor = db.query("tb_user", null, null, null, null, null, null);
        List<Userdata> list = new ArrayList<Userdata>();
        while (cursor.moveToNext()) {
            Userdata user = new Userdata();
            user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setname(cursor.getString(cursor.getColumnIndex("name")));
            user.setsound_and(cursor.getInt(cursor.getColumnIndex("sound_and")));
            user.setsetting(cursor.getInt(cursor.getColumnIndex("setting")));
            user.setpicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setsound_wav(cursor.getString(cursor.getColumnIndex("sound_wav")));
            list.add(user);
        }
        return list;
    }

    /** 根据id查询数据 */
    public Userdata getALamp_id(String  id) {
        openDatabase();
        Cursor cursor = db.query("tb_user", null, "_id=?", new String[] { id }, null, null, null);
        Userdata user = new Userdata();
        while (cursor.moveToNext()) {
            user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setname(cursor.getString(cursor.getColumnIndex("name")));
            user.setsound_and(cursor.getInt(cursor.getColumnIndex("sound_and")));
            user.setsetting(cursor.getInt(cursor.getColumnIndex("setting")));
            user.setpicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setsound_wav(cursor.getString(cursor.getColumnIndex("sound_wav")));
        }
        return user;
    }

    /** 根据昵称查询数据 */
    public Userdata getALamp_name(String name) {
        openDatabase();
        Cursor cursor = db.query("tb_user", null, "name=?", new String[] { name }, null, null, null);
        Userdata user = new Userdata();
        while (cursor.moveToNext()) {
            user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setname(cursor.getString(cursor.getColumnIndex("name")));
            user.setsound_and(cursor.getInt(cursor.getColumnIndex("sound_and")));
            user.setsetting(cursor.getInt(cursor.getColumnIndex("setting")));
            user.setpicture(cursor.getString(cursor.getColumnIndex("picture")));
            user.setsound_wav(cursor.getString(cursor.getColumnIndex("sound_wav")));
        }
        return user;
    }

    /** 查询有多少条记录 */
    public int getLampCount() {
        openDatabase();
        Cursor cursor = db.query("tb_user", null, null, null, null, null, null);
        return cursor.getCount();
    }

    /*关闭数据库*/
    public void close() {
        db.close();
    }
}