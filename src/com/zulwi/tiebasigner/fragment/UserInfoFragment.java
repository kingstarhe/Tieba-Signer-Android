package com.zulwi.tiebasigner.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.activity.MainActivity;
import com.zulwi.tiebasigner.adapter.TiebaListAdapter;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.exception.StatusCodeException;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.util.HttpUtil;
import com.zulwi.tiebasigner.view.CircularImage;
import com.zulwi.tiebasigner.view.ListTableView;

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {
	private CircularImage userAvatar;
	private ListTableView tiebaTable;
	@SuppressWarnings("unused")
	private TextView usernameTextView;
	private List<TiebaBean> tiebaList = new ArrayList<TiebaBean>();
	private TiebaListAdapter tiebaListAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	private Dialog dialog;
	private Thread getBaiduAccountInfo = new Thread() {
		public void run() {
			HttpUtil site = new HttpUtil(activity, accountBean.siteUrl + "/plugin.php?id=zw_client_api&a=baidu_account_info", accountBean.cookieString);
			String result;
			try {
				result = site.get();
				JSONObject jsonObject = new JSONObject(result);
				int status = jsonObject.getInt("status");
				String msg = jsonObject.getString("msg");
				if (status == -1) throw new Exception(msg);
				else if (status == 0) {
					JSONObject data = jsonObject.getJSONObject("data");
					handler.obtainMessage(HttpUtil.SUCCESSED, 0, 0, data).sendToTarget();
				} else if (status == 1) {
					JSONObject data = jsonObject.getJSONObject("data");
					handler.obtainMessage(HttpUtil.SUCCESSED, 1, 0, data).sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				handler.obtainMessage(HttpUtil.PARSE_ERROR, 0, 0, e).sendToTarget();
			} catch (StatusCodeException e) {
				e.printStackTrace();
				handler.obtainMessage(HttpUtil.STATUS_ERROR, 0, 0, e).sendToTarget();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				handler.obtainMessage(HttpUtil.NETWORK_FAIL, 0, 0, e).sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
				handler.obtainMessage(HttpUtil.NETWORK_FAIL, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
				handler.obtainMessage(HttpUtil.OTHER_ERROR, 0, 0, e).sendToTarget();
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			String tips = null;
			switch (msg.what) {
				case HttpUtil.NETWORK_FAIL:
					tips = "网络错误";
					break;
				case HttpUtil.STATUS_ERROR:
					StatusCodeException e = (StatusCodeException) msg.obj;
					tips = e.getMessage() + String.valueOf(e.getCode()) + "错误";
					break;
				case HttpUtil.PARSE_ERROR:
					tips = "JSON解析错误，请确认该站点是否支持客户端";
					break;
				case HttpUtil.SUCCESSED:
					if (msg.arg1 == 0) {
						JSONObject data = (JSONObject) msg.obj;
						try {
							tips = data.getString("bd_username");
						} catch (JSONException ej) {
							ej.printStackTrace();
							tips = "JSON解析错误";
						}
					} else {
						tips = "抱歉，请绑定百度账号";
					}
					break;
				default:
					Exception t = (Exception) msg.obj;
					tips = t.getMessage();
					break;
			}
			dialog.dismiss();
			Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
		this.accountBean = this.activity.getAccountBean();
		dialog = DialogUtil.createLoadingDialog(activity, "正在获取百度账号信息", true);
		dialog.show();
		getBaiduAccountInfo.start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
		userAvatar = (CircularImage) view.findViewById(R.id.userinfo_avatar);
		userAvatar.setImageResource(R.drawable.avatar);
		tiebaList.add(new TiebaBean(1, 8, "测试贴吧1"));
		tiebaList.add(new TiebaBean(2, 12, "测试贴吧2"));
		tiebaList.add(new TiebaBean(3, 11, "测试贴吧3"));
		tiebaListAdapter = new TiebaListAdapter(getActivity(), tiebaList);
		tiebaTable = (ListTableView) view.findViewById(R.id.userinfo_tieba_list);
		view.findViewById(R.id.userinfo_follows).setOnClickListener(this);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(this);
		tiebaTable.setAdapter(tiebaListAdapter);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which) {
				System.out.println(which);
			}
		});
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {

	}

}
