package com.zulwi.tiebasigner.db;

import android.content.Context;

public class SitesDBHelper extends DBHelper {
	@SuppressWarnings("unused")
	private static String createSql= "create table if not exists sites("
			+ "id integer primary key autoincrement,name varchar(20) unique,"
			+ "url varchar(60) unique)";
	private static String tableName= "sites";
	public SitesDBHelper(Context context) {
		super(context, tableName);
	}
}