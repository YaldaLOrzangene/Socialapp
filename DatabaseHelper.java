package com.example.socialapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "SocialApp.db";
	public static final int DATABASE_VERSION = 1;
	
	// User Table
	public static final String TABLE_USERS = "users";
	public static final String COLUMN_USER_ID = "id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
		COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_USERNAME + " TEXT UNIQUE, " +
		COLUMN_PASSWORD + " TEXT)";
		db.execSQL(createUserTable);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		onCreate(db);
	}
	
	public boolean registerUser(String username, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_USERNAME, username);
		values.put(COLUMN_PASSWORD, password);
		
		long result = db.insert(TABLE_USERS, null, values);
		return result != -1;
	}
	
	public boolean checkUsernamePassword(String username, String password) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
		COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
		new String[]{username, password});
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}
}