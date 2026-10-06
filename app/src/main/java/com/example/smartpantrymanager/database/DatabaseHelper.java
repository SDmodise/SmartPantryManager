package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.Recipe;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    // ---------- CREATE ----------
    public long addPantryItem(String name, double qty, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("quantity", qty);
        v.put("unit", unit);
        v.put("expiry_date", expiry);
        return getWritableDatabase().insert("pantry_items", null, v);
    }

    // ---------- READ ----------
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit, expiry_date FROM pantry_items ORDER BY name", null);
        while (c.moveToNext()) {
            list.add(new PantryItem(c.getLong(0), c.getString(1),
                    c.getDouble(2), c.getString(3), c.getString(4)));
        }
        c.close();
        return list;
    }

    // ---------- UPDATE ----------
    public int updatePantryItem(long id, String name, double qty, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("quantity", qty);
        v.put("unit", unit);
        v.put("expiry_date", expiry);
        return getWritableDatabase().update("pantry_items", v, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // ---------- DELETE ----------
    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?",
                new String[]{String.valueOf(id)});
    }
    // ---------- RECIPE SEEDING ----------
    private void addRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long recipeId = db.insert("recipes", null, rv);

        for (Object[] ing : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", recipeId);
            iv.put("name", (String) ing[0]);
            iv.put("quantity", (Double) ing[1]);
            iv.put("unit", (String) ing[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(db, "Scrambled Eggs on Toast",
                "1. Whisk the eggs with salt.\n2. Melt butter in a pan and scramble gently.\n3. Toast the bread and serve the eggs on top.",
                new Object[][]{{"egg", 2.0, "pcs"}, {"bread", 2.0, "pcs"}, {"butter", 10.0, "g"}, {"salt", 1.0, "tsp"}});

        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs with salt.\n2. Cook in a buttered pan until almost set.\n3. Add grated cheese, fold and serve.",
                new Object[][]{{"egg", 3.0, "pcs"}, {"cheese", 50.0, "g"}, {"butter", 10.0, "g"}, {"salt", 0.5, "tsp"}});

        addRecipe(db, "Tomato Pasta",
                "1. Boil the pasta until tender.\n2. Fry onion and garlic in olive oil, add chopped tomatoes and simmer 10 minutes.\n3. Toss the pasta in the sauce.",
                new Object[][]{{"pasta", 200.0, "g"}, {"tomato", 4.0, "pcs"}, {"onion", 1.0, "pcs"}, {"garlic", 2.0, "pcs"}, {"olive oil", 2.0, "tbsp"}});

        addRecipe(db, "Egg Fried Rice",
                "1. Fry chopped onion in oil.\n2. Push aside, scramble the eggs.\n3. Add cooked rice and soy sauce, stir-fry 3 minutes.",
                new Object[][]{{"rice", 200.0, "g"}, {"egg", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"soy sauce", 2.0, "tbsp"}, {"oil", 1.0, "tbsp"}});

        addRecipe(db, "Pancakes",
                "1. Mix flour, sugar, milk and egg into a smooth batter.\n2. Pour small rounds into a hot greased pan.\n3. Flip when bubbles appear and cook until golden.",
                new Object[][]{{"flour", 200.0, "g"}, {"milk", 300.0, "ml"}, {"egg", 1.0, "pcs"}, {"sugar", 2.0, "tbsp"}});

        addRecipe(db, "Mashed Potatoes",
                "1. Peel and boil the potatoes until soft.\n2. Drain and mash with butter and warm milk.\n3. Season with salt.",
                new Object[][]{{"potato", 500.0, "g"}, {"butter", 30.0, "g"}, {"milk", 100.0, "ml"}, {"salt", 1.0, "tsp"}});

        addRecipe(db, "Cheese Toastie",
                "1. Butter the outside of both bread slices.\n2. Put the cheese between them.\n3. Toast in a pan until golden on both sides.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"cheese", 40.0, "g"}, {"butter", 10.0, "g"}});

        addRecipe(db, "Banana Smoothie",
                "1. Peel the bananas.\n2. Blend with milk and honey until smooth.\n3. Serve cold.",
                new Object[][]{{"banana", 2.0, "pcs"}, {"milk", 250.0, "ml"}, {"honey", 1.0, "tbsp"}});

        addRecipe(db, "Simple Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Simmer in water with salt for 25 minutes.\n3. Blend or serve chunky.",
                new Object[][]{{"carrot", 2.0, "pcs"}, {"potato", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"salt", 1.0, "tsp"}});

        addRecipe(db, "Garlic Butter Rice",
                "1. Cook the rice.\n2. Fry crushed garlic in butter for 1 minute.\n3. Stir the garlic butter through the rice.",
                new Object[][]{{"rice", 200.0, "g"}, {"butter", 20.0, "g"}, {"garlic", 2.0, "pcs"}});

        addRecipe(db, "Tuna Mayo Sandwich",
                "1. Drain the tuna and mix with mayonnaise.\n2. Spread on a slice of bread.\n3. Top with the second slice.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"tuna", 1.0, "pcs"}, {"mayonnaise", 2.0, "tbsp"}});

        addRecipe(db, "Baked Potato with Cheese",
                "1. Prick the potatoes and bake at 200 C for 60 minutes.\n2. Split open and add butter and salt.\n3. Top with grated cheese.",
                new Object[][]{{"potato", 2.0, "pcs"}, {"cheese", 60.0, "g"}, {"butter", 15.0, "g"}, {"salt", 0.5, "tsp"}});

        addRecipe(db, "Chicken and Rice",
                "1. Brown the chicken pieces with onion.\n2. Add rice, salt and water.\n3. Cover and simmer about 20 minutes until the rice is cooked.",
                new Object[][]{{"chicken", 300.0, "g"}, {"rice", 200.0, "g"}, {"onion", 1.0, "pcs"}, {"salt", 1.0, "tsp"}});

        addRecipe(db, "Spaghetti Aglio e Olio",
                "1. Boil the pasta.\n2. Gently fry sliced garlic in olive oil.\n3. Toss the pasta in the oil and season with salt.",
                new Object[][]{{"pasta", 200.0, "g"}, {"garlic", 4.0, "pcs"}, {"olive oil", 3.0, "tbsp"}, {"salt", 1.0, "tsp"}});

        addRecipe(db, "French Toast",
                "1. Whisk eggs, milk and sugar.\n2. Soak the bread slices.\n3. Fry in a pan until golden on both sides.",
                new Object[][]{{"bread", 4.0, "pcs"}, {"egg", 2.0, "pcs"}, {"milk", 100.0, "ml"}, {"sugar", 1.0, "tbsp"}});

        addRecipe(db, "Creamy Porridge",
                "1. Heat the milk gently.\n2. Stir in the oats and cook for 5 minutes.\n3. Sweeten with sugar and serve hot.",
                new Object[][]{{"oat", 100.0, "g"}, {"milk", 300.0, "ml"}, {"sugar", 1.0, "tbsp"}});

        addRecipe(db, "Tomato Egg Scramble",
                "1. Fry chopped onion until soft.\n2. Add chopped tomatoes and cook 5 minutes.\n3. Pour in beaten eggs, stir until set and season.",
                new Object[][]{{"egg", 3.0, "pcs"}, {"tomato", 2.0, "pcs"}, {"onion", 1.0, "pcs"}, {"salt", 0.5, "tsp"}});

        addRecipe(db, "Peanut Butter Banana Toast",
                "1. Toast the bread.\n2. Spread with peanut butter.\n3. Top with sliced banana.",
                new Object[][]{{"bread", 2.0, "pcs"}, {"peanut butter", 2.0, "tbsp"}, {"banana", 1.0, "pcs"}});
    }
    // ---------- RECIPE READ ----------
    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, steps FROM recipes ORDER BY name", null);
        while (c.moveToNext()) {
            Recipe r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            r.setIngredients(getIngredientsForRecipe(r.getId()));
            list.add(r);
        }
        c.close();
        return list;
    }

    public Recipe getRecipeById(long id) {
        Recipe r = null;
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, name, steps FROM recipes WHERE id = ?",
                new String[]{String.valueOf(id)});
        if (c.moveToFirst()) {
            r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            r.setIngredients(getIngredientsForRecipe(id));
        }
        c.close();
        return r;
    }

    public List<Ingredient> getIngredientsForRecipe(long recipeId) {
        List<Ingredient> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT name, quantity, unit FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)});
        while (c.moveToNext()) {
            list.add(new Ingredient(c.getString(0), c.getDouble(1), c.getString(2)));
        }
        c.close();
        return list;
    }


}