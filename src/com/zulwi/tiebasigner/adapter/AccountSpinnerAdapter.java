package com.zulwi.tiebasigner.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.AccountBean;
import com.zulwi.tiebasigner.view.CircularImage;

@SuppressLint("DefaultLocale")
public class AccountSpinnerAdapter extends BaseAdapter implements Filterable {
	private Context context;
	private LayoutInflater inflater;
	public List<AccountBean> originalList;
	private List<AccountBean> resultList;
	private ArrayFilter filter;
	private final Object lock = new Object();
	private int maxMatch = 10;

	public AccountSpinnerAdapter(Context context, List<AccountBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.originalList = data;
	}

	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public AccountBean getItem(int position) {
		return resultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return resultList.get(position).id;
	}

	public void removeAll() {
		resultList.removeAll(resultList);
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
		AccountBean bean = resultList.get(position);
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

	public Filter getFilter() {
		if (filter == null) {
			filter = new ArrayFilter();
		}
		return filter;
	}

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (prefix == null || prefix.length() == 0) {
				synchronized (lock) {
					ArrayList<AccountBean> list = new ArrayList<AccountBean>(originalList);
					results.values = list;
					results.count = list.size();
					return results;
				}
			} else {
				String prefixString = prefix.toString().toLowerCase();
				final int count = originalList.size();
				final ArrayList<AccountBean> newValues = new ArrayList<AccountBean>(count);
				for (int i = 0; i < count; i++) {
					final AccountBean account = originalList.get(i);
					final String valueText = account.username.toLowerCase();
					if (valueText.startsWith(prefixString)) {
						newValues.add(account);
					}
					if (maxMatch > 0 && newValues.size() > maxMatch - 1) break;
				}
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
        @Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			resultList = (List<AccountBean>) results.values;
			if (results.count > 0) notifyDataSetChanged();
			else notifyDataSetInvalidated();
		}
	}

}