package com.zulwi.tiebasigner.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

public class BaseFragment extends Fragment {
	protected Context getContext(){
		return ((ActionBarActivity) getActivity()).getSupportActionBar().getThemedContext();
	}
}
