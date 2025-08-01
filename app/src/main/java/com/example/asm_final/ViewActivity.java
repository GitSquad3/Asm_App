package com.example.asm_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {

    private TextView tvStudentInfo;
    private Button btnGoToSetBudget;
    private Button btnLogoutToMain;
    private DatabaseHelper dbHelper;
    private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Try to get username from Intent
        loggedInUsername = getIntent().getStringExtra("USERNAME");

        // Fallback to SharedPreferences if Intent doesn't provide username
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            loggedInUsername = prefs.getString("loggedInUsername", null);
        }

        // If no username is found, show error and exit
        if (loggedInUsername == null || loggedInUsername.isEmpty()) {
            Toast.makeText(this, "Error: No logged-in user found", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        displayStudentInfo();
    }

    private void initializeViews() {
        dbHelper = new DatabaseHelper(this);
        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        btnGoToSetBudget = findViewById(R.id.btnGoToSetBudget);
        btnLogoutToMain = findViewById(R.id.btnLogoutToMain);
    }

    private void setupListeners() {
        btnGoToSetBudget.setOnClickListener(view -> {
            Intent intent = new Intent(ViewActivity.this, ViewExpenseActivity.class);
            intent.putExtra("USERNAME", loggedInUsername); // Pass username if needed
            startActivity(intent);
        });

        btnLogoutToMain.setOnClickListener(view -> {
            // Clear login state in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            prefs.edit().remove("loggedInUsername").apply();

            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayStudentInfo() {
        String query = "SELECT u." + DatabaseHelper.COL_USER_ID + ", " +
                "u." + DatabaseHelper.COL_USERNAME + ", " +
                "u." + DatabaseHelper.COL_FULL_NAME + ", " +
                "u." + DatabaseHelper.COL_EMAIL + ", " +
                "u." + DatabaseHelper.COL_PHONE + ", " +
                "u." + DatabaseHelper.COL_STUDENT_ID +
                " FROM " + DatabaseHelper.USER_TABLE + " u" +
                " INNER JOIN " + DatabaseHelper.STUDENT_TABLE + " s ON u." +
                DatabaseHelper.COL_USER_ID + " = s." + DatabaseHelper.COL_USER_ID +
                " WHERE u." + DatabaseHelper.COL_USERNAME + " = ?";

        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{loggedInUsername})) {
            if (cursor != null && cursor.moveToFirst()) {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE));
                String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STUDENT_ID));

                StringBuilder info = new StringBuilder();
                info.append("User ID: ").append(userId).append("\n")
                        .append("Username: ").append(username).append("\n")
                        .append("Full Name: ").append(fullName).append("\n")
                        .append("Email: ").append(email).append("\n")
                        .append("Phone: ").append(phone).append("\n")
                        .append("Student ID: ").append(studentId != null ? studentId : "N/A");

                tvStudentInfo.setText(info.toString());
            } else {
                tvStudentInfo.setText("No student information found for this user.");
                Toast.makeText(this, "No data available for " + loggedInUsername, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            tvStudentInfo.setText("");
            Toast.makeText(this, "Error retrieving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}