package com.mcafirst.mycampus.utils;

import android.util.Log;
import com.mcafirst.mycampus.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MockDataProvider {
    private static List<Node> cachedNodes;
    private static List<Room> cachedRooms;
    private static Map<String, Integer> liveOccupancy = new HashMap<>();
    
    public interface DataSyncListener {
        void onDataUpdated();
    }
    
    private static List<DataSyncListener> syncListeners = new ArrayList<>();

    public static void addDataSyncListener(DataSyncListener listener) {
        if (listener != null && !syncListeners.contains(listener)) {
            syncListeners.add(listener);
        }
    }

    public static List<Node> getCampusNodes() {
        if (cachedRooms == null) getRooms();
        if (cachedNodes != null) {
            updateAllNodeDensities();
            return cachedNodes;
        }

        List<Node> nodes = new ArrayList<>();
        Node gate = new Node("gate", "Main Gate", 1500, 2600, "Entrance", 0);
        Node entrySquare = new Node("entrySquare", "Entry Square", 1500, 2200, "Junction", 0);
        Node admin = new Node("admin", "Admin Block", 1500, 1600, "Office", 0);
        Node blockB = new Node("blockB", "Block B (Girls Hostel)", 2100, 1650, "Academic/Hostel", 0);
        Node blockA = new Node("blockA", "Block A", 2700, 1550, "Academic", 0);
        Node blockC = new Node("blockC", "Block C", 900, 1650, "Academic", 0);
        Node blockD = new Node("blockD", "Block D", 300, 1580, "Academic", 0);
        Node library = new Node("library", "Central Library", 1500, 1100, "Facility", 0);
        Node lectureHall = new Node("lectureHall", "Lecture Hall", 600, 1150, "Facility", 0);
        Node boysHostel = new Node("boysHostel", "3-Seater Boys Hostel", 2700, 1150, "Hostel", 0);
        Node pgHostel = new Node("pgHostel", "Single Seater (PG) Hostel", 2300, 750, "Hostel", 0);
        Node girlsHostelNew = new Node("girlsHostelNew", "Girls Hostel (New)", 3100, 1150, "Hostel", 0);
        Node directorHome = new Node("directorHome", "Director's Home", 350, 800, "Residential", 0);
        Node facultyAccom = new Node("facultyAccom", "Faculty Accommodation", 600, 800, "Residential", 0);

        gate.addEdge(new Edge(entrySquare, 400, false));
        entrySquare.addEdge(new Edge(gate, 400, false));
        entrySquare.addEdge(new Edge(admin, 600, false));
        admin.addEdge(new Edge(entrySquare, 600, false));
        entrySquare.addEdge(new Edge(blockC, 800, false));
        blockC.addEdge(new Edge(entrySquare, 800, false));
        entrySquare.addEdge(new Edge(blockB, 800, false));
        blockB.addEdge(new Edge(entrySquare, 800, false));
        admin.addEdge(new Edge(blockB, 600, false));
        blockB.addEdge(new Edge(admin, 600, false));
        blockB.addEdge(new Edge(blockA, 600, true));
        blockA.addEdge(new Edge(blockB, 600, true));
        admin.addEdge(new Edge(blockC, 600, false));
        blockC.addEdge(new Edge(admin, 600, false));
        blockC.addEdge(new Edge(blockD, 600, false));
        blockD.addEdge(new Edge(blockC, 600, false));
        admin.addEdge(new Edge(library, 500, false));
        library.addEdge(new Edge(admin, 500, false));
        lectureHall.addEdge(new Edge(blockC, 500, false));
        blockC.addEdge(new Edge(lectureHall, 500, false));
        lectureHall.addEdge(new Edge(blockD, 600, false));
        blockD.addEdge(new Edge(lectureHall, 600, false));
        lectureHall.addEdge(new Edge(facultyAccom, 350, false));
        facultyAccom.addEdge(new Edge(lectureHall, 350, false));
        lectureHall.addEdge(new Edge(directorHome, 430, false));
        directorHome.addEdge(new Edge(lectureHall, 430, false));
        blockA.addEdge(new Edge(boysHostel, 400, false));
        boysHostel.addEdge(new Edge(blockA, 400, false));
        boysHostel.addEdge(new Edge(pgHostel, 500, false));
        pgHostel.addEdge(new Edge(boysHostel, 500, false));
        boysHostel.addEdge(new Edge(girlsHostelNew, 400, false));
        girlsHostelNew.addEdge(new Edge(boysHostel, 400, false));

        nodes.add(gate); nodes.add(entrySquare); nodes.add(admin); nodes.add(blockA);
        nodes.add(blockB); nodes.add(blockC); nodes.add(blockD); nodes.add(library);
        nodes.add(lectureHall); nodes.add(boysHostel); nodes.add(pgHostel);
        nodes.add(girlsHostelNew); nodes.add(facultyAccom); nodes.add(directorHome);

        cachedNodes = nodes;
        updateAllNodeDensities();
        return nodes;
    }

    public static List<Room> getRooms() {
        if (cachedRooms != null) {
            for (Room r : cachedRooms) {
                if (liveOccupancy.containsKey(r.id)) r.currentOccupancy = liveOccupancy.get(r.id);
            }
            return cachedRooms;
        }

        List<Room> rooms = new ArrayList<>();
        // Block A (High Density - Red)
        rooms.add(new Room("a0_canteen", "Canteen", "blockA", 0, 45, 50));
        rooms.add(new Room("a0_elec_lab", "Electrical Lab", "blockA", 0, 28, 30));
        rooms.add(new Room("a1_ee101", "EE101", "blockA", 1, 38, 40));
        rooms.add(new Room("a2_cse_lab1", "CSE Lab 1", "blockA", 2, 32, 35));
        rooms.add(new Room("a3_ca301", "CA 301", "blockA", 3, 55, 60));
        
        // Block B
        rooms.add(new Room("b0_mess", "Hostel Mess", "blockB", 0, 0, 80));
        rooms.add(new Room("b1_common", "Common Room", "blockB", 1, 0, 40));
        
        // Block C (Moderate Density - Orange effect after 50%)
        // Total Cap: 175. For Orange (>50%), need > 87 people.
        rooms.add(new Room("c0_civil_lab1", "Civil Lab 1", "blockC", 0, 18, 30));
        rooms.add(new Room("c0_geotech_lab", "Geotech Lab", "blockC", 0, 12, 25));
        rooms.add(new Room("c1_room101", "Room C101", "blockC", 1, 35, 60));
        rooms.add(new Room("c2_room201", "Room C201", "blockC", 2, 25, 60));
        
        // Block D
        rooms.add(new Room("d0_mech_ws", "Mech Workshop", "blockD", 0, 0, 40));
        rooms.add(new Room("d1_drawing", "Drawing Hall", "blockD", 1, 0, 50));
        
        // Admin
        rooms.add(new Room("admin_reg", "Registrar Office", "admin", 0, 0, 10));
        rooms.add(new Room("admin_main", "Main Admin", "admin", 0, 0, 100));
        // Library
        rooms.add(new Room("lib_reading", "Reading Room", "library", 0, 0, 100));
        rooms.add(new Room("lib_floor1", "Stack Area", "library", 1, 0, 150));
        // Facilities & Hostels
        rooms.add(new Room("lh_main", "Main Hall", "lectureHall", 0, 0, 200));
        rooms.add(new Room("bh_common", "Common Room", "boysHostel", 0, 0, 50));
        rooms.add(new Room("pg_common", "PG Common", "pgHostel", 0, 0, 30));
        rooms.add(new Room("gh_common", "Common Room", "girlsHostelNew", 0, 0, 50));
        rooms.add(new Room("dh_living", "Living Area", "directorHome", 0, 0, 20));
        rooms.add(new Room("fa_staff", "Staff Lounge", "facultyAccom", 0, 0, 30));
        rooms.add(new Room("gate_security", "Security Post", "gate", 0, 0, 5));

        for (Room r : rooms) liveOccupancy.put(r.id, r.currentOccupancy);
        cachedRooms = rooms;
        updateAllNodeDensities();
        return rooms;
    }

    public static void updateOccupancy(String roomId, boolean isEntry) {
        int current = liveOccupancy.getOrDefault(roomId, 0);
        // Increased impact: +2 for entry for faster demonstrations
        int newValue = isEntry ? current + 2 : Math.max(0, current - 1);
        liveOccupancy.put(roomId, newValue);
        
        String nodeId = null;
        if (cachedRooms != null) {
            for (Room r : cachedRooms) {
                if (r.id.equals(roomId)) {
                    r.currentOccupancy = newValue;
                    nodeId = r.nodeId;
                    break;
                }
            }
        }
        
        if (nodeId != null) {
            updateNodeDensity(nodeId);
        } else {
            updateAllNodeDensities();
        }
        
        notifyListeners();
    }
    
    private static void notifyListeners() {
        for (DataSyncListener l : syncListeners) {
            l.onDataUpdated();
        }
    }
    
    private static void updateAllNodeDensities() {
        if (cachedNodes == null) return;
        for (Node node : cachedNodes) updateNodeDensity(node.id);
    }
    
    private static void updateNodeDensity(String nodeId) {
        if (cachedNodes == null || cachedRooms == null) return;
        Node targetNode = null;
        for (Node n : cachedNodes) if (n.id.equals(nodeId)) { targetNode = n; break; }
        if (targetNode == null) return;
        
        int totalOcc = 0, totalCap = 0;
        boolean hasRooms = false;
        for (Room r : cachedRooms) {
            if (r.nodeId.equals(nodeId)) {
                totalOcc += liveOccupancy.getOrDefault(r.id, r.currentOccupancy);
                totalCap += r.maxCapacity;
                hasRooms = true;
            }
        }
        if (hasRooms && totalCap > 0) {
            int percent = (int) ((float) totalOcc / totalCap * 100);
            targetNode.density = (totalOcc > 0) ? Math.max(1, percent) : 0;
        } else targetNode.density = 0;
    }

    public static List<Faculty> getFaculty() {
        List<Faculty> f = new ArrayList<>();
        f.add(new Faculty("Dr. Arunima Dutta", "blockA", "present", "10 AM - 12 PM", "FA 203"));
        f.add(new Faculty("Dr. Diprendu Sinha Roy", "blockA", "present", "10 AM - 12 PM", "FA 204"));
        f.add(new Faculty("Dr. D.B. Tariang", "blockA", "present", "CA402 DSA", "FA 205"));
        f.add(new Faculty("Dr. Nurul Amin Choudhury", "blockA", "present", "CA404 OOP", "FA 206"));
        f.add(new Faculty("Dr. Wanbanker Khongbuh", "blockA", "present", "CA406 DC", "FA 207"));
        f.add(new Faculty("Dr. A. P. Singh", "blockA", "present", "CA408 AFL", "FA 208"));
        f.add(new Faculty("Dr. Johny Singh", "blockA", "present", "CA454 Python", "FA 209"));
        return f;
    }

    public static List<Event> getOngoingEvents() {
        List<Event> e = new ArrayList<>();
        e.add(new Event("Cognitia Hackathon 2k26", "blockA", "4 April 2026", "Hackathon"));
        return e;
    }

    public static List<Event> getEvents() {
        List<Event> e = new ArrayList<>();
        e.add(new Event("Coding Conquest", "blockA", "9th April, 10:00 AM", "Coding"));
        e.add(new Event("Relay Typing Competition", "blockC", "10th April, 12:00 PM", "Tech"));
        e.add(new Event("Treasure Hunt", "gate", "11th April, 02:00 PM", "Fun"));
        return e;
    }

    public static List<Announcement> getAnnouncements() {
        List<Announcement> l = new ArrayList<>();
        l.add(new Announcement("Tech Fest Begins!", "NIT Meghalaya's Tech Fest from 9th-11th April.", "8 April"));
        return l;
    }

    public static List<ScheduleItem> getSchedule() {
        List<ScheduleItem> s = new ArrayList<>();
        
        // Monday
        s.add(new ScheduleItem("Monday", "CA454", "Python Programming Lab", "TBD", "TBD", "blockA", ""));
        s.add(new ScheduleItem("Monday", "CA452", "Data Structure and Algorithm Lab", "15:00", "16:55", "blockA", ""));
        
        // Wednesday
        s.add(new ScheduleItem("Wednesday", "CA404", "Object Oriented Programming", "09:00", "10:00", "blockA", ""));
        s.add(new ScheduleItem("Wednesday", "TBD", "Unspecified Class", "15:00", "TBD", "blockA", "Placeholder for missing class reported at 3:00 PM"));
        
        // Thursday
        s.add(new ScheduleItem("Thursday", "CA408", "Automata and Formal Languages", "09:00", "12:00", "blockA", "Morning slot placeholder"));
        
        // Friday
        s.add(new ScheduleItem("Friday", "CA452", "Data Structure and Algorithm Lab", "15:00", "16:55", "blockA", ""));

        return s;
    }
}
