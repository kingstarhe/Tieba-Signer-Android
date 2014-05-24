package com.zulwi.tiebasigner.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;

public class BindBaiduActivity extends ActionBarActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private CheckBox checkbox;
	private LinearLayout vcodeLayout;
	private Dialog progressDialog;

	private class loginThread extends Thread {
		private String username;
		private String password;

		public loginThread(String username, String password) {
			this.username =username;
			this.password =password;
		}
		
		@Override
		public void run() {
		    super.run();
		    HttpClient client = new DefaultHttpClient();
		    HttpPost post = new HttpPost("http://c.tieba.baidu.com/c/s/login");
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("User-Agent", "BaiduTieba for Android 6.0.1");
			post.addHeader("Host", "c.tieba.baidu.com");
			post.addHeader("Connection", "Keep-Alive");
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("_client_id", "wappc_1381896899508_641"));
			postParams.add(new BasicNameValuePair("_client_type", "4"));
			postParams.add(new BasicNameValuePair("_client_version", "6.0.1"));
			postParams.add(new BasicNameValuePair("_phone_imei", "709ece505ee4e7337d927fb74adb3535"));
			postParams.add(new BasicNameValuePair("cuid", "44CF42C6E5583F7FDF2EC5A2CE538A62|254808586242424"));
			postParams.add(new BasicNameValuePair("from", "baidu_appstore"));
			postParams.add(new BasicNameValuePair("isphone", "0"));
			postParams.add(new BasicNameValuePair("model", "M1"));
			postParams.add(new BasicNameValuePair("password", Base64.encodeToString(password.getBytes(), Base64.DEFAULT).trim()));
			postParams.add(new BasicNameValuePair("stErrorNums", "0"));
			postParams.add(new BasicNameValuePair("stMethod", "1"));
			postParams.add(new BasicNameValuePair("stMode", "1"));
			postParams.add(new BasicNameValuePair("stSize", "292"));
			postParams.add(new BasicNameValuePair("stTime", "488"));
			postParams.add(new BasicNameValuePair("stTimesNum", "0"));
			postParams.add(new BasicNameValuePair("timestamp","1400857242724"));
			postParams.add(new BasicNameValuePair("un", username));
			postParams.add(new BasicNameValuePair("sign", "6FDEA5177DB857BC4325746816ECB537"));
			System.out.println("pw:"+Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
			System.out.println("before setEntity");
			try {
				post.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
				System.out.println("entity"+EntityUtils.toString(post.getEntity()));
				HttpResponse response = client.execute(post);
				System.out.println("executed");
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					throw new StatusCodeException("HTTP状态码错误！", statusCode);
				} else {
					HttpEntity entity = response.getEntity();
					String result = EntityUtils.toString(entity, "utf-8");
					System.out.println("pre send");
					handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, result).sendToTarget();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, "UnsupportedEncodingException").sendToTarget();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, "ClientProtocolException").sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, "IOException").sendToTarget();
			} catch (StatusCodeException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, "StatusCodeException").sendToTarget();
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
					//Exception e = (Exception) msg.obj;
					tips = (String) msg.obj;
					break;
				case ClientApiUtil.SUCCESSED:
					String result = (String) msg.obj;
					Toast.makeText(BindBaiduActivity.this, result, Toast.LENGTH_LONG).show();
					System.out.println(result);
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
				new loginThread(username, password).start();
				break;
		}
	}

}
