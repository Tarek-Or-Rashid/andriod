package com.android.mt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.db.DBHelper;
import com.android.mt.model.User;
import com.android.mt.R;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvBalance;
    Button btnSendMoney, btnMobileRecharge, btnCashOut, btnPayment, btnAddMoney, btnTransactionHistory;

    DBHelper dbHelper;
    User currentUser;

    String currentUserPhone;  // Passed from login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvBalance = findViewById(R.id.tvBalance);

        btnSendMoney = findViewById(R.id.btnSendMoney);
        btnMobileRecharge = findViewById(R.id.btnMobileRecharge);
        btnCashOut = findViewById(R.id.btnCashOut);
        btnPayment = findViewById(R.id.btnPayment);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        btnTransactionHistory = findViewById(R.id.btnTransactionHistory);

        dbHelper = new DBHelper(this);

        // Get user phone from intent
        currentUserPhone = getIntent().getStringExtra("USER_PHONE");

        if (currentUserPhone == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUserData();

        btnSendMoney.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SendMoneyActivity.class);
            intent.putExtra("SENDER_PHONE", currentUserPhone);  // pass current user's phone
            startActivity(intent);
        });


        btnMobileRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RechargeActivity.class);
            startActivity(intent);
        });


        btnCashOut.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CashOutActivity.class);
            intent.putExtra("USER_PHONE", currentUserPhone);
            startActivity(intent);
        });


        btnPayment.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PaymentActivity.class);
            startActivity(intent);
        });


        btnAddMoney.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddMoneyActivity.class);
            intent.putExtra("USER_PHONE", currentUserPhone);
            startActivity(intent);
        });


        btnTransactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
        });

//        btnTransactionHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(DashboardActivity.this, TransactionHistoryActivity.class);
//            intent.putExtra("USER_ID", currentUser.getId()); // currentUser is already loaded
//            startActivity(intent);
//        });

    }

    private void loadUserData() {
        currentUser = dbHelper.getUserByPhone(currentUserPhone);

        if (currentUser != null) {
            tvWelcome.setText("Welcome, " + currentUser.getName());
            tvBalance.setText("Taka " + String.format("%.2f", currentUser.getBalance()));
        } else {
            Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}
