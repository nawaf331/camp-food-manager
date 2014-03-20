package com.V4Creations.FSMK.campfoodmanager.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.V4Creations.FSMK.campfoodmanager.R;
import com.V4Creations.FSMK.campfoodmanager.adapter.ResultListViewAdapter;
import com.V4Creations.FSMK.campfoodmanager.adapter.ResultListViewAdapter.ResultListViewItem;
import com.V4Creations.FSMK.campfoodmanager.db.CampFoodManagerDataBase;
import com.V4Creations.FSMK.campfoodmanager.db.GetAllStudensDetailsHelperAsynzTask;
import com.V4Creations.FSMK.campfoodmanager.db.GetAllStudensDetailsHelperAsynzTask.StudentsFullDetails;
import com.V4Creations.FSMK.campfoodmanager.db.GetDetailsHelperAsynzTask;
import com.V4Creations.FSMK.campfoodmanager.flash.LedFlashlightReceiver;
import com.V4Creations.FSMK.campfoodmanager.fragment.MenuFragment;
import com.V4Creations.FSMK.campfoodmanager.interfaces.GetFullStudentsDetailsInterface;
import com.V4Creations.FSMK.campfoodmanager.interfaces.GetSingleItemDetailsInterface;
import com.V4Creations.FSMK.campfoodmanager.util.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.AmbientLightManager;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.client.android.CaptureActivityHandler;
import com.google.zxing.client.android.FinishListener;
import com.google.zxing.client.android.InactivityTimer;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CampFoodManagerMainActivity extends BaseActivity implements
		SurfaceHolder.Callback, GetSingleItemDetailsInterface,
		GetFullStudentsDetailsInterface {

	public CampFoodManagerMainActivity() {
		super(R.string.app_name);
	}

	private static final String TAG = CampFoodManagerMainActivity.class
			.getSimpleName();

	private final long DETECTION_DELAY_HIGH = 2000L;
	private final long DETECTION_DELAY_LOW = 1000L;
	public static final int MAX_STUDENTS = 250;
	private final int MAX_PRIME_NUMBER = 2000;
	private List<Integer> mPrimNumbers;

	private StringBuilder mCSVBuilder;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private String characterSet;
	private Result savedResultToShow;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;
	private MenuFragment mMenuFragment;

	private TextView mTotalTextView, mLastScanIdTextView,
			mListViewTilteTexView;
	private ListView mResultListView;
	private RelativeLayout mResultRelativeLayout;

	private ArrayList<ResultListViewItem> mResultListViewItems;
	private ResultListViewAdapter mResultListViewAdapter;
	public int mLastScannedId = -1;
	public String mLastscannedDateString = "";
	public int mLastScannedtype = 0;
	private boolean isResultVisible;
	private String fileNamePrefix = "fsmk_camp_";

	private CampFoodManagerDataBase mDataBase;

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.capture);
		setBehindContentView(R.layout.menu_frame);
		mMenuFragment = new MenuFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, mMenuFragment).commit();
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
		generatePrime(MAX_PRIME_NUMBER);
		toggle();
		initViews();
	}

	private void generatePrime(int n) {
		boolean[] isPrimeNumber = new boolean[n + 1];
		mPrimNumbers = new ArrayList<Integer>();
		for (int i = 2; i < n; i++) {
			isPrimeNumber[i] = true;
		}
		for (int i = 2; i < n; i++) {
			if (isPrimeNumber[i]) {
				mPrimNumbers.add(i);
				for (int j = i; j * i <= n; j++) {
					isPrimeNumber[i * j] = false;
				}
			}
		}
	}

	private int findPrimPossition(int prim) {
		for (int i = 0; i < mPrimNumbers.size(); i++)
			if (mPrimNumbers.get(i) == prim)
				return i + 1;
		return -1;
	}

	private void setVisibilities() {
		if (isResultVisible) {
			viewfinderView.setVisibility(View.GONE);
			mTotalTextView.setVisibility(View.GONE);
			mLastScanIdTextView.setVisibility(View.GONE);
			mResultRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			viewfinderView.setVisibility(View.VISIBLE);
			mTotalTextView.setVisibility(View.VISIBLE);
			mLastScanIdTextView.setVisibility(View.VISIBLE);
			mResultRelativeLayout.setVisibility(View.GONE);
		}
	}

	private void initViews() {
		mTotalTextView = (TextView) findViewById(R.id.totalTextView);
		mLastScanIdTextView = (TextView) findViewById(R.id.lastScanIdTextView);
		mListViewTilteTexView = (TextView) findViewById(R.id.listTileTextView);
		mResultRelativeLayout = (RelativeLayout) findViewById(R.id.resultLinearLayout);
		initListView();
	}

	public void light(View v){
		boolean enabled = LedFlashlightReceiver
				.isFlashlightEnabled(getApplicationContext());
		Intent intent = new Intent(
				LedFlashlightReceiver.ACTION_CONTROL_FLASHLIGHT);
		intent.putExtra(LedFlashlightReceiver.EXT_ENABLED,
				!enabled);
		sendBroadcast(intent);
	}

	private void initListView() {
		mResultListView = (ListView) findViewById(R.id.resultListView);
		mResultListViewItems = new ArrayList<ResultListViewAdapter.ResultListViewItem>();
		mResultListViewAdapter = new ResultListViewAdapter(
				getApplicationContext(), mResultListViewItems);
		mResultListView.setAdapter(mResultListViewAdapter);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		mDataBase = new CampFoodManagerDataBase(getApplicationContext());
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		handler = null;
		resetStatusView();
		initCount();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);
		inactivityTimer.onResume();
		Intent intent = getIntent();
		characterSet = null;
		if (intent != null) {
			characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
		}
	}

	private void initCount() {
		int count = mDataBase.getCount(mMenuFragment.getSelectedDate(),
				mMenuFragment.getFoodType());
		mTotalTextView.setText(Integer.toString(count));
	}

	@Override
	protected void onPause() {
		mDataBase.close();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			} else if (viewfinderView.getVisibility() == View.VISIBLE) {
				finish();
			} else {
				restartPreviewAfterDelay(0L);
			}
			return true;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_CAMERA:
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		inactivityTimer.onActivity();
		ParsedResult parsedResult = ResultParser.parseResult(rawResult);
		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
			beepManager.playBeepSoundAndVibrate();
			drawResultPoints(barcode, scaleFactor, rawResult);
		}
		handleDecodeInternally(rawResult, parsedResult, barcode);
	}

	private void drawResultPoints(Bitmap barcode, float scaleFactor,
			Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				drawLine(canvas, paint, points[0], points[1], scaleFactor);
				drawLine(canvas, paint, points[2], points[3], scaleFactor);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(scaleFactor * point.getX(), scaleFactor
							* point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b, float scaleFactor) {
		if (a != null && b != null) {
			canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(),
					scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
		}
	}

	private void handleDecodeInternally(Result rawResult,
			ParsedResult parsedResult, Bitmap barcode) {
		int id = isAValidQRCode(parsedResult.getDisplayResult());
		if (id == -1) {
			Crouton.makeText(this, R.string.invalid_qr_code, Style.ALERT)
					.show();
			restartPreviewAfterDelay(DETECTION_DELAY_HIGH);
			return;
		}
		handleResultShowing(id);
	}

	private int isAValidQRCode(String scanedResult) {
		String[] values = scanedResult.split("#");
		try {
			int position = findPrimPossition(Integer.parseInt(values[0]) - 47);
			if (values[1].length() != 1)
				return -1;
			char sig = values[1].charAt(0);
			return (char) (((position + 11) % 26) + 65) == sig ? position : -1;
		} catch (Exception exception) {
			return -1;
		}
	}

	private void handleResultShowing(int value) {
		if (mMenuFragment.isConfirmEachQRCode()) {
			getAllResultDetails(value);
		} else {
			addToDBAndUI(value);
		}
	}

	private void getAllResultDetails(int value) {
		new GetDetailsHelperAsynzTask(this, mDataBase).execute(value);
	}

	private void addToDBAndUI(int value) {
		if (mDataBase.insertFoodDetails(value, mMenuFragment.getFoodType(),
				mMenuFragment.getSelectedDate())) {
			Crouton.makeText(this, R.string.welcome, Style.INFO).show();
			mLastScannedId = value;
			mLastScannedtype = mMenuFragment.getFoodType();
			mLastscannedDateString = mMenuFragment.getSelectedDate();
			mLastScanIdTextView.setText(Integer.toString(value));
			int total = Integer.parseInt(mTotalTextView.getText().toString());
			mTotalTextView.setText(Integer.toString(++total));
		} else
			Crouton.makeText(this, R.string.you_already_had_food, Style.ALERT)
					.show();
		restartPreviewAfterDelay(DETECTION_DELAY_LOW);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG,
					"initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, null, null,
						characterSet, cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler,
						R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	private void resetStatusView() {
		isResultVisible = false;
		setVisibilities();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public void notify(int id, ArrayList<ResultListViewItem> resultListViewItems) {

		mResultListViewItems.clear();
		for (int i = 0; i < resultListViewItems.size(); i++)
			mResultListViewItems.add(resultListViewItems.get(i));
		mResultListViewAdapter.notifyDataSetChanged();
		mListViewTilteTexView.setText(getString(R.string.list_title, id + ""));
		mListViewTilteTexView.setTag(id);
		isResultVisible = true;
		setVisibilities();
	}

	private void removeLastScannedFromUI() {
		mLastScannedId = -1;
		mLastscannedDateString = "";
		mLastScannedtype = 0;
		mTotalTextView.setText(R.string.previous_scan_not_available);
	}

	public void deleteLastScanFoodItem() {
		if (mDataBase.deleteFoodDetails(mLastScannedId, mLastScannedtype,
				mLastscannedDateString)) {
			Crouton.makeText(this, R.string.last_scan_deleted, Style.INFO)
					.show();
			removeLastScannedFromUI();
		} else
			Crouton.makeText(this, R.string.previous_scan_not_available,
					Style.ALERT).show();
	}

	public void reFetchCount() {
		int count = mDataBase.getCount(mMenuFragment.getSelectedDate(),
				mMenuFragment.getFoodType());
		mTotalTextView.setText(count + "");
	}

	public void noFood(View view) {
		restartPreviewAfterDelay(DETECTION_DELAY_LOW);
	}

	public void yesFood(View view) {
		addToDBAndUI((Integer) mListViewTilteTexView.getTag());
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		reFetchCount();
		removeLastScannedFromUI();
	}

	public void generatCSV() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.do_you_want_to_generate_csv_file_now)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								startProcess();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.setTitle(R.string.generate_scv);
		alert.setIcon(R.drawable.warnning);
		alert.show();
	}

	protected void startProcess() {
		mCSVBuilder = new StringBuilder();
		mCSVBuilder.append("Id,");
		String[] tempStrings = getResources()
				.getStringArray(R.array.date_array);
		for (int i = 0; i < tempStrings.length; i++)
			mCSVBuilder.append("Date,Breakfast,Lunch,Dinner,");
		mCSVBuilder.append(System.getProperty("line.separator"));
		new GetAllStudensDetailsHelperAsynzTask(this, mDataBase).execute();
	}

	private void writeToFile() {
		String parentDirectoryAddress = Environment
				.getExternalStorageDirectory()
				+ File.separator
				+ getString(R.string.app_name) + File.separator;
		File parentDirecory = new File(parentDirectoryAddress);
		parentDirecory.mkdirs();
		String writingAddress = parentDirectoryAddress + fileNamePrefix
				+ String.valueOf(System.currentTimeMillis()) + ".csv";

		BufferedWriter writer = null;
		try {
			File csvFile = new File(writingAddress);
			writer = new BufferedWriter(new FileWriter(csvFile));
			writer.write(mCSVBuilder.toString());
			Crouton.makeText(
					this,
					getString(R.string.file_successfully_wrote, writingAddress),
					Style.INFO).show();
		} catch (Exception e) {
			Crouton.makeText(this, R.string.file_writting_error, Style.ALERT)
					.show();
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				Crouton.makeText(this, R.string.file_writting_error,
						Style.ALERT).show();
			}
		}

	}

	@Override
	public void notifyFullDetails(
			ArrayList<StudentsFullDetails> studentsFullDetails) {
		for (int i = 0; i < studentsFullDetails.size(); i++) {
			mCSVBuilder.append(studentsFullDetails.get(i).mId).append(",");
			ArrayList<ResultListViewItem> resultListViewItems = studentsFullDetails
					.get(i).mCollection;
			for (int j = 0; j < resultListViewItems.size(); j++) {
				mCSVBuilder.append(resultListViewItems.get(j).mDateString)
						.append(",");
				mCSVBuilder.append(resultListViewItems.get(j).mIsBreakfast)
						.append(",");
				mCSVBuilder.append(resultListViewItems.get(j).mIsLunch).append(
						",");
				mCSVBuilder.append(resultListViewItems.get(j).mIsDinner)
						.append(",");
			}
			mCSVBuilder.append(System.getProperty("line.separator"));
		}
		writeToFile();
	}
}
