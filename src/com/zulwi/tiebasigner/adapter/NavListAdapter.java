package com.zulwi.tiebasigner.adapter;

import java.io.Serializable;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.NavigationBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("serial")
public class NavListAdapter extends BaseAdapter implements Serializable {
	@SuppressWarnings("unused")
    private Context context;
	private LayoutInflater inflater;
	public List<NavigationBean> list;

	public NavListAdapter(Context context, List<NavigationBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = data;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public NavigationBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_nav, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageResource(list.get(position).icon);
		viewHolder.title.setText(list.get(position).title);
		return convertView;
	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView title;
	}

}