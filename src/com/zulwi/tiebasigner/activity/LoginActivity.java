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
		// ��ѯ�����б����浽 siteMapList ��
		siteMapList = new ArrayList<SiteBean>();
		Cursor cursor = sitesDBHelper.query("sites");
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
				siteMapList.add(new SiteBean(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
			}
		}
		// ���� Spinner ��������
		int size = siteMapList.size();
		siteNameList = new String[size + 1];
		siteUrlList = new String[size];
		for (int i = 0; i < size; i++) {
			siteNameList[i] = siteMapList.get(i).name;
			siteUrlList[i] = siteMapList.get(i).url;
		}
		siteNameList[size] = "����վ��";
		siteSpinner = (Spinner) findViewById(R.id.site);
		// ΪSpinner �����������ѡ���¼�
		ArrayAdapter<String> siteSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, siteNameList);
		siteSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		siteSpinner.setAdapter(siteSpinnerAdapter);
		siteSpinner.setOnItemSelectedListener(this);
		if (size == 0) {
			Toast.makeText(this, "������ӿɹ���¼������վ��", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "վ������쳣����", Toast.LENGTH_LONG).show();
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
