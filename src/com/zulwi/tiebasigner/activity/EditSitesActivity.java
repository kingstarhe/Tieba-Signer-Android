package com.zulwi.tiebasigner.activity;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditSitesActivity extends ActionBarActivity {
	private ListView siteList;
	private TextView nameTextView;
	private TextView urlTextView;
	public static boolean hasChanged = false;

	public void addSite(View v) {
		String name = nameTextView.getText().toString().trim();
		String url = urlTextView.getText().toString().trim();
		try {
			if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url))
				throw new Exception("վ�����ƺ�վ����������Ϊ�գ�");
			int countName = LoginActivity.sitesDBHelper.rawQuery(
					"select * from sites where name=\'" + name + "\'", null)
					.getCount();
			int countUrl = LoginActivity.sitesDBHelper.rawQuery(
					"select * from sites where url=\'" + url + "\'", null)
					.getCount();
			if (countName != 0 || countUrl != 0)
				throw new Exception("���ʧ�ܣ������Ƿ������ظ����ƻ�URL");
			ContentValues value = new ContentValues();
			value.put("name", name);
			value.put("url", url);
			long id = LoginActivity.sitesDBHelper.insert(value);
			if (id != 0) {
				hasChanged = true;
				LoginActivity.siteListAdapter.addItem(id, name, url);
			} else {
				throw new Exception("���ʧ�ܣ�");
			}
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sites);
		urlTextView = (TextView) findViewById(R.id.addSiteUrl);
		nameTextView = (TextView) findViewById(R.id.addSiteName);
		siteList = (ListView) findViewById(R.id.siteList);
		LoginActivity.siteListAdapter = new SiteListAdapter(this,
				LoginActivity.siteMapList);
		siteList.setAdapter(LoginActivity.siteListAdapter);
	}

	@Override
	public void onBackPressed() {
		if (siteList.getCount() == 0) {
			Toast.makeText(this, "����Ӧ���һ��վ���Թ���¼", Toast.LENGTH_SHORT).show();
			return;
		} else {
			setResult(hasChanged ? 1 : 0, getIntent());
		}
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_sites, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.del_list) {
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("ȷ��Ҫ���վ���������˺����ݽ�����ɾ��");
			confirm.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginActivity.siteListAdapter.removeAll();
							LoginActivity.sitesDBHelper.deleteAll();
							hasChanged = true;
							Toast.makeText(EditSitesActivity.this, "�Ѿ�ȫ����գ�", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).create().show();
		}
		return super.onOptionsItemSelected(item);
	}

}
