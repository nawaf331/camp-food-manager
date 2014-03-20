package com.V4Creations.FSMK.campfoodmanager.db;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.V4Creations.FSMK.campfoodmanager.R;
import com.V4Creations.FSMK.campfoodmanager.adapter.ResultListViewAdapter.ResultListViewItem;
import com.V4Creations.FSMK.campfoodmanager.db.GetAllStudensDetailsHelperAsynzTask.StudentsFullDetails;
import com.V4Creations.FSMK.campfoodmanager.fragment.MenuFragment;
import com.V4Creations.FSMK.campfoodmanager.interfaces.GetFullStudentsDetailsInterface;
import com.V4Creations.FSMK.campfoodmanager.ui.CampFoodManagerMainActivity;

public class GetAllStudensDetailsHelperAsynzTask extends
		AsyncTask<Integer, Integer, ArrayList<StudentsFullDetails>> {
	GetFullStudentsDetailsInterface getStudentsDetailsInterface;
	private ProgressDialog progressDialog;
	private Context mContext;
	private CampFoodManagerDataBase mDataBase;
	private ArrayList<StudentsFullDetails> mStudentFStudentsFullDetails;

	public GetAllStudensDetailsHelperAsynzTask(
			CampFoodManagerMainActivity activity,
			CampFoodManagerDataBase database) {
		getStudentsDetailsInterface = (GetFullStudentsDetailsInterface) activity;
		mDataBase = database;
		mContext = activity;
		mStudentFStudentsFullDetails = new ArrayList<GetAllStudensDetailsHelperAsynzTask.StudentsFullDetails>();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage(mContext
				.getString(R.string.csv_file_generatting)
				+ mContext.getString(R.string.please_wait));
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	@Override
	protected ArrayList<StudentsFullDetails> doInBackground(Integer... params) {
		String[] dates = mContext.getResources().getStringArray(
				R.array.date_array);
		for (int j = 0; j < CampFoodManagerMainActivity.MAX_STUDENTS; j++) {
			ArrayList<ResultListViewItem> resultListViewItems = new ArrayList<ResultListViewItem>();
			for (int i = 0; i < dates.length; i++) {
				boolean isBreakfast, isLunch, isDinner;
				isBreakfast = mDataBase.getQueryResult(Integer.toString(j),
						Integer.toString(MenuFragment.TYPE_BREAK_FAST),
						dates[i]);
				isLunch = mDataBase.getQueryResult(Integer.toString(j),
						Integer.toString(MenuFragment.TYPE_LUNCH), dates[i]);
				isDinner = mDataBase.getQueryResult(Integer.toString(j),
						Integer.toString(MenuFragment.TYPE_DINNER), dates[i]);
				resultListViewItems.add(new ResultListViewItem(dates[i],
						isBreakfast, isLunch, isDinner));
			}
			mStudentFStudentsFullDetails.add(new StudentsFullDetails(
					resultListViewItems, j));
		}
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<StudentsFullDetails> result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		getStudentsDetailsInterface
				.notifyFullDetails(mStudentFStudentsFullDetails);
	}

	public static class StudentsFullDetails {
		public ArrayList<ResultListViewItem> mCollection;
		public int mId;

		public StudentsFullDetails(ArrayList<ResultListViewItem> collection,
				int id) {
			mCollection = collection;
			mId = id;
		}
	}
}