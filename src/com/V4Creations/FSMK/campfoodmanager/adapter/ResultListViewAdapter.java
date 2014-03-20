package com.V4Creations.FSMK.campfoodmanager.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.V4Creations.FSMK.campfoodmanager.R;

public class ResultListViewAdapter extends BaseAdapter {
	private ArrayList<ResultListViewItem> itemList;
	private LayoutInflater mInflater;

	public ResultListViewAdapter(Context context,
			ArrayList<ResultListViewItem> itemList) {
		this.itemList = itemList;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public ResultListViewItem getItem(int position) {
		return (ResultListViewItem) itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultListViewItem tempDirectoryListItem = getItem(position);
		if (convertView == null) {
			convertView = mInflater
					.inflate(
							com.V4Creations.FSMK.campfoodmanager.R.layout.result_list_view_item,
							null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			viewHolder.breakfastCheckBox = (RadioButton) convertView
					.findViewById(R.id.breakFastRadioButton);
			viewHolder.lunchCheckBox = (RadioButton) convertView
					.findViewById(R.id.lunchRadioButton);
			viewHolder.dinnerCheckBox = (RadioButton) convertView
					.findViewById(R.id.dinnerRadioButton);
			convertView.setTag(viewHolder);
		}
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.dateTextView.setText(tempDirectoryListItem.mDateString);
		viewHolder.breakfastCheckBox
				.setChecked(tempDirectoryListItem.mIsBreakfast);
		viewHolder.lunchCheckBox.setChecked(tempDirectoryListItem.mIsLunch);
		viewHolder.dinnerCheckBox.setChecked(tempDirectoryListItem.mIsDinner);
		return convertView;
	}

	private static class ViewHolder {
		TextView dateTextView;
		RadioButton breakfastCheckBox, lunchCheckBox, dinnerCheckBox;
	}

	public static class ResultListViewItem {
		public String mDateString;
		public boolean mIsBreakfast, mIsLunch, mIsDinner;

		public ResultListViewItem(String dateString, boolean isBreakfast,
				boolean isLunch, boolean isDinner) {

			mDateString = dateString;
			mIsBreakfast = isBreakfast;
			mIsLunch = isLunch;
			mIsDinner = isDinner;
		}
	}
}
