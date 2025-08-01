package com.example.asm_final;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite  .SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CampusExpense.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table and column constants
    private static final String USER_TABLE = "User";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User table with timestamp
        String createUserTable = "CREATE TABLE " + USER_TABLE + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_FULL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PHONE + " TEXT UNIQUE NOT NULL, " +
                COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createUserTable);

        // Create other tables (Student, Admin, etc.)
        db.execSQL("CREATE TABLE Student (" +
                "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES User(user_id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Admin (" +
                "admin_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES User(user_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Admin");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }

    public String registerUser(String username, String password, String fullName, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check for existing credentials
        String conflictField = checkExistingCredentials(db, username, email, phone);
        if (conflictField != null) {
            return conflictField;
        }

        // Insert new user
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);

        try {
            long result = db.insertOrThrow(USER_TABLE, null, values);
            return result != -1 ? "success" : "error";
        } catch (Exception e) {
            return "error";
        }
    }

    private String checkExistingCredentials(SQLiteDatabase db, String username, String email, String phone) {
        // Check username
        if (isFieldExists(db, COL_USERNAME, username)) {
            return "username";
        }
        // Check email
        if (isFieldExists(db, COL_EMAIL, email)) {
            return "email";
        }
        // Check phone
        if (isFieldExists(db, COL_PHONE, phone)) {
            return "phone";
        }
        return null;
    }

    private boolean isFieldExists(SQLiteDatabase db, String column, String value) {
        String query = "SELECT 1 FROM " + USER_TABLE + " WHERE " + column + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, new String[]{value})) {
            return cursor.moveToFirst();
        }
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + USER_TABLE +
                " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, new String[]{username, password})) {
            return cursor.moveToFirst();
        }
    }
}