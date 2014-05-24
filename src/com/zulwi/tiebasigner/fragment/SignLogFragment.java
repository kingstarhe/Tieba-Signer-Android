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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.SignLogListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;

public class SignLogFragment extends Fragment implements OnRefreshListener {
	private ListView signLogListView;
	private List<TiebaBean> signLogList = new ArrayList<TiebaBean>();
	private SignLogListAdapter signLogAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	private boolean loadedFlag = false;

	private class getSignLogThread extends Thread {
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
					Date date = new Date();  
					result = clientApiUtil.get("sign_log", "date="+(new SimpleDateFormat("yyyyMMdd")).format(date));
					  
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
			switch (msg.what) {
				case ClientApiUtil.ERROR:
					ClientApiException e = (ClientApiException) msg.obj;
					tips = e.getMessage();
					loadedFlag = true;
					break;
				case ClientApiUtil.SUCCESSED:
					JSONBean json = (JSONBean) msg.obj;
					System.out.println("!!!");
					loadedFlag = true;
					if (json.status == 0) {
						try {
							signLogList.clear();
							JSONArray logs = json.data.getJSONArray("log");
							for (int i = 0; i < logs.length(); i++) {
								JSONObject log = logs.getJSONObject(i);
								signLogList.add(new TiebaBean(log.getInt("tid"), log.getString("name"), log.getInt("exp")));
							}
							signLogAdapter = new SignLogListAdapter(getActivity(), signLogList);
							signLogListView.setAdapter(signLogAdapter);
						} catch (JSONException e1) {
							tips = "JSON解析失败";
						}
					} else {
						tips = json.message;
					}
					swipeLayout.setRefreshing(false);
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
	private SwipeRefreshLayout swipeLayout;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.accountBean = this.activity.getAccountBean();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_log_signlog, container, false);
		signLogListView = (ListView) view.findViewById(R.id.sign_log_list);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		swipeLayout.setRefreshing(true);
		new getSignLogThread().start();
		return view;
	}

	@Override
    public void onRefresh() {
		new getSignLogThread().start();
    }
}