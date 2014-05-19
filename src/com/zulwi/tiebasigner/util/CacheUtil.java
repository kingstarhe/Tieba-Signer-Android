package com.zulwi.tiebasigner.util;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.zulwi.tiebasigner.db.CacheDBHelper;

public class CacheUtil {
	public class UserCacheUtil {
		private int uid;
		private CacheDBHelper dbHelper;
		private Map<String, String> userCache = new HashMap<String, String>();

		public UserCacheUtil(int uid) {
			this.uid = uid;
			this.dbHelper = new CacheDBHelper(null);
			Cursor cursor = dbHelper.rawQuery("SELECT key, value FROM user_cache WHERE  uid=" + this.uid, null);
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
				userCache.put(cursor.getString(0), cursor.getString(1));
			}
		}

		public String getDataCache(String key) {
			return userCache.get(key);
		}

		public boolean saveDataCache(String key, String value) {
			Cursor cursor = dbHelper.rawQuery("SELECT id, value FROM user_cache WHERE  uid=" + this.uid + " AND key=\'" + key + "\'", null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				dbHelper.execSQL("UPDATE user_cache SET value=\'" + value + "\' WHERE id=" + cursor.getInt(0));
			} else {
				ContentValues values = new ContentValues();
				values.put("key", key);
				values.put("value", value);
				long id = dbHelper.insert("user_cache", values);
				if (id < 0) return false;
			}
			return true;
		}

		public void deleteDataCache(String key) {
			dbHelper.execSQL("DELETE FROM user_cache WHERE uid=" + this.uid + " AND key=\'" + key + "\'");
		}

		public byte[] getImgCache(String key) {
			Cursor cursor = dbHelper.rawQuery("SELECT value FROM img_cache WHERE  uid=" + this.uid + " AND key=\'" + key + "\'", null);
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getBlob(0);
			} else return null;
		}

		public boolean saveImgCache(String key, Bitmap img) {
			Cursor cursor = dbHelper.rawQuery("SELECT id, value FROM img_cache WHERE  uid=" + this.uid + " AND key=\'" + key + "\'", null);
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
				if (id < 0) return false;
			}
			return true;
		}

		public void destruct() {
			if (dbHelper != null) dbHelper.close();
		}

		protected void finalize() {
			destruct();
		}
	}
}
