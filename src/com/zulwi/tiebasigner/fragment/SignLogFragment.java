package com.zulwi.tiebasigner.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.LogListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;

public class SignLogFragment extends BaseFragment implements OnRefreshListener {
	private ListView signLogListView;
	private SwipeRefreshLayout logSwipeLayout;
	private SwipeRefreshLayout tipsSwipeLayout;
	private LogFragment fragment;
	private List<TiebaBean> signLogList = new ArrayList<TiebaBean>();
	private LogListAdapter logListAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	private boolean loadedFlag = false;
	private String currentDate;
	private String previousDate = "0";
	private String nextDate = "0";
	private int stat[] = { 0, 0, 0, 0, 0 };

	private class getSignLogThread extends Thread {
		private String date;

		public getSignLogThread(String date) {
			this.date = date;
		}

		@Override
		@SuppressLint("SimpleDateFormat")
		public void run() {
			ClientApiUtil clientApiUtil = new ClientApiUtil(activity, accountBean.siteUrl, accountBean.cookieString);
			try {
				JSONBean result;
				UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
				String cacheString = cache.getDataCache("signlog");
				if (loadedFlag == false && cacheString != null) {
					result = new JSONBean(new JSONObject(cache.getDataCache("userinfo")));
				} else {
					result = clientApiUtil.get("sign_log", "date=" + date);
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

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			setRefreshing(false);
			switch (msg.what) {
				case ClientApiUtil.ERROR:
					ClientApiException e = (ClientApiException) msg.obj;
					tips = e.getMessage();
					loadedFlag = true;
					break;
				case ClientApiUtil.SUCCESSED:
					JSONBean json = (JSONBean) msg.obj;
					loadedFlag = true;
					if (json.status == 0) {
						try {
							stat[4] = stat[3] = stat[2] = stat[1] = stat[0] = 0;
							signLogList.clear();
							currentDate = json.data.getString("date");
							previousDate = json.data.getString("previous_date");
							nextDate = json.data.getString("next_date");
							JSONArray logs = json.data.getJSONArray("log");
							for (int i = 0; i < logs.length(); i++) {
								JSONObject log = logs.getJSONObject(i);
								int status = log.getInt("status");
								stat[status + 2]++;
								signLogList.add(new TiebaBean(log.getInt("tid"), log.getString("name"), log.getInt("status")));
							}
							tipsSwipeLayout.setVisibility(signLogList.size() != 0 ? View.GONE : View.VISIBLE);
							logSwipeLayout.setVisibility(signLogList.size() == 0 ? View.GONE : View.VISIBLE);
							if (currentDate.length() == 8) fragment.setFragmentTitle(0, currentDate.substring(0, 4) + "-" + currentDate.substring(4, 6) + "-" + currentDate.substring(6) + "    " + String.valueOf(stat[4]) + "/" + String.valueOf(stat[0] + stat[1] + stat[2] + stat[3] + stat[4]));
							logListAdapter = new LogListAdapter(getActivity(), signLogList);
							signLogListView.setAdapter(logListAdapter);
						} catch (JSONException e1) {
							tips = "JSON解析失败";
						}
					} else {
						tips = json.message;
					}
					UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
					cache.saveDataCache("sign_log", json.jsonString);
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			if (tips != null && !tips.equals("")) Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.fragment = (LogFragment) getParentFragment();
		this.accountBean = this.activity.getAccountBean();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log_signlog, container, false);
		signLogListView = (ListView) view.findViewById(R.id.sign_log_list);
		logSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.log_swipe_container);
		logSwipeLayout.setOnRefreshListener(this);
		logSwipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		logSwipeLayout.setRefreshing(true);
		tipsSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.tip_swipe_container);
		tipsSwipeLayout.setOnRefreshListener(this);
		tipsSwipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		Date date = new Date();
		currentDate = new SimpleDateFormat("yyyyMMdd").format(date);
		onRefresh();
		return view;
	}

	@Override
	public void onRefresh() {
		new getSignLogThread(currentDate).start();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sign_log, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (logSwipeLayout.isRefreshing()) return false;
		switch (item.getItemId()) {
			case R.id.sign_log_pre:
				if (previousDate.equals("0")) {
					Toast.makeText(activity, "没有上一页了", Toast.LENGTH_SHORT).show();
				} else {
					setRefreshing(true);
					new getSignLogThread(previousDate).start();
				}
				break;
			case R.id.sign_log_next:
				if (nextDate.equals("0")) {
					Toast.makeText(activity, "没有上一页了", Toast.LENGTH_SHORT).show();
				} else {
					setRefreshing(true);
					new getSignLogThread(nextDate).start();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setRefreshing(boolean isRefreshing) {
		tipsSwipeLayout.setRefreshing(isRefreshing);
		logSwipeLayout.setRefreshing(isRefreshing);
	}
}