package com.android.mt.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mt.R;  // <-- এই লাইনটি যোগ করো
import com.android.mt.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvType.setText("Type: " + transaction.getType());
        holder.tvAmount.setText("Amount: " + transaction.getAmount());
        holder.tvCharge.setText("Charge: " + transaction.getCharge());
        holder.tvStatus.setText("Status: " + transaction.getStatus());
        holder.tvDateTime.setText("Date: " + transaction.getDateTime());
        holder.tvSource.setText("Source: " + transaction.getSource());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvAmount, tvCharge, tvStatus, tvDateTime, tvSource;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCharge = itemView.findViewById(R.id.tvCharge);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvSource = itemView.findViewById(R.id.tvSource);
        }
    }
}
