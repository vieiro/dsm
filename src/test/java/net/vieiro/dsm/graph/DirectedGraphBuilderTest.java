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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DirectedGraphBuilderTest {

    @Test
    void testShouldCreateSubgraphCorrectly() {
        // Given a graph
        // A->B->C
        // A---->C
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();
        // When we create a subgraph by removing a node...
        g = g.remove("B");

        // Then the graph must have two nodes
        Assertions.assertTrue(g.getOrder() == 2);
        Assertions.assertTrue(g.connects("A", "C"));

        Set<String> successorsOfA = new HashSet<>();
        g.successors("A").forEachRemaining(successorsOfA::add);
        Assertions.assertTrue(1 == successorsOfA.size());
        Assertions.assertTrue(successorsOfA.contains("C"));

        Set<String> predecessorsOfC = new HashSet<>();
        g.predecessors("C").forEachRemaining(predecessorsOfC::add);
        Assertions.assertTrue(1 == predecessorsOfC.size());
        Assertions.assertTrue(predecessorsOfC.contains("A"));

    }

    @Test
    void testShouldThrowWhenAccessingDeletedNode() {
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();
        final DirectedGraph<String> subgraph = g.remove("B");
        NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> {
            subgraph.assertContains("B");
        });
    }

    @Test
    void testShouldCreateGraphCorrectly() {
        // Given a graph builder
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        // When we build a simple graph
        // A->B->C
        // A---->C
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();
        // Then the graph must have three nodes
        Assertions.assertTrue(g.getOrder() == 3);
        Assertions.assertTrue(g.nodes().contains("A"));
        Assertions.assertTrue(g.nodes().contains("B"));
        Assertions.assertTrue(g.nodes().contains("C"));
        Assertions.assertFalse(g.nodes().contains("D"));

        Set<String> successorsOfA = new HashSet<>();
        g.successors("A").forEachRemaining(successorsOfA::add);
        System.err.format("Successors of A: %s%n", successorsOfA);
        Assertions.assertTrue(successorsOfA.contains("B"));
        Assertions.assertTrue(successorsOfA.contains("C"));

        Set<String> predecessorsOfC = new HashSet<>();
        g.predecessors("C").forEachRemaining(predecessorsOfC::add);
        System.err.format("Predecessors of C: %s%n", predecessorsOfC);
        Assertions.assertTrue(predecessorsOfC.contains("A"));
        Assertions.assertTrue(predecessorsOfC.contains("B"));

        Set<String> sinks = new HashSet<>();
        g.sinks().forEachRemaining(sinks::add);
        System.err.format("Sinks: %s%n", sinks);
        Assertions.assertTrue(sinks.size() == 1);
        Assertions.assertTrue(sinks.contains("C"));

        Set<String> sources = new HashSet<>();
        g.sources().forEachRemaining(sources::add);
        System.err.format("Sources : %s%n", sources);
        Assertions.assertTrue(sources.size() == 1);
        Assertions.assertTrue(sources.contains("A"));

    }

    @Test
    void testShouldComputeInOutDegreesCorrectly() {
        // Given a graph
        // A->B->C
        // A---->C
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();

        int[] io = g.getInAndOutDegrees("A");
        Assertions.assertTrue(0 == io[0]);
        Assertions.assertTrue(2 == io[1]);

        io = g.getInAndOutDegrees("C");
        Assertions.assertTrue(2 == io[0]);
        Assertions.assertTrue(0 == io[1]);

    }

    @Test
    void testShouldDetectIteratorExhausted() {
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();
        final Iterator<String> successors = g.successors("A");
        while (successors.hasNext()) {
            successors.next();
        }
        NoSuchElementException e = Assertions.assertThrows(NoSuchElementException.class, () -> {
            successors.next();
        });
    }

}
