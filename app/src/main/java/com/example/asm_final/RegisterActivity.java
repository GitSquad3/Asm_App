package com.example.asm_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etFullName, etPassword, etEmail, etPhone;
    private Button btnRegister;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);

        db = new DatabaseHelper(this);

        btnRegister.setOnClickListener(this::attemptRegistration);
    }

    private void attemptRegistration(View view) {
        // Get input values
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validate empty fields
        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty() ||
                email.isEmpty() || phone.isEmpty()) {
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
            showToast("Please enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        // Attempt registration
        String result = db.registerUser(username, password, fullName, email, phone);
        handleRegistrationResult(result);
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

    private void handleRegistrationResult(String result) {
        switch (result) {
            case "success":
                showToast("Registration successful!");
                navigateToLogin();
                break;
            case "username":
                etUsername.requestFocus();
                showToast("Username already exists");
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
                showToast("Registration failed. Please try again");
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}