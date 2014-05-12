package com.zulwi.tiebasigner.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ListTableView extends TableLayout {
	private BaseAdapter adapter;
	private Context context;
	private DataSetObserver Observer = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			for (int i = 0; i < adapter.getCount(); i++) {
				TableRow row = new TableRow(context);
				row.addView(adapter.getView(i, null, null));
				addView(row);
			}
			invalidate();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
		}
	};

	public ListTableView(Context context) {
		super(context);
		this.context = context;
	}

	public void init(){
		
	}
	
	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		adapter.registerDataSetObserver(Observer);
	}

}
