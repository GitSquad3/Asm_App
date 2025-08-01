package com.example.asm_final;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewActivity extends AppCompatActivity {
    private TextView tvStudentInfo;
    private Button btnGoToSetBudget, btnLogoutToMain;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        btnGoToSetBudget = findViewById(R.id.btnGoToSetBudget);
        btnLogoutToMain = findViewById(R.id.btnLogoutToMain);

        // Load student data
        loadStudentData();

        // Set button listeners
        btnGoToSetBudget.setOnClickListener(v -> {
            Intent intent = new Intent(ViewActivity.this, SetBudgetActivity.class);
            startActivity(intent);
        });

        btnLogoutToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadStudentData() {
        // Query to get student information
        String query = "SELECT u." + DatabaseHelper.COL_USER_ID + ", u." + DatabaseHelper.COL_USERNAME + ", u." +
                DatabaseHelper.COL_FULL_NAME + ", u." + DatabaseHelper.COL_EMAIL + ", u." + DatabaseHelper.COL_PHONE + ", u." +
                DatabaseHelper.COL_STUDENT_ID + " FROM " + DatabaseHelper.USER_TABLE + " u" +
                " INNER JOIN " + DatabaseHelper.STUDENT_TABLE + " s ON u." + DatabaseHelper.COL_USER_ID + " = s." +
                DatabaseHelper.COL_USER_ID;

        try (Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, null)) {
            StringBuilder info = new StringBuilder();
            if (cursor.moveToFirst()) {
                do {
                    info.append("User ID: ").append(cursor.getInt(0)).append("\n")
                            .append("Username: ").append(cursor.getString(1)).append("\n")
                            .append("Full Name: ").append(cursor.getString(2)).append("\n")
                            .append("Email: ").append(cursor.getString(3)).append("\n")
                            .append("Phone: ").append(cursor.getString(4)).append("\n")
                            .append("Student ID: ").append(cursor.getString(5) != null ? cursor.getString(5) : "N/A").append("\n\n");
                } while (cursor.moveToNext());
                tvStudentInfo.setText(info.toString());
            } else {
                tvStudentInfo.setText("No students found");
                Toast.makeText(this, "No student data available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving student info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            tvStudentInfo.setText("");
        }
    }
}