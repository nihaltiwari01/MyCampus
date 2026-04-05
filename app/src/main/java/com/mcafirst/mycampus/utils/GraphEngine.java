package com.mcafirst.mycampus.utils;

import com.mcafirst.mycampus.models.Edge;
import com.mcafirst.mycampus.models.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class GraphEngine {

    public static void calculatePaths(Node source, boolean avoidStairs) {
        source.minDistance = 0;
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>((n1, n2) -> Double.compare(n1.minDistance, n2.minDistance));
        nodeQueue.add(source);

        while (!nodeQueue.isEmpty()) {
            Node u = nodeQueue.poll();

            for (Edge e : u.edges) {
                if (avoidStairs && e.hasStairs) continue;

                Node v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    nodeQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    nodeQueue.add(v);
                }
            }
        }
    }

    public static List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = node.previous) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }

    public static void resetGraph(List<Node> nodes) {
        for (Node n : nodes) {
            n.minDistance = Double.POSITIVE_INFINITY;
            n.previous = null;
        }
    }
}
