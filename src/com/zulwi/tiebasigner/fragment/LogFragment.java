package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.bean.FragmentBean;

public class LogFragment extends Fragment {
	private FragmentManager fm;
	private SectionsPagerAdapter sectionsPagerAdapter;
	private ViewPager viewPager;
	private FragmentBean[] fragmentList = new FragmentBean[2];
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
		if (activity.getCurrentFragmentId() == 1) setTitle();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log, container, false);
		fm = getChildFragmentManager();
		fragmentList[0] = new FragmentBean("签到记录", new SignLogFragment());
		fragmentList[1] = new FragmentBean("一键签到", new MultiSignFragment());
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sign_log, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SignLogFragment signLogFragment = (SignLogFragment) fragmentList[0].fragment;
		viewPager.setCurrentItem(0);
		if (signLogFragment.isRefreshing()) return false;
		switch (item.getItemId()) {
			case R.id.sign_log_pre:
				signLogFragment.switchLog(0);
				break;
			case R.id.sign_log_next:
				signLogFragment.switchLog(1);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm, FragmentBean[] list) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList[position].fragment;
		}

		@Override
		public int getCount() {
			return fragmentList.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return fragmentList[position].title;
		}
	}

	protected void setFragmentTitle(int position, String title) {
		fragmentList[position].title = title;
		if (activity.getCurrentFragmentId() == 1) setTitle();
	}

	public void setTitle() {
		activity.setTitle(sectionsPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
	}

}
