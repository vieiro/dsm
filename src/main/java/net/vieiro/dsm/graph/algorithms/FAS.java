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
package net.vieiro.dsm.graph.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.vieiro.dsm.graph.DirectedGraph;

/**
 * Computes an approximation of the Feedback Arc Set of a directed graph.
 *
 * @param <ID> The type of the node.
 */
public final class FAS<ID> {

    private FAS() {
    }

    /**
     * Solves the Feedback Arc Set Problem using "Eades, P., Lin, X. and Smyth,
     * W.F. (1993) A fast and effective heuristic for the feedback arc set
     * problem. Information Processing Letters, 47 (6). pp. 319-323."
     *
     * @param <ID> The type of nodes in the graph.
     * @param graph The graph.
     * @return A FAS with the result.
     */
    public static <ID> List<ID> fas(DirectedGraph<ID> graph) {
        ArrayList<ID> s1 = new ArrayList<>();
        ArrayList<ID> s2 = new ArrayList<>();

        while (graph.getOrder() != 0) {
            {
                boolean hasSinks = false;
                do {
                    ArrayList<ID> sinks = new ArrayList<>();
                    graph.sinks().forEachRemaining((node) -> sinks.add(node));
                    s2.addAll(0, sinks);
                    graph = graph.remove(sinks);
                    hasSinks = !sinks.isEmpty();
                } while (hasSinks);
            }
            {
                boolean hasSources = false;
                do {
                    ArrayList<ID> sources = new ArrayList<>();
                    graph.sources().forEachRemaining((node) -> sources.add(node));
                    s1.addAll(sources);
                    graph = graph.remove(sources);
                    hasSources = !sources.isEmpty();
                } while (hasSources);
            }
            if (graph.getOrder() != 0) {
                TreeMap<Integer, ID> ordersAndNodes = new TreeMap<>();
                for (ID node : graph.nodes()) {
                    int[] inOut = graph.getInAndOutDegrees(node);
                    int degree = inOut[0] - inOut[1];
                    ordersAndNodes.put(Math.abs(degree), node);
                }
                System.out.format("Orders and nodes: %s%n", ordersAndNodes);
                ID nodeWithMinimumDegree = ordersAndNodes.firstEntry().getValue();
                s1.add(nodeWithMinimumDegree);
                graph = graph.remove(nodeWithMinimumDegree);
            }
        }

        s1.addAll(s2);
        return s1;
    }

}
