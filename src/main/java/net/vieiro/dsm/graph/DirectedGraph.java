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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * DirectedGraph represents a directed graph. Use DirectedGraphBuilder to build
 * one of these.
 *
 * @param <ID> The equals/hashcode identifier for nodes.
 */
public class DirectedGraph<ID> {

    /**
     * The adjacency matrix
     */
    final boolean adjacencyMatrix[][];
    /**
     * A map from nodes to indexes in the adjacency matrix
     */
    final Map<ID, Integer> indexes;
    /**
     * The set of nodes in this graph. This may be smaller than the adjacency
     * matrix size. This happens in subgraphs, for instance, where only a subset
     * of the original graph are used.
     */
    final Set<ID> nodes;

    DirectedGraph(Set<ID> nodes, Map<ID, Integer> indexes, boolean[][] adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.indexes = Collections.<ID, Integer>unmodifiableMap(indexes);
        this.nodes = Collections.unmodifiableSet(nodes);
    }

    protected void assertContains(ID node) {
        if (!nodes.contains(node)) {
            throw new NoSuchElementException(
                    String.format("This graph does not contain node %s", Objects.toString(node)));
        }
    }

    /**
     * Returns the order (number of nodes) of the digraph.
     *
     * @return The number of nodes of the digraph.
     */
    public int getOrder() {
        return nodes.size();
    }

    /**
     * Returns the in-degree (at index 0) and out-degree (at index 1) of the
     * given node, this is, the number of incident edges (in-degree) and
     * outward-directed edges (out-degree).
     *
     * @param node The node.
     * @return An array of two integers, the first (0) is the in-degree, the
     * second (1) is the out-degree
     */
    public int[] getInAndOutDegrees(ID node) {
        assertContains(node);
        int i = indexes.get(node);
        int inDegree = 0;
        int outDegree = 0;
        for (ID source : nodes) {
            int j = indexes.get(source);
            if (adjacencyMatrix[j][i]) {
                inDegree++;
            }
            if (adjacencyMatrix[i][j]) {
                outDegree++;
            }
        }
        return new int[]{inDegree, outDegree};
    }

    /**
     * Returns the nodes in this graph.
     *
     * @return The list of nodes.
     */
    public Set<ID> nodes() {
        return nodes;
    }

    /**
     * Returns an iterator over the successors of a given node.
     *
     * @param source The source node.
     * @return An iterator over the successors of source node.
     * @throws NoSuchElementException if source is not on this graph.
     */
    public Iterator<ID> successors(ID source) {
        assertContains(source);
        return new NodeIterator<>(this, NodeNodePredicate.sucessorPredicate(this, source));
    }

    /**
     * Returns an iterator over the predecessors of a given node.
     *
     * @param source The source node.
     * @return An iterator over the predecessors of source node.
     * @throws NoSuchElementException if source is not on this graph.
     */
    public Iterator<ID> predecessors(ID source) {
        assertContains(source);
        return new NodeIterator<>(this, NodeNodePredicate.predecessorPredicate(this, source));
    }

    /**
     * Returns an iterator over the sinks (nodes with no successors) of the
     * graph.
     *
     * @return An iterator over the sinks (nodes with no successors) of the
     * graph.
     */
    public Iterator<ID> sinks() {
        return new NodeIterator<>(this, NodePredicate.sinkPredicate(this));
    }

    /**
     * Returns an iterator over the sources (nodes with no predecessors) of the
     * graph.
     *
     * @return an iterator over the sources (nodes with no predecessors) of the
     * graph.
     */
    public Iterator<ID> sources() {
        return new NodeIterator<>(this, NodePredicate.sourcePredicate(this));
    }

    /**
     * Returns a graph with the give nodes removed.
     *
     * @param ids The nodes to remove
     * @return A digraph where all nodes (and edges) are removed.
     */
    public DirectedGraph<ID> remove(@SuppressWarnings("unchecked") ID[] ids) {
        return remove(Arrays.asList(ids));
    }

    /**
     * Returns a graph with the give node removed.
     *
     * @param id The node to remove.
     * @return A digraph where all nodes (and edges) are removed.
     */
    public DirectedGraph<ID> remove(ID id) {
        return remove(Collections.singleton(id));
    }
 
    /**
     * Returns a graph with the give nodes removed.
     *
     * @param ids The nodes to remove
     * @return A digraph where all nodes (and edges) are removed.
     */
    public DirectedGraph<ID> remove(Collection<ID> ids) {
        Set<ID> remainingIDs = new HashSet<>(this.nodes);
        remainingIDs.removeAll(ids);
        return new DirectedGraph<>(remainingIDs, indexes, adjacencyMatrix);
    }

    /**
     * Returns true if nodes A and B are connected
     *
     * @param A the source node.
     * @param B the target node.
     * @return True if there is an edge from A to B
     * @throws NoSuchElementException if A or B are not on this graph.
     */
    public boolean connects(ID A, ID B) {
        assertContains(A);
        assertContains(B);
        int iA = indexes.get(A);
        int iB = indexes.get(B);
        return adjacencyMatrix[iA][iB];
    }

}
