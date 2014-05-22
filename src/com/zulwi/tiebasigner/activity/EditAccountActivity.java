package com.zulwi.tiebasigner.activity;

import java.util.ArrayList;
import java.util.List;

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
			accountList.add(new AccountBean(accountCursor.getInt(0), accountCursor.getInt(1), accountCursor.getInt(2), accountCursor.getString(3), accountCursor.getString(4), accountCursor.getString(5), accountCursor.getInt(6), accountCursor.getString(7), accountCursor.getString(8)));
		}
		accountCursor.close();
		dbHelper.close();
		AccountListAdapter accountListAdapter = new AccountListAdapter(this, accountList);
		accountListView = (ListView) findViewById(R.id.account_list);
		accountListView.setAdapter(accountListAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_sites, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void doLogout(View view) {
		BaseDBHelper dbHelper = new BaseDBHelper(this);
		dbHelper.execSQL("DELETE FROM accounts WHERE current=1");
		dbHelper.close();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
		sendBroadcast(new Intent("com.zulwi.tiebasigner.LOGOUT"));
	}

}
