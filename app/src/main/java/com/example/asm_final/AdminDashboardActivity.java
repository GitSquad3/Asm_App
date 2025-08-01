package com.example.asm_final;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnManageStudents, btnAddStudent, btnUpdateStudent, btnDeleteStudent, btnViewStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        btnManageStudents = findViewById(R.id.btnManageStudents);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnUpdateStudent = findViewById(R.id.btnUpdateStudent);
        btnDeleteStudent = findViewById(R.id.btnDeleteStudent);
        btnViewStudents = findViewById(R.id.btnViewStudents);

        // Get username from intent
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            tvWelcome.setText("Welcome, Admin " + username + "!");
        }

        // Set button click listeners
        btnManageStudents.setOnClickListener(v -> {
            // Placeholder for Manage Students (can redirect to a unified management screen)
            Toast.makeText(this, "Manage Students clicked", Toast.LENGTH_SHORT).show();
        });

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStudentActivity.class);
            startActivity(intent);
        });

        btnUpdateStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateStudentActivity.class);
            startActivity(intent);
        });

        btnDeleteStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeleteStudentActivity.class);
            startActivity(intent);
        });

        btnViewStudents.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewStudentsActivity.class);
            startActivity(intent);
        });
    }
}