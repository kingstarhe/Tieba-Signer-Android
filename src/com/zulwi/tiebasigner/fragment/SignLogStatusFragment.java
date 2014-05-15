package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SignLogListAdapter;
import com.zulwi.tiebasigner.bean.TiebaBean;

public class SignLogStatusFragment extends Fragment {
	private TextView signLogStatusTextView;
	private ListView signLogListView;
	private List<TiebaBean> signLogList = new ArrayList<TiebaBean>();
	private SignLogListAdapter signLogAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_signlog_status, container, false);
		signLogStatusTextView = (TextView) view.findViewById(R.id.sign_log_status);
		signLogStatusTextView.setText("总计 79 个贴吧，其中 1 个贴吧签到失败");
		signLogListView = (ListView) view.findViewById(R.id.sign_log_list);
		for (int i = 1; i < 20; i++) {
			signLogList.add(new TiebaBean(i, "测试贴吧" + i, 8));
		}
		signLogAdapter = new SignLogListAdapter(getActivity(), signLogList);
		signLogListView.setAdapter(signLogAdapter);
		return view;
	}
}