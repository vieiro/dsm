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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.vieiro.dsm.graph.DirectedGraph;
import net.vieiro.dsm.graph.DirectedGraphBuilder;

class FASTest {

    @Test
    void testShouldComputeFASCorrectly() {
        // Given a graph
        // A->B->C
        // A---->C
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").build();
        // When we compute a FAS
        List<String> fas = FAS.fas(g);
        // Then the result should be "A", "B", "C"
        Assertions.assertTrue(fas.size() == 3);
        Assertions.assertEquals("A", fas.get(0));
        Assertions.assertEquals("B", fas.get(1));
        Assertions.assertEquals("C", fas.get(2));
    }

    @Test
    void testShouldComputeFASWithCyclesCorrectly() {
        // Given a graph
        // A->B->C
        // A---->C
        //    B<-C
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        DirectedGraph<String> g = builder.connect("A", "B").connect("A", "C").connect("B", "C").connect("C", "B").build();
        // When we compute a FAS
        List<String> fas = FAS.fas(g);
        System.err.format("FAS with cycles: %s%n", fas);
        // Then the result should be "A", "B", "C"
        Assertions.assertTrue(fas.size() == 3);
        Assertions.assertEquals("A", fas.get(0));
        Assertions.assertEquals("C", fas.get(1));
        Assertions.assertEquals("B", fas.get(2));
    }

}
