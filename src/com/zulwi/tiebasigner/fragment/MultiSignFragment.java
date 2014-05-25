package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.fragment.LogFragment.setTitleInterFace;

public class MultiSignFragment extends BaseFragment implements setTitleInterFace {
	private LinearLayout signLogOneKeySignButton;
	private LogFragment fragment;
	private MainActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.fragment = (LogFragment) getParentFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log_multisign, container, false);
		signLogOneKeySignButton = (LinearLayout) view.findViewById(R.id.sign_log_sign);
		signLogOneKeySignButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		fragment.currentFragmentId = 1;
		setTitle();
	}

	public void setTitle() {
		activity.setTitle("一键签到");
	}
}
