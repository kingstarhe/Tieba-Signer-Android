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
				throw new Exception("站点名称和站点域名不能为空！");
			int countName = LoginActivity.sitesDBHelper.rawQuery(
					"select * from sites where name=\'" + name + "\'", null)
					.getCount();
			int countUrl = LoginActivity.sitesDBHelper.rawQuery(
					"select * from sites where url=\'" + url + "\'", null)
					.getCount();
			if (countName != 0 || countUrl != 0)
				throw new Exception("添加失败！请检查是否已有重复名称或URL");
			ContentValues value = new ContentValues();
			value.put("name", name);
			value.put("url", url);
			long id = LoginActivity.sitesDBHelper.insert(value);
			if (id != 0) {
				hasChanged = true;
				LoginActivity.siteListAdapter.addItem(id, name, url);
			} else {
				throw new Exception("添加失败！");
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
			Toast.makeText(this, "至少应添加一个站点以供登录", Toast.LENGTH_SHORT).show();
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
			confirm.setTitle("确定要清空站点吗？所有账号数据将均被删除");
			confirm.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							LoginActivity.siteListAdapter.removeAll();
							LoginActivity.sitesDBHelper.deleteAll();
							hasChanged = true;
							Toast.makeText(EditSitesActivity.this, "已经全部清空！", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("取消",
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
