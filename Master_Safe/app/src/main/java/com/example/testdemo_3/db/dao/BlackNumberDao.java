package com.example.testdemo_3.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testdemo_3.db.BlackNumberOpenHelper;
import com.example.testdemo_3.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;

    //BlsckNumberDao单例模式
    //1，私有化构造方法
    private BlackNumberDao(Context context) {
        //创建数据库及其表结构
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //2，声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;

    //3.提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }


    /**
     * 增加一个条目
     *
     * @param phone 拦截的电话号码
     * @param mode  拦截类型（1：短信，2：电话，3：短信 + 电话）
     */
    public void insert(String phone, String mode) {
        //1，开启数据库，准备写入工作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);

        db.close();
    }

    //从数据库中删除一条电话号码
    //@param phone 删除电话号码
    public void delete(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        db.delete("blacknumber", "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 修改一个条目
     *
     * @param phone 待修改的条目对应的点好号码
     * @param mode  将要修改的拦截类型（1：短信，2：电话，3：短信 + 电话）
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);

        db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 查询全部条目
     *
     * @return 从数据库中查询到的所有的号码以及拦截类型所在的集合
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"},
                null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    /*
     * */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new String[]{index + ""});
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }
    /**
     * 获取数据表中的数据条数
     * @return 数据表中的数据总条数
     */
    public int getCount() {
        // 0.初始化数据条数
        int count = 0;
        // 1.开启数据库，准备进行写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        // 2.查询数据
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        // 3.循环读取全部数据
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        // 4.关闭游标和数据流
        cursor.close();
        db.close();
        // 5.返回数据集合
        return count;
    }

     /*根据电话号码获取拦截类型
     @param phone 电话号码
     @return 返回的拦截模式
     */
    public int getMode(String phone){
        int mode = 0;
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber",new String[]{"mode"},"phone = ?",new String[]{phone},null,null,null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
