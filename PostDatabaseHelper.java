package com.example.socialapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PostDatabaseHelper {
	
	private DBHelper dbHelper;
	
	public PostDatabaseHelper(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public List<Post> getAllPosts() {
		List<Post> postList = new ArrayList<>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// گرفتن تمام پست‌ها
		Cursor cursor = db.rawQuery("SELECT id, user_id, title, content FROM Posts", null);
		
		if (cursor.moveToFirst()) {
			do {
				int postId = cursor.getInt(0);
				int userId = cursor.getInt(1);
				String title = cursor.getString(2);
				String content = cursor.getString(3);
				
				// گرفتن نام کاربری
				String username = dbHelper.getUsernameById(userId);
				
				Post post = new Post(
				String.valueOf(postId),
				String.valueOf(userId),
				username,
				title,
				content
				);
				
				postList.add(post);
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return postList;
	}
}