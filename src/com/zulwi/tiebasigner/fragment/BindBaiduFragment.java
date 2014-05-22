package com.zulwi.tiebasigner.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;

public class BindBaiduFragment extends Fragment implements View.OnClickListener {
	private MainActivity activity;
	
	public BindBaiduFragment() {
	}
	
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
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_bindbaidu, container, false);
		return view;
    }

	@Override
    public void onStart() {
	    super.onStart();
    }

	@Override
	public void onClick(View v) {

	}

}
