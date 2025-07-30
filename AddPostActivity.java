package com.example.socialapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPostActivity extends Activity {
	
	private EditText etTitle, etContent;
	private Button btnSubmit;
	private DBHelper dbHelper;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_post);
		
		etTitle = findViewById(R.id.etTitle);
		etContent = findViewById(R.id.etContent);
		btnSubmit = findViewById(R.id.btnSubmit);
		
		dbHelper = new DBHelper(this);
		username = getIntent().getStringExtra("username");
		
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = etTitle.getText().toString().trim();
				String content = etContent.getText().toString().trim();
				
				if (title.isEmpty() || content.isEmpty()) {
					Toast.makeText(AddPostActivity.this, "همه فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
					return;
				}
				
				int userId = dbHelper.getUserId(username);
				if (userId == -1) {
					Toast.makeText(AddPostActivity.this, "خطا در دریافت اطلاعات کاربر", Toast.LENGTH_SHORT).show();
					return;
				}
				
				boolean success = dbHelper.insertPost(userId, title, content);
				if (success) {
					Toast.makeText(AddPostActivity.this, "پست ثبت شد", Toast.LENGTH_SHORT).show();
					finish(); // برگرد به MainActivity
					} else {
					Toast.makeText(AddPostActivity.this, "خطا در ذخیره پست", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}