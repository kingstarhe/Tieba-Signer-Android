package com.zulwi.tiebasigner.fragment;

import com.zulwi.tiebasigner.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zulwi.tiebasigner.activity.AboutActivity;
import com.zulwi.tiebasigner.activity.EditAccountActivity;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.view.CircularImage;

public class SettingFragment extends Fragment implements View.OnClickListener {
	private MainActivity activity;
	private AccountBean accountBean;
	private TextView usernameTextView;
	private TextView emailTextView;
	private TextView siteNameTextView;
	private TextView siteUrlTextView;
	private CircularImage avatarCircularImage;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.accountBean = this.activity.getAccountBean();
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
		siteNameTextView.setText(activity.getAccountBean().siteName);
		siteUrlTextView.setText(activity.getAccountBean().siteUrl);
		avatarCircularImage = (CircularImage) view.findViewById(R.id.setting_avatar);
		Bitmap avatar = accountBean.getAvatar();
		if (avatar != null) avatarCircularImage.setImageBitmap(avatar);
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
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.setting_userinfo:
				intent = new Intent(activity, EditAccountActivity.class);
				AccountBean currentAccountBean = accountBean;
				currentAccountBean.avatar = null;
				intent.putExtra("currentAccountBean", currentAccountBean);
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
				intent = new Intent(activity, AboutActivity.class);
				break;
		}
		if (intent != null) startActivity(intent);
		else Toast.makeText(activity, getString(R.string.maimeng) + " 这个功能还在开发中", Toast.LENGTH_SHORT).show();
	}

	public void setAvatar(Bitmap avatar) {
		if (avatar != null) avatarCircularImage.setImageBitmap(avatar);
	}

}
