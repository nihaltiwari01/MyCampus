package com.mcafirst.mycampus.models;

public class Edge {
    public Node target;
    public double weight;
    public boolean hasStairs;

    public Edge(Node target, double weight, boolean hasStairs) {
        this.target = target;
        this.weight = weight;
        this.hasStairs = hasStairs;
    }
}
