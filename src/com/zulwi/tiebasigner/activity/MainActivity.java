package com.zulwi.tiebasigner.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.FragmentBean;
import com.zulwi.tiebasigner.fragment.AccountFragment;
import com.zulwi.tiebasigner.fragment.LogFragment;
import com.zulwi.tiebasigner.fragment.PluginFragment;
import com.zulwi.tiebasigner.fragment.SettingFragment;

public class MainActivity extends ActionBarActivity {
	private FragmentManager fm;
	private List<FragmentBean> fragmentList = new ArrayList<FragmentBean>();
	private List<Button> bottonBarButton = new ArrayList<Button>();
	private int currentFragmentId = 0;
	private AccountBean accountBean;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(MainActivity.this, "请重新登录", Toast.LENGTH_LONG).show();
			unregisterReceiver(broadcastReceiver);
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_main);
		accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
		System.out.println(accountBean.formhash);
		fragmentList.add(new FragmentBean("账号", new AccountFragment()));
		fragmentList.add(new FragmentBean("记录", new LogFragment()));
		fragmentList.add(new FragmentBean("插件", new PluginFragment()));
		fragmentList.add(new FragmentBean("设置", new SettingFragment()));
		fm = getSupportFragmentManager();
		bottonBarButton.add((Button) findViewById(R.id.userinfo_button));
		bottonBarButton.add((Button) findViewById(R.id.signlog_button));
		bottonBarButton.add((Button) findViewById(R.id.plugin_button));
		bottonBarButton.add((Button) findViewById(R.id.setting_button));
		bottonBarButton.get(0).performClick();
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle("确定要退出客户端吗？");
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.zulwi.tiebasigner.LOGOUT");
		registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {
			((AccountFragment) fragmentList.get(0).fragment).refreshUserInfo();
		}
	}

	public void changeFragment(int position) {
		FragmentBean from = fragmentList.get(currentFragmentId);
		FragmentBean to = fragmentList.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (from == to && !to.fragment.isAdded()) ft.add(R.id.fragment_container, to.fragment);
		else ft = to.fragment.isAdded() ? ft.hide(from.fragment).show(to.fragment) : ft.hide(from.fragment).add(R.id.fragment_container, to.fragment);
		ft.commit();
		if (to.fragment.isAdded() && position == 1) ((LogFragment) to.fragment).setTitle();
		else setTitle(to.title);
		currentFragmentId = position;
	}

	public void setEnabled(int position) {
		for (int i = 0; i < bottonBarButton.size(); i++) {
			bottonBarButton.get(i).setEnabled(position == i ? false : true);
		}
	}

	public void onBottomBarButtonClick(View v) {
		switch (v.getId()) {
			case R.id.userinfo_button:
				setEnabled(0);
				changeFragment(0);
				break;
			case R.id.signlog_button:
				setEnabled(1);
				changeFragment(1);
				break;
			case R.id.plugin_button:
				setEnabled(2);
				changeFragment(2);
				break;
			case R.id.setting_button:
				setEnabled(3);
				changeFragment(3);
				break;
		}
	}

	public AccountBean getAccountBean() {
		return accountBean;
	}

	public void setAccountAvatar(Bitmap avatar) {
		accountBean.avatar = avatar;
		Fragment settingFragment = fragmentList.get(3).fragment;
		if (settingFragment.isAdded()) ((SettingFragment) settingFragment).setAvatar(avatar);
	}

	public int getCurrentFragmentId() {
		return currentFragmentId;
	}

}