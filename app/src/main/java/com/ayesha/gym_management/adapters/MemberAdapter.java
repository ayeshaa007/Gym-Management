package com.ayesha.gym_management.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import com.ayesha.gym_management.R;
import com.gymmanagement.app.models.Member;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private Context context;
    private List<Member> memberList;
    private OnMemberClickListener listener;
    private String currencySymbol;

    public interface OnMemberClickListener {
        void onEditClick(Member member);
        void onDeleteClick(Member member);
    }

    public MemberAdapter(Context context, List<Member> memberList, OnMemberClickListener listener) {
        this.context = context;
        this.memberList = memberList;
        this.listener = listener;

        SharedPreferences prefs = context.getSharedPreferences("GymPrefs", Context.MODE_PRIVATE);
        String currency = prefs.getString("currency", "PKR");
        this.currencySymbol = getCurrencySymbol(currency);
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.tvName.setText(member.getFullName());
        holder.tvPhone.setText(member.getPhone());
        holder.tvPlan.setText(member.getPlan());
        holder.tvFee.setText(currencySymbol + " " + String.format("%.0f", member.getFeeAmount()));
        holder.tvPaymentStatus.setText(member.getPaymentStatus());

        // Format expiry date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(member.getExpiryDate());
            holder.tvExpiryDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvExpiryDate.setText(member.getExpiryDate());
        }

        // Set status badge
        String status = member.getMembershipStatus();
        holder.tvStatus.setText(status);

        if (status.equals("Active")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_active));
        } else if (status.equals("Expired")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_expired);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_expired));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_expiring);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_expiring));
        }

        // Set payment status color
        if (member.getPaymentStatus().equals("Paid")) {
            holder.tvPaymentStatus.setTextColor(context.getResources().getColor(R.color.payment_paid));
        } else {
            holder.tvPaymentStatus.setTextColor(context.getResources().getColor(R.color.payment_unpaid));
        }

        // Click listeners
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditClick(member);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public void updateList(List<Member> newList) {
        this.memberList = newList;
        notifyDataSetChanged();
    }

    private String getCurrencySymbol(String currency) {
        switch (currency) {
            case "PKR": return "₨";
            case "INR": return "₹";
            case "USD": return "$";
            case "AED": return "د.إ";
            default: return "₨";
        }
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvPlan, tvFee, tvPaymentStatus, tvExpiryDate, tvStatus;
        MaterialButton btnEdit, btnDelete;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvPhone = itemView.findViewById(R.id.tvMemberPhone);
            tvPlan = itemView.findViewById(R.id.tvMemberPlan);
            tvFee = itemView.findViewById(R.id.tvMemberFee);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}