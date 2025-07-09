package com.example.domain;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.common.EntityBase;

public class Pizza extends EntityBase {
    private static final double PROFIT = 1.2D;

    private String name;
    private String description;
    private String url;

    private Set<Ingredient> ingredients;

    protected Pizza(UUID id, String name, String description, String url, Set<Ingredient> ingredients) {
        super(id);
        this.name = name;
        this.description = description;
        this.url = url;
        this.ingredients = this.cloneIngredients(ingredients);
              
    }

    public double getPrice() {
        return ingredients.stream()
                .map(i -> i.getCost())
                .reduce(0D, Double::sum) * PROFIT;
    }

    public Set<Ingredient> getIngredients() {
        return this.cloneIngredients(ingredients);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public void addIngedient(Ingredient ingredient) {
        // pizza.addingredient
        ingredients.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        // pizza.removeingredient
        ingredients.remove(ingredient);
    }

    public void update(String name, String description, String url) {
        // pizza.update
        this.name = name;
        this.description = description;
        this.url = url;
    }
    private Set<Ingredient> cloneIngredients(Set<Ingredient> ingredients){
        return ingredients.stream()
                .map(Ingredient::copy)
                .collect(Collectors.toSet());
    }

    public static Pizza create(UUID id, String name, String description, String url, Set<Ingredient> ingredients) {
        return new Pizza(id, name, description, url, ingredients);
    }

    public static Pizza copy(Pizza other) {
        return new Pizza(
                other.getId(),
                other.getName(),
                other.getDescription(),
                other.getUrl(),
                other.getIngredients());
    }

}
