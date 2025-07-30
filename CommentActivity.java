package com.example.socialapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
	EditText etComment;
	Button btnSend;
	ListView lvComments;
	DBHelper dbHelper;
	int postId;
	int userId;
	String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		etComment = findViewById(R.id.etComment);
		btnSend = findViewById(R.id.btnSend);
		lvComments = findViewById(R.id.lvComments);
		
		dbHelper = new DBHelper(this);
		
		// گرفتن اطلاعات ارسال‌شده از پست
		postId = getIntent().getIntExtra("post_id", -1);
		username = getIntent().getStringExtra("username");
		userId = dbHelper.getUserId(username);
		
		loadComments();
		
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String commentText = etComment.getText().toString().trim();
				if (!commentText.isEmpty()) {
					dbHelper.insertComment(userId, postId, commentText);
					etComment.setText("");
					loadComments();
					} else {
					Toast.makeText(CommentActivity.this, "متن کامنت نمی‌تواند خالی باشد", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void loadComments() {
		List<String> comments = dbHelper.getCommentsForPost(postId);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, comments);
		lvComments.setAdapter(adapter);
	}
}