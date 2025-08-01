package com.example.asm_final;

import android.app.AlertDialog;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;

public class BudgetManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private int currentUserId;

    public BudgetManager(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void showSetBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        android.view.View dialogView = android.view.LayoutInflater.from(context).inflate(R.layout.activity_set_budget, null);
        builder.setView(dialogView);

        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etMonth = dialogView.findViewById(R.id.etMonth);
        EditText etYear = dialogView.findViewById(R.id.etYear);

        // Set current month and year
        Calendar cal = Calendar.getInstance();
        etMonth.setText(String.valueOf(cal.get(Calendar.MONTH) + 1));
        etYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        builder.setTitle("Set Monthly Budget")
                .setPositiveButton("Set", (dialog, which) -> {
                    try {
                        double amount = Double.parseDouble(etAmount.getText().toString());
                        int month = Integer.parseInt(etMonth.getText().toString());
                        int year = Integer.parseInt(etYear.getText().toString());

                        if (dbHelper.setBudget(currentUserId, amount, month, year)) {
                            Toast.makeText(context, "Budget set successfully!", Toast.LENGTH_SHORT).show();
                            if (context instanceof ViewExpenseActivity) {
                                ((ViewExpenseActivity) context).updateBudgetInfo();
                            }
                        } else {
                            Toast.makeText(context, "Failed to set budget", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public double getBudget(int month, int year) {
        return dbHelper.getBudget(currentUserId, month, year);
    }

    public double getTotalExpenses(int month, int year) {
        return dbHelper.getTotalExpenses(currentUserId, month, year);
    }
}