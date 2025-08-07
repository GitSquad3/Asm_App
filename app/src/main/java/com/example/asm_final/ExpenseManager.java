package com.example.asm_final;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
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
        showExpenseDialog(null);
    }

    public void showEditExpenseDialog(int expenseId) {
        // Load existing expense data
        Cursor cursor = dbHelper.getExpenseById(expenseId);
        if (cursor != null && cursor.moveToFirst()) {
            String title = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_TITLE, "");
            double amount = getDoubleFromCursor(cursor, DatabaseHelper.COL_EXPENSE_AMOUNT, 0.0);
            String category = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_CATEGORY, "");
            String date = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DATE, "");
            String description = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DESCRIPTION, "");

            // Show dialog with existing data
            showExpenseDialog(expenseId);
            cursor.close();
        }
    }

    private void showExpenseDialog(Integer expenseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_add_expense, null);
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

        // Ngày mặc định hôm nay
        final String[] selectedDate = {new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())};

        // Nếu là edit => load dữ liệu từ DB
        if (expenseId != null) {
            Cursor cursor = dbHelper.getExpenseById(expenseId);
            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_TITLE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_AMOUNT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_CATEGORY));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DESCRIPTION));

                etTitle.setText(title);
                etAmount.setText(String.valueOf(amount));
                etDescription.setText(description);
                int spinnerPos = categoryAdapter.getPosition(category);
                spinnerCategory.setSelection(spinnerPos);
                selectedDate[0] = date;
                btnDate.setText("Date: " + date);
                cursor.close();
            }
        } else {
            btnDate.setText("Date: " + selectedDate[0]);
        }

        // Chọn ngày
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

        builder.setTitle(expenseId == null ? "Add New Expense" : "Edit Expense")
                .setPositiveButton(expenseId == null ? "Add" : "Update", (dialog, which) -> {
                    String newTitle = etTitle.getText().toString().trim();
                    String amountStr = etAmount.getText().toString().trim();
                    String newCategory = spinnerCategory.getSelectedItem().toString();
                    String newDescription = etDescription.getText().toString().trim();

                    if (newTitle.isEmpty()) {
                        Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (amountStr.isEmpty()) {
                        Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double newAmount;
                    try {
                        newAmount = Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success;
                    if (expenseId == null) {
                        success = dbHelper.addExpense(currentUserId, newTitle, newAmount, newCategory, selectedDate[0], newDescription);
                        Toast.makeText(context, success ? "Expense added successfully!" : "Failed to add expense", Toast.LENGTH_SHORT).show();
                    } else {
                        success = dbHelper.updateExpense(expenseId, newTitle, newAmount, newCategory, selectedDate[0], newDescription);
                        Toast.makeText(context, success ? "Expense updated successfully!" : "Failed to update expense", Toast.LENGTH_SHORT).show();
                    }

                    if (success && context instanceof ViewExpenseActivity) {
                        ((ViewExpenseActivity) context).loadData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    public void loadExpenses(ListView listView) {
        List<ExpenseDisplayItem> expenseDisplayList = new ArrayList<>();
        Cursor cursor = dbHelper.getExpenses(currentUserId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = getIntFromCursor(cursor, DatabaseHelper.COL_EXPENSE_ID, -1);
                String title = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_TITLE, "");
                double amount = getDoubleFromCursor(cursor, DatabaseHelper.COL_EXPENSE_AMOUNT, 0.0);
                String category = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_CATEGORY, "");
                String date = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DATE, "");
                expenseDisplayList.add(new ExpenseDisplayItem(id, String.format("%s - ₫%,.0f (%s) - %s", title, amount, category, date)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        ArrayAdapter<ExpenseDisplayItem> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, expenseDisplayList);
        listView.setAdapter(adapter);
    }

    public void sortExpenses(ListView listView, String sortBy, String selectedCategory) {
        List<ExpenseItem> items = new ArrayList<>();
        Cursor cursor = dbHelper.getExpenses(currentUserId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_TITLE, "");
                double amount = getDoubleFromCursor(cursor, DatabaseHelper.COL_EXPENSE_AMOUNT, 0.0);
                String category = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_CATEGORY, "");
                String date = getStringFromCursor(cursor, DatabaseHelper.COL_EXPENSE_DATE, "");

                if (selectedCategory.equals("All") || category.equals(selectedCategory)) {
                    items.add(new ExpenseItem(title, amount, category, date));
                }
            } while (cursor.moveToNext());
            cursor.close();

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

            List<String> expenseList = new ArrayList<>();
            for (ExpenseItem item : items) {
                expenseList.add(String.format("%s - ₫%,.0f (%s) - %s", item.title, item.amount, item.category, item.date));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, expenseList);
            listView.setAdapter(adapter);
        }
    }

    public void showDeleteExpenseConfirmation(int expenseId) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteExpense(expenseId)) {
                        Toast.makeText(context, "Expense deleted successfully!", Toast.LENGTH_SHORT).show();
                        if (context instanceof ViewExpenseActivity) {
                            ((ViewExpenseActivity) context).loadData();
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete expense", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private int getIntFromCursor(Cursor cursor, String columnName, int defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex >= 0) {
                return cursor.getInt(columnIndex);
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

    static class ExpenseDisplayItem {
        int id;
        String displayString;

        ExpenseDisplayItem(int id, String displayString) {
            this.id = id;
            this.displayString = displayString;
        }

        @Override
        public String toString() {
            return displayString;
        }
    }
}
