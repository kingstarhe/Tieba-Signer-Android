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
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.CacheUtil;
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
	private ImageView[][] followsAndFollowAvatarImgView = new ImageView[2][4];
	private LinearLayout fallowBar;
	private LinearLayout fansBar;
	private TiebaListAdapter tiebaListAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	public SwipeRefreshLayout swipeLayout;
	private boolean loadedFlag = false;
	private final static int LOAD_USERINFO = 0;
	private final static int UNBIND_BAIDU = LOAD_USERINFO + 1;
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			swipeLayout.setRefreshing(true);
			onRefresh();
		}
	};

	private class loadUserAvatarThread extends Thread {
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

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			try {
				HttpResponse resp = httpClient.execute(httpget);
				if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
					HttpEntity entity = resp.getEntity();
					InputStream in = entity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					handler.obtainMessage(ClientApiUtil.LOAD_IMG, imgViewGroup, which, new BaiduAccountBean(userId, bitmap)).sendToTarget();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	private class unbindBaiduThread extends Thread {
		@Override
		public void run() {
			ClientApiUtil clientApiUtil = new ClientApiUtil(accountBean);
			try {
				JSONBean result;
				result = clientApiUtil.get("unbind_baidu");
				handler.obtainMessage(ClientApiUtil.SUCCESSED, UNBIND_BAIDU, 0, result).sendToTarget();
			} catch (HttpResultException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

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
					JSONBean json = (JSONBean) msg.obj;
					JSONObject object = json.data;
					switch (msg.arg1) {
						case LOAD_USERINFO:
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
								Bitmap cacheAccountAvatar = CacheUtil.getAvatarCache(baiduAccountId, activity);
								if (loadedFlag == false && cacheAccountAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 0, 0, new BaiduAccountBean(baiduAccountId, cacheAccountAvatar)).sendToTarget();
								else new loadUserAvatarThread(baiduAccountId, baiduAccountAvatarUrl, 0, 0).start();
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
								for (ImageView imgView[] : followsAndFollowAvatarImgView) {
									for (ImageView iv : imgView) {
										iv.setImageResource(android.R.color.transparent);
									}
								}
								JSONArray follow = object.getJSONObject("follow").getJSONArray("user_list");
								for (int i = 0; i < follow.length() && i < 4; i++) {
									JSONObject followInfo = follow.getJSONObject(i);
									String userId = followInfo.getString("id");
									Bitmap cacheAvatar = CacheUtil.getAvatarCache(userId, activity);
									if (loadedFlag == false && cacheAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 1, i, new BaiduAccountBean(userId, cacheAvatar)).sendToTarget();
									else new loadUserAvatarThread(userId, followInfo.getString("head_photo"), 1, i).start();
								}
								fallowBar.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
								followTipsTextView.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
								JSONArray fans = object.getJSONObject("fans").getJSONArray("user_list");
								for (int i = 0; i < fans.length() && i < 4; i++) {
									JSONObject fansInfo = fans.getJSONObject(i);
									String userId = fansInfo.getString("id");
									Bitmap cacheAvatar = CacheUtil.getAvatarCache(userId, activity);
									if (loadedFlag == false && cacheAvatar != null) handler.obtainMessage(ClientApiUtil.LOAD_IMG, 2, i, new BaiduAccountBean(userId, cacheAvatar)).sendToTarget();
									else new loadUserAvatarThread(userId, fansInfo.getString("head_photo"), 2, i).start();
								}
								fansBar.setVisibility(fans.length() == 0 ? View.GONE : View.VISIBLE);
								fansTipsTextView.setVisibility(follow.length() == 0 ? View.GONE : View.VISIBLE);
								if (!fragment.binded) fragment.changeFragment(0);
								fragment.binded = true;
							} catch (JSONException ej) {
								ej.printStackTrace();
								tips = "JSON解析错误";
							}
							swipeLayout.setRefreshing(false);
							loadedFlag = true;
							break;
						case UNBIND_BAIDU:
							if (json.status == 0) {
								onRefresh();
							} else swipeLayout.setRefreshing(false);
							tips = json.message;
							break;
					}
					break;
				case ClientApiUtil.LOAD_IMG:
					BaiduAccountBean baiduAccountBean = (BaiduAccountBean) msg.obj;
					CacheUtil.saveAvatarCache(baiduAccountBean.userId, baiduAccountBean.avatar, activity);
					switch (msg.arg1) {
						case 0:
							userAvatar.setImageBitmap(baiduAccountBean.avatar);
							activity.setAccountAvatar(baiduAccountBean.avatar);
							break;
						default:
							followsAndFollowAvatarImgView[msg.arg1 - 1][msg.arg2].setImageBitmap(baiduAccountBean.avatar);
					}
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			activity.showLoadingDialog(false);
			if (tips != null && !tips.equals("")) Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
		}
	};
	private AccountFragment fragment;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.fragment = (AccountFragment) getParentFragment();
		this.activity = (MainActivity) activity;
		this.accountBean = this.activity.getAccountBean();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.zulwi.tiebasigner.REFRESH_USERINFO");
		activity.registerReceiver(broadcastReceiver, intentFilter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.userinfo, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			new AlertDialog.Builder(activity).setTitle("取消绑定").setMessage("确认要解除绑定吗？\n(解除绑定后自动签到将停止，所有记录将被清除)").setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					activity.showLoadingDialog(true);
					swipeLayout.setRefreshing(true);
					new unbindBaiduThread().start();
				}
			}).setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setCancelable(false).create().show();
		}
		return super.onOptionsItemSelected(item);
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
		followsAndFollowAvatarImgView[0][0] = (ImageView) view.findViewById(R.id.follow_avatar_1);
		followsAndFollowAvatarImgView[0][1] = (ImageView) view.findViewById(R.id.follow_avatar_2);
		followsAndFollowAvatarImgView[0][2] = (ImageView) view.findViewById(R.id.follow_avatar_3);
		followsAndFollowAvatarImgView[0][3] = (ImageView) view.findViewById(R.id.follow_avatar_4);
		followsAndFollowAvatarImgView[1][0] = (ImageView) view.findViewById(R.id.fans_avatar_1);
		followsAndFollowAvatarImgView[1][1] = (ImageView) view.findViewById(R.id.fans_avatar_2);
		followsAndFollowAvatarImgView[1][2] = (ImageView) view.findViewById(R.id.fans_avatar_3);
		followsAndFollowAvatarImgView[1][3] = (ImageView) view.findViewById(R.id.fans_avatar_4);
		tiebaTable = (ListTableView) view.findViewById(R.id.userinfo_tieba_list);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which) {
			}
		});
		tiebaListSwitcher = (TextView) view.findViewById(R.id.userinfo_more_tieba);
		tiebaListSwitcher.setOnClickListener(this);
		view.findViewById(R.id.userinfo_follows).setOnClickListener(this);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(this);
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
		fragment.refreshUserInfo();
	}

	protected void setUp(JSONBean data) {
		handler.obtainMessage(ClientApiUtil.SUCCESSED, LOAD_USERINFO, 0, data).sendToTarget();
	}

	@Override
	public void onDestroy() {
		activity.unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

}
