package com.example.domain;

import java.util.UUID;

import com.example.common.EntityBase;






public class Ingredient extends EntityBase {
    
    private String name;
    private double cost;
    
    public String getName() {
        return name;
    }
    public double getCost() {
        return cost;
    }    
    protected Ingredient(UUID id, String name, double cost) {
        //ingredient.new
        /*
         * la creacccion real del objeto(SI)
         * el mapeo de la bb.dd(No)
         */
        super(id);
        this.name = name;
        this.cost = cost;
    } 
    public void update(String name, double cost){
        //ingredient.update
        this.name = name;
        this.cost = cost;
    }      
    public static Ingredient create(UUID id, String name, double cost){
        //ingredient.new
        return new Ingredient(id, name, cost);
    }

    public static Ingredient copy(Ingredient other){                
        return new Ingredient(other.getId(), other.getName(), other.getCost());
    }

}

