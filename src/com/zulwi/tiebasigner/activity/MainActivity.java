package com.zulwi.tiebasigner.activity;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.fragment.NavigationDrawerFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private NavigationDrawerFragment navigationDrawerFragment;
	private CharSequence title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		title = getString(R.string.user_info);
		navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position, CharSequence title, Fragment from, Fragment to) {
		this.title = title;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (!to.isAdded() && from == to) ft.add(R.id.container, to);
		else ft = to.isAdded() ? ft.hide(from).show(to) : ft.hide(from).add(R.id.container, to);
		ft.commit();
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(title);
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle("确定退出客户端吗？");
		confirm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!navigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) return true;
		return super.onOptionsItemSelected(item);
	}

}
