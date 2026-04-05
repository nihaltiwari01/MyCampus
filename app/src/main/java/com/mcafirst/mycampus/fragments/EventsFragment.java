package com.mcafirst.mycampus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mcafirst.mycampus.MainActivity;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.adapters.AnnouncementsAdapter;
import com.mcafirst.mycampus.adapters.EventsAdapter;
import com.mcafirst.mycampus.utils.MockDataProvider;

public class EventsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        // Announcements Setup
        RecyclerView rvAnnouncements = view.findViewById(R.id.rv_announcements);
        AnnouncementsAdapter announcementsAdapter = new AnnouncementsAdapter(MockDataProvider.getAnnouncements());
        rvAnnouncements.setAdapter(announcementsAdapter);

        // Ongoing Events Setup
        RecyclerView rvOngoingEvents = view.findViewById(R.id.rv_ongoing_events);
        EventsAdapter ongoingAdapter = new EventsAdapter(MockDataProvider.getOngoingEvents(), nodeId -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToNode(nodeId);
            }
        });
        rvOngoingEvents.setAdapter(ongoingAdapter);

        // Upcoming Events Setup
        RecyclerView rvEvents = view.findViewById(R.id.rv_events);
        EventsAdapter eventsAdapter = new EventsAdapter(MockDataProvider.getEvents(), nodeId -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToNode(nodeId);
            }
        });
        rvEvents.setAdapter(eventsAdapter);

        return view;
    }
}
