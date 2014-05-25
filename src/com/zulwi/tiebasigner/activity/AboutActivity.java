package com.zulwi.tiebasigner.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.zulwi.tiebasigner.R;

public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_about);
	}

	public void openAuthorWebSite(View view) {
		openWebSite("http://jerrys.me");
	}

	public void openBaiduClassAuthorWebSite(View view) {
		openWebSite("http://www.baidu.com/p/%E6%98%9F%E5%BC%A6%E9%9B%AA");
	}

	public void openTesterWebSite(View view) {
		openWebSite("http://linzian.us");
	}

	public void openStudioWebSite(View view) {
		openWebSite("http://www.zhuwei.cc");
	}

	private void openWebSite(String url) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}