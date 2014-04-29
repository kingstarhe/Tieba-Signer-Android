package com.zulwi.tiebasigner.adapter;

import java.util.List;

import com.zulwi.tiebasigner.activity.EditSitesActivity;
import com.zulwi.tiebasigner.activity.LoginActivity;
import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.beans.SiteBean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SiteListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<SiteBean> list;

	public void removeAll() {
		list.removeAll(list);
		notifyDataSetChanged();
	}

	public SiteListAdapter(Context context, List<SiteBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = data;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public void addItem(long id, String name, String url) {
		list.add(new SiteBean(id, name, url));
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_sites, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.url = (TextView) convertView.findViewById(R.id.url);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(list.get(position).name);
		viewHolder.url.setText(list.get(position).url);
		Button delBotton = (Button) convertView.findViewById(R.id.del_btn);
		delBotton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder confirm = new AlertDialog.Builder(context);
				confirm.setTitle("确定要删除该站点吗？该站点下的所有账号数据将均被删除");
				confirm.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								int del = LoginActivity.sitesDBHelper.delete("sites",list.get(position).id);
								if (del != 0) {
									list.remove(position);
									EditSitesActivity.hasChanged = true;
									SiteListAdapter.this.notifyDataSetChanged();
								} else {
									Toast.makeText(context, "删除失败！",Toast.LENGTH_SHORT).show();
								}
							}
						})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).create().show();
			}
		});
		return convertView;
	}

	private static class ViewHolder {
		TextView title;
		TextView url;
	}
}