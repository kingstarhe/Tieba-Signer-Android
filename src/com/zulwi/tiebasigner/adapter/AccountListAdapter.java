package com.zulwi.tiebasigner.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;

@SuppressWarnings("serial")
public class AccountListAdapter extends BaseAdapter implements Serializable {
	private Context context;
	private LayoutInflater inflater;
	public List<AccountBean> list;

	public AccountListAdapter(Context context, List<AccountBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_site, null);
			viewHolder.accountInfo = (TextView) convertView.findViewById(R.id.account_info);
			viewHolder.siteInfo = (TextView) convertView.findViewById(R.id.site_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.accountInfo.setText(list.get(position).username);
		viewHolder.siteInfo.setText(list.get(position).siteUrl);
		return convertView;
	}

	private static class ViewHolder {
		TextView accountInfo;
		TextView siteInfo;
	}
}