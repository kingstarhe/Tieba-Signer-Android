package com.zulwi.tiebasigner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;

public class SignLogStatusFragment extends Fragment {
	private TextView signLogStatusTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_signlog_status, container, false);
		signLogStatusTextView = (TextView) view.findViewById(R.id.sign_log_status);
		signLogStatusTextView.setText("总计 79 个贴吧，其中 1 个贴吧签到失败");
		return view;
	}
}
