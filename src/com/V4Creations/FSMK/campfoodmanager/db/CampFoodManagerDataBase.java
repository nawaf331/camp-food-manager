package com.V4Creations.FSMK.campfoodmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CampFoodManagerDataBase {

	String TAG = "CampFoodManagerDataBase";
	public static final String DATABASE_NAME = "camp_food_manager_db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "food_history";
	public static final String COL_ID = "id";
	public static final String COL_TYPE = "food_session_type";
	public static final String COL_DATE = "food_date";
	public static final String COL_COUNT = "count(*)";

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + COL_ID + " INTEGER, " + COL_TYPE
			+ " INTEGER, " + COL_DATE + " VARCHAR(50), " + "PRIMARY KEY ("
			+ COL_ID + "," + COL_TYPE + "," + COL_DATE + "));";
	public static final String DROP_TABLE = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;
	private final Context mContext;
	private DatabaseHelper mDatabaseHelper;

	public CampFoodManagerDataBase(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(mContext);
	}

	public void close() {
		mDatabaseHelper.close();
	}

	public boolean insertFoodDetails(int id, int type, String dateString) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_ID, id);
		contentValues.put(COL_TYPE, type);
		contentValues.put(COL_DATE, dateString);
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		if (db.insert(TABLE_NAME, COL_ID, contentValues) != -1)
			return true;
		return false;
	}

	public boolean deleteFoodDetails(int id, int type, String dateString) {
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		if (db.delete(TABLE_NAME, COL_ID + "=? AND " + COL_TYPE + "=? AND "
				+ COL_DATE + "=?",
				new String[] { Integer.toString(id), Integer.toString(type),
						dateString }) != 0)
			return true;
		return false;
	}

	public int getCount(String dateString, int type) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { COL_COUNT },
				COL_DATE + "=? AND " + COL_TYPE + "=?", new String[] {
						dateString, Integer.toString(type) }, null, null, null);
		String result = "0";
		if (cursor.moveToNext()) {
			result = cursor.getString(cursor.getColumnIndex(COL_COUNT));
		}
		cursor.close();
		return Integer.parseInt(result);
	}

	public boolean getQueryResult(String idString, String typeString,
			String dateString) {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { COL_ID, COL_DATE,
				COL_TYPE }, COL_ID + "=? AND " + COL_DATE + "=? AND "
				+ COL_TYPE + "=?", new String[] { idString, dateString,
				typeString }, null, null, null);
		boolean result = cursor.moveToNext();
		cursor.close();
		return result;
	}

	public void clearAllData() {
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		db.execSQL(CampFoodManagerDataBase.DROP_TABLE);
		db.execSQL(CampFoodManagerDataBase.CREATE_TABLE);
	}

}
