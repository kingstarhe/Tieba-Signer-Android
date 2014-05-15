package com.zulwi.tiebasigner.activity;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

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
		Cursor accountCursor = dbHelper.rawQuery("select * from accounts where current=1", null);
		if (accountCursor.getCount() > 0) {
			accountCursor.moveToFirst();
			int sid = accountCursor.getInt(1);
			Cursor siteCursor = dbHelper.rawQuery("select * from sites where id=" + sid, null);
			String siteUrl = "";
			if (siteCursor.getCount() > 0) {
				siteCursor.moveToFirst();
				siteUrl = siteCursor.getString(2);
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				intent.putExtra("accountBean", new AccountBean(accountCursor.getString(2), accountCursor.getString(3), siteUrl, accountCursor.getString(4)));
				intent.putExtra("siteBean", new SiteBean(siteCursor.getInt(0), siteCursor.getString(1), siteCursor.getString(2)));
				startActivity(intent);
			}
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
