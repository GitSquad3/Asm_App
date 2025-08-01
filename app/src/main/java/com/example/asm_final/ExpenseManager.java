package com.example.asm_final;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class ExpenseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private int currentUserId;
    private String[] categories = {"Food", "Transport", "Books", "Entertainment", "Other"};

    public ExpenseManager(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        android.view.View dialogView = android.view.LayoutInflater.from(context).inflate(R.layout.activity_add_expense, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etExpenseTitle);
        EditText etAmount = dialogView.findViewById(R.id.etExpenseAmount);
        EditText etDescription = dialogView.findViewById(R.id.etExpenseDescription);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerExpenseCategory);
        Button btnDate = dialogView.findViewById(R.id.btnExpenseDate);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        final String[] selectedDate = {new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())};
        btnDate.setText("Date: " + selectedDate[0]);

        btnDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate[0] = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        btnDate.setText("Date: " + selectedDate[0]);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        builder.setTitle("Add New Expense")
                .setPositiveButton("Add", (dialog, which) -> {
                    try {
                        String title = etTitle.getText().toString();
                        double amount = Double.parseDouble(etAmount.getText().toString());
                        String category = spinnerCategory.getSelectedItem().toString();
                        String description = etDescription.getText().toString();

                        if (title.isEmpty()) {
                            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (dbHelper.addExpense(currentUserId, title, amount, category, selectedDate[0], description)) {
                            Toast.makeText(context, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                            if (context instanceof ViewExpenseActivity) {
                                ((ViewExpenseActivity) context).loadData();
                            }
                        } else {
                            Toast.makeText(context, "Failed to add expense", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void loadExpenses(ListView listView) {
        List<String> expenseList = new ArrayList<>();

        Cursor cursor = dbHelper.getExpenses(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_TITLE, "");
                double amount = getDoubleFromCursor(cursor, DatabaseHelper.COL_EXPENSE_AMOUNT, 0.0);
                String category = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_CATEGORY, "");
                String date = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DATE, "");

                expenseList.add(String.format("%s - ₫%,.0f (%s) - %s", title, amount, category, date));
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, expenseList);
        listView.setAdapter(adapter);
    }

    public void sortExpenses(ListView listView, String sortBy, String selectedCategory) {
        List<String> expenseList = new ArrayList<>();
        Cursor cursor = dbHelper.getExpenses(currentUserId);

        if (cursor != null && cursor.moveToFirst()) {
            List<ExpenseItem> items = new ArrayList<>();

            do {
                String title = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_TITLE, "");
                double amount = getDoubleFromCursor(cursor, DatabaseHelper.COL_EXPENSE_AMOUNT, 0.0);
                String category = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_CATEGORY, "");
                String date = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DATE, "");

                // Filter by category if not "All"
                if (selectedCategory.equals("All") || category.equals(selectedCategory)) {
                    items.add(new ExpenseItem(title, amount, category, date));
                }
            } while (cursor.moveToNext());
            cursor.close();

            // Sort based on selection
            switch (sortBy) {
                case "Date (Oldest)":
                    items.sort((a, b) -> a.date.compareTo(b.date));
                    break;
                case "Amount (High to Low)":
                    items.sort((a, b) -> Double.compare(b.amount, a.amount));
                    break;
                case "Amount (Low to High)":
                    items.sort((a, b) -> Double.compare(a.amount, b.amount));
                    break;
                default: // Date (Newest)
                    items.sort((a, b) -> b.date.compareTo(a.date));
                    break;
            }

            for (ExpenseItem item : items) {
                expenseList.add(String.format("%s - ₫%,.0f (%s) - %s",
                        item.title, item.amount, item.category, item.date));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, expenseList);
        listView.setAdapter(adapter);
    }

    // Helper methods for safe cursor access
    private String getStringFromCursor(Cursor cursor, String columnName, String defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex >= 0) {
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return defaultValue;
    }

    private double getDoubleFromCursor(Cursor cursor, String columnName, double defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex >= 0) {
                return cursor.getDouble(columnIndex);
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return defaultValue;
    }

    private static class ExpenseItem {
        String title, category, date;
        double amount;

        ExpenseItem(String title, double amount, String category, String date) {
            this.title = title;
            this.amount = amount;
            this.category = category;
            this.date = date;
        }
    }
}