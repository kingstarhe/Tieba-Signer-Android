package com.zulwi.tiebasigner.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.adapter.AccountListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;
import com.zulwi.tiebasigner.util.UserCacheUtil;

public class EditAccountActivity extends ActionBarActivity {
	private ListView accountListView;
	private List<AccountBean> accountList = new ArrayList<AccountBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_edit_account);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_account, menu);
		return true;
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

}
