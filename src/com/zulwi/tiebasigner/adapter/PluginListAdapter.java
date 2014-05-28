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
import com.zulwi.tiebasigner.bean.PluginBean;

@SuppressWarnings("serial")
public class PluginListAdapter extends BaseAdapter implements Serializable {
	private Context context;
	private LayoutInflater inflater;
	private List<PluginBean> list;

	public PluginListAdapter(Context context, List<PluginBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
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
	public PluginBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_plugin, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.plugin_name);
			viewHolder.version = (TextView) convertView.findViewById(R.id.version);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(list.get(position).name);
		viewHolder.version.setText(String.valueOf(list.get(position).version));
		return convertView;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView version;
	}
}