package com.mcafirst.mycampus.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.models.Event;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private final List<Event> events;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onNavClick(String nodeId);
    }

    public EventsAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvName.setText(event.name);
        holder.tvTime.setText("Time: " + event.time);
        holder.tvLocation.setText("Location ID: " + event.nodeId);
        holder.tvCategory.setText(event.category);

        // Simple color coding for categories
        if ("Coding".equals(event.category)) {
            holder.tvCategory.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        } else if ("Robotics".equals(event.category)) {
            holder.tvCategory.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
        }

        holder.btnNav.setOnClickListener(v -> listener.onNavClick(event.nodeId));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvLocation, tvCategory;
        MaterialButton btnNav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_event_category);
            tvName = itemView.findViewById(R.id.tv_event_name);
            tvTime = itemView.findViewById(R.id.tv_event_time);
            tvLocation = itemView.findViewById(R.id.tv_event_location);
            btnNav = itemView.findViewById(R.id.btn_nav_event);
        }
    }
}
