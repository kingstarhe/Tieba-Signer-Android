package com.zulwi.tiebasigner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.zulwi.tiebasigner.db.CacheDBHelper;

public class CacheUtil {
	private int uid;
	private int sid;
	private Context context;
	private Map<String, String> userCache = new HashMap<String, String>();
	private final static String sdcardPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
	private final static String appPath = sdcardPath + "/Zulwi/TiebaSigner";
	private final static String imgCachePath = appPath + "/cache/img";
	private final static String avatarCachePath = imgCachePath + "/avatar";

	{
		File dir = new File(avatarCachePath);
		if (!dir.exists()) dir.mkdirs();
		dir = new File(avatarCachePath, ".nomedia");
		if (!dir.exists()) {
			try {
				dir.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public CacheUtil(Context context, int sid, int uid) {
		this.context = context;
		this.uid = uid;
		this.sid = sid;
		CacheDBHelper dbHelper = new CacheDBHelper(this.context);
		Cursor cursor = dbHelper.rawQuery("SELECT key, value FROM user_cache WHERE  uid=" + this.uid + " AND sid=" + this.sid, null);
		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			userCache.put(cursor.getString(0), cursor.getString(1));
		}
		cursor.close();
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
			cursor.close();
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
		dbHelper.execSQL("DELETE FROM user_cache WHERE key=\'" + key + "\' AND sid=" + sid + " AND uid=" + uid);
		dbHelper.close();
	}

	public void deleteAllDataCache() {
		CacheDBHelper dbHelper = new CacheDBHelper(this.context);
		dbHelper.execSQL("DELETE FROM user_cache WHERE sid=" + sid + " AND uid=" + uid);
		dbHelper.close();
	}

	public static Bitmap getAvatarCache(String key, Context context) {

		String avatarFilePath = avatarCachePath + "/" + key + ".png";
		File avatarFile = new File(avatarFilePath);
		if (avatarFile.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(avatarFilePath);
			return bm;
		}
		return null;
	}

	public static boolean saveAvatarCache(String key, Bitmap img, Context context) {
		File avatarFile = new File(avatarCachePath, key + ".png");
		if (avatarFile.exists()) avatarFile.delete();
		try {
			FileOutputStream out = new FileOutputStream(avatarFile);
			img.compress(CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
