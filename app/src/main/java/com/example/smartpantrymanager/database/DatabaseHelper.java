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
}