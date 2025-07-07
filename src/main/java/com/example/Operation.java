package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class Operation {
    static void run(){
        List<BinaryOperator<Integer>> operations = new ArrayList<>();
        operations.add((a,b)->a+b);
        operations.add((a,b)->a*b);
        operations.add((a,b)->a-b);
        operations.add((a,b)->a/b);
        operations.forEach(op->System.out.println(op.apply(2, 2)));
    }
}
