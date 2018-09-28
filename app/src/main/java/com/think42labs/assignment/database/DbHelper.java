package com.think42labs.assignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.think42labs.assignment.model.User;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USER = "user";

    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_AGE = "user_age";
    private static final String COLUMN_PHONE_NUMBER = "user_phonenumber";
    private static final String COLUMN_AADHAR_NO = "user_aadhar";
    private static final String COLUMN_ADDRESS = "user_address";
    private static final String COLUMN_IMAGE = "user_image";

    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_AGE + " INTEGER,"
            + COLUMN_PHONE_NUMBER + " INTEGER," + COLUMN_AADHAR_NO + " INTEGER,"
            + COLUMN_ADDRESS + " TEXT," + COLUMN_IMAGE + " TEXT" + " )";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);

    }

    // create user record

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_AGE, user.getAge());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNo());
        values.put(COLUMN_AADHAR_NO, user.getAadhaarNo());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_IMAGE, user.getImagePath());
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    //fetch all user records
    public List<User> getallrecords() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_NAME,
                COLUMN_AGE,
                COLUMN_PHONE_NUMBER,
                COLUMN_AADHAR_NO,
                COLUMN_ADDRESS,
                COLUMN_IMAGE
        };

        String sortOrder = COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, columns, null, null, null, null, sortOrder);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
                User user = new User(email, null);
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setAge(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_AGE))));
                user.setPhoneNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER))));
                user.setAadhaarNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_AADHAR_NO))));
                user.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
                user.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));

                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    //update user
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_AGE, user.getAge());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNo());
        values.put(COLUMN_AADHAR_NO, user.getAadhaarNo());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_IMAGE, user.getImagePath());

        db.update(TABLE_USER, values, COLUMN_USER_EMAIL + " = ?", new String[]{String.valueOf(user.getEmail())});
        db.close();
    }

    //delete record
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_EMAIL + " = ?", new String[]{String.valueOf(user.getEmail())});
        db.close();
    }

    public boolean isEmailExistAlready(String email) {
        String[] columns = {COLUMN_USER_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    //method to check username and password

    public boolean login(String email, String password) {
        String[] columns = {COLUMN_USER_EMAIL};
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_NAME,
                COLUMN_AGE,
                COLUMN_PHONE_NUMBER,
                COLUMN_AADHAR_NO,
                COLUMN_ADDRESS,
                COLUMN_IMAGE
        };

        Cursor cursor =
                db.query(TABLE_USER, columns,
                        "user_email=?",
                        new String[]{username}, null, null, null, null);
        User user = null;
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL));
            user = new User(email, null);
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            user.setAge(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_AGE))));
            user.setPhoneNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER))));
            user.setAadhaarNo(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_AADHAR_NO))));
            user.setAddress(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)));
            user.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
        }
        cursor.close();
        return user;
    }
}
