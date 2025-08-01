package com.example.asm_final;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ViewExpenseActivity extends AppCompatActivity {

        private DatabaseHelper dbHelper;
        private BudgetManager budgetManager;
        private ExpenseManager expenseManager;
        private ReportManager reportManager;

        private int currentUserId = 1; // Default to test user

        // UI Components
        private TextView tvBudgetInfo, tvTotalExpenses;
        private Button btnSetBudget, btnViewReports, btnAddExpense, btnSortExpenses;
        private ListView lvExpenses;
        private Spinner spinnerCategory, spinnerSortBy;

        private String[] categories = {"All", "Food", "Transport", "Books", "Entertainment", "Other"};
        private String[] sortOptions = {"Date (Newest)", "Date (Oldest)", "Amount (High to Low)", "Amount (Low to High)"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_expense_view);

            initializeManagers();
            initializeViews();
            setupSpinners();
            setupListeners();
            loadData();
        }

        private void initializeManagers() {
            dbHelper = new DatabaseHelper(this);
            budgetManager = new BudgetManager(this, currentUserId);
            expenseManager = new ExpenseManager(this, currentUserId);
            reportManager = new ReportManager(this, currentUserId);
        }

        private void initializeViews() {
            tvBudgetInfo = findViewById(R.id.tvBudgetInfo);
            tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
            btnSetBudget = findViewById(R.id.btnSetBudget);
            btnViewReports = findViewById(R.id.btnViewReports);
            btnAddExpense = findViewById(R.id.btnAddExpense);
            btnSortExpenses = findViewById(R.id.btnSortExpenses);
            lvExpenses = findViewById(R.id.lvExpenses);
            spinnerCategory = findViewById(R.id.spinnerCategory);
            spinnerSortBy = findViewById(R.id.spinnerSortBy);
        }

        private void setupSpinners() {
            // Setup category spinner
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(categoryAdapter);

            // Setup sort options spinner
            ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptions);
            sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSortBy.setAdapter(sortAdapter);
        }

        private void setupListeners() {
            btnSetBudget.setOnClickListener(v -> budgetManager.showSetBudgetDialog());
            btnViewReports.setOnClickListener(v -> reportManager.showSpendingReports());
            btnAddExpense.setOnClickListener(v -> expenseManager.showAddExpenseDialog());
            btnSortExpenses.setOnClickListener(v -> sortExpenses());
        }

        public void loadData() {
            updateBudgetInfo();
            loadExpenses();
        }

        public void updateBudgetInfo() {
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentYear = cal.get(Calendar.YEAR);

            double budget = budgetManager.getBudget(currentMonth, currentYear);
            double totalExpenses = budgetManager.getTotalExpenses(currentMonth, currentYear);
            double remaining = budget - totalExpenses;
            double percentage = budget > 0 ? (totalExpenses / budget) * 100 : 0;

            String budgetInfo = String.format("Budget: ₫%,.0f\nSpent: ₫%,.0f\nRemaining: ₫%,.0f\nUsage: %.1f%%",
                    budget, totalExpenses, remaining, percentage);
            tvBudgetInfo.setText(budgetInfo);
            tvTotalExpenses.setText(String.format("Total Expenses: ₫%,.0f", totalExpenses));
        }

        private void loadExpenses() {
            expenseManager.loadExpenses(lvExpenses);
        }

        private void sortExpenses() {
            String sortBy = spinnerSortBy.getSelectedItem().toString();
            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            expenseManager.sortExpenses(lvExpenses, sortBy, selectedCategory);
        }
}