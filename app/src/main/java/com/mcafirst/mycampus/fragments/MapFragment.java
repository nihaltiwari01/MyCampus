package com.mcafirst.mycampus.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mcafirst.mycampus.MainActivity;
import com.mcafirst.mycampus.R;
import com.mcafirst.mycampus.models.Node;
import com.mcafirst.mycampus.models.Room;
import com.mcafirst.mycampus.utils.GraphEngine;
import com.mcafirst.mycampus.utils.MockDataProvider;
import com.mcafirst.mycampus.views.InteractiveMapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapFragment extends Fragment implements MockDataProvider.DataSyncListener {

    private InteractiveMapView mapView;
    private List<Node> nodes;
    private Node startNode;
    private Node destinationNode;
    private Node pendingDestination;
    private Node currentLocation;

    private static final String PREFS_NAME = "CampusPrefs";
    private static final String KEY_LAST_LOCATION = "last_location_id";

    // Overlay components
    private CardView cardBuildingDetails;
    private TextView overlayNodeName;
    private TextView overlayNodeType;
    private LinearLayout layoutFloorIcons;
    private TextView tvFloorRooms;
    private MaterialButton overlayBtnStart;
    private MaterialButton overlayBtnDest;
    
    // Quick Action Buttons
    private LinearLayout layoutQrActions;
    private MaterialButton btnEntry;
    private MaterialButton btnExit;

    private int selectedFloor = -1;
    private Node selectedNode = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.map_view);
        nodes = MockDataProvider.getCampusNodes();
        if (mapView != null) {
            mapView.setNodes(nodes);
        }

        MockDataProvider.addDataSyncListener(this);
        loadLastLocation();

        cardBuildingDetails = view.findViewById(R.id.card_building_details);
        overlayNodeName = view.findViewById(R.id.overlay_node_name);
        overlayNodeType = view.findViewById(R.id.overlay_node_type);
        layoutFloorIcons = view.findViewById(R.id.layout_floor_icons);
        tvFloorRooms = view.findViewById(R.id.tv_floor_rooms);
        overlayBtnStart = view.findViewById(R.id.overlay_btn_start);
        overlayBtnDest = view.findViewById(R.id.overlay_btn_dest);
        
        setupQrButtons();

        View closeBtn = view.findViewById(R.id.btn_close_overlay);
        if (closeBtn != null) closeBtn.setOnClickListener(v -> hideOverlay());

        View heatmapBtn = view.findViewById(R.id.btn_heatmap);
        if (heatmapBtn != null) {
            heatmapBtn.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                if (mapView != null) mapView.setHeatmapEnabled(v.isSelected());
            });
        }

        View myLocationBtn = view.findViewById(R.id.btn_my_location);
        if (myLocationBtn != null) myLocationBtn.setOnClickListener(v -> showLocationSelector());

        if (mapView != null) mapView.setOnNodeClickListener(this::showNodeDetails);

        if (pendingDestination != null) {
            setDestination(pendingDestination);
            pendingDestination = null;
        }

        return view;
    }

    private void setupQrButtons() {
        layoutQrActions = new LinearLayout(requireContext());
        layoutQrActions.setOrientation(LinearLayout.HORIZONTAL);
        layoutQrActions.setWeightSum(2);
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        
        btnEntry = new MaterialButton(requireContext());
        btnEntry.setText("ENTRY (IN)");
        btnEntry.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        btnEntry.setTextColor(Color.WHITE);
        btnEntry.setCornerRadius(12);
        btnEntry.setLayoutParams(btnParams);
        btnParams.setMargins(0, 0, 8, 16);
        
        btnExit = new MaterialButton(requireContext());
        btnExit.setText("EXIT (OUT)");
        btnExit.setBackgroundColor(Color.parseColor("#F44336")); // Red
        btnExit.setTextColor(Color.WHITE);
        btnExit.setCornerRadius(12);
        btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        btnParams.setMargins(8, 0, 0, 16);
        btnExit.setLayoutParams(btnParams);
        
        layoutQrActions.addView(btnEntry);
        layoutQrActions.addView(btnExit);
    }

    @Override
    public void onDataUpdated() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            if (mapView != null) mapView.invalidate();
            if (cardBuildingDetails != null && cardBuildingDetails.getVisibility() == View.VISIBLE) refreshFloorRooms();
        });
    }

    private void loadLastLocation() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastId = prefs.getString(KEY_LAST_LOCATION, null);
        if (lastId != null) {
            for (Node n : nodes) {
                if (n.id.equals(lastId)) {
                    currentLocation = n;
                    startNode = n;
                    if (mapView != null) mapView.setCurrentLocation(n);
                    break;
                }
            }
        }
        if (startNode == null && nodes != null && !nodes.isEmpty()) startNode = nodes.get(0);
    }

    private void saveLastLocation(String nodeId) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LAST_LOCATION, nodeId).apply();
    }

    private void simulateQrScan(Node buildingNode, boolean isEntry) {
        List<Room> buildingRooms = new ArrayList<>();
        for (Room r : MockDataProvider.getRooms()) {
            if (r.nodeId.equals(buildingNode.id)) buildingRooms.add(r);
        }

        if (buildingRooms.isEmpty()) {
            Toast.makeText(getContext(), "No rooms available in " + buildingNode.name, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] roomNames = new String[buildingRooms.size()];
        for (int i = 0; i < buildingRooms.size(); i++) roomNames[i] = buildingRooms.get(i).name;

        new AlertDialog.Builder(requireContext())
                .setTitle((isEntry ? "Entry Scan" : "Exit Scan") + " - " + buildingNode.name)
                .setItems(roomNames, (dialog, which) -> {
                    Room selectedRoom = buildingRooms.get(which);
                    MockDataProvider.updateOccupancy(selectedRoom.id, isEntry);
                    Toast.makeText(getContext(), (isEntry ? "Entered " : "Exited ") + selectedRoom.name, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void refreshFloorRooms() {
        if (selectedNode == null || selectedFloor == -1) return;
        StringBuilder roomsStr = new StringBuilder();
        roomsStr.append("Rooms on Floor ").append(selectedFloor).append(":\n");
        for (Room r : MockDataProvider.getRooms()) {
            if (r.nodeId.equals(selectedNode.id) && r.floor == selectedFloor) {
                int occ = r.currentOccupancy;
                int max = r.maxCapacity;
                String status = (occ >= max) ? " [FULL]" : " (" + occ + "/" + max + ")";
                roomsStr.append("• ").append(r.name).append(status).append("\n");
            }
        }
        tvFloorRooms.setText(roomsStr.toString());
    }

    private void hideOverlay() {
        if (cardBuildingDetails != null) cardBuildingDetails.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mapView != null) mapView.setRenderEffect(null);
        selectedNode = null;
        selectedFloor = -1;
    }

    private void showLocationSelector() {
        if (nodes == null || nodes.isEmpty()) return;
        String[] nodeNames = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) nodeNames[i] = nodes.get(i).name;

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Your Current Location")
                .setItems(nodeNames, (dialog, which) -> {
                    currentLocation = nodes.get(which);
                    saveLastLocation(currentLocation.id);
                    if (mapView != null) mapView.setCurrentLocation(currentLocation);
                    startNode = currentLocation;
                    if (destinationNode != null) calculateRoute();
                })
                .show();
    }

    public void setDestination(Node node) {
        this.destinationNode = node;
        if (!isAdded()) { this.pendingDestination = node; return; }
        calculateRoute();
    }

    public void calculateRoute() {
        if (startNode == null || destinationNode == null) return;
        GraphEngine.resetGraph(nodes);
        boolean avoidStairs = (getActivity() instanceof MainActivity) && ((MainActivity) getActivity()).isAccessibilityModeEnabled();
        GraphEngine.calculatePaths(startNode, avoidStairs);
        List<Node> path = GraphEngine.getShortestPathTo(destinationNode);
        if (mapView != null) mapView.setActiveRoute(path);
    }

    private void showNodeDetails(Node node) {
        if (node == null || cardBuildingDetails == null) return;
        selectedNode = node;
        cardBuildingDetails.setVisibility(View.VISIBLE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mapView != null) {
            mapView.setRenderEffect(RenderEffect.createBlurEffect(15f, 15f, Shader.TileMode.CLAMP));
        }

        if (overlayNodeName != null) overlayNodeName.setText(node.name);
        if (overlayNodeType != null) overlayNodeType.setText(node.type);

        LinearLayout rootLayout = (LinearLayout) cardBuildingDetails.getChildAt(0);
        if (layoutQrActions.getParent() == null) {
            rootLayout.addView(layoutQrActions, rootLayout.indexOfChild(overlayBtnStart));
        }
        
        btnEntry.setOnClickListener(v -> simulateQrScan(node, true));
        btnExit.setOnClickListener(v -> simulateQrScan(node, false));

        if (layoutFloorIcons != null) {
            layoutFloorIcons.removeAllViews();
            Set<Integer> floors = new HashSet<>();
            for (Room r : MockDataProvider.getRooms()) if (r.nodeId.equals(node.id)) floors.add(r.floor);
            List<Integer> sortedFloors = new ArrayList<>(floors);
            Collections.sort(sortedFloors);

            for (int floor : sortedFloors) {
                MaterialButton floorBtn = new MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 16, 0);
                floorBtn.setLayoutParams(lp);
                floorBtn.setText("F" + floor);
                floorBtn.setCornerRadius(30);

                floorBtn.setOnClickListener(v -> {
                    for (int i = 0; i < layoutFloorIcons.getChildCount(); i++) {
                        View child = layoutFloorIcons.getChildAt(i);
                        if (child instanceof MaterialButton) {
                            ((MaterialButton) child).setBackgroundColor(Color.TRANSPARENT);
                            ((MaterialButton) child).setTextColor(Color.BLACK);
                        }
                    }
                    floorBtn.setBackgroundColor(Color.parseColor("#1976D2"));
                    floorBtn.setTextColor(Color.WHITE);
                    selectedFloor = floor;
                    refreshFloorRooms();
                });
                layoutFloorIcons.addView(floorBtn);
            }
            if (!sortedFloors.isEmpty()) layoutFloorIcons.getChildAt(0).performClick();
            else { selectedFloor = -1; tvFloorRooms.setText("No floor details available."); }
        }

        if (overlayBtnStart != null) {
            overlayBtnStart.setOnClickListener(v -> {
                startNode = node; currentLocation = node; saveLastLocation(node.id);
                if (mapView != null) mapView.setCurrentLocation(node);
                calculateRoute(); hideOverlay();
            });
        }
        if (overlayBtnDest != null) {
            overlayBtnDest.setOnClickListener(v -> { destinationNode = node; calculateRoute(); hideOverlay(); });
        }
    }
}
