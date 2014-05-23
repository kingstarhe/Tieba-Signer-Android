package com.zulwi.tiebasigner.fragment;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.FragmentBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;

public class AccountFragment extends Fragment {
	private FragmentBean[] fragmentList = new FragmentBean[2];
	private int currentFragmentId = 0;
	protected boolean binded = false;
	private MainActivity activity;
	private AccountBean accountBean;
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
					JSONBean data = (JSONBean) msg.obj;
					loadedFlag = true;
					if (data.status == 0) {
						if (!binded) changeFragment(1);
						((UserInfoFragment) fragmentList[1].fragment).setUp(data);
						binded = true;
					} else {
						if (binded) changeFragment(0);
						binded = false;
						tips = "请绑定百度账号";
						finishUserInfoRefresh();
					}
					UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
					cache.saveDataCache("userinfo", data.jsonString);
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
		this.accountBean = this.activity.getAccountBean();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_account, container, false);
		fragmentList[0] = new FragmentBean("绑定", new BindBaiduFragment());
		fragmentList[1] = new FragmentBean("资料", new UserInfoFragment());
		changeFragment(0);
		refreshUserInfo();
		return view;
	}

	protected void changeFragment(int position) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(R.id.account_container, fragmentList[position].fragment);
		ft.commit();
		currentFragmentId = position;
	}

	protected void finishUserInfoRefresh() {
		if (currentFragmentId == 0) ((BindBaiduFragment) (fragmentList[0].fragment)).swipeLayout.setRefreshing(false);
		else ((UserInfoFragment) (fragmentList[1].fragment)).swipeLayout.setRefreshing(false);
	}

	protected void refreshUserInfo() {
		new getBaiduAccountInfo().start();
	}

}
