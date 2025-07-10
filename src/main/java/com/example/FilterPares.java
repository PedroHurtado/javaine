package com.example;

import java.io.*;
import java.nio.file.*;

import java.util.stream.*;

public class FilterPares {
    public static void run() {

        Path inputPath = Paths.get("input.txt");
        Path outputPath = Paths.get("output.txt");

        try (Stream<String> lines = Files.lines(inputPath);
                BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

            NumberWriter numberWriter = new NumberWriter(writer);

            lines
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .mapToInt(Integer::parseInt)  //error no controlado
                    .filter(n -> n % 2 == 0)
                    .forEach(numberWriter::writeNumber);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class NumberWriter {
        private final BufferedWriter writer;

        public NumberWriter(BufferedWriter writer) {
            this.writer = writer;
        }

        public void writeNumber(int n) {
            try {
                writer.write(n + "\n");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
