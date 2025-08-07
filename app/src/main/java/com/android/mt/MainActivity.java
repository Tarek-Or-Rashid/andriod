package com.android.mt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.activity.RegisterActivity;
import com.android.mt.activity.LoginActivity;  // LoginActivity import koro

public class MainActivity extends AppCompatActivity {

    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // LoginActivity open korar jonno:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // RegisterActivity open korbe:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }
}
