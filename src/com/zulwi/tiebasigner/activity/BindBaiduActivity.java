package com.zulwi.tiebasigner.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.HttpResultBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.CodingUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.HttpUtil;

@SuppressLint("DefaultLocale")
public class BindBaiduActivity extends ActionBarActivity {
	private AccountBean accountBean;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText vCodeEditText;
	private CheckBox riskCheckbox;
	private CheckBox freeCheckbox;
	private LinearLayout vCodeLayout;
	private Dialog progressDialog;
	private ImageView vCodeImageView;
	private String vCodeMd5;
	private String vCodeImageUrl;
	private int sid;

	private class loginThread extends Thread {
		private String username;
		private String password;
		private String vCode;

		public loginThread(String username, String password, String vCode) {
			this.username = username;
			this.password = password;
			this.vCode = vCode;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			super.run();
			List<NameValuePair> header = new ArrayList<NameValuePair>();
			header.add(new BasicNameValuePair("Content-Type", "application/x-www-form-urlencoded"));
			header.add(new BasicNameValuePair("User-Agent", "BaiduTieba for Android 6.0.1"));
			header.add(new BasicNameValuePair("Host", "c.tieba.baidu.com"));
			header.add(new BasicNameValuePair("Connection", "Keep-Alive"));
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("_client_id", "wappc_1388018171504_766"));
			postParams.add(new BasicNameValuePair("_client_type", "1"));
			postParams.add(new BasicNameValuePair("_client_version", "6.0.1"));
			postParams.add(new BasicNameValuePair("_phone_imei", "841d9bb5540813bed5fa9a08144d0a11"));
			postParams.add(new BasicNameValuePair("cuid", "8E752B2A26409EA5C4937FFE28D77528|342648859043605"));
			postParams.add(new BasicNameValuePair("from", "baidu_appstore"));
			postParams.add(new BasicNameValuePair("isphone", "0"));
			postParams.add(new BasicNameValuePair("model", "M1"));
			postParams.add(new BasicNameValuePair("passwd", Base64.encodeToString(password.getBytes(), Base64.DEFAULT).trim()));
			postParams.add(new BasicNameValuePair("stErrorNums", "0"));
			postParams.add(new BasicNameValuePair("stMethod", "1"));
			postParams.add(new BasicNameValuePair("stMode", "1"));
			postParams.add(new BasicNameValuePair("stSize", String.valueOf(new Random(50).nextInt(2000))));
			postParams.add(new BasicNameValuePair("stTime", String.valueOf(new Random(50).nextInt(200))));
			postParams.add(new BasicNameValuePair("stTimesNum", "0"));
			postParams.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
			postParams.add(new BasicNameValuePair("un", username));
			if (vCode != null) postParams.add(new BasicNameValuePair("vcode", vCode));
			if (vCode != null) postParams.add(new BasicNameValuePair("vcode_md5", vCodeMd5));
			StringBuilder signString = new StringBuilder();
			for (int i = 0; i < postParams.size(); i++) {
				signString.append(postParams.get(i).getName() + "=" + postParams.get(i).getValue());
			}
			postParams.add(new BasicNameValuePair("sign", CodingUtil.MD5(signString.toString() + "tiebaclient!!!").toUpperCase()));
			try {
				HttpResultBean resultBean = HttpUtil.post("http://c.tieba.baidu.com/c/s/login", postParams, header);
				handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, resultBean.result).sendToTarget();
			} catch (HttpResultException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			}
		}
	}

	private class getSiteSid extends Thread {
		@Override
		public void run() {
			super.run();
			ClientApiUtil api = new ClientApiUtil(BindBaiduActivity.this, accountBean);
			try {
				JSONBean jsonBean = api.get("cloud_info");
				sid = jsonBean.data.getInt("sid");
			} catch (HttpResultException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			} catch (JSONException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, new HttpResultException("站点 SID 获取失败，将无法正常绑定百度账号！")).sendToTarget();
			}
		}
	}

	private class loadVCodeImageThread extends Thread {
		private String url;

		public loadVCodeImageThread(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpGet httpget = new HttpGet(url);
			try {
				HttpResponse resp = httpClient.execute(httpget);
				if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
					HttpEntity entity = resp.getEntity();
					InputStream in = entity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					handler.obtainMessage(ClientApiUtil.LOAD_IMG, bitmap).sendToTarget();
				}
			} catch (IOException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR).sendToTarget();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	@SuppressLint({ "HandlerLeak", "SetJavaScriptEnabled", "DefaultLocale" })
	private Handler handler = new Handler() {
		@SuppressLint("DefaultLocale")
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
					String result = (String) msg.obj;
					try {
						JSONObject json = new JSONObject(result);
						int errorCode = json.getInt("error_code");
						String baiduId = new Random(10000000).nextInt(99999999) + "" + new Random(10000000).nextInt(99999999) + "" + new Random(10000000).nextInt(99999999) + "" + new Random(10000000).nextInt(99999999);
						String baiduCookie = "BAIDUID=" + baiduId.toUpperCase() + ":FG=1;BDUSS=" + json.getJSONObject("user").getString("BDUSS") + ";";
						System.out.println("baiduCookie: " + baiduCookie);
						vCodeLayout.setVisibility(View.GONE);
						if (errorCode == 0) {
							tips = "登录成功，正在发送 Cookie 至助手站点...";
							System.out.println(sid);
							LayoutInflater inflater = getLayoutInflater();
							View layout = inflater.inflate(R.layout.dialog_send_cookie, (ViewGroup) findViewById(R.layout.dialog_send_cookie));
							AlertDialog.Builder builder = new AlertDialog.Builder(BindBaiduActivity.this);
							AlertDialog sendCookieDialog = builder.setTitle("发送 Cookie").setView(layout).setPositiveButton("关闭", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Toast.makeText(BindBaiduActivity.this, "完成绑定，请检查cookie是否有效！", Toast.LENGTH_LONG).show();
								}
							}).create();
							WebView webView = (WebView) layout.findViewById(R.id.send_cookie);
							CookieSyncManager.createInstance(BindBaiduActivity.this);
							CookieManager cookieManager = CookieManager.getInstance();
							cookieManager.setAcceptCookie(true);
							cookieManager.setCookie(accountBean.siteUrl, accountBean.cookieString);
							CookieSyncManager.getInstance().sync();
							String url = "https://api.ikk.me/v2/manual_bind.php?sid=" + sid + "&formhash=" + accountBean.formhash;
							System.out.println(url);
							WebSettings webSettings = webView.getSettings();
							webSettings.setJavaScriptEnabled(true);
							webSettings.setSupportMultipleWindows(false);
							webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
							webView.postUrl(url, EncodingUtils.getBytes("cookie=" + baiduCookie + ";", "UTF-8"));
							sendCookieDialog.show();
						} else if (errorCode == 5) {
							JSONObject anti = json.getJSONObject("anti");
							vCodeMd5 = anti.getString("vcode_md5");
							vCodeImageUrl = anti.getString("vcode_pic_url");
							vCodeEditText.setText("");
							vCodeLayout.setVisibility(View.VISIBLE);
							tips = "请输入验证码";
							loadVCodeImage();
						} else {
							tips = json.getString("error_msg");
						}
					} catch (JSONException ej) {
						ej.printStackTrace();
						tips = "JSON解析错误";
					}
					System.out.println(result);
					break;
				case ClientApiUtil.LOAD_IMG:
					Bitmap bitmap = (Bitmap) msg.obj;
					vCodeImageView.setImageBitmap(bitmap);
					break;
			}
			progressDialog.dismiss();
			if (tips != null && !tips.equals("")) Toast.makeText(BindBaiduActivity.this, tips, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountBean = (AccountBean) getIntent().getSerializableExtra("accountBean");
		new getSiteSid().start();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_bind_baidu);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		vCodeEditText = (EditText) findViewById(R.id.vcode);
		riskCheckbox = (CheckBox) findViewById(R.id.checkbox_risk);
		freeCheckbox = (CheckBox) findViewById(R.id.checkbox_free);
		vCodeLayout = (LinearLayout) findViewById(R.id.vcode_layout);
		vCodeImageView = (ImageView) findViewById(R.id.vcode_image);
		progressDialog = DialogUtil.createLoadingDialog(this, "正在登录,请稍后...", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.login:
				boolean needVCode = vCodeLayout.getVisibility() == View.VISIBLE;
				if (!riskCheckbox.isChecked() || !freeCheckbox.isChecked()) {
					Toast.makeText(this, "必须同时勾选 \"" + getString(R.string.bd_checkbox_risk) + "\" 和 \"" + getString(R.string.bd_checkbox_free) + "\" 才能继续", Toast.LENGTH_LONG).show();
					return;
				}
				String username = usernameEditText.getText().toString().trim();
				String password = passwordEditText.getText().toString().trim();
				String vCode = vCodeEditText.getText().toString().trim();
				if (username.equals("") || password.equals("") || (needVCode && vCode.equals(""))) {
					Toast.makeText(this, needVCode ? "用户名、密码、验证码均不能为空！" : "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
					return;
				}
				progressDialog.show();
				new loginThread(username, password, needVCode ? vCode : null).start();
				break;
			case R.id.vcode_image:
				Toast.makeText(this, "刷新验证码...", Toast.LENGTH_LONG).show();
				loadVCodeImage();
				break;
		}
	}

	private void loadVCodeImage() {
		new loadVCodeImageThread(vCodeImageUrl).start();
	}
}
