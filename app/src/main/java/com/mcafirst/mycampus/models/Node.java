package com.mcafirst.mycampus.models;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String id;
    public String name;
    public float x;
    public float y;
    public String type;
    public int density; // 0-100
    public List<Edge> edges = new ArrayList<>();

    // For Dijkstra
    public double minDistance = Double.POSITIVE_INFINITY;
    public Node previous;

    public Node(String id, String name, float x, float y, String type, int density) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
        this.density = density;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }
}
