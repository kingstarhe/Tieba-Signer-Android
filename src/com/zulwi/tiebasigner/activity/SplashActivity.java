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
			AccountBean accountBean = new AccountBean(accountCursor.getInt(0), accountCursor.getInt(1), accountCursor.getInt(2), accountCursor.getString(3), accountCursor.getString(4), accountCursor.getString(5), accountCursor.getInt(6), accountCursor.getString(7), accountCursor.getString(8));
			accountCursor.close();
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			intent.putExtra("accountBean", accountBean);
			startActivity(intent);
		} else {
			Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
			SplashActivity.this.startActivity(intent);
		}
		dbHelper.close();
		finish();
	}

	@Override
	public void onBackPressed() {
		return;
	}
}