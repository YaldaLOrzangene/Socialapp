package com.example.socialapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "SocialAppUser.db";
	public static final int DATABASE_VERSION = 1;
	
	// جدول کاربران
	public static final String TABLE_USERS = "users";
	public static final String COL_USER_ID = "id";
	public static final String COL_USERNAME = "username";
	public static final String COL_PASSWORD = "password";
	
	// جدول فالوورها
	public static final String TABLE_FOLLOWERS = "followers";
	public static final String COL_FOLLOWED_ID = "followed_id";
	public static final String COL_FOLLOWER_ID = "follower_id";
	
	public UserDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// ساخت جدول کاربران
		db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
		COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COL_USERNAME + " TEXT UNIQUE, " +
		COL_PASSWORD + " TEXT)");
		
		// ساخت جدول فالوورها
		db.execSQL("CREATE TABLE " + TABLE_FOLLOWERS + " (" +
		COL_FOLLOWED_ID + " INTEGER, " +
		COL_FOLLOWER_ID + " INTEGER, " +
		"FOREIGN KEY(" + COL_FOLLOWED_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
		"FOREIGN KEY(" + COL_FOLLOWER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWERS);
		onCreate(db);
	}
	
	// ثبت‌نام کاربر جدید
	public boolean registerUser(String username, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_USERNAME, username);
		values.put(COL_PASSWORD, password);
		long result = db.insert(TABLE_USERS, null, values);
		return result != -1;
	}
	
	// بررسی ورود کاربر
	public boolean loginUser(String username, String password) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?",
		new String[]{username, password});
		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}
	
	// گرفتن ID کاربر با یوزرنیم
	public int getUserId(String username) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + "=?",
		new String[]{username});
		int userId = -1;
		if (cursor.moveToFirst()) {
			userId = cursor.getInt(0);
		}
		cursor.close();
		return userId;
	}
	
	// فالو کردن کاربر
	public boolean followUser(int followerId, int followedId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_FOLLOWED_ID, followedId);
		values.put(COL_FOLLOWER_ID, followerId);
		long result = db.insert(TABLE_FOLLOWERS, null, values);
		return result != -1;
	}
	
	// بررسی فالو بودن
	public boolean isFollowing(int followerId, int followedId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOLLOWERS +
		" WHERE " + COL_FOLLOWER_ID + "=? AND " + COL_FOLLOWED_ID + "=?", new String[]{
			String.valueOf(followerId), String.valueOf(followedId)
		});
		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}
	
	// تعداد فالوورها
	public int getFollowersCount(int userId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FOLLOWERS +
		" WHERE " + COL_FOLLOWED_ID + "=?", new String[]{String.valueOf(userId)});
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
	
	// تعداد فالووینگ‌ها
	public int getFollowingCount(int userId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FOLLOWERS +
		" WHERE " + COL_FOLLOWER_ID + "=?", new String[]{String.valueOf(userId)});
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}
}