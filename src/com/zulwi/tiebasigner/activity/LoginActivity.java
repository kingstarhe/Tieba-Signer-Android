package com.zulwi.tiebasigner.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.SitesDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnItemSelectedListener {
	private Spinner siteSpinner;
	private int lastSelectedPosition;
	private ArrayAdapter<String> siteSpinnerAdapter;
	private String[] siteList;
	private List<SiteBean> siteMapList;
	private SiteListAdapter siteListAdapter;
	private SitesDBHelper sitesDBHelper = new SitesDBHelper(this);;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		createListAdapter();
	}

	public void createListAdapter() {
		List<SiteBean> list = new ArrayList<SiteBean>();
		Cursor cursor = sitesDBHelper.query("sites");
		int count = cursor.getCount();
		if (count > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
				list.add(new SiteBean(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
			}
		}
		siteMapList = list;
		int size = siteMapList.size();
		siteList = new String[size + 1];
		for (int i = 0; i < size; i++) {
			siteList[i] = siteMapList.get(i).name;
		}
		siteList[size] = "管理站点";
		siteSpinner = (Spinner) findViewById(R.id.site);
		siteSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, siteList);
		siteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		siteSpinner.setAdapter(siteSpinnerAdapter);
		siteSpinner.setOnItemSelectedListener(this);
		if (size == 0) {
			Toast.makeText(this, "请先添加可供登录的助手站点", Toast.LENGTH_SHORT).show();
		}
	}

	public void startEditSiteActivity() {
		Intent intent = new Intent(LoginActivity.this, EditSitesActivity.class);
		intent.putExtra("siteListAdapter", siteListAdapter);
		intent.putExtra("siteMapList", (Serializable) siteMapList);
		this.startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 0:
				break;
			case 1:
				createListAdapter();
				break;
			default:
				Toast.makeText(this, "站点管理活动异常结束", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		sitesDBHelper.close();
		super.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View v, int which, long arg3) {
		if (which == siteList.length - 1) {
			startEditSiteActivity();
			siteSpinner.setSelection(lastSelectedPosition);
			return;
		}
		lastSelectedPosition = which;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
}
