package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Functional {
    public static void run() {
        //JDK 9
        //List.of
        Stream<Integer> nums = Stream.of(1,2,3,4,5,6,7,8,9,10);
        nums.filter(v->v%2==0).forEach(Functional::print);

        //nums.filter(v->v%2==0);  //error por llegar al final de la secuecia

        List<Integer> temp = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        List<Integer> numeros = Collections.unmodifiableList(new ArrayList<>(temp));
        //numeros.add(11); error por final de secuencia

        
        /*for (int i = 1; i <= 10; i++) {
            numeros.add(i);
        }*/
        System.out.println("Lista original: " + numeros);

        // 1.1 Buscar todos los pares
        List<Integer> pares = numeros.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Pares: " + pares);

        // 1.1 Buscar todos los impares
        List<Integer> impares = numeros.stream()
                .filter(n -> n % 2 != 0)
                .collect(Collectors.toList());
        System.out.println("Impares: " + impares);

        // 1.2 Transformar todos los pares a su valor multiplicado por 2
        List<Integer> paresMultiplicados = pares.stream()
                .map(n -> n * 2)
                .collect(Collectors.toList());
        System.out.println("Pares multiplicados por 2: " + paresMultiplicados);

        // 1.3 Encontrar el primer par
        Optional<Integer> primerPar = numeros.stream()
                .filter(n -> n % 2 == 0)
                .findFirst();
        if (primerPar.isPresent()) {
            System.out.println("Primer par: " + primerPar.get());
        } else {
            System.out.println("No se encontró ningún número par.");
        }

        // 1.4 Sumar todos los pares
        int sumaPares = pares.stream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("Suma de los pares: " + sumaPares);

        // 1.4 Sumar todos los pares usando reduce
        int sumaParesConReduce = pares.stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println("Suma de los pares con reduce: " + sumaParesConReduce);

    }
    static void print(Integer v){
        System.out.println(v);
    }
}
