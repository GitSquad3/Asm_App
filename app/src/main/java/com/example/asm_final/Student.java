package com.example.asm_final;

public class Student {
    private String studentId;
    private String username;
    private String fullName;
    private String email;
    private String phone;

    public Student(String studentId, String username, String fullName, String email, String phone) {
        this.studentId = studentId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}