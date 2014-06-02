package com.zulwi.tiebasigner.activity;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.zulwi.tiebasigner.util.DialogUtil;

public class MainActivity extends ActionBarActivity {
	private FragmentManager fm;
	private FragmentBean[] fragments = new FragmentBean[4];
	private Button[] bottonBarButtons = new Button[4];
	private int currentFragmentId = 0;
	private static long lastClickTime;
	private AccountBean accountBean;
	private Dialog dialog;

	private BroadcastReceiver finishMainBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(MainActivity.this, intent.getStringExtra("message"), Toast.LENGTH_LONG).show();
			unregisterReceiver(finishMainBroadcastReceiver);
			unregisterReceiver(switchAccountBroadcastReceiver);
			finish();
		}
	};

	private BroadcastReceiver switchAccountBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent newIntent = new Intent(MainActivity.this, MainActivity.class);
			newIntent.putExtra("accountBean", intent.getSerializableExtra("accountBean"));
			unregisterReceiver(finishMainBroadcastReceiver);
			unregisterReceiver(switchAccountBroadcastReceiver);
			finish();
			startActivity(newIntent);
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
		fragments[0] = new FragmentBean("账号", new AccountFragment());
		fragments[1] = new FragmentBean("记录", new LogFragment());
		fragments[2] = new FragmentBean("插件", new PluginFragment());
		fragments[3] = new FragmentBean("设置", new SettingFragment());
		fm = getSupportFragmentManager();
		bottonBarButtons[0] = (Button) findViewById(R.id.userinfo_button);
		bottonBarButtons[1] = (Button) findViewById(R.id.signlog_button);
		bottonBarButtons[2] = (Button) findViewById(R.id.plugin_button);
		bottonBarButtons[3] = (Button) findViewById(R.id.setting_button);
		bottonBarButtons[0].performClick();
		dialog = DialogUtil.createLoadingDialog(this, "加载中，请稍后", false);
		dialog.show();
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
		IntentFilter finishMainBroadcastReceiverIntentFilter = new IntentFilter();
		finishMainBroadcastReceiverIntentFilter.addAction("com.zulwi.tiebasigner.FINISH_MAIN");
		registerReceiver(finishMainBroadcastReceiver, finishMainBroadcastReceiverIntentFilter);
		IntentFilter switchAccountBroadcastReceiverIntentFilter = new IntentFilter();
		switchAccountBroadcastReceiverIntentFilter.addAction("com.zulwi.tiebasigner.SWITCH_ACCOUNT");
		registerReceiver(switchAccountBroadcastReceiver, switchAccountBroadcastReceiverIntentFilter);
	}

	public void changeFragment(int position) {
		FragmentBean from = fragments[currentFragmentId];
		FragmentBean to = fragments[position];
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
		for (int i = 0; i < bottonBarButtons.length; i++) {
			bottonBarButtons[i].setEnabled(position == i ? false : true);
		}
	}

	public void onBottomBarButtonClick(View v) {
		if (isFastDoubleClick()) return;
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
		accountBean.setAvatar(avatar);
		Fragment settingFragment = fragments[3].fragment;
		if (settingFragment.isAdded()) ((SettingFragment) settingFragment).setAvatar(avatar);
	}

	public int getCurrentFragmentId() {
		return currentFragmentId;
	}

	public void showLoadingDialog(boolean show) {
		if (show) dialog.show();
		else dialog.dismiss();
	}

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) { return true; }
		lastClickTime = time;
		return false;
	}

}