package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector.Characteristics;

public class TotalCountCollector {
    public static Collector<Integer, LongAdder, Long> countingElements() {
        Supplier<LongAdder> supplier = LongAdder::new;
        BiConsumer<LongAdder, Integer> accumulator = (adder, elem) -> adder.increment();
        BinaryOperator<LongAdder> combiner = (a1, a2) -> {
            a1.add(a2.sum());
            return a1;
        };
        Function<LongAdder, Long> finisher = LongAdder::sum;
        
        return Collector.of(supplier, accumulator, combiner, finisher, Characteristics.UNORDERED);
    }
    public static void run(){
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Stream secuencial
        long total1 = numbers.stream()
            .filter(n -> n % 2 == 0) // solo pares
            .collect(TotalCountCollector.countingElements());
        System.out.println("Total elementos pares (stream): " + total1);

        // Stream paralelo
        long total2 = numbers.parallelStream()
            .filter(n -> n > 5) // mayores que 5
            .collect(TotalCountCollector.countingElements());
        System.out.println("Total elementos mayores que 5 (parallelStream): " + total2);
    }
}

