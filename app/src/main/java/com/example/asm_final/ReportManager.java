package com.example.asm_final;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;
import java.util.Calendar;

public class ReportManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private int currentUserId;
    private String[] categories = {"Food", "Transport", "Books", "Entertainment", "Other"};

    public ReportManager(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void showSpendingReports() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        android.view.View dialogView = android.view.LayoutInflater.from(context).inflate(R.layout.activity_spending_report, null);
        builder.setView(dialogView);

        TextView tvCategoryReport = dialogView.findViewById(R.id.tvCategoryReport);
        TextView tvMonthlyReport = dialogView.findViewById(R.id.tvMonthlyReport);

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        // Monthly summary
        double totalExpenses = dbHelper.getTotalExpenses(currentUserId, currentMonth, currentYear);
        double budget = dbHelper.getBudget(currentUserId, currentMonth, currentYear);
        double percentage = budget > 0 ? (totalExpenses / budget) * 100 : 0;

        String monthlyReport = String.format("Monthly Summary:\n\n" +
                        "Total Spent: ₫%,.0f\n" +
                        "Budget: ₫%,.0f\n" +
                        "Remaining: ₫%,.0f\n" +
                        "Usage: %.1f%%",
                totalExpenses, budget, budget - totalExpenses, percentage);

        tvMonthlyReport.setText(monthlyReport);

        // Category breakdown
        StringBuilder categoryReport = new StringBuilder("Category Breakdown:\n\n");
        for (String category : categories) {
            double categoryTotal = dbHelper.getTotalExpensesByCategory(currentUserId, category, currentMonth, currentYear);
            if (categoryTotal > 0) {
                categoryReport.append(String.format("%s: ₫%,.0f\n", category, categoryTotal));
            }
        }

        tvCategoryReport.setText(categoryReport.toString());

        builder.setTitle("Spending Reports")
                .setPositiveButton("Close", null)
                .show();
    }

    // Helper methods for safe cursor access (if needed in future)
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
}