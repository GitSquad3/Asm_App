package com.example.asm_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {
    private EditText etStudentId, etFullName, etEmail, etPhone;
    private Button btnAdd;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Initialize views
        etStudentId = findViewById(R.id.etStudentId);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnAdd = findViewById(R.id.btnAdd);
        db = new DatabaseHelper(this);

        // Set button click listener
        btnAdd.setOnClickListener(this::attemptAddStudent);
    }

    private void attemptAddStudent(View view) {
        String studentId = etStudentId.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validate empty fields
        if (studentId.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showToast("Please fill in all fields");
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Normalize and validate phone number
        phone = normalizePhoneNumber(phone);
        if (!isValidVietnamesePhone(phone)) {
            showToast("Please enter a valid Vietnamese phone number");
            etPhone.requestFocus();
            return;
        }

        // Attempt to add student
        String result = db.addStudent(studentId, fullName, email, phone);
        handleAddStudentResult(result);
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

    private void handleAddStudentResult(String result) {
        switch (result) {
            case "success":
                showToast("Student added successfully!");
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;
            case "username":
                etStudentId.requestFocus();
                showToast("Student ID already exists");
                break;
            case "email":
                etEmail.requestFocus();
                showToast("Email already registered");
                break;
            case "phone":
                etPhone.requestFocus();
                showToast("Phone number already registered");
                break;
            default:
                showToast("Failed to add student. Please try again");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}