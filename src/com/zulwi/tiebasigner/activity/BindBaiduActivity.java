package com.zulwi.tiebasigner.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;

public class BindBaiduActivity extends ActionBarActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox checkbox;
	private LinearLayout vcodeLayout;
	private Dialog progressDialog;

	private class loginThread extends Thread {
		public loginThread(String username, String password) {
			handler.obtainMessage(0, 0, 0).sendToTarget();
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
					Exception e = (Exception) msg.obj;
					tips = e.getMessage();
					break;
				case ClientApiUtil.SUCCESSED:

					break;
			}
			progressDialog.dismiss();
			if (tips != null && !tips.equals("")) Toast.makeText(BindBaiduActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_bind_baidu);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		checkbox = (CheckBox) findViewById(R.id.checkbox);
		vcodeLayout = (LinearLayout) findViewById(R.id.vcode_layout);
		progressDialog = DialogUtil.createLoadingDialog(this, "正在登录,请稍后...", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) { return true; }
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.login:
				if (!checkbox.isChecked()) {
					Toast.makeText(this, "必须勾选 \"" + getString(R.string.bd_checkbox) + "\" 才能继续", Toast.LENGTH_LONG).show();
					return;
				}
				String username = usernameEditText.getText().toString().trim();
				String password = passwordEditText.getText().toString().trim();
				if (username.equals("") || password.equals("")) {
					Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
					return;
				}
				progressDialog.show();
				new loginThread(username, password);
				break;
		}
	}

}
