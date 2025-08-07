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

public class CashOutActivity extends AppCompatActivity {

    EditText etAgentPhone, etAmount, etPassword;
    Button btnCashOut;

    DBHelper dbHelper;
    String userPhone;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_out);

        etAgentPhone = findViewById(R.id.etAgentPhone);
        etAmount = findViewById(R.id.etAmount);
        etPassword = findViewById(R.id.etPassword);
        btnCashOut = findViewById(R.id.btnCashOut);

        dbHelper = new DBHelper(this);
        userPhone = getIntent().getStringExtra("USER_PHONE");
        currentUser = dbHelper.getUserByPhone(userPhone);

        btnCashOut.setOnClickListener(v -> processCashOut());
    }

    private void processCashOut() {
        String agentPhone = etAgentPhone.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String inputPassword = etPassword.getText().toString();

        if (agentPhone.isEmpty() || amountStr.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // পাসওয়ার্ড যাচাই (ধরি User মডেলে getPassword() আছে)
        if (!inputPassword.equals(currentUser.getPassword())) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            return;
        }

        User agentUser = dbHelper.getUserByPhone(agentPhone);
        if (agentUser == null || !"agent".equalsIgnoreCase(agentUser.getRole())) {
            Toast.makeText(this, "Invalid agent number", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate charge (10 per 1000)
        double charge = (amount / 1000.0) * 10;
        charge = Math.round(charge * 100.0) / 100.0; // Round to 2 decimals

        double totalDeduction = amount + charge;

        if (currentUser.getBalance() < totalDeduction) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Admin user
        User adminUser = dbHelper.getAdminUser();
        if (adminUser == null) {
            Toast.makeText(this, "Admin user not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate agent and admin share
        double agentProfit = (charge / 10.0) * 4.0;
        double adminProfit = (charge / 10.0) * 6.0;

        // Final credit to agent = amount + agentProfit
        double agentReceiveAmount = amount + agentProfit;

        // Update balances
        dbHelper.updateBalance(currentUser.getId(), currentUser.getBalance() - totalDeduction);
        dbHelper.updateBalance(agentUser.getId(), agentUser.getBalance() + agentReceiveAmount);
        dbHelper.updateBalance(adminUser.getId(), adminUser.getBalance() + adminProfit);

        // Get current datetime
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Log transaction
        Transaction transaction = new Transaction();
        transaction.setType("cashout");
        transaction.setSenderId(currentUser.getId());
        transaction.setReceiverId(agentUser.getId());
        transaction.setAmount(amount);
        transaction.setCharge(charge);
        transaction.setAgentProfit(agentProfit);
        transaction.setAdminProfit(adminProfit);
        transaction.setDateTime(dateTime);
        transaction.setSource("cashout");
        transaction.setStatus("Success");

        dbHelper.insertTransaction(transaction);

        Toast.makeText(this, "Cash out successful!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
