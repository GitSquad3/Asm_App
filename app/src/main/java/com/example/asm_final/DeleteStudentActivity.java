package com.example.asm_final;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteStudentActivity extends AppCompatActivity {
    private EditText etSearchStudentId, etUsername, etFullName, etEmail, etPhone;
    private Button btnSearch, btnDelete;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_student);

        // Initialize views
        etSearchStudentId = findViewById(R.id.etSearchStudentId);
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSearch = findViewById(R.id.btnSearch);
        btnDelete = findViewById(R.id.btnDelete);
        db = new DatabaseHelper(this);

        // Initially disable delete button
        btnDelete.setEnabled(false);

        // Set search button listener
        btnSearch.setOnClickListener(v -> searchStudent());

        // Set delete button listener
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void searchStudent() {
        String studentId = etSearchStudentId.getText().toString().trim();
        if (studentId.isEmpty()) {
            showToast("Please enter a student ID");
            return;
        }

        // Clear previous data
        clearFields();

        // Query database for student details
        Cursor cursor = db.getAllStudents();
        boolean found = false;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String dbStudentId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STUDENT_ID));
                if (dbStudentId != null && dbStudentId.equals(studentId)) {
                    etUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)));
                    etFullName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME)));
                    etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)));
                    etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE)));
                    btnDelete.setEnabled(true);
                    found = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        if (!found) {
            showToast("Student ID not found");
            btnDelete.setEnabled(false);
        }
    }

    private void showDeleteConfirmation() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            showToast("No student selected for deletion");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete the student: " + etFullName.getText().toString() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        attemptDeleteStudent();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }

    private void attemptDeleteStudent() {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            showToast("No student selected for deletion");
            return;
        }

        // Attempt to delete student
        boolean success = db.deleteStudent(username);
        if (success) {
            showToast("Student deleted successfully!");
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            showToast("Failed to delete student. Please try again.");
        }
    }

    private void clearFields() {
        etUsername.setText("");
        etFullName.setText("");
        etEmail.setText("");
        etPhone.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}