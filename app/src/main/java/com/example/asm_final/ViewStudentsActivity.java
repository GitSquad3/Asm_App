package com.example.asm_final;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ViewStudentsActivity extends AppCompatActivity {
    private RecyclerView rvStudents;
    private Button btnBack;
    private DatabaseHelper db;
    private StudentAdapter adapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);

        // Initialize views
        rvStudents = findViewById(R.id.rvStudents);
        btnBack = findViewById(R.id.btnBack);
        db = new DatabaseHelper(this);
        studentList = new ArrayList<>();

        // Set up RecyclerView
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList);
        rvStudents.setAdapter(adapter);

        // Load student data
        loadStudents();

        // Set back button listener
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadStudents() {
        Cursor cursor = db.getAllStudents();
        studentList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String studentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STUDENT_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE));
                studentList.add(new Student(studentId, username, fullName, email, phone));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }
}