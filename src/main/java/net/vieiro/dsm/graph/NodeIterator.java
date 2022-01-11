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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

class NodeIterator<ID> implements Iterator<ID> {

    private final DirectedGraph<ID> graph;
    private final Predicate<ID> predicate;
    private final Iterator<ID> targets;
    private ID target;

    NodeIterator(DirectedGraph<ID> graph, Predicate<ID> predicate) {
        this.graph = graph;
        this.predicate = predicate;
        this.targets = this.graph.nodes.iterator();
        this.target = null;
        advanceToNext();
    }

    private boolean advanceToNext() {
        while (targets.hasNext()) {
            target = targets.next();
            if (this.predicate.test(target)) {
                return true;
            }
        }
        target = null;
        return false;
    }

    @Override
    public boolean hasNext() {
        return target != null;
    }

    @Override
    public ID next() {
        if (!hasNext()) {
            throw new NoSuchElementException("This iterator has no more elements");
        }
        ID next = target;
        advanceToNext();
        return next;
    }

}
