package com.zulwi.tiebasigner.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zulwi.tiebasigner.R;
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
	private String[] siteUrlList;
	private String[] siteNameList;
	private List<SiteBean> siteMapList;
	private SitesDBHelper sitesDBHelper = new SitesDBHelper(this);
	private int lastSelectedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		flushSiteList();
	}

	public void flushSiteList() {
		// 查询已有列表并保存到 siteMapList 中
		siteMapList = new ArrayList<SiteBean>();
		Cursor cursor = sitesDBHelper.query("sites");
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
				siteMapList.add(new SiteBean(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
			}
		}
		// 创建 Spinner 所需数据
		int size = siteMapList.size();
		siteNameList = new String[size + 1];
		siteUrlList = new String[size];
		for (int i = 0; i < size; i++) {
			siteNameList[i] = siteMapList.get(i).name;
			siteUrlList[i] = siteMapList.get(i).url;
		}
		siteNameList[size] = "管理站点";
		siteSpinner = (Spinner) findViewById(R.id.site);
		// 为Spinner 添加适配器和选择事件
		ArrayAdapter<String> siteSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, siteNameList);
		siteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		siteSpinner.setAdapter(siteSpinnerAdapter);
		siteSpinner.setOnItemSelectedListener(this);
		if (size == 0) {
			Toast.makeText(this, "请先添加可供登录的助手站点", Toast.LENGTH_SHORT).show();
		}
	}

	public void startEditSiteActivity() {
		Intent intent = new Intent(LoginActivity.this, EditSitesActivity.class);
		intent.putExtra("siteMapList", (Serializable) siteMapList);
		this.startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 0:
				break;
			case 1:
				flushSiteList();
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
		if (which == siteUrlList.length) {
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
