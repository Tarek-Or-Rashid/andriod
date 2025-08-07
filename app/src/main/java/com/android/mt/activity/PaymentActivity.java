package com.android.mt.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.R;
import com.android.mt.db.DBHelper;

public class PaymentActivity extends AppCompatActivity {

    private Spinner paymentTypeSpinner;
    private EditText amountInput, receiverInput;
    private Button btnPay;
    private DBHelper dbHelper;
    private int userId, adminId = 1; // ধরলাম admin id = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentTypeSpinner = findViewById(R.id.paymentTypeSpinner);
        amountInput = findViewById(R.id.amountInput);
        receiverInput = findViewById(R.id.receiverInput);
        btnPay = findViewById(R.id.btnPay);

        dbHelper = new DBHelper(this);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        String[] types = {"Electricity Bill", "Gas Bill", "Water Bill", "Internet Bill", "Merchant Payment"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        paymentTypeSpinner.setAdapter(adapter);

        btnPay.setOnClickListener(v -> {
            String type = paymentTypeSpinner.getSelectedItem().toString();
            String receiver = receiverInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();

            if (TextUtils.isEmpty(receiver)) {
                Toast.makeText(this, "Enter receiver ID or name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            double balance = dbHelper.getUserBalance(userId);

            if (balance < amount) {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bill payment has 15tk profit for admin
            if (!type.equals("Merchant Payment")) {
                double newBalance = balance - amount;
                dbHelper.updateUserBalance(userId, newBalance);
                dbHelper.updateUserBalance(adminId, dbHelper.getUserBalance(adminId) + 15);

                dbHelper.insertTransaction("BillPayment", userId, adminId, amount, 15, 0, 0, "Paid " + type + " to " + receiver, "Success");

                Toast.makeText(this, "Bill Paid! Admin earned 15 tk.", Toast.LENGTH_SHORT).show();
            } else {
                // Merchant Payment (No charge)
                dbHelper.updateUserBalance(userId, balance - amount);
                dbHelper.insertTransaction("MerchantPayment", userId, 0, amount, 0, 0, 0, "Paid to merchant: " + receiver, "Success");

                Toast.makeText(this, "Merchant Payment Successful!", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }
}
