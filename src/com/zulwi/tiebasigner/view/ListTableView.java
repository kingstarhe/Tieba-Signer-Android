package com.zulwi.tiebasigner.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ListTableView extends TableLayout {
	private BaseAdapter adapter;
	private Context context;
	private onClickListener listener;

	private DataSetObserver Observer = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			removeAllViews();
			for (int i = 0; i < adapter.getCount(); i++) {
				final int n = i;
				final TableRow row = new TableRow(context);
				row.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (listener != null) listener.onClick(row, n, n == adapter.getCount() - 1);
					}
				});
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
		this(context, null);
	}

	public ListTableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setShrinkAllColumns(true);
		setStretchAllColumns(true);
	}

	public void init() {

	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		adapter.registerDataSetObserver(Observer);
		adapter.notifyDataSetChanged();
	}

	public void setOnClickListener(onClickListener listener) {
		this.listener = listener;
	}

	public interface onClickListener {
		void onClick(View v, int which, boolean last);
	}

}
