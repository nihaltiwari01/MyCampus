package com.mcafirst.mycampus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mcafirst.mycampus.fragments.DirectoryFragment;
import com.mcafirst.mycampus.fragments.EventsFragment;
import com.mcafirst.mycampus.fragments.MapFragment;
import com.mcafirst.mycampus.fragments.NextClassFragment;
import com.mcafirst.mycampus.models.Node;
import com.mcafirst.mycampus.utils.MockDataProvider;

public class MainActivity extends AppCompatActivity {

    private boolean accessibilityMode = false;
    private MapFragment mapFragment = new MapFragment();
    private DirectoryFragment directoryFragment = new DirectoryFragment();
    private NextClassFragment nextClassFragment = new NextClassFragment();
    private EventsFragment eventsFragment = new EventsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_map) {
                selectedFragment = mapFragment;
            } else if (itemId == R.id.nav_directory) {
                selectedFragment = directoryFragment;
            } else if (itemId == R.id.nav_events) {
                selectedFragment = eventsFragment;
            } else if (itemId == R.id.nav_next_class) {
                selectedFragment = nextClassFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Set default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, mapFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_accessibility);
        if (item != null && item.getActionView() != null) {
            SwitchCompat accessibilitySwitch = item.getActionView().findViewById(R.id.switch_widget);
            if (accessibilitySwitch != null) {
                accessibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    accessibilityMode = isChecked;
                });
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isAccessibilityModeEnabled() {
        return accessibilityMode;
    }

    public void navigateToNode(String nodeId) {
        // Switch to map tab
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setSelectedItemId(R.id.nav_map);

        // Find node and set as destination
        for (Node node : MockDataProvider.getCampusNodes()) {
            if (node.id.equals(nodeId)) {
                mapFragment.setDestination(node);
                break;
            }
        }
    }
}
