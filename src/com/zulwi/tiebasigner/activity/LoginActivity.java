package com.zulwi.tiebasigner.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.AccountFilterAdapter;
import com.zulwi.tiebasigner.adapter.SiteListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.util.AccountUtil;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.CacheUtil;

public class LoginActivity extends Activity implements OnItemSelectedListener {
	private Spinner siteSpinner;
	private Dialog progressDialog;
	private AutoCompleteTextView usernameEditor;
	private EditText passwordEditor;
	private List<SiteBean> siteList = new ArrayList<SiteBean>();
	private List<AccountBean> accountList = new ArrayList<AccountBean>();
	private BaseDBHelper dbHelper = new BaseDBHelper(this);
	private boolean autoLogin = false;
	private AccountBean selectedAccount;
	private final static int NEW_ACCOUNT = 0;
	private final static int OVERRIDE_ACCOUNT = NEW_ACCOUNT + 1;
	private final static int NORMAL_LOGIN = OVERRIDE_ACCOUNT + 1;
	private final static int QUICK_LOGIN = NORMAL_LOGIN + 1;

	private class LoginThread extends Thread {
		private String username;
		private String password;
		private String url;
		private AccountBean accountBean;
		private boolean override;

		public LoginThread(String username, String password, String url, boolean override) {
			this.username = username;
			this.password = password;
			this.url = url;
			this.override = override;
		}

		public LoginThread(AccountBean accountBean) {
			this.accountBean = accountBean;
		}

