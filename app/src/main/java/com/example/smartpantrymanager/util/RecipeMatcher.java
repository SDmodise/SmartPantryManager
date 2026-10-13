package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
Strict-matching logic: a recipe is suggested only if EVERY ingredient
it needs is in the pantry in at least the required quantity.
*/
public class RecipeMatcher {

    /** Returns only the recipes the user can make right now. */
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes,
                                                   List<PantryItem> pantry) {
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (canMake(recipe.getIngredients(), pantry)) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    /** True only if every required ingredient is covered by the pantry. */
    public static boolean canMake(List<Ingredient> required, List<PantryItem> pantry) {
        if (required.isEmpty()) {
            return false; // a recipe with no ingredients is not a valid match
        }
        for (Ingredient need : required) {
            String name = normalizeName(need.getName());
            String dim = dimension(need.getUnit());

            double have = 0;
            for (PantryItem p : pantry) {
                if (normalizeName(p.getName()).equals(name)
                        && dimension(p.getUnit()).equals(dim)) {
                    have += toBase(p.getQuantity(), p.getUnit());
                }
            }

            if (have < toBase(need.getQuantity(), need.getUnit())) {
                return false; // one missing or insufficient ingredient = not suggested
            }
        }
        return true;
    }

    /** Lower-cases, trims and strips simple plurals so "Tomatoes" matches "tomato". */
    static String normalizeName(String s) {
        s = s.trim().toLowerCase(Locale.ROOT);
        if (s.endsWith("ies") && s.length() > 4) {
            return s.substring(0, s.length() - 3) + "y";   // berries -> berry
        }
        if (s.endsWith("oes")) {
            return s.substring(0, s.length() - 2);         // tomatoes -> tomato
        }
        if (s.endsWith("s") && !s.endsWith("ss")) {
            return s.substring(0, s.length() - 1);         // eggs -> egg
        }
        return s;
    }

    /** Converts a quantity to a base unit: grams, millilitres, or a plain count. */
    static double toBase(double qty, String unit) {
        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "kg":   return qty * 1000;
            case "g":    return qty;
            case "l":    return qty * 1000;
            case "ml":   return qty;
            case "tbsp": return qty * 15;
            case "tsp":  return qty * 5;
            case "cup":  return qty * 240;
            default:     return qty; // pcs
        }
    }

    /** Groups units so only compatible ones are compared (g with kg, ml with l). */
    static String dimension(String unit) {
        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "kg": case "g":
                return "MASS";
            case "l": case "ml": case "tbsp": case "tsp": case "cup":
                return "VOLUME";
            default:
                return "COUNT";
        }
    }
}