package com.android.mt.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mt.db.DBHelper;
import com.android.mt.model.User;
import com.android.mt.model.Transaction;
import com.android.mt.R;

public class SendMoneyActivity extends AppCompatActivity {

    EditText etReceiverPhone, etAmount;
    Button btnSend;

    DBHelper dbHelper;
    String senderPhone;
    User senderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_money);

        etReceiverPhone = findViewById(R.id.etReceiverPhone);
        etAmount = findViewById(R.id.etAmount);
        btnSend = findViewById(R.id.btnSend);

        dbHelper = new DBHelper(this);
        senderPhone = getIntent().getStringExtra("SENDER_PHONE");
        senderUser = dbHelper.getUserByPhone(senderPhone);

        btnSend.setOnClickListener(v -> processSendMoney());
    }

    private void processSendMoney() {
        String receiverPhone = etReceiverPhone.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (receiverPhone.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if sender has enough balance including 5 Taka admin charge
        if (senderUser.getBalance() < amount + 5) {
            Toast.makeText(this, "Insufficient balance (including 5 Taka admin charge)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if receiver exists
        User receiverUser = dbHelper.getUserByPhone(receiverPhone);
        if (receiverUser == null) {
            Toast.makeText(this, "Receiver not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update sender balance
        double senderNewBalance = senderUser.getBalance() - amount - 5;
        dbHelper.updateBalance(senderUser.getId(), senderNewBalance);
        senderUser.setBalance(senderNewBalance);

        // Update receiver balance
        double receiverNewBalance = receiverUser.getBalance() + amount;
        dbHelper.updateBalance(receiverUser.getId(), receiverNewBalance);
        receiverUser.setBalance(receiverNewBalance);

        // Insert transaction record
        Transaction transaction = new Transaction();
        transaction.setType("SendMoney");
        transaction.setSenderId(senderUser.getId());
        transaction.setReceiverId(receiverUser.getId());
        transaction.setAmount(amount);
        transaction.setCharge(5);  // admin charge
        transaction.setDateTime(String.valueOf(System.currentTimeMillis()));
        transaction.setSource("SendMoney");  // source field you added
        dbHelper.insertTransaction(transaction);

        Toast.makeText(this, "Money sent successfully!", Toast.LENGTH_SHORT).show();
        finish(); // close activity and go back
    }
}
