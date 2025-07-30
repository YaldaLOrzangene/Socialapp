package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddCommentActivity extends AppCompatActivity {
	
	private EditText etComment;
	private Button btnSubmitComment;
	private DBHelper dbHelper;
	private int postId;
	private String currentUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_comment);
		
		etComment = findViewById(R.id.etComment);
		btnSubmitComment = findViewById(R.id.btnSubmitComment);
		dbHelper = new DBHelper(this);
		
		// گرفتن postId و username از Intent
		Intent intent = getIntent();
		if (intent != null) {
			postId = intent.getIntExtra("post_id", -1);
			currentUsername = intent.getStringExtra("username");
		}
		
		if (postId == -1 || currentUsername == null || currentUsername.isEmpty()) {
			Toast.makeText(this, "خطا در دریافت اطلاعات پست یا کاربر", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		btnSubmitComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String commentText = etComment.getText().toString().trim();
				
				if (commentText.isEmpty()) {
					Toast.makeText(AddCommentActivity.this, "متن کامنت نمی‌تواند خالی باشد", Toast.LENGTH_SHORT).show();
					return;
				}
				
				int userId = dbHelper.getUserId(currentUsername);
				if (userId == -1) {
					Toast.makeText(AddCommentActivity.this, "شناسه کاربر نامعتبر است", Toast.LENGTH_SHORT).show();
					return;
				}
				
				boolean success = dbHelper.insertComment(userId, postId, commentText);
				
				if (success) {
					Toast.makeText(AddCommentActivity.this, "کامنت با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
					finish(); // بستن اکتیویتی بعد از ثبت موفق
					} else {
					Toast.makeText(AddCommentActivity.this, "خطا در ثبت کامنت", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}