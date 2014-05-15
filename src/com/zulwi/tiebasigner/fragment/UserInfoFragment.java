package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.TiebaListAdapter;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.view.CircularImage;
import com.zulwi.tiebasigner.view.ListTableView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserInfoFragment extends BaseFragment {
	private CircularImage userAvatar;
	private ListTableView tiebaTable;
	private TextView usernameTextView;
	private List<TiebaBean> tiebaList = new ArrayList<TiebaBean>();
	private TiebaListAdapter tiebaListAdapter;
	private MainActivity activity;
	private View.OnClickListener emptyOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
		}
	};

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
		View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
		userAvatar = (CircularImage) view.findViewById(R.id.userinfo_avatar);
		userAvatar.setImageResource(R.drawable.avatar);
		tiebaList.add(new TiebaBean(1, 8, "≤‚ ‘Ã˘∞…1"));
		tiebaList.add(new TiebaBean(2, 12, "≤‚ ‘Ã˘∞…2"));
		tiebaList.add(new TiebaBean(3, 11, "≤‚ ‘Ã˘∞…3"));
		tiebaListAdapter = new TiebaListAdapter(getActivity(), tiebaList);
		tiebaTable = (ListTableView) view.findViewById(R.id.userinfo_tieba_list);
		view.findViewById(R.id.userinfo_follows).setOnClickListener(emptyOnClickListener);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(emptyOnClickListener);
		tiebaTable.setAdapter(tiebaListAdapter);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which) {
				System.out.println(which);
			}
		});
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

}
