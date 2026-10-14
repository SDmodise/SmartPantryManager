package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.smartpantrymanager.util.NavHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerPantry);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        adapter = new PantryAdapter(dbHelper.getAllPantryItems(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddEditItemActivity.class)));
         NavHelper.setup(this, R.id.nav_pantry);

         checkExpiringItems(dbHelper.getAllPantryItems());
    }



    // Runs every time we come back to this screen, so the list is always up to date
    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.setItems(items);
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ID, item.getId());
        intent.putExtra(AddEditItemActivity.EXTRA_NAME, item.getName());
        intent.putExtra(AddEditItemActivity.EXTRA_QUANTITY, item.getQuantity());
        intent.putExtra(AddEditItemActivity.EXTRA_UNIT, item.getUnit());
        intent.putExtra(AddEditItemActivity.EXTRA_EXPIRY, item.getExpiryDate());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    refreshList();
                    Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // If the Settings toggle is on, warn about items expiring within 3 days (or already expired)
    private void checkExpiringItems(List<PantryItem> items) {
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        if (!prefs.getBoolean(SettingsActivity.KEY_EXPIRY_ALERTS, true)) {
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        long limit = System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000;
        int count = 0;

        for (PantryItem item : items) {
            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.isEmpty()) {
                continue;
            }
            try {
                Date date = format.parse(expiry);
                if (date != null && date.getTime() <= limit) {
                    count++;
                }
            } catch (ParseException ignored) {
                // skip items with a badly formatted date
            }
        }

        if (count > 0) {
            Toast.makeText(this, count + " item(s) expiring soon or expired",
                    Toast.LENGTH_LONG).show();
        }
    }
}