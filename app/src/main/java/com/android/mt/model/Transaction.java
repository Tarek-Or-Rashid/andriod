package com.android.mt.model;

public class Transaction {

    private int id;
    private String type;        // Send Money, Add Money, CashOut, etc.
    private int senderId;
    private int receiverId;
    private double amount;
    private double charge;
    private double agentProfit; // নতুন ফিল্ড: এজেন্টের লাভ
    private double adminProfit; // নতুন ফিল্ড: অ্যাডমিনের লাভ
    private String dateTime;
    private String source;      // Example: SendMoneyActivity, RechargeActivity
    private String status;      // Optional: "Success", "Failed", "Pending"

    // ---- Getters & Setters ----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public double getAgentProfit() {
        return agentProfit;
    }

    public void setAgentProfit(double agentProfit) {
        this.agentProfit = agentProfit;
    }

    public double getAdminProfit() {
        return adminProfit;
    }

    public void setAdminProfit(double adminProfit) {
        this.adminProfit = adminProfit;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
