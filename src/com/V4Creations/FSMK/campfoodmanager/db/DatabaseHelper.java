package com.V4Creations.FSMK.campfoodmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	String TAG = "DatabaseHelper";

	DatabaseHelper(Context context) {
		super(context, CampFoodManagerDataBase.DATABASE_NAME, null,
				CampFoodManagerDataBase.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CampFoodManagerDataBase.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(CampFoodManagerDataBase.DROP_TABLE);
		db.execSQL(CampFoodManagerDataBase.CREATE_TABLE);
	}
}
