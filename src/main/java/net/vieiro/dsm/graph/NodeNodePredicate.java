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

import java.util.function.Predicate;

/**
 * A predicate that verifies the relationships of two nodes.
 *
 * @param <ID> The type of the nodes.
 */
abstract class NodeNodePredicate<ID> implements Predicate<ID> {

    protected final DirectedGraph<ID> graph;
    protected final ID sourceNode;
    protected final int sourceNodeIndex;

    /**
     * Builds a new NodePredicate with the given graph and source node.
     *
     * @param graph The graph
     * @param sourceNode The source node.
     */
    NodeNodePredicate(DirectedGraph<ID> graph, ID sourceNode) {
        this.graph = graph;
        this.graph.assertContains(sourceNode);
        this.sourceNode = sourceNode;
        this.sourceNodeIndex = this.graph.indexes.get(sourceNode);
    }

    /**
     * A predicate that checks if the nodes are successors of the source node.
     *
     * @param <ID> The type of the nodes.
     * @param graph The graph
     * @param sourceNode The source node
     * @return A predicate that checks if nodes are successors of sourceNode
     */
    static <ID> NodeNodePredicate<ID> sucessorPredicate(DirectedGraph<ID> graph, ID sourceNode) {
        return new NodeNodePredicate<ID>(graph, sourceNode) {
            @Override
            public boolean test(ID target) {
                int targetIndex = this.graph.indexes.get(target);
                return this.graph.adjacencyMatrix[sourceNodeIndex][targetIndex];
            }
        };
    }

    /**
     * A predicate that checks if the nodes are predecessors of the source node.
     *
     * @param <ID> The type of the nodes.
     * @param graph The graph
     * @param sourceNode The source node
     * @return A predicate that checks if a node is a predecessor of the source
     * node.
     */
    static <ID> NodeNodePredicate<ID> predecessorPredicate(DirectedGraph<ID> graph, ID sourceNode) {
        return new NodeNodePredicate<ID>(graph, sourceNode) {
            @Override
            public boolean test(ID target) {
                int targetIndex = this.graph.indexes.get(target);
                return this.graph.adjacencyMatrix[targetIndex][sourceNodeIndex];
            }
        };
    }

}
