package com.android.mt.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.R;
import com.android.mt.db.DBHelper;
import com.android.mt.model.Transaction;
import com.android.mt.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RechargeActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etAmount;
    private Spinner spinnerOperator;
    private Button btnRecharge;

    private DBHelper dbHelper;
    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAmount = findViewById(R.id.etAmount);
        spinnerOperator = findViewById(R.id.spinnerOperator);
        btnRecharge = findViewById(R.id.btnRecharge);

        dbHelper = new DBHelper(this);

        // ✅ Get current user ID
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        currentUser = dbHelper.getUserByPhone(dbHelper.getUserById(userId).getPhone());

        // ✅ Populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.operators_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperator.setAdapter(adapter);

        btnRecharge.setOnClickListener(v -> performRecharge());
    }

    private void performRecharge() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String operator = spinnerOperator.getSelectedItem().toString();
        String amountStr = etAmount.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length() != 11) {
            etPhoneNumber.setError("Valid phone number required");
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount required");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (currentUser == null || currentUser.getBalance() < amount) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deduct from user balance
        double newBalance = currentUser.getBalance() - amount;
        dbHelper.updateBalance(currentUser.getId(), newBalance);

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setType("Recharge");
        transaction.setSenderId(currentUser.getId());
        transaction.setReceiverId(-1); // No receiver for recharge
        transaction.setAmount(amount);
        transaction.setCharge(0);
        transaction.setAgentProfit(0);
        transaction.setAdminProfit(0);
        transaction.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        transaction.setSource(operator + " - " + phoneNumber);
        transaction.setStatus("Success");

        dbHelper.insertTransaction(transaction);

        Toast.makeText(this, "Recharge successful!", Toast.LENGTH_SHORT).show();
        finish(); // Optional: close the activity
    }
}
