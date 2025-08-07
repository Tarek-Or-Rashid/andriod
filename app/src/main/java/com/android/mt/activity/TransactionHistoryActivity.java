package com.android.mt.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mt.R;
import com.android.mt.db.DBHelper;
import com.android.mt.model.Transaction;

import java.util.ArrayList;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView rvTransactionHistory;
    private TransactionAdapter adapter;
    private ArrayList<Transaction> transactionList;
    private DBHelper dbHelper;

    private int userId; // will be fetched from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        rvTransactionHistory = findViewById(R.id.rvTransactionHistory);
        rvTransactionHistory.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
        transactionList = new ArrayList<>();

        // ✅ Get userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTransactions();

        adapter = new TransactionAdapter(transactionList);
        rvTransactionHistory.setAdapter(adapter);
    }

    private void loadTransactions() {
        Cursor cursor = dbHelper.getTransactionsByUser(userId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction();

                transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                transaction.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                transaction.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow("senderId")));
                transaction.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow("receiverId")));
                transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                transaction.setCharge(cursor.getDouble(cursor.getColumnIndexOrThrow("charge")));
                //transaction.setAgentProfit(cursor.getDouble(cursor.getColumnIndexOrThrow("agentProfit")));
                //transaction.setAdminProfit(cursor.getDouble(cursor.getColumnIndexOrThrow("adminProfit")));
                transaction.setDateTime(cursor.getString(cursor.getColumnIndexOrThrow("dateTime")));
                transaction.setSource(cursor.getString(cursor.getColumnIndexOrThrow("source")));
                transaction.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                transactionList.add(transaction);
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Toast.makeText(this, "No transaction history found.", Toast.LENGTH_SHORT).show();
        }
    }
}
