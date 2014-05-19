package com.zulwi.tiebasigner.adapter;

import java.io.Serializable;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.TiebaBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressWarnings("serial")
public class TiebaListAdapter extends BaseAdapter implements Serializable {
	private Context context;
	private LayoutInflater inflater;
	private List<TiebaBean> list;
	public boolean overview;

	public TiebaListAdapter(Context context, List<TiebaBean> data, boolean overview) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		this.list = data;
		this.overview = overview;
	}

	@Override
	public int getCount() {
		return list.size() + 1;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public TiebaBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (position == list.size()) {
				convertView = inflater.inflate(R.layout.list_more, null);
				viewHolder.tips = (TextView) convertView.findViewById(R.id.tips);
			} else {
				convertView = inflater.inflate(R.layout.list_tieba, null);
				viewHolder.tiebaName = (TextView) convertView.findViewById(R.id.tieba_name);
				viewHolder.level = (TextView) convertView.findViewById(R.id.level);
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == list.size()) {
			viewHolder.tips.setText(overview ? "显示更多" : "收缩列表");
		} else {
			viewHolder.tiebaName.setText(list.get(position).name);
			viewHolder.level.setText(String.valueOf(list.get(position).level));
		}
		return convertView;
	}

	private static class ViewHolder {
		public TextView tiebaName;
		public TextView level;
		public TextView tips;
	}
}