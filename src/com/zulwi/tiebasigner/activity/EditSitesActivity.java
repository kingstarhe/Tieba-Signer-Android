package com.zulwi.tiebasigner.activity;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.SitesDBHelper;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.Common;
import com.zulwi.tiebasigner.util.InternetUtil;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	private SitesDBHelper sitesDBHelper = new SitesDBHelper(this);
	public static boolean hasChanged = false;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			if (msg.what != InternetUtil.SUCCESSED && msg.arg1 == InternetUtil.EDIT_SITE) EditDialog.show();
			switch (msg.what) {
				case InternetUtil.NETWORK_FAIL:
					tips = "�������";
					break;
				case InternetUtil.STATUS_ERROR:
					StatusCodeException e = (StatusCodeException) msg.obj;
					tips = e.getMessage() + String.valueOf(e.getCode()) + "����";
					break;
				case InternetUtil.PARSE_ERROR:
					tips = "JSON����������ȷ�ϸ�վ���Ƿ�֧�ֿͻ���";
					break;
				case InternetUtil.SUCCESSED:
					if (msg.arg1 == InternetUtil.ADD_SITE) {
						SiteBean addingSite = (SiteBean) msg.obj;
						boolean successed = siteListAdapter.addItem(addingSite.name, addingSite.url);
						if (successed) hasChanged = true;
						tips = successed ? "��ӳɹ�" : "���ʧ��";
					} else if (msg.arg1 == InternetUtil.EDIT_SITE) {
						SiteBean editingSite = (SiteBean) msg.obj;
						boolean successed = siteListAdapter.updateItem(editingSite.position, editingSite.name, editingSite.url);
						if (successed) hasChanged = true;
						tips = successed ? "�༭�ɹ�" : "�༭ʧ��";
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
		setContentView(R.layout.activity_edit_sites);
		urlTextView = (TextView) findViewById(R.id.addSiteUrl);
		nameTextView = (TextView) findViewById(R.id.addSiteName);
		siteList = (ListView) findViewById(R.id.siteList);
		Intent intent = getIntent();
		siteMapList = (List<SiteBean>) intent.getSerializableExtra("siteMapList");
		siteListAdapter = new SiteListAdapter(this, siteMapList);
		siteList.setAdapter(siteListAdapter);
		registerForContextMenu(siteList);
		progressDialog = Common.createLoadingDialog(this, "���ڼ��վ��,���Ժ�...", false);
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
		menu.setHeaderTitle("����");
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
				confirm.setTitle("ȷ��Ҫɾ����վ���𣿸�վ���µ������˺����ݽ�����ɾ��");
				confirm.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int del = siteListAdapter.remove(menuInfo.position);
						if (del != 0) {
							hasChanged = true;
						} else {
							Toast.makeText(EditSitesActivity.this, "ɾ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
						}
					}
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
		EditDialog = builder.setTitle("�༭վ��").setView(layout).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String name = siteEditor.getText().toString().trim();
				final String url = formatUrl(urlEditor.getText().toString().trim());
				if (name.equals("") || url.equals("http://")) {
					Toast.makeText(EditSitesActivity.this, "վ�����ƻ�վ����������Ϊ�գ�", Toast.LENGTH_SHORT).show();
					return;
				}
				progressDialog.show();
				new Thread(new EditSiteThread(position, name, url)).start();
			}
		}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create();
		EditDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.del_list) {
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("ȷ��Ҫ���վ���������˺����ݽ�����ɾ��");
			confirm.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					siteListAdapter.removeAll();
					hasChanged = true;
					Toast.makeText(EditSitesActivity.this, "�Ѿ�ȫ����գ�", Toast.LENGTH_SHORT).show();
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).create().show();
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
			Toast.makeText(EditSitesActivity.this, "վ�����ƻ�վ����������Ϊ�գ�", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog.show();
		new Thread(new EditSiteThread(name, url)).start();
	}

	@Override
	public void onBackPressed() {
		if (siteList.getCount() == 0) {
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("����δ���վ�㣬�޷����ص�¼���棬ȷ���˳��ͻ�����");
			confirm.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_MAIN);  
                    intent.addCategory(Intent.CATEGORY_HOME);  
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
                    startActivity(intent);  
                    android.os.Process.killProcess(android.os.Process.myPid());
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
					if (countName != 0 || countUrl != 0) throw new Exception("���ʧ�ܣ������Ƿ������ظ����ƻ�URL");
				}
				InternetUtil site = new InternetUtil(EditSitesActivity.this, url + "/plugin.php?id=zw_client_api&a=get_api_info");
				String result = site.get();
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.getInt("status");
				if (status == -1) throw new ClientProtocolException("״̬�����");
				handler.obtainMessage(InternetUtil.SUCCESSED, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, position == -1 ? new SiteBean(name, url) : new SiteBean(name, url, position)).sendToTarget();
			} catch (JSONException e) {
				handler.obtainMessage(InternetUtil.PARSE_ERROR, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (StatusCodeException e) {
				handler.obtainMessage(InternetUtil.STATUS_ERROR, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (ClientProtocolException e) {
				handler.obtainMessage(InternetUtil.NETWORK_FAIL, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (IOException e) {
				handler.obtainMessage(InternetUtil.NETWORK_FAIL, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, e).sendToTarget();
			} catch (Exception e) {
				handler.obtainMessage(InternetUtil.OTHER_ERROR, position == -1 ? InternetUtil.ADD_SITE : InternetUtil.EDIT_SITE, 0, e).sendToTarget();
			}
		}
	}
}
