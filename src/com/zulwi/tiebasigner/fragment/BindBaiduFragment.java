package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.BindBaiduActivity;
import com.zulwi.tiebasigner.activity.MainActivity;

public class BindBaiduFragment extends Fragment implements OnClickListener, OnRefreshListener{
	private MainActivity activity;
	protected SwipeRefreshLayout swipeLayout;
	private AccountFragment fragment;	
	
	public BindBaiduFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.fragment = (AccountFragment)getParentFragment();
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
	public void onClick(View v) {
		startActivityForResult(new Intent(activity, BindBaiduActivity.class), 1);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
    public void onRefresh() {
		fragment.refreshUserInfo();
    }

}
