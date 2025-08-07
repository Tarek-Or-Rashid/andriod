package com.android.mt.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.R;
import com.android.mt.db.DBHelper;
import com.android.mt.model.Transaction;
import com.android.mt.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMoneyActivity extends AppCompatActivity {

    EditText etBankCardNumber, etAmount;
    Button btnAddMoney;
    DBHelper dbHelper;
    User currentUser;
    String currentUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_money);

        dbHelper = new DBHelper(this);

        etBankCardNumber = findViewById(R.id.etBankCardNumber);
        etAmount = findViewById(R.id.etAmount);
        btnAddMoney = findViewById(R.id.btnAddMoney);

        currentUserPhone = getIntent().getStringExtra("USER_PHONE");
        currentUser = dbHelper.getUserByPhone(currentUserPhone);

        btnAddMoney.setOnClickListener(v -> {
            String cardNumber = etBankCardNumber.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (cardNumber.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidCardNumber(cardNumber)) {
                Toast.makeText(this, "Invalid bank/card number", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount <= 0) {
                Toast.makeText(this, "Amount must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update user balance
            double newBalance = currentUser.getBalance() + amount;
            dbHelper.updateBalance(currentUser.getId(), newBalance);

            // Add transaction record
            Transaction transaction = new Transaction();
            transaction.setType("Add Money");
            transaction.setSenderId(currentUser.getId());  // sender and receiver can be same
            transaction.setReceiverId(currentUser.getId());
            transaction.setAmount(amount);
            transaction.setCharge(0);
            transaction.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
             // Save bank/card number as source

            dbHelper.insertTransaction(transaction);

            Toast.makeText(this, "Money added successfully", Toast.LENGTH_SHORT).show();

            finish();
        });
    }

    // Basic validation for numeric card number between 10 and 16 digits
    private boolean isValidCardNumber(String number) {
        return number.matches("\\d{10,16}");
    }
}
