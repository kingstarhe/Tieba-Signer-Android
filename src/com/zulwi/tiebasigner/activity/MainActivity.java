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
import android.widget.Button;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.FragmentBean;
import com.zulwi.tiebasigner.fragment.PluginFragment;
import com.zulwi.tiebasigner.fragment.SettingFragment;
import com.zulwi.tiebasigner.fragment.SignLogFragment;
import com.zulwi.tiebasigner.fragment.UserInfoFragment;

public class MainActivity extends FragmentActivity {
	private FragmentManager fm;
	private List<FragmentBean> fragmentList = new ArrayList<FragmentBean>();
	private List<Button> bottonBarButton = new ArrayList<Button>();
	private TextView titleTextView;
	private int currentFragmentId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		fragmentList.add(new FragmentBean("ÕËºÅ", new UserInfoFragment()));
		fragmentList.add(new FragmentBean("¼ÇÂ¼", new SignLogFragment()));
		fragmentList.add(new FragmentBean("²å¼þ", new PluginFragment()));
		fragmentList.add(new FragmentBean("ÉèÖÃ", new SettingFragment()));
		fm = getSupportFragmentManager();
		titleTextView = (TextView) findViewById(R.id.main_title);
		bottonBarButton.add((Button) findViewById(R.id.userinfo_button));
		bottonBarButton.add((Button) findViewById(R.id.signlog_button));
		bottonBarButton.add((Button) findViewById(R.id.plugin_button));
		bottonBarButton.add((Button) findViewById(R.id.setting_button));
		bottonBarButton.get(0).performClick();
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
		FragmentBean from = fragmentList.get(currentFragmentId);
		FragmentBean to = fragmentList.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		if (from == to && !to.fragment.isAdded()) ft.add(R.id.fragment_container, to.fragment);
		else ft = to.fragment.isAdded() ? ft.hide(from.fragment).show(to.fragment) : ft.hide(from.fragment).add(R.id.fragment_container, to.fragment);
		ft.commit();
		titleTextView.setText(to.title);
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

}
