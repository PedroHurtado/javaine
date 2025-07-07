package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class Operation {
    static void run(){
        Function<Integer, Function<Integer,Integer>> sum = a->b->a+b;
        
        //var r = (int a)->(int b)->a+b;
       

        List<BinaryOperator<Integer>> operations = new ArrayList<>();
        operations.add((a,b)->a+b);
        operations.add((a,b)->a*b);
        operations.add((a,b)->a-b);
        operations.add((a,b)->a/b);
        operations.forEach(op->System.out.println(op.apply(2, 2)));
        var result = sum(5);
        System.out.println(result.apply(3)); //8
        result.apply(100); //105
    }
    
    /*static Function<Integer,Integer> sum(Integer a){
        return b->a+b;
    }*/
}
