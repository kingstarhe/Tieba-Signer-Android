package com.zulwi.tiebasigner.fragment;

import com.zulwi.tiebasigner.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.view.CircularImage;

public class SettingFragment extends Fragment implements View.OnClickListener{
	private MainActivity activity;
	private TextView usernameTextView;
	private TextView emailTextView;
	private TextView siteNameTextView;
	private TextView siteUrlTextView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		usernameTextView = (TextView) view.findViewById(R.id.setting_username);
		emailTextView = (TextView) view.findViewById(R.id.setting_email);
		siteNameTextView = (TextView) view.findViewById(R.id.setting_sitename);
		siteUrlTextView = (TextView) view.findViewById(R.id.setting_siteurl);
		usernameTextView.setText(activity.getAccountBean().username);
		emailTextView.setText(activity.getAccountBean().email);
		siteNameTextView.setText(activity.getSiteBean().name);
		siteUrlTextView.setText(activity.getSiteBean().url);
		((CircularImage) view.findViewById(R.id.setting_avatar)).setImageResource(R.drawable.avatar);
		view.findViewById(R.id.setting_userinfo).setOnClickListener(this);
		view.findViewById(R.id.setting_siteinfo).setOnClickListener(this);
		view.findViewById(R.id.setting_remind).setOnClickListener(this);
		view.findViewById(R.id.setting_clear_cache).setOnClickListener(this);
		view.findViewById(R.id.setting_faq).setOnClickListener(this);
		view.findViewById(R.id.setting_donate).setOnClickListener(this);
		view.findViewById(R.id.setting_about).setOnClickListener(this);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
    public void onClick(View v) {
	    switch(v.getId()){
	    	case R.id.setting_userinfo:
	    		break;
	    	case R.id.setting_siteinfo:
	    		break;
	    	case R.id.setting_remind:
	    		break;
	    	case R.id.setting_faq:
	    		break;
	    	case R.id.setting_donate:
	    		break;
	    	case R.id.setting_about:
	    		break;
	    }
    }

}
