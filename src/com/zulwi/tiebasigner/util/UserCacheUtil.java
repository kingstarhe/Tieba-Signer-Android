package com.zulwi.tiebasigner.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zulwi.tiebasigner.db.CacheDBHelper;

public class UserCacheUtil {
	private int uid;
	private int sid;
	private Context context;
	private Map<String, String> userCache = new HashMap<String, String>();

	public UserCacheUtil(Context context, int sid, int uid) {
		this.context = context;
		this.uid = uid;
		this.sid = sid;
		CacheDBHelper dbHelper = new CacheDBHelper(this.context);
		Cursor cursor = dbHelper.rawQuery("SELECT key, value FROM user_cache WHERE  uid=" + this.uid + " AND sid=" + this.sid, null);
		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			userCache.put(cursor.getString(0), cursor.getString(1));
		}
		dbHelper.close();
	}

	public String getDataCache(String key) {
		return userCache.get(key);
	}

	public boolean saveDataCache(String key, String value) {
		CacheDBHelper dbHelper = new CacheDBHelper(this.context);
		Cursor cursor = dbHelper.rawQuery("SELECT id, value FROM user_cache WHERE  sid=" + sid + " AND uid=" + this.uid + " AND key=\'" + key + "\'", null);
		ContentValues values = new ContentValues();
		values.put("key", key);
		values.put("value", value);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			dbHelper.update("user_cache", values, "id=" + cursor.getInt(0));
			dbHelper.execSQL("UPDATE user_cache SET value=\'" + value + "\' WHERE id=" + cursor.getInt(0));
		} else {
			values.put("sid", sid);
			values.put("uid", uid);
			long id = dbHelper.insert("user_cache", values);
			if (id < 0) {
				dbHelper.close();
				return false;
			}
		}
		dbHelper.close();
		return true;
	}

	public void deleteDataCache(String key) {
		CacheDBHelper dbHelper = new CacheDBHelper(this.context);
		dbHelper.execSQL("DELETE FROM user_cache WHERE key=\'" + key + "\'");
		dbHelper.close();
	}

	public static Bitmap getImgCache(String key, Context context) {
		CacheDBHelper dbHelper = new CacheDBHelper(context);
		Cursor cursor = dbHelper.rawQuery("SELECT value FROM img_cache WHERE key=\'" + key + "\'", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			byte[] img = cursor.getBlob(0);
			dbHelper.close();
			return BitmapFactory.decodeByteArray(img, 0, img.length);
		} else {
			dbHelper.close();
			return null;
		}
	}

	public static boolean saveImgCache(String key, Bitmap img, Context context) {
		CacheDBHelper dbHelper = new CacheDBHelper(context);
		Cursor cursor = dbHelper.rawQuery("SELECT id, value FROM img_cache WHERE key=\'" + key + "\'", null);
		ContentValues values = new ContentValues();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.PNG, 100, os);
		values.put("value", os.toByteArray());
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			dbHelper.update("user_cache", values, "WHERE id=" + cursor.getInt(0));
		} else {
			values.put("key", key);
			long id = dbHelper.insert("user_cache", values);
			if (id < 0) {
				dbHelper.close();
				return false;
			}
		}
		dbHelper.close();
		return true;
	}
}
