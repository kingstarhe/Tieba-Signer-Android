package com.zulwi.tiebasigner.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
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
import com.zulwi.tiebasigner.bean.JSONBean;
import com.zulwi.tiebasigner.bean.TiebaBean;
import com.zulwi.tiebasigner.exception.ClientApiException;
import com.zulwi.tiebasigner.util.ClientApiUtil;
import com.zulwi.tiebasigner.util.DialogUtil;
import com.zulwi.tiebasigner.view.CircularImage;
import com.zulwi.tiebasigner.view.ListTableView;

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {
	private CircularImage userAvatar;
	private ListTableView tiebaTable;
	private TextView usernameTextView;
	private TextView emailTextView;
	private TextView SexTextView;
	private TextView TiebaAgeTextView;
	private TextView TiebaTipsTextView;
	private TextView FollowTipsTextView;
	private TextView FansTipsTextView;
	private String baiduAccountUsername;
	private String baiduAccountEmail;
	private int baiduAccountSex;
	private String baiduAccountTiebaAge;
	private int baiduAccountFansNum;
	private int baiduAccountFollowNum;
	private int baiduAccountTiebaNum;
	private List<TiebaBean> tiebaList = new ArrayList<TiebaBean>();
	private List<TiebaBean> overviewTiebaList = new ArrayList<TiebaBean>();
	private TiebaListAdapter tiebaListAdapter;
	private MainActivity activity;
	private AccountBean accountBean;
	private Dialog dialog;
	private Thread getBaiduAccountInfo = new Thread() {
		public void run() {
			ClientApiUtil clientApiUtil = new ClientApiUtil(activity, accountBean.siteUrl, accountBean.cookieString);
			try {
				JSONBean result = clientApiUtil.get("baidu_account_info");
				handler.obtainMessage(ClientApiUtil.SUCCESSED, 0, 0, result).sendToTarget();
			} catch (ClientApiException e) {
				e.printStackTrace();
				handler.obtainMessage(ClientApiUtil.ERROR, 0, 0, e).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
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
							baiduAccountEmail = object.getString("email");
							baiduAccountSex = object.getInt("sex");
							baiduAccountTiebaAge = object.getString("tb_age");
							baiduAccountFansNum = object.getInt("fans_num");
							baiduAccountFollowNum = object.getInt("follow_num");
							baiduAccountTiebaNum = object.getInt("tb_num");
							usernameTextView.setText(baiduAccountUsername);
							emailTextView.setText(baiduAccountEmail);
							switch (baiduAccountSex) {
								case 1:
									SexTextView.setText("♂");
									SexTextView.setTextColor(Color.parseColor("#00b4fd"));
									break;
								case 2:
									SexTextView.setText("♀");
									SexTextView.setTextColor(Color.RED);
									break;
								default:
									SexTextView.setText("?");
									SexTextView.setTextColor(Color.GRAY);
							}
							TiebaAgeTextView.setText(baiduAccountTiebaAge + getString(R.string.years));
							TiebaTipsTextView.setText(getString(R.string.loading_tiebas).replace("0", String.valueOf(baiduAccountTiebaNum)));
							FollowTipsTextView.setText(getString(R.string.loading_follows).replace("0", String.valueOf(baiduAccountFollowNum)));
							FansTipsTextView.setText(getString(R.string.loading_fans).replace("0", String.valueOf(baiduAccountFansNum)));
							JSONArray tiebas = object.getJSONArray("tiebas");
							for (int i = 0; i < tiebas.length(); i++) {
								JSONObject tieba = tiebas.getJSONObject(i);
								tiebaList.add(new TiebaBean(tieba.getInt("forum_id"), tieba.getInt("level_id"), tieba.getString("forum_name")));
							}
							for (int i = 0; i < tiebaList.size() && i < 4; i++) {
								overviewTiebaList.add(tiebaList.get(i));
							}
							tiebaListAdapter = new TiebaListAdapter(getActivity(), overviewTiebaList, true);
							tiebaTable.setAdapter(tiebaListAdapter);
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
		View view = inflater.inflate(R.layout.fragment_userinfo, container, false);
		usernameTextView = (TextView) view.findViewById(R.id.userinfo_name);
		emailTextView = (TextView) view.findViewById(R.id.userinfo_email);
		SexTextView = (TextView) view.findViewById(R.id.userinfo_sex);
		TiebaAgeTextView = (TextView) view.findViewById(R.id.userinfo_tiebaage);
		TiebaTipsTextView = (TextView) view.findViewById(R.id.userinfo_tieba_tips);
		FollowTipsTextView = (TextView) view.findViewById(R.id.userinfo_follow_tips);
		FansTipsTextView = (TextView) view.findViewById(R.id.userinfo_fans_tips);
		userAvatar = (CircularImage) view.findViewById(R.id.userinfo_avatar);
		userAvatar.setImageResource(R.drawable.avatar);
		tiebaTable = (ListTableView) view.findViewById(R.id.userinfo_tieba_list);
		view.findViewById(R.id.userinfo_follows).setOnClickListener(this);
		view.findViewById(R.id.userinfo_fans).setOnClickListener(this);
		tiebaTable.setOnClickListener(new ListTableView.onClickListener() {
			@Override
			public void onClick(View v, int which, boolean last) {
				if (last) {
					tiebaListAdapter = new TiebaListAdapter(getActivity(), tiebaListAdapter.overview ? tiebaList : overviewTiebaList, !tiebaListAdapter.overview);
					tiebaTable.setAdapter(tiebaListAdapter);
				}
			}
		});
		dialog = DialogUtil.createLoadingDialog(activity, "正在获取百度账号信息", true);
		dialog.show();
		getBaiduAccountInfo.start();
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
