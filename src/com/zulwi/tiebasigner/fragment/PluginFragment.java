package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.PluginListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.PluginBean;
import com.zulwi.tiebasigner.exception.HttpResultException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.UserCacheUtil;
import com.zulwi.tiebasigner.view.ListTableView;

public class PluginFragment extends Fragment implements OnRefreshListener {
	private ListTableView tiebaTable;
	private MainActivity activity;
	private AccountBean accountBean;
	private boolean loadedFlag = false;
	private Map<String, String> supportedPlugins = new HashMap<String, String>();
	private List<PluginBean> pluginList = new ArrayList<PluginBean>();
	{
		supportedPlugins.put("zw_custom_page", "自定义页面");
		supportedPlugins.put("zw_blockid", "循环封禁");
		supportedPlugins.put("x_tdou", "T豆");
		supportedPlugins.put("xxx_post", "客户端回帖");
		supportedPlugins.put("xxx_meizi", "妹纸认证");
	}

	private class getPluginInfo extends Thread {
		@Override
		public void run() {
			ClientApiUtil clientApiUtil = new ClientApiUtil(accountBean);
			try {
				JSONBean result;
				UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
				String cacheString = cache.getDataCache("plugin_info");
				if (loadedFlag == false && cacheString != null) {
					result = new JSONBean(new JSONObject(cache.getDataCache("plugin_info")));
				} else {
					result = clientApiUtil.get("plugin_info");
				}
				handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, result).sendToTarget();
			} catch (HttpResultException e) {
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
					HttpResultException e = (HttpResultException) msg.obj;
					tips = e.getMessage();
					loadedFlag = true;
					break;
				case ClientApiUtil.SUCCESSED:
					JSONBean json = (JSONBean) msg.obj;
					loadedFlag = true;
					if (json.status == 0) {
						JSONArray plugins;
						try {
							plugins = json.data.getJSONArray("plugins");
							pluginList.clear();
							for (int i = 0; i < plugins.length(); i++) {
								JSONObject plugin = plugins.getJSONObject(i);
								pluginList.add(new PluginBean(plugin.getString("id"), plugin.getString("name"), plugin.getString("ver")));
							}
							PluginListAdapter adapter = new PluginListAdapter(activity, pluginList);
							tiebaTable.setAdapter(adapter);
						} catch (JSONException ej) {
							ej.printStackTrace();
							tips = "JSON解析错误";
						}
					} else {
						tips = json.message;
					}
					UserCacheUtil cache = new UserCacheUtil(activity, accountBean.sid, accountBean.uid);
					cache.saveDataCache("plugin_info", json.jsonString);
					activity.showLoadingDialog(false);
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			swipeLayout.setRefreshing(false);
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_plugin, container, false);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		tiebaTable = (ListTableView) view.findViewById(R.id.plugin_list);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which) {
				Toast.makeText(activity, getString(R.string.maimeng) + " 这个功能还在开发中", Toast.LENGTH_SHORT).show();
			}
		});
		swipeLayout.setRefreshing(true);
		new getPluginInfo().start();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onRefresh() {
		new getPluginInfo().start();
	}

}
