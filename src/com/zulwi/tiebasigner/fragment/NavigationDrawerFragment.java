package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.NavListAdapter;
import com.zulwi.tiebasigner.bean.NavigationBean;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class NavigationDrawerFragment extends Fragment {
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private NavigationDrawerCallbacks callbacks;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private View fragmentContainerView;
	private NavListAdapter adapter;
	private int currentSelectedPosition = 0;
	private boolean fromSavedInstanceState;
	private boolean userLearnedDrawer;

	public NavigationDrawerFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		if (savedInstanceState != null) {
			currentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			fromSavedInstanceState = true;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		selectItem(currentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		drawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});
		List<NavigationBean> navigations = new ArrayList<NavigationBean>();
		navigations.add(new NavigationBean(R.drawable.icon_userinfo, getString(R.string.user_info), new UserInfoFragment()));
		navigations.add(new NavigationBean(R.drawable.icon_sign_log, getString(R.string.sign_log), new SignLogFragment()));
		navigations.add(new NavigationBean(R.drawable.icon_blockid, getString(R.string.block_id), new SignLogFragment()));
		navigations.add(new NavigationBean(R.drawable.icon_sitepost, getString(R.string.site_post), new SignLogFragment()));
		navigations.add(new NavigationBean(R.drawable.icon_setting, getString(R.string.setting), new SettingFragment()));
		System.out.println(navigations.get(0).fragment);
		adapter = new NavListAdapter(getActionBar().getThemedContext(), navigations);
		drawerListView.setAdapter(adapter);
		drawerListView.setItemChecked(currentSelectedPosition, true);
		return drawerListView;
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	public void setUp(int fragmentId, DrawerLayout drawer) {
		fragmentContainerView = getActivity().findViewById(fragmentId);
		drawerLayout = drawer;
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.drawable.ic_drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) return;
				getActivity().supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) { return; }
				if (!userLearnedDrawer) {
					userLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
				}
				getActivity().supportInvalidateOptionsMenu();
			}
		};
		if (!userLearnedDrawer && !fromSavedInstanceState) {
			drawerLayout.openDrawer(fragmentContainerView);
		}
		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});
		drawerLayout.setDrawerListener(drawerToggle);
	}

	private void selectItem(int position) {
		if (drawerListView != null) drawerListView.setItemChecked(position, true);
		if (drawerLayout != null) drawerLayout.closeDrawer(fragmentContainerView);
		if (callbacks != null) {
			NavigationBean oldNav = adapter.getItem(currentSelectedPosition);
			NavigationBean newNav = adapter.getItem(position);
			callbacks.onNavigationDrawerItemSelected(position, newNav.title, oldNav.fragment, newNav.fragment);
		}
		currentSelectedPosition = position;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (drawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) return true;
		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public static interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(int position, CharSequence title, Fragment from, Fragment to);
	}
}
