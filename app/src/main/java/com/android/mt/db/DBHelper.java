package com.android.mt.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import com.android.mt.model.Transaction;
import com.android.mt.model.User;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MoneyTransfer.db";
    private static final int DATABASE_VERSION = 2;  // Version 2 because we're adding new columns

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "phone TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT, " +
                "balance REAL)");

        // Create transactions table with new columns added
        db.execSQL("CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT, " +
                "senderId INTEGER, " +
                "receiverId INTEGER, " +
                "amount REAL, " +
                "charge REAL, " +
                "agentProfit REAL DEFAULT 0, " +   // new column
                "adminProfit REAL DEFAULT 0, " +   // new column
                "dateTime TEXT, " +
                "source TEXT, " +
                "status TEXT DEFAULT 'Success')");  // new column
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Instead of dropping tables, we do ALTER TABLE for preserving data:
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE transactions ADD COLUMN agentProfit REAL DEFAULT 0");
            db.execSQL("ALTER TABLE transactions ADD COLUMN adminProfit REAL DEFAULT 0");
            db.execSQL("ALTER TABLE transactions ADD COLUMN status TEXT DEFAULT 'Success'");
        }
    }

    // Insert a new user - unchanged
    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", user.getName());
        cv.put("phone", user.getPhone());
        cv.put("password", user.getPassword());
        cv.put("role", user.getRole());
        cv.put("balance", user.getBalance());
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    // Check if phone number exists - unchanged
    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE phone = ?", new String[]{phone});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get user by phone number - unchanged
    public User getUserByPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE phone=?", new String[]{phone});
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setRole(cursor.getString(4));
            user.setBalance(cursor.getDouble(5));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Update user balance - unchanged
    public void updateBalance(int userId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("balance", newBalance);
        db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
    }

    // Get admin user - unchanged
    public User getAdminUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE role = 'admin' LIMIT 1", null);
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setRole(cursor.getString(4));
            user.setBalance(cursor.getDouble(5));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setRole(cursor.getString(4));
            user.setBalance(cursor.getDouble(5));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }


    // Insert a transaction and add 5 Taka to admin balance - updated to include new columns
    public void insertTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", transaction.getType());
        cv.put("senderId", transaction.getSenderId());
        cv.put("receiverId", transaction.getReceiverId());
        cv.put("amount", transaction.getAmount());
        cv.put("charge", transaction.getCharge());
        cv.put("agentProfit", transaction.getAgentProfit());
        cv.put("adminProfit", transaction.getAdminProfit());
        cv.put("dateTime", transaction.getDateTime());
        cv.put("source", transaction.getSource());
        cv.put("status", transaction.getStatus());
        db.insert("transactions", null, cv);


        Cursor cursor = db.rawQuery("SELECT id, balance FROM users WHERE role = 'admin' LIMIT 1", null);
        if (cursor.moveToFirst()) {
            int adminId = cursor.getInt(0);
            double currentBalance = cursor.getDouble(1);
            updateBalance(adminId, currentBalance + 5);
        }
        cursor.close();
    }
// নিচে আগের কোডের পরেই নতুন method গুলো যুক্ত করো

    // ইউজারের ব্যালেন্স রিটার্ন করে
    public double getUserBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT balance FROM users WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);
            cursor.close();
            return balance;
        }
        cursor.close();
        return 0.0;
    }


    public void updateUserBalance(int userId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("balance", newBalance);
        db.update("users", cv, "id = ?", new String[]{String.valueOf(userId)});
    }


    public void insertTransaction(String type, int senderId, int receiverId, double amount,
                                  double charge, double agentProfit, double adminProfit,
                                  String source, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        cv.put("senderId", senderId);
        cv.put("receiverId", receiverId);
        cv.put("amount", amount);
        cv.put("charge", charge);
        cv.put("agentProfit", agentProfit);
        cv.put("adminProfit", adminProfit);
        cv.put("dateTime", String.valueOf(System.currentTimeMillis()));  // Timestamp
        cv.put("source", source);
        cv.put("status", status);

        db.insert("transactions", null, cv);
    }



    public Cursor getTransactionsByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM transactions WHERE senderId=? OR receiverId=? ORDER BY id DESC",
                new String[]{String.valueOf(userId), String.valueOf(userId)}
        );
    }
}
