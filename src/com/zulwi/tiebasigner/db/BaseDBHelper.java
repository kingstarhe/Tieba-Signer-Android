package com.zulwi.tiebasigner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDBHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "database.db";
	private final static int VERSION = 1;
	private SQLiteDatabase db;

	public BaseDBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL("CREATE TABLE IF NOT EXISTS sites(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20) UNIQUE, url VARCHAR(60) UNIQUE, info TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS accounts(id INTEGER PRIMARY KEY AUTOINCREMENT, sid INTEGER, uid INTEGER, username VARCHAR(30), email VARCHAR(60), cookie VARCHAR(1000), formhash CHAR(8), current SMALLINT(1))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long insert(String tableName, ContentValues values) {
		db = getWritableDatabase();
		return db.insert(tableName, null, values);
	}

	public int update(String tableName, ContentValues values, String whereClause) {
		db = getWritableDatabase();
		return db.update(tableName, values, whereClause, null);
	}

	public int update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
		return db.update(tableName, values, whereClause, whereArgs);
	}

	public Cursor query(String tableName) {
		db = getWritableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null, null);
		return cursor;
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	public void execSQL(String sql) {
		db = getWritableDatabase();
		db.execSQL(sql);
	}

	public int delete(String tableName, long id) {
		if (db == null) db = getWritableDatabase();
		return db.delete(tableName, "id=?", new String[] { String.valueOf(id) });
	}

	public void deleteAll(String tableName) {
		execSQL("DELETE FROM " + tableName);
		execSQL("delete from sqlite_sequence where name=\'" + tableName + "\'");
	}

	@Override
	public void close() {
		if (db != null) db.close();
	}
}