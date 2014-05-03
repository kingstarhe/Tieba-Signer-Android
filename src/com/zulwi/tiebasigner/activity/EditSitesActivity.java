package com.zulwi.tiebasigner.activity;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
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
	private final static int OTHER_ERROR = 4;
	private final static int SUCCESSED = 5;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			switch (msg.what) {
			case NETWORK_FAIL:
				tips = "网络错误";
				break;
			case STATUS_ERROR:
				StatusCodeException e = (StatusCodeException) msg.obj;
				tips = e.getMessage() + "状态码：" + String.valueOf(e.getCode());
				break;
			case UNSUPPORTTED:
				tips = "该站点不支持客户端";
				break;
			case PARSE_ERROR:
				JSONException j = (JSONException) msg.obj;
				tips = "JSON解析错误：" + j.getMessage();
				break;
			case SUCCESSED:
				SiteBean site = (SiteBean) msg.obj;
				if (LoginActivity.siteListAdapter.addItem(site.name, site.url)) {
					hasChanged = true;
					tips = "添加成功";
				} else {
					tips = "添加失败";
				}
				break;
			default:
				Throwable t = (Throwable) msg.obj;
				tips = t.getMessage();
				break;
			}
			progressDialog.dismiss();
			Toast.makeText(EditSitesActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};
	public static boolean hasChanged = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sites);
		urlTextView = (TextView) findViewById(R.id.addSiteUrl);
		nameTextView = (TextView) findViewById(R.id.addSiteName);
		siteList = (ListView) findViewById(R.id.siteList);
		LoginActivity.siteListAdapter = new SiteListAdapter(this,LoginActivity.siteMapList);
		siteList.setAdapter(LoginActivity.siteListAdapter);
		registerForContextMenu(siteList);
		progressDialog = new ProgressDialog(EditSitesActivity.this);
		progressDialog.setTitle("请稍后...");
		progressDialog.setMessage("正在添加,请稍后...");
		progressDialog.setCancelable(false);
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.sites_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit_site:
			break;
		case R.id.del_site:
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("确定要删除该站点吗？该站点下的所有账号数据将均被删除");
			confirm.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int del = LoginActivity.siteListAdapter.remove(menuInfo.position);
							if (del != 0) {
								hasChanged = true;
							} else {
								Toast.makeText(EditSitesActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
							}
						}
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).create().show();
			break;
		}
		return super.onContextItemSelected(item);
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

	public void addSite(View v) {
		progressDialog.show();
		final String name = nameTextView.getText().toString().trim();
		String inputUrl = urlTextView.getText().toString().trim();
		final String url = inputUrl.startsWith("http://") || inputUrl.startsWith("https://") ? inputUrl : "http://" + inputUrl;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url))
						throw new Exception("站点名称和站点域名不能为空！");
					int countName = LoginActivity.sitesDBHelper.rawQuery("select * from sites where name=\'" + name + "\'", null).getCount();
					int countUrl = LoginActivity.sitesDBHelper.rawQuery("select * from sites where url=\'" + url + "\'", null).getCount();
					if (countName != 0 || countUrl != 0)
						throw new Exception("添加失败！请检查是否已有重复名称或URL");
					InternetUtil site = new InternetUtil(EditSitesActivity.this, url + "/plugin.php?id=zw_client_api&a=get_api_info");
					String result = site.get();
					JSONObject jsonObject = new JSONObject(result);
					int status = jsonObject.getInt("status");
					if (status == -1)
						throw new ClientProtocolException("状态码错误！");
					handler.obtainMessage(SUCCESSED, new SiteBean(name, url)).sendToTarget();
				} catch (JSONException e) {
					handler.obtainMessage(PARSE_ERROR, e).sendToTarget();
				} catch (StatusCodeException e) {
					handler.obtainMessage(STATUS_ERROR, e).sendToTarget();
				} catch (ClientProtocolException e) {
					handler.obtainMessage(NETWORK_FAIL, e).sendToTarget();
				} catch (IOException e) {
					handler.obtainMessage(NETWORK_FAIL, e).sendToTarget();
				} catch (Exception e) {
					handler.obtainMessage(OTHER_ERROR, e).sendToTarget();
				}
			}
		}).start();
	}
}
