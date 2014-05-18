package com.zulwi.tiebasigner.activity;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.HttpUtil;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditSitesActivity extends ActionBarActivity {
	private ListView siteList;
	private TextView nameTextView;
	private TextView urlTextView;
	private Dialog progressDialog;
	private AlertDialog EditDialog;
	private List<SiteBean> siteMapList;
	private SiteListAdapter siteListAdapter;
	private BaseDBHelper sitesDBHelper = new BaseDBHelper(this);
	public static boolean hasChanged = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			if (msg.what != HttpUtil.SUCCESSED && msg.arg1 == HttpUtil.EDIT_SITE) EditDialog.show();
			switch (msg.what) {
				case HttpUtil.NETWORK_FAIL:
					tips = "网络错误";
					break;
				case HttpUtil.STATUS_ERROR:
					StatusCodeException e = (StatusCodeException) msg.obj;
					tips = e.getMessage() + String.valueOf(e.getCode()) + "错误";
					break;
				case HttpUtil.PARSE_ERROR:
					tips = "JSON解析错误，请确认该站点是否支持客户端";
					break;
				case HttpUtil.SUCCESSED:
					if (msg.arg1 == HttpUtil.ADD_SITE) {
						SiteBean addingSite = (SiteBean) msg.obj;
						boolean successed = siteListAdapter.addItem(addingSite.name, addingSite.url);
						if (successed) hasChanged = true;
						tips = successed ? "添加成功" : "添加失败";
					} else if (msg.arg1 == HttpUtil.EDIT_SITE) {
						SiteBean editingSite = (SiteBean) msg.obj;
						boolean successed = siteListAdapter.updateItem(editingSite.position, editingSite.name, editingSite.url);
						if (successed) hasChanged = true;
						tips = successed ? "编辑成功" : "编辑失败";
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_sites);
		urlTextView = (TextView) findViewById(R.id.addSiteUrl);
		nameTextView = (TextView) findViewById(R.id.addSiteName);
		siteList = (ListView) findViewById(R.id.siteList);
		Intent intent = getIntent();
		siteMapList = (List<SiteBean>) intent.getSerializableExtra("siteMapList");
		siteListAdapter = new SiteListAdapter(this, siteMapList);
		siteList.setAdapter(siteListAdapter);
		registerForContextMenu(siteList);
		progressDialog = DialogUtil.createLoadingDialog(this, "正在检查站点,请稍后...", false);
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
		menu.setHeaderTitle("操作");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.edit_site:
				SiteBean site = siteListAdapter.getItem(menuInfo.position);
				createEditDialog(site, menuInfo.position);
				break;
			case R.id.del_site:
				AlertDialog.Builder confirm = new AlertDialog.Builder(this);
				confirm.setTitle("确定要删除该站点吗？该站点下的所有账号数据将均被删除");
				confirm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int del = siteListAdapter.remove(menuInfo.position);
						if (del != 0) {
							hasChanged = true;
						} else {
							Toast.makeText(EditSitesActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
				break;
		}
		return super.onContextItemSelected(item);
	}

	public void createEditDialog(SiteBean site, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_edit_sites, (ViewGroup) findViewById(R.layout.dialog_edit_sites));
		final EditText siteEditor = (EditText) layout.findViewById(R.id.nameEditor);
		final EditText urlEditor = (EditText) layout.findViewById(R.id.urlEditor);
		siteEditor.setText(site.name);
		urlEditor.setText(site.url);
		EditDialog = builder.setTitle("编辑站点").setView(layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = siteEditor.getText().toString().trim();
				final String url = formatUrl(urlEditor.getText().toString().trim());
				if (name.equals("") || url.equals("http://")) {
					Toast.makeText(EditSitesActivity.this, "站点名称或站点域名不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog.show();
				new Thread(new EditSiteThread(position, name, url)).start();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		EditDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.del_list:
				AlertDialog.Builder confirm = new AlertDialog.Builder(this);
				confirm.setTitle("确定要清空站点吗？所有账号数据将均被删除");
				confirm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						siteListAdapter.removeAll();
						hasChanged = true;
						Toast.makeText(EditSitesActivity.this, "已经全部清空！", Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
				break;
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String formatUrl(String url) {
		url = url.startsWith("http://") || url.startsWith("https://") ? url : "http://" + url;
		url = url.endsWith("/") && !url.equals("http://") ? url.substring(0, url.length() - 1) : url;
		return url;
	}

	public void addSite(View v) {
		final String name = nameTextView.getText().toString().trim();
		final String url = formatUrl(urlTextView.getText().toString().trim());
		if (name.equals("") || url.equals("http://")) {
			Toast.makeText(EditSitesActivity.this, "站点名称或站点域名不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog.show();
		new Thread(new EditSiteThread(name, url)).start();
	}

	@Override
	public void onBackPressed() {
		if (siteList.getCount() == 0) {
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("您尚未添加站点，无法返回登录界面，确定退出客户端吗？");
			confirm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create().show();
			return;
		} else {
			setResult(hasChanged ? 1 : 0, new Intent());
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		sitesDBHelper.close();
		siteListAdapter.closeDB();
		super.onDestroy();
	}

	private class EditSiteThread implements Runnable {
		private String name;
		private String url;
		private int position = -1;

		public EditSiteThread(String name, String url) {
			this.name = name;
			this.url = url;
		}

		public EditSiteThread(int position, String name, String url) {
			this(name, url);
			this.position = position;
		}

		@Override
		public void run() {
			try {
				if (position == -1) {
					int countName = sitesDBHelper.rawQuery("select * from sites where name=\'" + name + "\'", null).getCount();
					int countUrl = sitesDBHelper.rawQuery("select * from sites where url=\'" + url + "\'", null).getCount();
					if (countName != 0 || countUrl != 0) throw new Exception("添加失败！请检查是否已有重复名称或URL");
				}
				HttpUtil site = new HttpUtil(EditSitesActivity.this, url + "/plugin.php?id=zw_client_api&a=api_info");
				String result = site.get();
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.getInt("status");
				if (status == -1) throw new ClientProtocolException("状态码错误！");
				handler.obtainMessage(HttpUtil.SUCCESSED, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, position == -1 ? new SiteBean(name, url) : new SiteBean(name, url, position)).sendToTarget();
			} catch (JSONException e) {
				handler.obtainMessage(HttpUtil.PARSE_ERROR, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (StatusCodeException e) {
				handler.obtainMessage(HttpUtil.STATUS_ERROR, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (ClientProtocolException e) {
				handler.obtainMessage(HttpUtil.NETWORK_FAIL, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (IOException e) {
				handler.obtainMessage(HttpUtil.NETWORK_FAIL, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (Exception e) {
				handler.obtainMessage(HttpUtil.OTHER_ERROR, position == -1 ? HttpUtil.ADD_SITE : HttpUtil.EDIT_SITE, 0, e).sendToTarget();
			}
		}
	}
}