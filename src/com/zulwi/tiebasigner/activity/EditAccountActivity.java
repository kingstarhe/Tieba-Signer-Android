package com.zulwi.tiebasigner.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.AccountListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;

public class EditAccountActivity extends ActionBarActivity {
	private ListView accountListView;
	private List<AccountBean> accountList = new ArrayList<AccountBean>();
	private AccountBean currentAccountBean;

	private class LoginThread extends Thread {
		private AccountBean accountBean;

		public LoginThread(AccountBean accountBean) {
			this.accountBean = accountBean;
		}

		@Override
		public void run() {
			super.run();
			try {
				ClientApiUtil client = new ClientApiUtil(accountBean);
				JSONBean jsonBean = client.get("check_login");
				if (jsonBean.status != 0) throw new HttpResultException(HttpResultException.AUTH_FAIL);
				accountBean.avatar = null;
				handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, accountBean).sendToTarget();
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
			System.out.println("login type:" + msg.arg1);
			System.out.println("override:" + msg.arg2);
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
					BaseDBHelper dbHelper = new BaseDBHelper(EditAccountActivity.this);
					dbHelper.execSQL("update accounts set current=0 where current=1");
					dbHelper.execSQL("update accounts set current=1 where id=" + accountBean.id);
					dbHelper.close();
					startMainActivity(accountBean);
					break;
			}
			progressDialog.dismiss();
			Toast.makeText(EditAccountActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_account);
		currentAccountBean = (AccountBean) getIntent().getSerializableExtra("currentAccountBean");
		BaseDBHelper dbHelper = new BaseDBHelper(this);
		Cursor accountCursor = dbHelper.rawQuery("select accounts.*, sites.name, sites.url from accounts left join sites on accounts.sid=sites.id;", null);
		for (accountCursor.moveToFirst(); !(accountCursor.isAfterLast()); accountCursor.moveToNext()) {
			AccountBean accountBean = new AccountBean(accountCursor.getInt(0), accountCursor.getInt(1), accountCursor.getInt(2), accountCursor.getString(3), accountCursor.getString(4), accountCursor.getString(5), accountCursor.getString(6), accountCursor.getInt(7), accountCursor.getString(8), accountCursor.getString(9));
			UserCacheUtil cache = new UserCacheUtil(this, accountCursor.getInt(1), accountCursor.getInt(2));
			String userInfo = cache.getDataCache("user_info");
			if (userInfo != null & !userInfo.trim().equals("")) {
				try {
					JSONObject jsonObject = new JSONObject(userInfo);
					int status = jsonObject.getInt("status");
					if (status == 0) {
						JSONObject data = jsonObject.getJSONObject("data");
						String baiduAccountId = data.getString("id");
						accountBean.avatar = UserCacheUtil.getAvatarCache(baiduAccountId, this);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			accountList.add(accountBean);
		}
		accountCursor.close();
		dbHelper.close();
		AccountListAdapter accountListAdapter = new AccountListAdapter(this, accountList);
		accountListView = (ListView) findViewById(R.id.account_list);
		accountListView.setAdapter(accountListAdapter);
		registerForContextMenu(accountListView);
		progressDialog = DialogUtil.createLoadingDialog(this, "正在登录,请稍后...", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_account, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.account_context, menu);
		menu.setHeaderTitle("操作");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final AccountBean accountBean = (AccountBean) accountListView.getAdapter().getItem(menuInfo.position);
		switch (item.getItemId()) {
			case R.id.switch_account:
				if (currentAccountBean.id == accountBean.id) {
					Toast.makeText(EditAccountActivity.this, "已登录该账号，无需切换", Toast.LENGTH_SHORT).show();
					return true;
				}
				progressDialog.show();
				new LoginThread(accountBean).start();
				break;
			case R.id.del_account:
				AlertDialog.Builder confirm = new AlertDialog.Builder(this);
				confirm.setTitle("确定要删除该账号吗？该账号的所有数据将均被删除");
				confirm.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BaseDBHelper dbHelper = new BaseDBHelper(EditAccountActivity.this);
						dbHelper.execSQL("DELETE FROM accounts where id=" + accountBean.id);
						((AccountListAdapter) accountListView.getAdapter()).remove(menuInfo.position);
						if (currentAccountBean.id == accountBean.id) startLoginActivity("请重新登录");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;
			case R.id.add_account:
				BaseDBHelper dbHelper = new BaseDBHelper(this);
				dbHelper.execSQL("UPDATE accounts SET current=0 WHERE current=1;");
				dbHelper.close();
				startLoginActivity("请添加新账号");
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void doLogout(View view) {
		BaseDBHelper dbHelper = new BaseDBHelper(this);
		dbHelper.execSQL("DELETE FROM accounts WHERE current=1;");
		dbHelper.close();
		startLoginActivity("请重新登录");
	}

	private void startLoginActivity(String message) {
		startActivity(new Intent(this, LoginActivity.class));
		finish();
		Intent intent = new Intent("com.zulwi.tiebasigner.FINISH_MAIN");
		intent.putExtra("message", message);
		sendBroadcast(intent);
	}

	private void startMainActivity(AccountBean accountBean) {
		finish();
		Intent intent = new Intent("com.zulwi.tiebasigner.SWITCH_ACCOUNT");
		intent.putExtra("accountBean", accountBean);
		sendBroadcast(intent);
	}

}
