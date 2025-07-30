package com.example.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
	
	private DBHelper dbHelper;
	private ListView listViewPosts;
	private PostAdapter postAdapter;
	private List<Post> postList;
	private Button btnAddPost;
	private String currentUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dbHelper = new DBHelper(this);
		listViewPosts = findViewById(R.id.listViewPosts);
		btnAddPost = findViewById(R.id.btnAddPost);
		
		currentUsername = getIntent().getStringExtra("username");
		
		// بررسی مقدار username از Intent
		if (currentUsername == null || currentUsername.isEmpty()) {
			Toast.makeText(this, "نام کاربری دریافت نشد", Toast.LENGTH_LONG).show();
			finish(); // پایان Activity برای جلوگیری از کرش
			return;
		}
		
		btnAddPost.setOnClickListener(v -> {
			Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
			intent.putExtra("username", currentUsername);
			startActivity(intent);
		});
		
		loadPosts();
	}
	
	private void loadPosts() {
		postList = dbHelper.getAllPosts();
		postAdapter = new PostAdapter(this, postList);
		listViewPosts.setAdapter(postAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadPosts();
	}
	
	public String getCurrentUsername() {
		return currentUsername;
	}
}