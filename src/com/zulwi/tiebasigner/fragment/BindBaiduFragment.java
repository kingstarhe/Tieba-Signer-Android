package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.BindBaiduActivity;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.bean.AccountBean;

public class BindBaiduFragment extends BaseFragment implements OnClickListener, OnRefreshListener {
	private MainActivity activity;
	protected SwipeRefreshLayout swipeLayout;
	private AccountFragment fragment;
	private AccountBean accountBean;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			swipeLayout.setRefreshing(true);
			onRefresh();
		}
	};

	public BindBaiduFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.fragment = (AccountFragment) getParentFragment();
		this.accountBean = this.activity.getAccountBean();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_bindbaidu, container, false);
		view.findViewById(R.id.bind_now).setOnClickListener(this);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.zulwi.tiebasigner.REFRESH_USERINFO");
		activity.registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(activity, BindBaiduActivity.class);
		System.out.println(accountBean.cookieString);
		intent.putExtra("accountBean", new AccountBean(accountBean.uid, accountBean.username, accountBean.email, accountBean.siteUrl, accountBean.cookieString, accountBean.formhash));
		startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 1) {
			swipeLayout.setRefreshing(true);
			onRefresh();
		}
	}

	@Override
	public void onRefresh() {
		fragment.refreshUserInfo();
	}
	
	@Override
	public void onDestroy() {
		activity.unregisterReceiver(broadcastReceiver);
	    super.onDestroy();
	}

}
