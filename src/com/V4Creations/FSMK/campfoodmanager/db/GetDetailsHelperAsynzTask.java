package com.V4Creations.FSMK.campfoodmanager.db;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.V4Creations.FSMK.campfoodmanager.R;
import com.V4Creations.FSMK.campfoodmanager.adapter.ResultListViewAdapter.ResultListViewItem;
import com.V4Creations.FSMK.campfoodmanager.fragment.MenuFragment;
import com.V4Creations.FSMK.campfoodmanager.interfaces.GetSingleItemDetailsInterface;
import com.V4Creations.FSMK.campfoodmanager.ui.CampFoodManagerMainActivity;

public class GetDetailsHelperAsynzTask extends
		AsyncTask<Integer, Integer, ArrayList<ResultListViewItem>> {
	GetSingleItemDetailsInterface getSingleItemDetailsInterface;
	private int mId;
	private ProgressDialog progressDialog;
	private Context mContext;
	private CampFoodManagerDataBase mDataBase;

	public GetDetailsHelperAsynzTask(CampFoodManagerMainActivity activity,
			CampFoodManagerDataBase database) {
		getSingleItemDetailsInterface = (GetSingleItemDetailsInterface) activity;
		mDataBase = database;
		mContext = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage(mContext.getString(R.string.please_wait));
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	protected ArrayList<ResultListViewItem> doInBackground(Integer... params) {
		mId = params[0];
		String idString = params[0].toString();

		ArrayList<ResultListViewItem> resultListViewItems = new ArrayList<ResultListViewItem>();
		String[] dates = mContext.getResources().getStringArray(
				R.array.date_array);
		for (int i = 0; i < dates.length; i++) {
			boolean isBreakfast, isLunch, isDinner;
			isBreakfast = mDataBase.getQueryResult(idString,
					Integer.toString(MenuFragment.TYPE_BREAK_FAST), dates[i]);
			isLunch = mDataBase.getQueryResult(idString,
					Integer.toString(MenuFragment.TYPE_LUNCH), dates[i]);
			isDinner = mDataBase.getQueryResult(idString,
					Integer.toString(MenuFragment.TYPE_DINNER), dates[i]);
			resultListViewItems.add(new ResultListViewItem(dates[i],
					isBreakfast, isLunch, isDinner));
		}
		return resultListViewItems;
	}

	@Override
	protected void onPostExecute(ArrayList<ResultListViewItem> result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		getSingleItemDetailsInterface.notify(mId, result);
	}
}