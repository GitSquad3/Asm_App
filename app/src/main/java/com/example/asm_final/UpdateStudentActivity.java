package com.example.asm_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateStudentActivity extends AppCompatActivity {
    private EditText etSearchStudentId, etUsername, etFullName, etEmail, etPhone;
    private Button btnSearch, btnUpdate;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        // Initialize views
        etSearchStudentId = findViewById(R.id.etSearchStudentId);
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSearch = findViewById(R.id.btnSearch);
        btnUpdate = findViewById(R.id.btnUpdate);
        db = new DatabaseHelper(this);

        // Set search button listener
        btnSearch.setOnClickListener(v -> searchStudent());

        // Set update button listener
        btnUpdate.setOnClickListener(v -> attemptUpdateStudent());
    }

    private void searchStudent() {
        String studentId = etSearchStudentId.getText().toString().trim();
        if (studentId.isEmpty()) {
            showToast("Please enter a student ID");
            return;
        }

        // Query database for student details
        Cursor cursor = db.getAllStudents();
        boolean found = false;
        if (cursor.moveToFirst()) {
            do {
                String dbStudentId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_STUDENT_ID));
                System.out.println("Searching: " + studentId + " | Found: " + dbStudentId);
                if (dbStudentId != null && dbStudentId.equals(studentId)) {
                    etUsername.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_USERNAME)));
                    etFullName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_FULL_NAME)));
                    etEmail.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_EMAIL)));
                    etPhone.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_PHONE)));
                    enableInputFields(true);
                    found = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (!found) {
            showToast("Student ID not found");
            clearFields();
            enableInputFields(false);
        }
    }

    private void attemptUpdateStudent() {
        String oldUsername = etUsername.getText().toString().trim();
        String newFullName = etFullName.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();

        // Validate empty fields
        if (newFullName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            showToast("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Normalize and validate phone number
        newPhone = normalizePhoneNumber(newPhone);
        if (!isValidVietnamesePhone(newPhone)) {
            showToast("Please enter a valid Vietnamese phone number");
            etPhone.requestFocus();
            return;
        }

        // Attempt to update student
        boolean success = db.updateStudent(oldUsername, oldUsername, newFullName, newEmail, newPhone);
        if (success) {
            showToast("Student updated successfully!");
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            showToast("Failed to update student. Check for duplicate email/phone.");
        }
    }

    private String normalizePhoneNumber(String phone) {
        if (phone.startsWith("0")) {
            return "+84" + phone.substring(1);
        } else if (!phone.startsWith("+")) {
            return "+84" + phone;
        }
        return phone;
    }

    private boolean isValidVietnamesePhone(String phone) {
        return phone.matches("^\\+84(90|91|92|93|94|95|96|97|98|99|3[2-9]|5[6-9]|7[0|6-9]|8[1-9])\\d{7}$");
    }

    private void enableInputFields(boolean enable) {
        etUsername.setEnabled(false); // Username remains read-only
        etFullName.setEnabled(enable);
        etEmail.setEnabled(enable);
        etPhone.setEnabled(enable);
        btnUpdate.setEnabled(enable);
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