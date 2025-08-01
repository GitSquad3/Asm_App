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

        // Set listeners
        btnSearch.setOnClickListener(v -> searchStudent());
        btnUpdate.setOnClickListener(v -> attemptUpdateStudent());
    }

    private void searchStudent() {
        String studentId = etSearchStudentId.getText().toString().trim();
        if (studentId.isEmpty()) {
            showToast("Please enter a student ID");
            return;
        }

        Cursor cursor = db.getAllStudents();
        boolean found = false;

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_STUDENT_ID);
            int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COL_USERNAME);
            int fullNameIndex = cursor.getColumnIndex(DatabaseHelper.COL_FULL_NAME);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.COL_EMAIL);
            int phoneIndex = cursor.getColumnIndex(DatabaseHelper.COL_PHONE);

            do {
                if (idIndex != -1 && studentId.equals(cursor.getString(idIndex))) {
                    etUsername.setText(usernameIndex != -1 ? cursor.getString(usernameIndex) : "");
                    etFullName.setText(fullNameIndex != -1 ? cursor.getString(fullNameIndex) : "");
                    etEmail.setText(emailIndex != -1 ? cursor.getString(emailIndex) : "");
                    etPhone.setText(phoneIndex != -1 ? cursor.getString(phoneIndex) : "");
                    enableInputFields(true);
                    found = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

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

        if (newFullName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            showToast("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        newPhone = normalizePhoneNumber(newPhone);
        if (!isValidVietnamesePhone(newPhone)) {
            showToast("Please enter a valid Vietnamese phone number");
            etPhone.requestFocus();
            return;
        }

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
        return phone.matches("^\\+84(90|91|92|93|94|95|96|97|98|99|3[2-9]|5[6-9]|7[06-9]|8[1-9])\\d{7}$");
    }

    private void enableInputFields(boolean enable) {
        etUsername.setEnabled(false); // Username is read-only
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