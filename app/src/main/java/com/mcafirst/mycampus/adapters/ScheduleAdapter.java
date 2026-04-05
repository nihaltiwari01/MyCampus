package com.mcafirst.mycampus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.models.ScheduleItem;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final List<ScheduleItem> items;

    public ScheduleAdapter(List<ScheduleItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = items.get(position);
        holder.tvDay.setText(item.day);
        holder.tvName.setText(item.courseName);
        holder.tvCode.setText(item.courseCode);
        holder.tvTime.setText(String.format("%s - %s", item.startTime, item.endTime));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvName, tvCode, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_item_day);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvCode = itemView.findViewById(R.id.tv_item_code);
            tvTime = itemView.findViewById(R.id.tv_item_time);
        }
    }
}
