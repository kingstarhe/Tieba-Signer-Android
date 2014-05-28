package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zulwi.tiebasigner.R;

public class MultiSignFragment extends Fragment {
	private LinearLayout signLogOneKeySignButton;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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

}
