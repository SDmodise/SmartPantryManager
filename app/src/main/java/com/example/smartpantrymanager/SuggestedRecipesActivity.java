package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.RecipeMatcher;
import com.example.smartpantrymanager.util.NavHelper;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView tvNoMatches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        recyclerView = findViewById(R.id.recyclerSuggested);
        tvNoMatches = findViewById(R.id.tvNoMatches);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        NavHelper.setup(this, R.id.nav_suggested);
    }

    // Re-run the matching each time the screen appears, so it reflects the current pantry
    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        List<Recipe> suggested = RecipeMatcher.getSuggestedRecipes(allRecipes, pantry);

        if (suggested.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoMatches.setVisibility(View.VISIBLE);
        } else {
            tvNoMatches.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(new RecipeAdapter(suggested, this));
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
