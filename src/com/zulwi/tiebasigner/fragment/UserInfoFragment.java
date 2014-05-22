package com.zulwi.tiebasigner.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.TiebaListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.BaiduAccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;
import com.zulwi.tiebasigner.view.CircularImage;
import com.zulwi.tiebasigner.view.ListTableView;

public class UserInfoFragment extends Fragment implements View.OnClickListener, OnRefreshListener {
	private CircularImage userAvatar;
	private ListTableView tiebaTable;
	private TextView tiebaListSwitcher;
	private TextView usernameTextView;
	private TextView introTextView;
	private TextView sexTextView;
	private TextView tiebaAgeTextView;
	private TextView tiebaTipsTextView;
	private TextView followTipsTextView;
	private TextView fansTipsTextView;
	private String baiduAccountUsername;
	private String baiduAccountId;
	private String baiduAccountAvatarUrl;
	private String baiduAccountIntro;
	private int baiduAccountSex;
	private String baiduAccountTiebaAge;
	private int baiduAccountFansNum;
	private int baiduAccountFollowNum;
	private int baiduAccountTiebaNum;
	private List<TiebaBean> tiebaList = new ArrayList<TiebaBean>();
	private List<TiebaBean> overviewTiebaList = new ArrayList<TiebaBean>();
	private ImageView[] followsAvatarImgView = new ImageView[4];
	private ImageView[] fansAvatarImgView = new ImageView[4];
	private LinearLayout fallowBar;
	private LinearLayout fansBar;
	private TiebaListAdapter tiebaListAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	private Dialog dialog;
	public SwipeRefreshLayout swipeLayout;
	private boolean loadedFlag = false;

