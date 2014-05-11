package com.zulwi.tiebasigner.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.FragmentBean;
import com.zulwi.tiebasigner.fragment.PluginFragment;
import com.zulwi.tiebasigner.fragment.SettingFragment;
import com.zulwi.tiebasigner.fragment.SignLogFragment;
import com.zulwi.tiebasigner.fragment.UserInfoFragment;

public class MainActivity extends FragmentActivity {
	private FragmentManager fm;
	private List<FragmentBean> fragmentList = new ArrayList<FragmentBean>();
	private int currentFragmentId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		fragmentList.add(new FragmentBean("资料", new UserInfoFragment()));
		fragmentList.add(new FragmentBean("记录", new SignLogFragment()));
		fragmentList.add(new FragmentBean("插件", new PluginFragment()));
		fragmentList.add(new FragmentBean("设置", new SettingFragment()));
		fm = getSupportFragmentManager();
		changeFragment(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) { return true; }
		return super.onOptionsItemSelected(item);
	}

	private void changeFragment(int position) {
		Fragment oldFragment = fragmentList.get(currentFragmentId).fragment;
		Fragment newFragment = fragmentList.get(position).fragment;
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (oldFragment == newFragment && !newFragment.isAdded()) ft.add(R.id.fragment_container, newFragment);
		else ft = newFragment.isAdded() ? ft.hide(oldFragment).show(newFragment) : ft.hide(oldFragment).add(R.id.fragment_container, newFragment);
		ft.commit();
		currentFragmentId = position;
	}

	public void bottomBarButtonOnClick(View v) {
		switch (v.getId()) {
			case R.id.userinfo_button:
				changeFragment(0);
				break;
			case R.id.signlog_button:
				changeFragment(1);
				break;
			case R.id.plugin_button:
				changeFragment(2);
				break;
			case R.id.setting_button:
				changeFragment(3);
				break;
		}
	}

}
