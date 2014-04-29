package com.zulwi.tiebasigner.activity;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;
import com.zulwi.tiebasigner.beans.SiteBean;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.utils.InternetUtil;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class EditSitesActivity extends ActionBarActivity {
	private ListView siteList;
	private TextView nameTextView;
	private TextView urlTextView;
	private ProgressDialog progressDialog;
	private final static int NETWORK_FAIL = 0;
	private final static int STATUS_ERROR = 1;
	private final static int UNSUPPORTTED = 2;
	private final static int PARSE_ERROR = 3;
	private final static int UNKNOWN_ERROR = 4;
	private final static int SUCCESSED = 5;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWORK_FAIL:
				Toast.makeText(EditSitesActivity.this, "网络错误",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case STATUS_ERROR:
				StatusCodeException e = (StatusCodeException) msg.obj;
				Toast.makeText(EditSitesActivity.this,
						e.getMessage() + "状态码：" + String.valueOf(e.getCode()),
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case UNSUPPORTTED:
				Toast.makeText(EditSitesActivity.this, "该站点不支持客户端",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case PARSE_ERROR:
				Toast.makeText(EditSitesActivity.this, "JSON解析错误",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				break;
			case SUCCESSED:
				SiteBean site = (SiteBean) msg.obj;
				ContentValues value = new ContentValues();
				value.put("name", site.name);
				value.put("url", site.url);
				long id = LoginActivity.sitesDBHelper.insert("sites", value);
				if (id != 0) {
					hasChanged = true;
					LoginActivity.siteListAdapter.addItem(id, site.name,
							site.url);
				} else {
					Toast.makeText(EditSitesActivity.this, "添加失败",
							Toast.LENGTH_SHORT).show();
				}
				progressDialog.dismiss();
				break;
			default:
				Throwable t = (Throwable) msg.obj;
				Toast.makeText(EditSitesActivity.this, t.getMessage(),
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};
	public static boolean hasChanged = false;

	public void addSite(View v) {
		progressDialog.show();
		new Thread(new addSiteThread(nameTextView.getText().toString().trim(),
				urlTextView.getText().toString().trim())).start();
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
		progressDialog = new ProgressDialog(EditSitesActivity.this);
		progressDialog.setTitle("请稍后...");
		progressDialog.setMessage("正在添加,请稍后...");
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
							LoginActivity.sitesDBHelper.deleteAll("sites");
							hasChanged = true;
							Toast.makeText(EditSitesActivity.this, "已经全部清空！",
									Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create().show();
		}
		return super.onOptionsItemSelected(item);
	}

	class addSiteThread implements Runnable {
		private String name;
		private String url;

		public addSiteThread(String name, String url) {
			this.name = name;
			this.url = url;
		}

		@Override
		public void run() {
			Looper.prepare();
			try {
				if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url))
					throw new Exception("站点名称和站点域名不能为空！");
				int countName = LoginActivity.sitesDBHelper
						.rawQuery(
								"select * from sites where name=\'" + name
										+ "\'", null).getCount();
				int countUrl = LoginActivity.sitesDBHelper.rawQuery(
						"select * from sites where url=\'" + url + "\'", null)
						.getCount();
				if (countName != 0 || countUrl != 0)
					throw new Exception("添加失败！请检查是否已有重复名称或URL");
				InternetUtil site = new InternetUtil(EditSitesActivity.this,
						url + "/plugin.php?id=zw_client_api&a=get_api_info");
				site.get();
				String result = site.getResult();
				handler.obtainMessage(SUCCESSED, new SiteBean(name, url)).sendToTarget();
			} catch (StatusCodeException e) {
				handler.obtainMessage(STATUS_ERROR, e).sendToTarget();
			} catch (ClientProtocolException e) {
				handler.obtainMessage(NETWORK_FAIL, e).sendToTarget();
			} catch (IOException e) {
				handler.obtainMessage(NETWORK_FAIL, e).sendToTarget();
			} catch (Exception e) {
				handler.obtainMessage(UNKNOWN_ERROR, e).sendToTarget();
			}
		}
	}
}
