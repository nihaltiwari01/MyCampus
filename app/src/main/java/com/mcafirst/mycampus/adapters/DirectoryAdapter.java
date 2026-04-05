package com.mcafirst.mycampus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.models.DirectoryItem;

import java.util.ArrayList;
import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    private List<DirectoryItem> items;
    private OnNavItemClickListener navListener;

    public interface OnNavItemClickListener {
        void onNavClick(String nodeId);
    }

    public DirectoryAdapter(List<DirectoryItem> items, OnNavItemClickListener navListener) {
        this.items = items;
        this.navListener = navListener;
    }

    public void updateList(List<DirectoryItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_directory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DirectoryItem item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvDetails.setText(item.details);

        if (item.status != null && !item.status.isEmpty()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText("Status: " + item.status);
            holder.statusIndicator.setVisibility(View.VISIBLE);

            // Update indicator color based on status
            int color = 0xFF4CAF50; // Default green
            if ("occupied".equals(item.status) || "in-class".equals(item.status)) {
                color = 0xFFFF9800; // Orange
            } else if ("unavailable".equals(item.status)) {
                color = 0xFFF44336; // Red
            }
            holder.statusIndicator.setBackgroundColor(color);
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.statusIndicator.setVisibility(View.GONE);
        }

        if (item.type == DirectoryItem.Type.FACULTY && item.officeHours != null) {
            holder.tvOfficeHours.setVisibility(View.VISIBLE);
            holder.tvOfficeHours.setText("Office Hours: " + item.officeHours);
        } else {
            holder.tvOfficeHours.setVisibility(View.GONE);
        }

        holder.btnNav.setOnClickListener(v -> navListener.onNavClick(item.nodeId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvStatus, tvOfficeHours;
        View statusIndicator;
        MaterialButton btnNav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDetails = itemView.findViewById(R.id.tv_item_details);
            tvStatus = itemView.findViewById(R.id.tv_item_status);
            tvOfficeHours = itemView.findViewById(R.id.tv_item_office_hours);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            btnNav = itemView.findViewById(R.id.btn_nav_item);
        }
    }
}
