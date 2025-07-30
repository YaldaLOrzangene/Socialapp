package com.example.socialapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	EditText edtUsername, edtPassword;
	Button btnLogin, btnRegister;
	DBHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		edtUsername = findViewById(R.id.edtUsername);
		edtPassword = findViewById(R.id.edtPassword);
		btnLogin = findViewById(R.id.btnLogin);
		btnRegister = findViewById(R.id.btnRegister);
		dbHelper = new DBHelper(this);
		
		btnLogin.setOnClickListener(v -> {
			String username = edtUsername.getText().toString().trim();
			String password = edtPassword.getText().toString().trim();
			
			if (dbHelper.checkUser(username, password)) {
				Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra("username", username);
				startActivity(intent);
				finish();
				} else {
				Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnRegister.setOnClickListener(v -> {
			startActivity(new Intent(this, RegisterActivity.class));
		});
	}
}