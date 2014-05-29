package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;

public class MultiSignFragment extends Fragment {
	private LinearLayout signLogOneKeySignButton;
	private MainActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log_multisign, container, false);
		signLogOneKeySignButton = (LinearLayout) view.findViewById(R.id.sign_log_sign);
		signLogOneKeySignButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(activity, getString(R.string.maimeng) + " 这个功能还在开发中", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

}
