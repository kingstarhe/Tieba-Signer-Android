package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.TiebaListAdapter;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.view.CircularImage;
import com.zulwi.tiebasigner.view.ListTableView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class UserInfoFragment extends BaseFragment {
	private CircularImage userAvatar;
	private TableLayout tiebaTable;
	private List<TiebaBean> tiebaList = new ArrayList<TiebaBean>();
	private TiebaListAdapter tiebaListAdapter;
	private View.OnClickListener emptyOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
		// tiebaListView = (ListView)
		// view.findViewById(R.id.userinfo_tieba_list);
		tiebaList.add(new TiebaBean(1, 8, "≤‚ ‘Ã˘∞…1"));
		tiebaList.add(new TiebaBean(2, 12, "≤‚ ‘Ã˘∞…2"));
		tiebaList.add(new TiebaBean(3, 11, "≤‚ ‘Ã˘∞…3"));
		tiebaListAdapter = new TiebaListAdapter(getActivity(), tiebaList);
		// tiebaListView.setAdapter(tiebaListAdapter);
		tiebaTable = (TableLayout) view.findViewById(R.id.userinfo_tieba_list);
		for (int i = 0; i < tiebaListAdapter.getCount(); i++) {
			final int n = i;
			TableRow row = new TableRow(getActivity());
			row.addView(tiebaListAdapter.getView(i, null, null));
			row.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					System.out.println(n);
				}
			});
			tiebaTable.addView(row);
		}
		view.findViewById(R.id.userinfo_follows).setOnClickListener(emptyOnClickListener);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(emptyOnClickListener);
		//tiebaTable.setAdapter(tiebaListAdapter);
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
