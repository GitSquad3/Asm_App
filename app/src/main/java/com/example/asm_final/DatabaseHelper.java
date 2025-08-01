package com.example.asm_final;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CampusExpense.db";
    private static final int DATABASE_VERSION = 4;

    // Đổi thành public để các lớp khác truy cập
    public static final String USER_TABLE = "User";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_STUDENT_ID = "student_id";

    public static final String STUDENT_TABLE = "Student";
    public static final String COL_STUDENT_TABLE_ID = "student_id";

    // Expense table
    public static final String EXPENSE_TABLE = "Expense";
    public static final String COL_EXPENSE_ID = "expense_id";
    public static final String COL_EXPENSE_TITLE = "title";
    public static final String COL_EXPENSE_AMOUNT = "amount";
    public static final String COL_EXPENSE_CATEGORY = "category";
    public static final String COL_EXPENSE_DATE = "date";
    public static final String COL_EXPENSE_DESCRIPTION = "description";

    // Budget table
    public static final String BUDGET_TABLE = "Budget";
    public static final String COL_BUDGET_ID = "budget_id";
    public static final String COL_BUDGET_AMOUNT = "amount";
    public static final String COL_BUDGET_MONTH = "month";
    public static final String COL_BUDGET_YEAR = "year";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + USER_TABLE + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_STUDENT_ID + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_FULL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PHONE + " TEXT UNIQUE NOT NULL, " +
                COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createUserTable);

        db.execSQL("CREATE TABLE " + STUDENT_TABLE + " (" +
                COL_STUDENT_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COL_USER_ID + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE Admin (" +
                "admin_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COL_USER_ID + ") ON DELETE CASCADE)");

        // Create Expense table
        String createExpenseTable = "CREATE TABLE " + EXPENSE_TABLE + " (" +
                COL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_EXPENSE_TITLE + " TEXT NOT NULL, " +
                COL_EXPENSE_AMOUNT + " REAL NOT NULL, " +
                COL_EXPENSE_CATEGORY + " TEXT NOT NULL, " +
                COL_EXPENSE_DATE + " TEXT NOT NULL, " +
                COL_EXPENSE_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COL_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createExpenseTable);

        // Create Budget table
        String createBudgetTable = "CREATE TABLE " + BUDGET_TABLE + " (" +
                COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_BUDGET_AMOUNT + " REAL NOT NULL, " +
                COL_BUDGET_MONTH + " INTEGER NOT NULL, " +
                COL_BUDGET_YEAR + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + USER_TABLE + "(" + COL_USER_ID + ") ON DELETE CASCADE, " +
                "UNIQUE(" + COL_USER_ID + ", " + COL_BUDGET_MONTH + ", " + COL_BUDGET_YEAR + "))";
        db.execSQL(createBudgetTable);

        // Thêm dữ liệu mẫu để kiểm tra
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_USERNAME, "admin");
        adminValues.put(COL_PASSWORD, "admin123");
        adminValues.put(COL_FULL_NAME, "Administrator");
        adminValues.put(COL_EMAIL, "admin@example.com");
        adminValues.put(COL_PHONE, "+84912345678");
        long adminUserId = db.insert(USER_TABLE, null, adminValues);
        if (adminUserId != -1) {
            ContentValues adminRole = new ContentValues();
            adminRole.put(COL_USER_ID, adminUserId);
            db.insert("Admin", null, adminRole);
        }

        // Thêm sinh viên mẫu
        ContentValues testValues = new ContentValues();
        testValues.put(COL_USERNAME, "testuser");
        testValues.put(COL_STUDENT_ID, "ST001");
        testValues.put(COL_PASSWORD, "test123");
        testValues.put(COL_FULL_NAME, "Test User");
        testValues.put(COL_EMAIL, "test@example.com");
        testValues.put(COL_PHONE, "+84987654321");
        long testUserId = db.insert(USER_TABLE, null, testValues);
        if (testUserId != -1) {
            ContentValues studentValues = new ContentValues();
            studentValues.put(COL_USER_ID, testUserId);
            db.insert(STUDENT_TABLE, null, studentValues);

            // Add sample budget for test user
            ContentValues budgetValues = new ContentValues();
            budgetValues.put(COL_USER_ID, testUserId);
            budgetValues.put(COL_BUDGET_AMOUNT, 1000000.0); // 1 million VND
            budgetValues.put(COL_BUDGET_MONTH, 12);
            budgetValues.put(COL_BUDGET_YEAR, 2024);
            db.insert(BUDGET_TABLE, null, budgetValues);

            // Add sample expenses for test user
            String[] categories = {"Food", "Transport", "Books", "Entertainment", "Other"};
            String[] titles = {"Lunch", "Bus fare", "Textbook", "Movie", "Phone bill"};
            double[] amounts = {50000, 15000, 200000, 100000, 50000};

            for (int i = 0; i < 5; i++) {
                ContentValues expenseValues = new ContentValues();
                expenseValues.put(COL_USER_ID, testUserId);
                expenseValues.put(COL_EXPENSE_TITLE, titles[i]);
                expenseValues.put(COL_EXPENSE_AMOUNT, amounts[i]);
                expenseValues.put(COL_EXPENSE_CATEGORY, categories[i]);
                expenseValues.put(COL_EXPENSE_DATE, "2024-12-15");
                expenseValues.put(COL_EXPENSE_DESCRIPTION, "Sample expense " + (i + 1));
                db.insert(EXPENSE_TABLE, null, expenseValues);
            }
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Admin");
        db.execSQL("DROP TABLE IF EXISTS " + STUDENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BUDGET_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
        onCreate(db);
    }

    public String registerUser(String username, String password, String fullName, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        String conflictField = checkExistingCredentials(db, username, email, phone);
        if (conflictField != null) {
            return conflictField;
        }

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_FULL_NAME, fullName);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);

        try {
            long userId = db.insertOrThrow(USER_TABLE, null, values);
            return userId != -1 ? "success" : "error";
        } catch (Exception e) {
            return "error";
        }
    }

    public String addStudent(String studentId, String fullName, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        String username = "stu_" + System.currentTimeMillis();
        String conflictField = checkExistingCredentials(db, username, email, phone);
        if (conflictField != null) {
            return conflictField;
        }

        ContentValues userValues = new ContentValues();
        userValues.put(COL_USERNAME, username);
        userValues.put(COL_STUDENT_ID, studentId);
        userValues.put(COL_PASSWORD, "default123");
        userValues.put(COL_FULL_NAME, fullName);
        userValues.put(COL_EMAIL, email);
        userValues.put(COL_PHONE, phone);

        try {
            long userId = db.insertOrThrow(USER_TABLE, null, userValues);
            if (userId != -1) {
                ContentValues studentValues = new ContentValues();
                studentValues.put(COL_USER_ID, userId);
                long result = db.insert(STUDENT_TABLE, null, studentValues);
                return result != -1 ? "success" : "error";
            }
            return "error";
        } catch (Exception e) {
            return "error";
        }
    }

    public boolean updateStudent(String oldUsername, String newUsername, String newFullName, String newEmail, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!isFieldExists(db, COL_USERNAME, oldUsername)) {
            return false;
        }
        if (!newUsername.isEmpty() && isFieldExists(db, COL_USERNAME, newUsername) && !newUsername.equals(oldUsername)) {
            return false;
        }
        if (!newEmail.isEmpty() && isFieldExists(db, COL_EMAIL, newEmail) && !newEmail.equals(getFieldValue(db, COL_EMAIL, oldUsername))) {
            return false;
        }
        if (!newPhone.isEmpty() && isFieldExists(db, COL_PHONE, newPhone) && !newPhone.equals(getFieldValue(db, COL_PHONE, oldUsername))) {
            return false;
        }

        ContentValues values = new ContentValues();
        if (!newUsername.isEmpty()) values.put(COL_USERNAME, newUsername);
        if (!newFullName.isEmpty()) values.put(COL_FULL_NAME, newFullName);
        if (!newEmail.isEmpty()) values.put(COL_EMAIL, newEmail);
        if (!newPhone.isEmpty()) values.put(COL_PHONE, newPhone);

        try {
            int rows = db.update(USER_TABLE, values, COL_USERNAME + " = ?", new String[]{oldUsername});
            return rows > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteStudent(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rows = db.delete(USER_TABLE, COL_USERNAME + " = ?", new String[]{username});
            return rows > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u." + COL_STUDENT_ID + ", u." + COL_USERNAME + ", u." + COL_FULL_NAME + ", u." + COL_EMAIL + ", u." + COL_PHONE +
                " FROM " + USER_TABLE + " u INNER JOIN " + STUDENT_TABLE + " s ON u." + COL_USER_ID + " = s." + COL_USER_ID;
        return db.rawQuery(query, null);
    }

    private String getFieldValue(SQLiteDatabase db, String column, String username) {
        String query = "SELECT " + column + " FROM " + USER_TABLE + " WHERE " + COL_USERNAME + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, new String[]{username})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return "";
        }
    }

    private String checkExistingCredentials(SQLiteDatabase db, String username, String email, String phone) {
        if (isFieldExists(db, COL_USERNAME, username)) {
            return "username";
        }
        if (isFieldExists(db, COL_EMAIL, email)) {
            return "email";
        }
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

    public boolean isAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM " + USER_TABLE + " u INNER JOIN Admin a ON u." +
                COL_USER_ID + " = a.user_id WHERE u." + COL_USERNAME + " = ? LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, new String[]{username})) {
            return cursor.moveToFirst();
        }
    }
    public boolean setBudget(int userId, double amount, int month, int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_BUDGET_AMOUNT, amount);
        values.put(COL_BUDGET_MONTH, month);
        values.put(COL_BUDGET_YEAR, year);

        try {
            // Use INSERT OR REPLACE to handle unique constraint
            long result = db.insertWithOnConflict(BUDGET_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            return result != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public double getBudget(int userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_BUDGET_AMOUNT + " FROM " + BUDGET_TABLE +
                " WHERE " + COL_USER_ID + " = ? AND " + COL_BUDGET_MONTH + " = ? AND " + COL_BUDGET_YEAR + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(month), String.valueOf(year)})) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
            return 0.0;
        }
    }

    // Expense methods
    public boolean addExpense(int userId, String title, double amount, String category, String date, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_EXPENSE_TITLE, title);
        values.put(COL_EXPENSE_AMOUNT, amount);
        values.put(COL_EXPENSE_CATEGORY, category);
        values.put(COL_EXPENSE_DATE, date);
        values.put(COL_EXPENSE_DESCRIPTION, description);

        try {
            long result = db.insert(EXPENSE_TABLE, null, values);
            return result != -1;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor getExpenses(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXPENSE_TABLE + " WHERE " + COL_USER_ID + " = ? ORDER BY " + COL_EXPENSE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public Cursor getExpensesByCategory(int userId, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXPENSE_TABLE + " WHERE " + COL_USER_ID + " = ? AND " + COL_EXPENSE_CATEGORY + " = ? ORDER BY " + COL_EXPENSE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId), category});
    }

    public Cursor getExpensesByDateRange(int userId, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXPENSE_TABLE + " WHERE " + COL_USER_ID + " = ? AND " + COL_EXPENSE_DATE + " BETWEEN ? AND ? ORDER BY " + COL_EXPENSE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
    }

    public double getTotalExpenses(int userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + EXPENSE_TABLE +
                " WHERE " + COL_USER_ID + " = ? AND strftime('%m', " + COL_EXPENSE_DATE + ") = ? AND strftime('%Y', " + COL_EXPENSE_DATE + ") = ?";
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), monthStr, yearStr})) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
            return 0.0;
        }
    }

    public double getTotalExpensesByCategory(int userId, String category, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + EXPENSE_TABLE +
                " WHERE " + COL_USER_ID + " = ? AND " + COL_EXPENSE_CATEGORY + " = ? AND " +
                "strftime('%m', " + COL_EXPENSE_DATE + ") = ? AND strftime('%Y', " + COL_EXPENSE_DATE + ") = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), category,
                String.format("%02d", month), String.valueOf(year)
        });

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public Cursor getExpensesByCategorySummary(int userId, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_EXPENSE_CATEGORY + ", SUM(" + COL_EXPENSE_AMOUNT + ") as total_amount, COUNT(*) as count " +
                "FROM " + EXPENSE_TABLE +
                " WHERE " + COL_USER_ID + " = ? AND strftime('%m', " + COL_EXPENSE_DATE + ") = ? AND strftime('%Y', " + COL_EXPENSE_DATE + ") = ? " +
                "GROUP BY " + COL_EXPENSE_CATEGORY + " ORDER BY total_amount DESC";
        String monthStr = String.format("%02d", month);
        String yearStr = String.valueOf(year);
        return db.rawQuery(query, new String[]{String.valueOf(userId), monthStr, yearStr});
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_USER_ID + " FROM " + USER_TABLE + " WHERE " + COL_USERNAME + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{username})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return -1;
        }
    }
}