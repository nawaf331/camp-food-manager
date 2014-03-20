package com.V4Creations.FSMK.campfoodmanager.fragment;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.V4Creations.FSMK.campfoodmanager.R;
import com.V4Creations.FSMK.campfoodmanager.ui.CampFoodManagerMainActivity;
import com.V4Creations.FSMK.campfoodmanager.ui.PreferencesActivity;
import com.V4Creations.FSMK.campfoodmanager.util.Settings;
import com.actionbarsherlock.app.SherlockFragment;

public class MenuFragment extends SherlockFragment {
	String TAG = "MenuFragment";
	CampFoodManagerMainActivity activity;
	public static final int TYPE_BREAK_FAST = 0;
	public static final int TYPE_LUNCH = 1;
	public static final int TYPE_DINNER = 2;

	private Button mCsvButton, mSettingsButton, mContinueButton, mDeleteButton;
	private CheckBox mConfirmCheckBox;
	private Spinner mDateSpinner;
	private RadioGroup mRadioGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (CampFoodManagerMainActivity) getActivity();
		return inflater.inflate(R.layout.menu_layout, null, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initValues();
	}

	private void initValues() {
		initDate();
		initFoodTime();
		initListeners();
		mConfirmCheckBox.setChecked(Settings.isConfirmEachQRCode(activity));
	}

	private void initListeners() {
		mRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						activity.reFetchCount();
					}
				});
		mDateSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						activity.reFetchCount();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
		mCsvButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.generatCSV();
			}
		});
		mSettingsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingsActivity = new Intent(activity,
						PreferencesActivity.class);
				startActivityForResult(settingsActivity, 0);
			}
		});
		mContinueButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.toggle();
			}
		});
		mDeleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showLastScanDeleteDialog();
			}
		});
		mConfirmCheckBox
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Settings.setConfirmEachQRCode(activity, isChecked);
					}
				});
	}

	protected void showLastScanDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(R.string.do_you_want_to_delete_your_last_scan)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								activity.deleteLastScanFoodItem();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.delete_last_scan);
		alert.setIcon(R.drawable.ic_action_delete_light);
		alert.show();
	}

	private void initFoodTime() {
		int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if (hourOfDay < 11) {
			RadioButton breakfastRadioButton = (RadioButton) getView()
					.findViewById(R.id.breakFastRadioButton);
			breakfastRadioButton.setChecked(true);
		} else if (hourOfDay < 17) {
			RadioButton lunchRadioButton = (RadioButton) getView()
					.findViewById(R.id.lunchRadioButton);
			lunchRadioButton.setChecked(true);
		} else {
			RadioButton dinnerRadioButton = (RadioButton) getView()
					.findViewById(R.id.dinnerRadioButton);
			dinnerRadioButton.setChecked(true);
		}
	}

	private void initDate() {
		int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int index = dayOfMonth - 19;
		if (index < 0)
			index = 0;
		mDateSpinner.setSelection(index);
	}

	private void initView() {
		mCsvButton = (Button) getView().findViewById(R.id.csvButton);
		mSettingsButton = (Button) getView().findViewById(R.id.settingsButton);
		mContinueButton = (Button) getView().findViewById(R.id.continueButton);
		mDeleteButton = (Button) getView().findViewById(R.id.deleteButton);
		mConfirmCheckBox = (CheckBox) getView().findViewById(
				R.id.confirmCheckBox);
		mDateSpinner = (Spinner) getView().findViewById(R.id.dateSpinner);
		mRadioGroup = (RadioGroup) getView().findViewById(
				R.id.foodTimeRadioGroup);
	}

	public String getSelectedDate() {
		return (String) mDateSpinner.getSelectedItem();
	}

	public int getFoodType() {
		int id = mRadioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) getView().findViewById(id);
		return getTypeFromString(radioButton.getText().toString());
	}

	private int getTypeFromString(String string) {
		if (string.equals(getString(R.string.break_fast)))
			return TYPE_BREAK_FAST;
		else if (string.equals(getString(R.string.lunch)))
			return TYPE_LUNCH;
		else
			return TYPE_DINNER;
	}

	public boolean isConfirmEachQRCode() {
		return mConfirmCheckBox.isChecked();
	}
}
