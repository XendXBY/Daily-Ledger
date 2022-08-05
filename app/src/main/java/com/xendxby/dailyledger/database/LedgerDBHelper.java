package com.xendxby.dailyledger.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xendxby.dailyledger.entity.LedgerItemInfo;
import com.xendxby.dailyledger.util.BigDecimalUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LedgerDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ledger.db";
    private static final String TABLE_NAME = "items";
    private static final int DB_VERSION = 1;
    private static LedgerDBHelper mHelper = null;

    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private LedgerDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 单例模式创建帮助器的唯一实例
    public static LedgerDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new LedgerDBHelper(context);
        }

        return mHelper;
    }

    // 打开数据库读链接
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    // 打开数据库写链接
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    // 关闭数据库链接
    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }

        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " time VARCHAR NOT NULL," +
                " type INTEGER NOT NULL," +          // type: 条目的类型 0：支出 1：收入
                " category INTEGER NOT NULL," +
                " remark VARCHAR," +
                " price FLOAT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void insertLedgerItem(LedgerItemInfo itemInfo) {
        ContentValues values = new ContentValues();
        values.put("time", itemInfo.time);
        values.put("type", itemInfo.getTypeIntValue());
        values.put("category", itemInfo.category);
        values.put("remark", itemInfo.remark);
        values.put("price", itemInfo.price.floatValue());

        mWDB.insert(TABLE_NAME, null, values);
    }

    public void deleteLedgerItem(int id) {
        mWDB.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
    }

    public List<LedgerItemInfo> getAllLedgerItems() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<LedgerItemInfo> infos = new ArrayList<>();

        Cursor cursor = mRDB.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            LedgerItemInfo info = new LedgerItemInfo(
                    cursor.getInt(0),
                    cursor.getString(1),
                    LedgerItemInfo.RecordTypeEnum.OUTCOME,
                    cursor.getInt(3),
                    cursor.getString(4),
                    BigDecimalUtil.floatToBigDecimal(cursor.getFloat(5))
            );
            info.setTypeByIntValue(cursor.getInt(2));
            infos.add(info);
        }

        cursor.close();
        return infos;
    }
}
