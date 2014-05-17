package com.zulwi.tiebasigner.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				checkLogin();
			}
		}, 2000);
	}

	public void checkLogin() {
		BaseDBHelper dbHelper = new BaseDBHelper(this);
		Cursor accountCursor = dbHelper.rawQuery("SELECT accounts.*, sites.name, sites.url FROM accounts LEFT JOIN sites ON accounts.sid=sites.id WHERE accounts.current=1 LIMIT 1;", null);
		if (accountCursor.getCount() > 0) {
			accountCursor.moveToFirst();
			System.out.println("----------------------------");
			System.out.println("id:" + accountCursor.getInt(0));
			System.out.println("sid:" + accountCursor.getInt(1));
			System.out.println("username:" + accountCursor.getString(2));
			System.out.println("email:" + accountCursor.getString(3));
			System.out.println("cookie:" + accountCursor.getString(4));
			System.out.println("current:" + accountCursor.getInt(5));
			System.out.println("name:" + accountCursor.getString(6));
			System.out.println("url:" + accountCursor.getString(7));
			AccountBean accountBean = new AccountBean(accountCursor.getInt(0), accountCursor.getInt(1), accountCursor.getString(2), accountCursor.getString(3), accountCursor.getString(4), accountCursor.getInt(5), accountCursor.getString(6), accountCursor.getString(7));
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.putExtra("accountBean", accountBean);
			startActivity(intent);
		} else {
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			SplashActivity.this.startActivity(intent);
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		return;
	}
}