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
package net.vieiro.dsm;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import net.vieiro.dsm.graph.DirectedGraph;
import net.vieiro.dsm.graph.DirectedGraphBuilder;
import net.vieiro.dsm.graph.algorithms.FAS;
import net.vieiro.dsm.graph.dsm.DSMExcelGenerator;

/**
 * Reads a simple file containing dependencies and generates an Excel sheet with
 * a proposed Dependency Structure Matrix.
 * https://en.wikipedia.org/wiki/Design_structure_matrix
 */
public class Main {

    /**
     * Reads a graph of strings from a Reader.
     *
     * @param reader The reader.
     * @return a DirectedGraph of Strings
     * @throws Exception on exceptional circumstances.
     */
    private static DirectedGraph<String> read(Reader reader) throws Exception {
        DirectedGraphBuilder<String> builder = new DirectedGraphBuilder<>();
        BufferedReader br = new BufferedReader(reader);
        do {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] parts = line.split(":");
            if (parts == null || parts.length != 2) {
                System.err.format("Warning: Ignoring line '%s'%n", line);
            } else {
                String source = parts[0].trim();
                String target = parts[1].trim();
                builder.connect(source, target);
            }
        } while (true);
        return builder.build();
    }

    /**
     * Reads a file with the following format: - Each file is an edge, from a
     * source node to a target node, separated by colons.
     *
     * @param args The command line arguments.
     * @throws Exception thrown on exceptional circumstances.
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.format("java %s input-file%n", Main.class.getName());
            System.err.println("Each line in the file is an edge from a source to a target node, separated with a colon ':'.\n");
            System.exit(1);
        }

        DirectedGraph<String> dependencies = null;
        try ( FileReader reader = new FileReader(args[0])) {
            dependencies = read(reader);
        }

        List<String> fas = FAS.fas(dependencies);

        try ( BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("output.xlsx"))) {
            DSMExcelGenerator generator = new DSMExcelGenerator(dependencies, fas, output);
            generator.run();
        }

    }

}
