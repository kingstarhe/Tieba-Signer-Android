package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.FragmentBean;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SignLogFragment extends Fragment {
	private FragmentManager fm;
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private List<FragmentBean> fragmentList = new ArrayList<FragmentBean>();

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
		View view = inflater.inflate(R.layout.fragment_signlog, container, false);
		fm = getChildFragmentManager();
		fragmentList.add(new FragmentBean("签到记录", new SignLogStatusFragment()));
		fragmentList.add(new FragmentBean("一键签到", new SignLogSignFragment()));
		sectionsPagerAdapter = new SectionsPagerAdapter(fm, fragmentList);
		viewPager = (ViewPager) view.findViewById(R.id.sign_log_pager);
		viewPager.setAdapter(sectionsPagerAdapter);
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private List<FragmentBean> list;

		public SectionsPagerAdapter(FragmentManager fm, List<FragmentBean> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			return list.get(position).fragment;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return list.get(position).title;
		}
	}

}
