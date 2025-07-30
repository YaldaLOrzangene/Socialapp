package com.example.socialapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	EditText edtUsername, edtPassword;
	Button btnRegister, btnBack;
	DBHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		edtUsername = findViewById(R.id.edtUsername);
		edtPassword = findViewById(R.id.edtPassword);
		btnRegister = findViewById(R.id.btnRegister);
		btnBack = findViewById(R.id.btnBack);
		
		dbHelper = new DBHelper(this);
		
		btnRegister.setOnClickListener(v -> {
			String username = edtUsername.getText().toString().trim();
			String password = edtPassword.getText().toString().trim();
			
			if (username.isEmpty() || password.isEmpty()) {
				Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
				} else {
				boolean success = dbHelper.insertUser(username, password);
				if (success) {
					Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(this, LoginActivity.class));
					finish();
					} else {
					Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btnBack.setOnClickListener(v -> {
			finish(); // برگشت به LoginActivity
		});
	}
}