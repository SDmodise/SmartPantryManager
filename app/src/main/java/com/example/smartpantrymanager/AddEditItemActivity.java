package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class AddEditItemActivity extends AppCompatActivity {

    // Keys used to pass data between activities through an Intent
    public static final String EXTRA_ID = "item_id";
    public static final String EXTRA_NAME = "item_name";
    public static final String EXTRA_QUANTITY = "item_quantity";
    public static final String EXTRA_UNIT = "item_unit";
    public static final String EXTRA_EXPIRY = "item_expiry";

    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs"};

    private DatabaseHelper dbHelper;
    private EditText etName, etQuantity, etExpiry;
    private Spinner spinnerUnit;
    private long editId = -1; // -1 means "adding a new item"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        dbHelper = new DatabaseHelper(this);

        TextView tvFormTitle = findViewById(R.id.tvFormTitle);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnClearExpiry = findViewById(R.id.btnClearExpiry);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        spinnerUnit.setAdapter(unitAdapter);

        // If the Intent carries an item id, we are editing: pre-fill the form
        editId = getIntent().getLongExtra(EXTRA_ID, -1);
        if (editId != -1) {
            tvFormTitle.setText("Edit Ingredient");
            etName.setText(getIntent().getStringExtra(EXTRA_NAME));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_QUANTITY, 0)));
            etExpiry.setText(getIntent().getStringExtra(EXTRA_EXPIRY));

            String unit = getIntent().getStringExtra(EXTRA_UNIT);
            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(unit)) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }
        }

        etExpiry.setOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> etExpiry.setText(""));
        btnSave.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            etExpiry.setText(date);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyText = etQuantity.getText().toString().trim();
        String unit = spinnerUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        // ----- Input validation -----
        boolean valid = true;

        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            valid = false;
        } else if (name.length() > 50) {
            etName.setError("Name must be 50 characters or fewer");
            valid = false;
        }

        double quantity = 0;
        if (qtyText.isEmpty()) {
            etQuantity.setError("Quantity is required");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(qtyText);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (!valid) {
            return; // stop here: do not save invalid data
        }

        // ----- Save: Create or Update -----
        if (editId == -1) {
            dbHelper.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updatePantryItem(editId, name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish(); // return to the pantry list
    }
}
