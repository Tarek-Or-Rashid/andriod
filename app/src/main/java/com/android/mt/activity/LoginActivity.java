package com.android.mt.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.R;
import com.android.mt.db.DBHelper;
import com.android.mt.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText phoneInput, passwordInput;
    Button loginBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Phone and Password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = dbHelper.getUserByPhone(phone);

            if (user != null && user.getPassword().equals(password)) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                // ✅ Save userId to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("userId", user.getId());
                editor.apply();

                // ✅ Move to DashboardActivity
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("USER_PHONE", phone); // optional
                startActivity(intent);
                finish(); // prevent going back to login
            } else {
                Toast.makeText(this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
