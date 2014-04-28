package com.zulwi.tiebasigner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static String databaseName = "database.db";
	private static int databaseVersion = 1;
	private static String createSql;
	@SuppressWarnings("unused")
	private Context context;
	private String tableName;
	private SQLiteDatabase db;

	public DBHelper(Context context, String tableName) {
		super(context, databaseName, null, databaseVersion);
		this.context = context;
		this.tableName = tableName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(createSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long insert(ContentValues values) {
		db = getWritableDatabase();
		return db.insert(tableName, null, values);
	}

	public int update(ContentValues values, String whereClause) {
		return update(values, whereClause, null);
	}

	public int update(ContentValues values, String whereClause,
			String[] whereArgs) {
		return db.update(tableName, values, whereClause, whereArgs);
	}

	public Cursor query() {
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

	public int delete(long id) {
		if (db == null)
			db = getWritableDatabase();
		return db
				.delete(tableName, "id=?", new String[] { String.valueOf(id) });
	}

	public void deleteAll() {
		execSQL("DELETE FROM " + tableName);
		execSQL("delete from sqlite_sequence where name=\'"+tableName+"\'");
	}

	@Override
	public void close() {
		if (db != null)
			db.close();
	}
}