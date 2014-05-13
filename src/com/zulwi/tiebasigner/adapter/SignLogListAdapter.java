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
public class SignLogListAdapter extends BaseAdapter implements Serializable {
	private Context context;
	private LayoutInflater inflater;
	public List<TiebaBean> list;

	public SignLogListAdapter(Context context, List<TiebaBean> data) {
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
	public TiebaBean getItem(int position) {
		return list.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_sign_log, null);
			viewHolder.tiebaName = (TextView) convertView.findViewById(R.id.tieba_name);
			viewHolder.status = (TextView) convertView.findViewById(R.id.status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tiebaName.setText(list.get(position).name);
		viewHolder.status.setText(String.valueOf(list.get(position).status));
		return convertView;
	}

	private static class ViewHolder {
		public TextView tiebaName;
		public TextView status;
	}
}