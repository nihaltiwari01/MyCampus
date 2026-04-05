package com.mcafirst.mycampus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcafirst.mycampus.MainActivity;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.adapters.ScheduleAdapter;
import com.mcafirst.mycampus.models.ScheduleItem;
import com.mcafirst.mycampus.utils.MockDataProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NextClassFragment extends Fragment {

    private TextView tvClassName, tvClassLocation, tvClassTime;
    private RecyclerView rvSchedule;
    private ScheduleItem nextClass;
    private List<ScheduleItem> otherSchedule = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_class, container, false);

        tvClassName = view.findViewById(R.id.tv_class_name);
        tvClassLocation = view.findViewById(R.id.tv_class_location);
        tvClassTime = view.findViewById(R.id.tv_class_time);
        rvSchedule = view.findViewById(R.id.rv_schedule);

        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));

        findClasses();
        displayClasses();

        view.findViewById(R.id.btn_route_to_class).setOnClickListener(v -> {
            if (nextClass != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToNode(nextClass.nodeId);
            } else {
                Toast.makeText(getContext(), "No upcoming class found to route", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void findClasses() {
        List<ScheduleItem> schedule = MockDataProvider.getSchedule();
        String currentDay = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        
        nextClass = null;
        otherSchedule.clear();

        // 1. Find the first class of today as 'Next Class'
        for (ScheduleItem item : schedule) {
            if (item.day.equalsIgnoreCase(currentDay)) {
                if (nextClass == null) {
                    nextClass = item;
                } else {
                    otherSchedule.add(item);
                }
            } else {
                otherSchedule.add(item);
            }
        }
        
        // 2. Fallback if no class today
        if (nextClass == null && !schedule.isEmpty()) {
            nextClass = schedule.get(0);
            otherSchedule.addAll(schedule.subList(1, schedule.size()));
        }
    }

    private void displayClasses() {
        if (nextClass != null) {
            tvClassName.setText(nextClass.courseName);
            tvClassLocation.setText(String.format("%s (%s)", nextClass.nodeId.toUpperCase(), nextClass.courseCode));
            tvClassTime.setText(String.format("%s - %s", nextClass.startTime, nextClass.endTime));
        } else {
            tvClassName.setText("No Classes Scheduled");
            tvClassLocation.setText("");
            tvClassTime.setText("");
        }

        ScheduleAdapter adapter = new ScheduleAdapter(otherSchedule);
        rvSchedule.setAdapter(adapter);
    }
}
