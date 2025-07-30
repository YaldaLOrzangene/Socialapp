package com.example.socialapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.socialapp.Post;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "SocialApp.db";
	public static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// جدول کاربران
		db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"username TEXT UNIQUE, " +
		"password TEXT)");
		
		// جدول پست‌ها
		db.execSQL("CREATE TABLE IF NOT EXISTS Posts (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"user_id INTEGER, " +
		"title TEXT, " +
		"content TEXT, " +
		"FOREIGN KEY(user_id) REFERENCES Users(id))");
		
		// جدول لایک‌ها با محدودیت یکتا
		db.execSQL("CREATE TABLE IF NOT EXISTS Likes (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"user_id INTEGER, " +
		"post_id INTEGER, " +
		"UNIQUE(user_id, post_id), " +
		"FOREIGN KEY(user_id) REFERENCES Users(id), " +
		"FOREIGN KEY(post_id) REFERENCES Posts(id))");
		
		// جدول کامنت‌ها
		db.execSQL("CREATE TABLE IF NOT EXISTS Comments (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"user_id INTEGER, " +
		"post_id INTEGER, " +
		"comment TEXT, " +
		"created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
		"FOREIGN KEY(user_id) REFERENCES Users(id), " +
		"FOREIGN KEY(post_id) REFERENCES Posts(id))");
		
		// جدول فالوورها
		db.execSQL("CREATE TABLE IF NOT EXISTS Followers (" +
		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"follower_id INTEGER, " +
		"followed_id INTEGER, " +
		"FOREIGN KEY(follower_id) REFERENCES Users(id), " +
		"FOREIGN KEY(followed_id) REFERENCES Users(id))");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Followers");
		db.execSQL("DROP TABLE IF EXISTS Comments");
		db.execSQL("DROP TABLE IF EXISTS Likes");
		db.execSQL("DROP TABLE IF EXISTS Posts");
		db.execSQL("DROP TABLE IF EXISTS Users");
		onCreate(db);
	}
	
	// ثبت‌نام
	public boolean insertUser(String username, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("username", username);
		values.put("password", password);
		long result = db.insert("Users", null, values);
		return result != -1;
	}
	
	// بررسی ورود
	public boolean checkUser(String username, String password) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
		"SELECT * FROM Users WHERE username = ? AND password = ?",
		new String[]{username, password});
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}
	
	// گرفتن آیدی کاربر با نام‌کاربری
	public int getUserId(String username) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
		"SELECT id FROM Users WHERE username = ?",
		new String[]{username});
		if (cursor.moveToFirst()) {
			int id = cursor.getInt(0);
			cursor.close();
			return id;
		}
		cursor.close();
		return -1;
	}
	
	// افزودن پست
	public boolean insertPost(int userId, String title, String content) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("user_id", userId);
		values.put("title", title);
		values.put("content", content);
		long result = db.insert("Posts", null, values);
		Log.d("DBHelper", "Post inserted: " + result);
		return result != -1;
	}
	
	// لایک کردن پست
	public boolean insertLike(int userId, int postId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("user_id", userId);
		values.put("post_id", postId);
		long result = -1;
		try {
			result = db.insertOrThrow("Likes", null, values);
			} catch (Exception e) {
			Log.e("DBHelper", "Error inserting like: " + e.getMessage());
		}
		return result != -1;
	}
	
	// افزودن کامنت
	public boolean insertComment(int userId, int postId, String comment) {
		if (comment == null || comment.trim().isEmpty()) {
			return false;
		}
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("user_id", userId);
		values.put("post_id", postId);
		values.put("comment", comment);
		long result = db.insert("Comments", null, values);
		return result != -1;
	}
	
	// فالو کردن کاربر
	public boolean followUser(int followerId, int followedId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("follower_id", followerId);
		values.put("followed_id", followedId);
		long result = db.insert("Followers", null, values);
		return result != -1;
	}
	
	// گرفتن نام کاربری با آیدی
	public String getUsernameById(int userId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT username FROM Users WHERE id = ?", new String[]{String.valueOf(userId)});
		if (cursor.moveToFirst()) {
			String username = cursor.getString(0);
			cursor.close();
			return username;
		}
		cursor.close();
		return "کاربر ناشناس";
	}
	
	// گرفتن همه پست‌ها
	public List<Post> getAllPosts() {
		List<Post> posts = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
		"SELECT Posts.id AS postId, Posts.user_id, Users.username, title, content " +
		"FROM Posts INNER JOIN Users ON Posts.user_id = Users.id", null);
		
		if (cursor.moveToFirst()) {
			do {
				int postId = cursor.getInt(cursor.getColumnIndex("postId"));
				String username = cursor.getString(cursor.getColumnIndex("username"));
				String title = cursor.getString(cursor.getColumnIndex("title"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				
				posts.add(new Post(String.valueOf(postId), username, title, content, ""));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return posts;
	}
	
	// گرفتن تعداد لایک‌های یک پست
	public int getLikeCount(int postId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Likes WHERE post_id = ?", new String[]{String.valueOf(postId)});
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	// گرفتن تعداد کامنت‌های یک پست
	public int getCommentCount(int postId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Comments WHERE post_id = ?", new String[]{String.valueOf(postId)});
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public boolean deletePost(int postId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("Likes", "post_id = ?", new String[]{String.valueOf(postId)});
		db.delete("Comments", "post_id = ?", new String[]{String.valueOf(postId)});
		int result = db.delete("Posts", "id = ?", new String[]{String.valueOf(postId)});
		return result > 0;
	}
	
	public List<String> getCommentsForPost(int postId) {
		List<String> comments = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
		"SELECT Comments.comment, Users.username FROM Comments " +
		"JOIN Users ON Comments.user_id = Users.id " +
		"WHERE post_id = ? ORDER BY created_at DESC",
		new String[]{String.valueOf(postId)}
		);
		
		if (cursor.moveToFirst()) {
			do {
				String username = cursor.getString(1);
				String comment = cursor.getString(0);
				comments.add(username + ": " + comment);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return comments;
	}
	
	public boolean hasUserLikedPost(int userId, int postId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		boolean liked = false;
		try {
			cursor = db.rawQuery(
			"SELECT * FROM likes WHERE user_id = ? AND post_id = ?",
			new String[]{String.valueOf(userId), String.valueOf(postId)}
			);
			liked = cursor.moveToFirst();
			} finally {
			if (cursor != null) cursor.close();
			db.close();
		}
		return liked;
	}
	
	

    public boolean removeLike(int userId, int postId) {
    SQLiteDatabase db = this.getWritableDatabase();
    int rows = db.delete("likes", "user_id = ? AND post_id = ?", new String[]{String.valueOf(userId), String.valueOf(postId)});
    return rows > 0;
}
    
	public List<String> getUsernamesWhoLikedPost(int postId) {
		List<String> usernames = new ArrayList<>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		String query = "SELECT users.username FROM likes " +
		"JOIN users ON likes.user_id = users.id " +
		"WHERE likes.post_id = ?";
		Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});
		
		if (cursor.moveToFirst()) {
			do {
				usernames.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return usernames;
	}
	
}