	private class getBaiduAccountInfo extends Thread {
		public void run() {
			ClientApiUtil clientApiUtil = new ClientApiUtil(activity, accountBean.siteUrl, accountBean.cookieString);
			try {
				JSONBean result;
				UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
				String cacheString = cache.getDataCache("userinfo");
				if (loadedFlag == false && cacheString != null) {
					result = new JSONBean(new JSONObject(cache.getDataCache("userinfo")));
				} else {
					result = clientApiUtil.get("baidu_account_info");
				}
				handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, result).sendToTarget();
			} catch (ClientApiException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class loadUserAvatarThread implements Runnable {
		private String url;
		private String userId;
		private int imgViewGroup;
		private int which;

		public loadUserAvatarThread(String userId, String url, int imgViewGroup, int which) {
			this.url = url;
			this.userId = userId;
			this.imgViewGroup = imgViewGroup;
			this.which = which;
		}

		public void run() {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			try {
				HttpResponse resp = httpclient.execute(httpget);
				if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
					HttpEntity entity = resp.getEntity();
					InputStream in = entity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					handler.obtainMessage(ClientApiUtil.LOAD_IMG, imgViewGroup, which, new BaiduAccountBean(userId, bitmap)).sendToTarget();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
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
					ClientApiException e = (ClientApiException) msg.obj;
					tips = e.getMessage();
					break;
				case ClientApiUtil.SUCCESSED:
					JSONBean data = (JSONBean) msg.obj;
					JSONObject object = data.data;
					if (data.status == 0) {
						try {
							baiduAccountUsername = object.getString("username");
							baiduAccountId = object.getString("id");
							baiduAccountIntro = object.getString("intro");
							baiduAccountSex = object.getInt("sex");
							baiduAccountTiebaAge = object.getString("tb_age");
							baiduAccountFansNum = object.getInt("fans_num");
							baiduAccountFollowNum = object.getInt("follow_num");
							baiduAccountTiebaNum = object.getInt("tb_num");
							baiduAccountAvatarUrl = object.getString("avatar");
							Bitmap cacheAccountAvatar = UserCacheUtil.getImgCache(baiduAccountId, activity);
							if (loadedFlag == false && cacheAccountAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 0, 0, new BaiduAccountBean(baiduAccountId, cacheAccountAvatar)).sendToTarget();
							else new Thread(new loadUserAvatarThread(baiduAccountId, baiduAccountAvatarUrl, 0, 0)).start();
							usernameTextView.setText(baiduAccountUsername);
							introTextView.setText(baiduAccountIntro);
							switch (baiduAccountSex) {
								case 1:
									sexTextView.setText("♂");
									sexTextView.setTextColor(Color.parseColor("#00B4FD"));
									break;
								case 2:
									sexTextView.setText("♀");
									sexTextView.setTextColor(Color.parseColor("#E53A37"));
									break;
								default:
									sexTextView.setText("?");
									sexTextView.setTextColor(Color.GRAY);
							}
							tiebaAgeTextView.setText(baiduAccountTiebaAge + getString(R.string.years));
							tiebaTipsTextView.setText(getString(R.string.loading_tiebas).replace("0", String.valueOf(baiduAccountTiebaNum)));
							followTipsTextView.setText(getString(R.string.loading_follows).replace("0", String.valueOf(baiduAccountFollowNum)));
							fansTipsTextView.setText(getString(R.string.loading_fans).replace("0", String.valueOf(baiduAccountFansNum)));
							JSONArray tiebas = object.getJSONArray("tiebas");
							tiebaList.clear();
							for (int i = 0; i < tiebas.length(); i++) {
								JSONObject tieba = tiebas.getJSONObject(i);
								tiebaList.add(new TiebaBean(tieba.getInt("forum_id"), tieba.getInt("level_id"), tieba.getString("forum_name")));
							}
							overviewTiebaList.clear();
							for (int i = 0; i < tiebaList.size() && i < 4; i++) {
								overviewTiebaList.add(tiebaList.get(i));
							}
							tiebaListAdapter = new TiebaListAdapter(getActivity(), overviewTiebaList);
							tiebaListSwitcher.setVisibility(View.VISIBLE);
							if (tiebaList.size() > 4) tiebaListSwitcher.setText(getString(R.string.show_more));
							else if (tiebaList.size() <= 4 && tiebaList.size() != 0) tiebaListSwitcher.setVisibility(View.GONE);
							tiebaTable.setAdapter(tiebaListAdapter);
							for (ImageView imgView : followsAvatarImgView) {
								imgView.setImageResource(android.R.color.transparent);
							}
							for (ImageView imgView : fansAvatarImgView) {
								imgView.setImageResource(android.R.color.transparent);
							}
							JSONArray follow = object.getJSONObject("follow").getJSONArray("user_list");
							for (int i = 0; i < follow.length() && i < 4; i++) {
								JSONObject followInfo = follow.getJSONObject(i);
								String userId = followInfo.getString("id");
								Bitmap cacheAvatar = UserCacheUtil.getImgCache(userId, activity);
								if (loadedFlag == false && cacheAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 1, i, new BaiduAccountBean(userId, cacheAvatar)).sendToTarget();
								else new Thread(new loadUserAvatarThread(userId, followInfo.getString("head_photo"), 1, i)).start();
							}
							fallowBar.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
							followTipsTextView.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
							JSONArray fans = object.getJSONObject("fans").getJSONArray("user_list");
							for (int i = 0; i < fans.length() && i < 4; i++) {
								JSONObject fansInfo = fans.getJSONObject(i);
								String userId = fansInfo.getString("id");
								Bitmap cacheAvatar = UserCacheUtil.getImgCache(userId, activity);
								if (loadedFlag == false && cacheAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 1, i, new BaiduAccountBean(userId, cacheAvatar)).sendToTarget();
								else new Thread(new loadUserAvatarThread(userId, fansInfo.getString("head_photo"), 2, i)).start();
							}
							fansBar.setVisibility(fans.length() == 0 ? View.GONE : View.VISIBLE);
							fansTipsTextView.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
							UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
							cache.saveDataCache("userinfo", data.jsonString);
							activity.finishUserInfoRefresh();
							if (!activity.binded) activity.changeFragment(0);
							activity.binded = true;
							loadedFlag = true;
						} catch (JSONException ej) {
							ej.printStackTrace();
							tips = "JSON解析错误";
						}
					} else {
						if(activity.binded) activity.changeFragment(1);
						activity.binded = false;
						tips = "抱歉，请绑定百度账号";
					}
					break;
				case ClientApiUtil.LOAD_IMG:
					BaiduAccountBean baiduAccountBean = (BaiduAccountBean) msg.obj;
					UserCacheUtil.saveImgCache(baiduAccountBean.userId, baiduAccountBean.avatar, activity);
					switch (msg.arg1) {
						case 0:
							userAvatar.setImageBitmap(baiduAccountBean.avatar);
							activity.setAccountAvatar(baiduAccountBean.avatar);
							break;
						case 1:
							followsAvatarImgView[msg.arg2].setImageBitmap(baiduAccountBean.avatar);
							break;
						case 2:
							fansAvatarImgView[msg.arg2].setImageBitmap(baiduAccountBean.avatar);
							break;
					}
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			dialog.dismiss();
			if (tips != null && !tips.equals("")) Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.accountBean = this.activity.getAccountBean();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		usernameTextView = (TextView) view.findViewById(R.id.userinfo_name);
		introTextView = (TextView) view.findViewById(R.id.userinfo_intro);
		sexTextView = (TextView) view.findViewById(R.id.userinfo_sex);
		tiebaAgeTextView = (TextView) view.findViewById(R.id.userinfo_tiebaage);
		tiebaTipsTextView = (TextView) view.findViewById(R.id.userinfo_tieba_tips);
		followTipsTextView = (TextView) view.findViewById(R.id.userinfo_follow_tips);
		fallowBar = (LinearLayout) view.findViewById(R.id.userinfo_follows);
		fansTipsTextView = (TextView) view.findViewById(R.id.userinfo_fans_tips);
		fansBar = (LinearLayout) view.findViewById(R.id.userinfo_fans);
		userAvatar = (CircularImage) view.findViewById(R.id.userinfo_avatar);
		followsAvatarImgView[0] = (ImageView) view.findViewById(R.id.follow_avatar_1);
		followsAvatarImgView[1] = (ImageView) view.findViewById(R.id.follow_avatar_2);
		followsAvatarImgView[2] = (ImageView) view.findViewById(R.id.follow_avatar_3);
		followsAvatarImgView[3] = (ImageView) view.findViewById(R.id.follow_avatar_4);
		fansAvatarImgView[0] = (ImageView) view.findViewById(R.id.fans_avatar_1);
		fansAvatarImgView[1] = (ImageView) view.findViewById(R.id.fans_avatar_2);
		fansAvatarImgView[2] = (ImageView) view.findViewById(R.id.fans_avatar_3);
		fansAvatarImgView[3] = (ImageView) view.findViewById(R.id.fans_avatar_4);
		tiebaTable = (ListTableView) view.findViewById(R.id.userinfo_tieba_list);
		tiebaListSwitcher = (TextView) view.findViewById(R.id.userinfo_more_tieba);
		tiebaListSwitcher.setOnClickListener(this);
		view.findViewById(R.id.userinfo_follows).setOnClickListener(this);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(this);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which) {
			}
		});
		dialog = DialogUtil.createLoadingDialog(activity, "正在获取百度账号信息", true);
		dialog.show();
		refreshUserInfo();
		return view;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.userinfo_more_tieba:
				String oldText = tiebaListSwitcher.getText().toString();
				if (oldText.equals(getString(R.string.show_more))) {
					tiebaListAdapter = new TiebaListAdapter(getActivity(), tiebaList);
					tiebaListSwitcher.setText(getString(R.string.shrink_list));
				} else if (oldText.equals(getString(R.string.shrink_list))) {
					tiebaListAdapter = new TiebaListAdapter(getActivity(), overviewTiebaList);
					tiebaListSwitcher.setText(getString(R.string.show_more));
				} else break;
				tiebaTable.setAdapter(tiebaListAdapter);
				break;
		}
	}

	@Override
	public void onRefresh() {
		refreshUserInfo();
	}

	public void refreshUserInfo() {
		new getBaiduAccountInfo().start();
	}

}
