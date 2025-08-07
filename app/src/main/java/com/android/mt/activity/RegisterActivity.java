package com.android.mt.activity;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.R;
import com.android.mt.db.DBHelper;
import com.android.mt.model.User;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, phoneInput, passwordInput;
    Spinner roleSpinner;
    Button submitBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        roleSpinner = findViewById(R.id.roleSpinner);
        submitBtn = findViewById(R.id.submitBtn);

        // Setup role spinner
        String[] roles = {"user", "agent", "merchant","admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        submitBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Name, Phone, and Password are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() < 10) {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isPhoneExists(phone)) {
                Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setName(name);
            user.setPhone(phone);
            user.setPassword(password);
            user.setRole(role);      // Set selected role from spinner
            user.setBalance(0.0);    // initial balance

            boolean inserted = dbHelper.insertUser(user);
            if (inserted) {
                Toast.makeText(this, "Registered successfully as " + role + "!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
