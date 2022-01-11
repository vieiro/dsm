/*
 * Copyright 2022 Antonio Vieiro <antonio@vieiro.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vieiro.dsm.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builds a directed graph.
 *
 * @param <ID> The type of equal/hashcode used to identify nodes.
 */
public class DirectedGraphBuilder<ID> {

    private final HashMap<ID, Set<ID>> edges;
    private final HashSet<ID> nodes;

    /**
     * Creates a new DirectedGraphBuilder:
     */
    public DirectedGraphBuilder() {
        nodes = new HashSet<>();
        edges = new HashMap<>();
    }

    /**
     * Connects two nodes in the graph. Nodes are added to the graph
     * automatically.
     *
     * @param source The source node.
     * @param target The target node.
     * @return this.
     */
    public DirectedGraphBuilder<ID> connect(ID source, ID target) {
        nodes.add(source);
        nodes.add(target);
        Set<ID> successors = edges.get(source);
        if (successors == null) {
            successors = new HashSet<>(nodes.size());
            edges.put(source, successors);
        }
        successors.add(target);
        return this;
    }

    /**
     * Builds a directed graph.
     *
     * @return The directed graph.
     */
    public DirectedGraph<ID> build() {
        int numberOfNodes = nodes.size();
        boolean[][] adjacencyMatrix = new boolean[numberOfNodes][numberOfNodes];
        HashMap<ID, Integer> ordering = new HashMap<>();
        int nextNodeIndex = 0;
        for (ID node : nodes) {
            ordering.put(node, nextNodeIndex++);
        }
        for (Map.Entry<ID, Set<ID>> edgesFromSource : edges.entrySet()) {
            ID source = edgesFromSource.getKey();
            int sourceIndex = ordering.get(source);
            Set<ID> targets = edgesFromSource.getValue();
            for (ID target : targets) {
                int targetIndex = ordering.get(target);
                adjacencyMatrix[sourceIndex][targetIndex] = true;
            }
        }
        return new DirectedGraph<>(ordering.keySet(), ordering, adjacencyMatrix);
    }

}
