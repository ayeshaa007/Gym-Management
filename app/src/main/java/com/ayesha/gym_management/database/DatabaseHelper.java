package com.gymmanagement.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gymmanagement.app.models.Member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "GymManagement.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table name
    private static final String TABLE_MEMBERS = "members";
    
    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FULL_NAME = "fullName";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_JOIN_DATE = "joinDate";
    private static final String COLUMN_PLAN = "plan";
    private static final String COLUMN_FEE_AMOUNT = "feeAmount";
    private static final String COLUMN_PAYMENT_STATUS = "paymentStatus";
    private static final String COLUMN_EXPIRY_DATE = "expiryDate";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + TABLE_MEMBERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_JOIN_DATE + " TEXT,"
                + COLUMN_PLAN + " TEXT,"
                + COLUMN_FEE_AMOUNT + " REAL,"
                + COLUMN_PAYMENT_STATUS + " TEXT,"
                + COLUMN_EXPIRY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_MEMBERS_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        onCreate(db);
    }
    
    // Add a new member
    public long addMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_FULL_NAME, member.getFullName());
        values.put(COLUMN_PHONE, member.getPhone());
        values.put(COLUMN_JOIN_DATE, member.getJoinDate());
        values.put(COLUMN_PLAN, member.getPlan());
        values.put(COLUMN_FEE_AMOUNT, member.getFeeAmount());
        values.put(COLUMN_PAYMENT_STATUS, member.getPaymentStatus());
        values.put(COLUMN_EXPIRY_DATE, member.getExpiryDate());
        
        long id = db.insert(TABLE_MEMBERS, null, values);
        db.close();
        return id;
    }
    
    // Get a single member
    public Member getMember(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MEMBERS,
                new String[]{COLUMN_ID, COLUMN_FULL_NAME, COLUMN_PHONE, COLUMN_JOIN_DATE,
                        COLUMN_PLAN, COLUMN_FEE_AMOUNT, COLUMN_PAYMENT_STATUS, COLUMN_EXPIRY_DATE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        
        Member member = null;
        if (cursor != null && cursor.moveToFirst()) {
            member = new Member(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            cursor.close();
        }
        
        db.close();
        return member;
    }
    
    // Get all members
    public List<Member> getAllMembers() {
        List<Member> memberList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEMBERS + " ORDER BY " + COLUMN_ID + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Member member = new Member(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                memberList.add(member);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return memberList;
    }
    
    // Update member
    public int updateMember(Member member) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_FULL_NAME, member.getFullName());
        values.put(COLUMN_PHONE, member.getPhone());
        values.put(COLUMN_JOIN_DATE, member.getJoinDate());
        values.put(COLUMN_PLAN, member.getPlan());
        values.put(COLUMN_FEE_AMOUNT, member.getFeeAmount());
        values.put(COLUMN_PAYMENT_STATUS, member.getPaymentStatus());
        values.put(COLUMN_EXPIRY_DATE, member.getExpiryDate());
        
        int rowsAffected = db.update(TABLE_MEMBERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(member.getId())});
        db.close();
        return rowsAffected;
    }
    
    // Delete member
    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMBERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    // Get member count
    public int getMemberCount() {
        String countQuery = "SELECT * FROM " + TABLE_MEMBERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    
    // Calculate expiry date based on plan
    public static String calculateExpiryDate(String joinDate, String plan) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(joinDate));
            
            switch (plan) {
                case "1 Month":
                    calendar.add(Calendar.MONTH, 1);
                    break;
                case "3 Months":
                    calendar.add(Calendar.MONTH, 3);
                    break;
                case "6 Months":
                    calendar.add(Calendar.MONTH, 6);
                    break;
            }
            
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return joinDate;
        }
    }
    
    // Get total revenue
    public double getTotalRevenue() {
        double total = 0;
        String query = "SELECT " + COLUMN_FEE_AMOUNT + " FROM " + TABLE_MEMBERS 
                + " WHERE " + COLUMN_PAYMENT_STATUS + " = 'Paid'";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                total += cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return total;
    }
}
