package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.Recipe;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvDetailSteps);
        Button btnBack = findViewById(R.id.btnDetailBack);
        btnBack.setOnClickListener(v -> finish());

        // The recipe id arrives through the Intent that opened this screen
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(recipe.getName());
        tvSteps.setText(recipe.getSteps());

        StringBuilder sb = new StringBuilder();
        for (Ingredient ing : recipe.getIngredients()) {
            sb.append("- ").append(formatQuantity(ing.getQuantity()))
                    .append(" ").append(ing.getUnit())
                    .append(" ").append(ing.getName()).append("\n");
        }
        tvIngredients.setText(sb.toString().trim());
    }

    // Shows 2 instead of 2.0, but keeps 0.5 as 0.5
    private String formatQuantity(double q) {
        if (q == Math.floor(q)) {
            return String.valueOf((long) q);
        }
        return String.valueOf(q);
    }
}
