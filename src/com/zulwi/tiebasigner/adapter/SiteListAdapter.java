package com.zulwi.tiebasigner.adapter;

import java.io.Serializable;
import java.util.List;

import com.zulwi.tiebasigner.R;
import com.zulwi.tiebasigner.bean.SiteBean;
import com.zulwi.tiebasigner.db.BaseDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressWarnings("serial")
public class SiteListAdapter extends BaseAdapter implements Serializable {
	private Context context;
	private LayoutInflater inflater;
	public List<SiteBean> list;
	private BaseDBHelper sitesDBHelper;

	public SiteListAdapter(Context context, List<SiteBean> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = data;
		sitesDBHelper = new BaseDBHelper(this.context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public boolean addItem(String name, String url) {
		ContentValues value = new ContentValues();
		value.put("name", name);
		value.put("url", url);
		int id = (int) sitesDBHelper.insert("sites", value);
		if (id != 0) {
			list.add(new SiteBean(id, name, url));
			notifyDataSetChanged();
			return true;
		} else {
			return false;
		}
	}

	public boolean updateItem(int position, String name, String url) {
		SiteBean oldSite = getItem(position);
		ContentValues value = new ContentValues();
		value.put("name", name);
		value.put("url", url);
		int count = sitesDBHelper.update("sites", value, "id=" + String.valueOf(oldSite.id));
		if (count != 0) {
			SiteBean site = new SiteBean(oldSite.id, name, url);
			list.set(position, site);
			notifyDataSetChanged();
			return true;
		}
		return false;
	}

	@Override
	public SiteBean getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.list.get(position).id;
	}

	public int remove(int position) {
		int del = sitesDBHelper.delete("sites", list.get(position).id);
		if (del != 0) {
			list.remove(position);
			notifyDataSetChanged();
		}
		return del;
	}

	public void removeAll() {
		sitesDBHelper.deleteAll("sites");
		list.removeAll(list);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_site, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.url = (TextView) convertView.findViewById(R.id.url);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(list.get(position).name);
		viewHolder.url.setText(list.get(position).url);
		return convertView;
	}

	public void closeDB() {
		if (sitesDBHelper != null) sitesDBHelper.close();
	}

	private static class ViewHolder {
		TextView title;
		TextView url;
	}
}