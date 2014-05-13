package com.zulwi.tiebasigner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zulwi.tiebasigner.R;

public class SignLogSignFragment extends Fragment {
	private Button signLogOneKeySignButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_signlog_sign, container, false);
		signLogOneKeySignButton = (Button) view.findViewById(R.id.sign_log_sign);
		return view;
	}
}
