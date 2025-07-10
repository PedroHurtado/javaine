package com.example;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamSql {
    public static <A, B> Stream<Pair<A, B>> leftJoin(
            Stream<A> streamA,
            Collection<B> collectionB,
            BiPredicate<A, B> predicate) {
        return streamA.flatMap(a -> {
            Stream<Pair<A, B>> joined = collectionB.stream()
                    .filter(b -> predicate.test(a, b))
                    .map(b -> new Pair<>(a, b));

            // Usamos iterator para evitar collect
            Iterator<Pair<A, B>> it = joined.iterator();
            if (it.hasNext()) {
                return Stream.concat(
                        Stream.of(it.next()),
                        StreamSupport.stream(
                                Spliterators.spliteratorUnknownSize(it, 0), false));
            } else {
                return Stream.of(new Pair<>(a, null));
            }
        });
    }

    // Simple clase Pair para ejemplo
    public static class Pair<X, Y> {
        public final X first;
        public final Y second;

        public Pair(X first, Y second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }
}
