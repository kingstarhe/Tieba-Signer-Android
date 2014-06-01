package com.zulwi.tiebasigner.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.view.CircularImage;

@SuppressWarnings("serial")
public class AccountListAdapter extends BaseAdapter implements Serializable{
	private Context context;
	private LayoutInflater inflater;
	public List<AccountBean> list;

	public AccountListAdapter(Context context, List<AccountBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.list = data;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public AccountBean getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.list.get(position).id;
	}

	public void removeAll() {
		list.removeAll(list);
		notifyDataSetChanged();
	}
	
	public void remove(int position){
		list.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_account, null);
			viewHolder.accountAvatar = (CircularImage) convertView.findViewById(R.id.account_avatar);
			viewHolder.accountInfo = (TextView) convertView.findViewById(R.id.account_info);
			viewHolder.siteInfo = (TextView) convertView.findViewById(R.id.site_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		AccountBean bean = list.get(position);
		if (bean.avatar != null) viewHolder.accountAvatar.setImageBitmap(bean.avatar);
		viewHolder.accountInfo.setText(bean.username);
		Uri url = Uri.parse(bean.siteUrl);
		viewHolder.siteInfo.setText(bean.siteName + " - " + url.getHost() + url.getPath());
		return convertView;
	}

	private static class ViewHolder {
		CircularImage accountAvatar;
		TextView accountInfo;
		TextView siteInfo;
	}

}