		@Override
		public void run() {
			super.run();
			try {
				if (accountBean == null) {
					AccountUtil accountUtil = new AccountUtil(username, password, url);
					AccountBean account = accountUtil.doLogin();
					handler.obtainMessage(ClientApiUtil.SUCCESSED, NORMAL_LOGIN, override ? OVERRIDE_ACCOUNT : NEW_ACCOUNT, account).sendToTarget();
				} else {
					ClientApiUtil client = new ClientApiUtil(accountBean);
					JSONBean jsonBean = client.get("check_login");
					if (jsonBean.status != 0) throw new HttpResultException(HttpResultException.AUTH_FAIL);
					accountBean.avatar = null;
					handler.obtainMessage(ClientApiUtil.SUCCESSED, QUICK_LOGIN, 0, accountBean).sendToTarget();
				}
			} catch (HttpResultException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			switch (msg.what) {
				case ClientApiUtil.ERROR:
					HttpResultException e = (HttpResultException) msg.obj;
					tips = e.getMessage();
					break;
				case ClientApiUtil.SUCCESSED:
					AccountBean accountBean = (AccountBean) msg.obj;
					tips = "欢迎回来，" + accountBean.username + "！";
					dbHelper.execSQL("update accounts set current=0 where current=1");
					if (msg.arg1 == QUICK_LOGIN) {
						dbHelper.execSQL("update accounts set current=1 where id=" + accountBean.id);
						startMainActivity(accountBean);
					} else if (msg.arg1 == NORMAL_LOGIN) {
						ContentValues value = new ContentValues();
						SiteBean siteBean = (SiteBean) siteSpinner.getSelectedItem();
						value.put("uid", accountBean.uid);
						value.put("sid", siteBean.id);
						value.put("username", accountBean.username);
						value.put("email", accountBean.email);
						value.put("cookie", accountBean.cookieString);
						value.put("formhash", accountBean.formhash);
						value.put("current", 1);
						switch (msg.arg2) {
							case NEW_ACCOUNT:
								accountBean.id = (int) dbHelper.insert("accounts", value);
								break;
							case OVERRIDE_ACCOUNT:
								Cursor cursor = dbHelper.rawQuery("SELECT * FROM accounts WHERE sid=" + siteBean.id + " AND username=\'" + accountBean.username + "\'", null);
								accountBean.id = cursor.getInt(0);
								dbHelper.update("accounts", value, "id=" + accountBean.id);
								cursor.close();
								break;
						}
						accountBean.setSite(siteBean);
						startMainActivity(accountBean);
					}
					break;
			}
			progressDialog.dismiss();
			Toast.makeText(LoginActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};
	private int lastSelectedPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		usernameEditor = (AutoCompleteTextView) findViewById(R.id.username);
		usernameEditor.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AdapterView<AccountFilterAdapter> adapter = (AdapterView<AccountFilterAdapter>) parent;
				selectedAccount = adapter.getAdapter().getItem(position);
				usernameEditor.setText(selectedAccount.username);
				passwordEditor.setText("****************");
				autoLogin = true;
			}
		});
		usernameEditor.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				passwordEditor.setText("");
				autoLogin = false;
			}
		});
		passwordEditor = (EditText) findViewById(R.id.password);
		passwordEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (autoLogin && hasFocus) {
					autoLogin = false;
					passwordEditor.setText("");
				} else if (autoLogin && !hasFocus) {
					autoLogin = true;
					passwordEditor.setText("****************");
				}
			}
		});
		progressDialog = DialogUtil.createLoadingDialog(this, "正在登录,请稍后...", false);
		siteSpinner = (Spinner) findViewById(R.id.site);
		refreshSiteList();
	}

	public void refreshSiteList() {
		siteList.clear();
		Cursor siteCursor = dbHelper.query("sites");
		if (siteCursor.getCount() > 0) {
			for (siteCursor.moveToFirst(); !(siteCursor.isAfterLast()); siteCursor.moveToNext()) {
				siteList.add(new SiteBean(siteCursor.getInt(0), siteCursor.getString(1), siteCursor.getString(2)));
			}
			siteCursor.close();
		}
		int size = siteList.size();
		siteList.add(new SiteBean("管理站点", "增加、编辑和删除站点"));
		SiteListAdapter siteSpinnerAdapter = new SiteListAdapter(this, siteList);
		siteSpinner.setAdapter(siteSpinnerAdapter);
		siteSpinner.setOnItemSelectedListener(this);
		if (size == 0) Toast.makeText(this, "请先添加可供登录的助手站点", Toast.LENGTH_SHORT).show();
	}

	public void startEditSiteActivity() {
		Intent intent = new Intent(LoginActivity.this, EditSitesActivity.class);
		intent.putExtra("siteList", (Serializable) siteList);
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
				refreshSiteList();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View v, int which, long arg3) {
		if (which == siteList.size() - 1) {
			startEditSiteActivity();
			siteSpinner.setSelection(lastSelectedPosition);
			return;
		}
		accountList.clear();
		Cursor accountCursor = dbHelper.rawQuery("select accounts.*, sites.name, sites.url from accounts left join sites on accounts.sid=sites.id WHERE accounts.sid=" + siteSpinner.getSelectedItemId(), null);
		for (accountCursor.moveToFirst(); !(accountCursor.isAfterLast()); accountCursor.moveToNext()) {
			AccountBean accountBean = new AccountBean(accountCursor.getInt(0), accountCursor.getInt(1), accountCursor.getInt(2), accountCursor.getString(3), accountCursor.getString(4), accountCursor.getString(5), accountCursor.getString(6), accountCursor.getInt(7), accountCursor.getString(8), accountCursor.getString(9));
			CacheUtil cache = new CacheUtil(this, accountBean);
			String userInfo = cache.getDataCache("user_info");
			if (userInfo != null & !userInfo.trim().equals("")) {
				try {
					JSONObject jsonObject = new JSONObject(userInfo);
					int status = jsonObject.getInt("status");
					if (status == 0) {
						JSONObject data = jsonObject.getJSONObject("data");
						String baiduAccountId = data.getString("id");
						accountBean.setAvatar(CacheUtil.getAvatarCache(baiduAccountId, this));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			accountList.add(accountBean);
		}
		accountCursor.close();
		AccountFilterAdapter spinnerAdapter = new AccountFilterAdapter(this, accountList);
		usernameEditor.setAdapter(spinnerAdapter);
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
		if (autoLogin) {
			progressDialog.show();
			new LoginThread(selectedAccount).start();
		} else {
			String username = usernameEditor.getText().toString().trim();
			String password = passwordEditor.getText().toString().trim();
			if (username.equals("") || password.equals("")) {
				Toast.makeText(this, "助手账号或助手密码不能为空！", Toast.LENGTH_SHORT).show();
				return;
			}
			Cursor cursor = dbHelper.rawQuery("SELECT * FROM accounts WHERE sid=" + siteSpinner.getSelectedItemId() + " AND username=\'" + username + "\';", null);
			int count = cursor.getCount();
			cursor.close();
			progressDialog.show();
			new LoginThread(username, password, ((SiteBean) siteSpinner.getSelectedItem()).url, count > 0).start();
		}
	}
}