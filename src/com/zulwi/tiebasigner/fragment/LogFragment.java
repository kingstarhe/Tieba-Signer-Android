package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.bean.FragmentBean;

public class LogFragment extends Fragment {
	private FragmentManager fm;
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private List<FragmentBean> fragmentList = new ArrayList<FragmentBean>();
	private MainActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log, container, false);
		fm = getChildFragmentManager();
		fragmentList.add(new FragmentBean("签到记录", new SignLogFragment()));
		fragmentList.add(new FragmentBean("一键签到", new MultiSignFragment()));
		sectionsPagerAdapter = new SectionsPagerAdapter(fm, fragmentList);
		viewPager = (ViewPager) view.findViewById(R.id.sign_log_pager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setTitle();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		viewPager.setAdapter(sectionsPagerAdapter);
		return view;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm, List<FragmentBean> list) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position).fragment;
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentList.get(position).title;
		}
	}

	protected void setFragmentTitle(int position, String title) {
		fragmentList.get(position).title = title;
		if (activity.getCurrentFragmentId() == 1) setTitle();
	}

	public void setTitle() {
		activity.setTitle(sectionsPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
	}
}
