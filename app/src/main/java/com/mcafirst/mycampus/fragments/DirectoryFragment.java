package com.mcafirst.mycampus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mcafirst.mycampus.MainActivity;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.adapters.DirectoryAdapter;
import com.mcafirst.mycampus.models.DirectoryItem;
import com.mcafirst.mycampus.models.Faculty;
import com.mcafirst.mycampus.models.Room;
import com.mcafirst.mycampus.utils.MockDataProvider;

import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private DirectoryAdapter adapter;
    private List<DirectoryItem> allItems;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);

        recyclerView = view.findViewById(R.id.rv_directory);
        searchView = view.findViewById(R.id.search_view);

        setupData();
        setupRecyclerView();
        setupSearch();

        return view;
    }

    private void setupData() {
        allItems = new ArrayList<>();
        
        // Convert Rooms to DirectoryItems
        for (Room r : MockDataProvider.getRooms()) {
            allItems.add(new DirectoryItem(
                    r.name,
                    "Floor " + r.floor + " | Room",
                    null, // Room status removed
                    r.nodeId,
                    DirectoryItem.Type.ROOM
            ));
        }

        // Convert Faculty to DirectoryItems
        for (Faculty f : MockDataProvider.getFaculty()) {
            allItems.add(new DirectoryItem(
                    f.name,
                    f.roomNumber + " | Faculty",
                    f.status,
                    f.nodeId,
                    DirectoryItem.Type.FACULTY,
                    f.officeHours
            ));
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DirectoryAdapter(new ArrayList<>(allItems), nodeId -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToNode(nodeId);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<DirectoryItem> filteredList = new ArrayList<>();
        String query = text.toLowerCase();
        for (DirectoryItem item : allItems) {
            boolean matchesName = item.name != null && item.name.toLowerCase().contains(query);
            boolean matchesDetails = item.details != null && item.details.toLowerCase().contains(query);
            boolean matchesStatus = item.status != null && item.status.toLowerCase().contains(query);
            
            if (matchesName || matchesDetails || matchesStatus) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }
}
