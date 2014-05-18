package com.zulwi.tiebasigner.activity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.AccountUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.InternetUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnItemSelectedListener {
	private Spinner siteSpinner;
	private Dialog progressDialog;
	private EditText usernameEditor;
	private EditText passwordEditor;
	private String[] siteUrlList;
	private String[] siteNameList;
	private List<SiteBean> siteMapList;
	private BaseDBHelper sitesDBHelper = new BaseDBHelper(this);
	private int lastSelectedPosition;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			switch (msg.what) {
				case InternetUtil.NETWORK_FAIL:
					tips = "网络错误";
					break;
				case InternetUtil.STATUS_ERROR:
					StatusCodeException e = (StatusCodeException) msg.obj;
					tips = e.getMessage() + String.valueOf(e.getCode()) + "错误";
					break;
				case InternetUtil.PARSE_ERROR:
					tips = "JSON解析错误，请确认该站点是否支持客户端";
					break;
				case InternetUtil.SUCCESSED:
					AccountBean accountBean = (AccountBean) msg.obj;
					tips = "欢迎回来，" + accountBean.username + "！";
					BaseDBHelper dbHelper = new BaseDBHelper(LoginActivity.this);
					dbHelper.execSQL("delete from accounts where username=\'" + accountBean.username + "\'");
					dbHelper.execSQL("update accounts set current=0 where current=1");
					ContentValues value = new ContentValues();
					int sid = siteMapList.get(lastSelectedPosition).id;
					Cursor siteCursor = dbHelper.rawQuery("select * from sites where id=" + sid, null);
					if (siteCursor.getCount() > 0) {
						siteCursor.moveToFirst();
						value.put("sid", sid);
						value.put("username", accountBean.username);
						value.put("email", accountBean.email);
						value.put("cookie", accountBean.cookieString);
						value.put("current", 1);
						dbHelper.insert("accounts", value);
						accountBean.sid = sid;
						accountBean.siteName = siteCursor.getString(1);
						accountBean.siteUrl = siteCursor.getString(2);
						dbHelper.close();
						startMainActivity(accountBean);
					} else {
						tips = "站点信息错误！";
					}
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			progressDialog.dismiss();
			Toast.makeText(LoginActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};

	private class LoginThread implements Runnable {
		private String username;
		private String password;
		private String url;

		public LoginThread(String username, String password, String url) {
			this.username = username;
			this.password = password;
			this.url = url;
		}

		@Override
		public void run() {
			try {
				AccountUtil accountUtil = new AccountUtil(username, password, url);
				AccountBean accountBean = accountUtil.doLogin();
				handler.obtainMessage(InternetUtil.SUCCESSED, 0, 0, accountBean).sendToTarget();
			} catch (JSONException e) {
				e.printStackTrace();
				handler.obtainMessage(InternetUtil.PARSE_ERROR, 0, 0, e).sendToTarget();
			} catch (StatusCodeException e) {
				e.printStackTrace();
				handler.obtainMessage(InternetUtil.STATUS_ERROR, 0, 0, e).sendToTarget();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				handler.obtainMessage(InternetUtil.NETWORK_FAIL, 0, 0, e).sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
				handler.obtainMessage(InternetUtil.NETWORK_FAIL, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				handler.obtainMessage(InternetUtil.OTHER_ERROR, 0, 0, e).sendToTarget();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		usernameEditor = (EditText) findViewById(R.id.username);
		passwordEditor = (EditText) findViewById(R.id.password);
		progressDialog = DialogUtil.createLoadingDialog(this, "正在登录,请稍后...", false);
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
		startActivityForResult(intent, 1);
	}

	public void startMainActivity(AccountBean accountBean) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("accountBean", accountBean);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 0:
				break;
			case 1:
				flushSiteList();
				break;
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

	@Override
	public void onBackPressed() {
		AlertDialog.Builder confirm = new AlertDialog.Builder(this);
		confirm.setTitle("确定要退出客户端吗？");
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
	}

	public void doLogin(View v) {
		String username = usernameEditor.getText().toString().trim();
		String password = passwordEditor.getText().toString().trim();
		if (username.equals("") || password.equals("")) {
			Toast.makeText(this, "助手账号或助手密码不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		progressDialog.show();
		new Thread(new LoginThread(username, password, siteUrlList[lastSelectedPosition])).start();
	}

}