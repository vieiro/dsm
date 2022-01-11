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
 * A predicate that checks a property of a node on a graph.
 *
 * @param <ID> The types of nodes.
 */
abstract class NodePredicate<ID> implements Predicate<ID> {

    protected final DirectedGraph<ID> graph;

    /**
     * Builds a graph predicate from a graph.
     *
     * @param graph the graph.
     */
    NodePredicate(DirectedGraph<ID> graph) {
        this.graph = graph;
    }

    /**
     * Verifies if a node is a source of the graph.
     *
     * @param <ID> The type of the nodes.
     * @param graph The graph
     * @return A predicate that checks if a node is a source (has no
     * predecessors).
     */
    public static final <ID> NodePredicate<ID> sourcePredicate(DirectedGraph<ID> graph) {
        return new NodePredicate<ID>(graph) {

            @Override
            public boolean test(ID node) {
                int nodeIndex = this.graph.indexes.get(node);
                for (ID target : this.graph.nodes) {
                    int targetIndex = this.graph.indexes.get(target);
                    if (this.graph.adjacencyMatrix[targetIndex][nodeIndex]) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    /**
     * Verifies if a node is a sink of the graph.
     *
     * @param <ID> The type of the nodes.
     * @param graph The graph
     * @return A predicate that checks if a node is a sink (has no successors).
     */
    public static final <ID> NodePredicate<ID> sinkPredicate(DirectedGraph<ID> graph) {
        return new NodePredicate<ID>(graph) {

            @Override
            public boolean test(ID node) {
                int nodeIndex = this.graph.indexes.get(node);
                for (ID target : this.graph.nodes) {
                    int targetIndex = this.graph.indexes.get(target);
                    if (this.graph.adjacencyMatrix[nodeIndex][targetIndex]) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

}
