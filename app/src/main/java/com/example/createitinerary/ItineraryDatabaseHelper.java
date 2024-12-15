package com.example.createitinerary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItineraryDatabaseHelper extends SQLiteOpenHelper {

    // Database configuration
    private static final String DATABASE_NAME = "itinerary.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_ITINERARY = "itinerary";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TRIP_NAME = "trip_name";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    // SQL to create the table
    private static final String CREATE_TABLE_ITINERARY =
            "CREATE TABLE " + TABLE_ITINERARY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TRIP_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESTINATION + " TEXT NOT NULL, " +
                    COLUMN_START_DATE + " TEXT NOT NULL, " +
                    COLUMN_END_DATE + " TEXT NOT NULL);";

    public ItineraryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITINERARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITINERARY);
        onCreate(db);
    }

    // Insert a new itinerary
    public long insertItinerary(String tripName, String destination, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_NAME, tripName);
        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);

        long result = db.insert(TABLE_ITINERARY, null, values);
        db.close();
        return result;
    }

    // Retrieve all itineraries
    public Cursor getAllItineraries() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_ITINERARY,
                null, // Select all columns
                null, // No WHERE clause
                null, // No arguments
                null, // No groupBy
                null, // No having
                COLUMN_ID + " DESC" // Order by ID descending
        );
    }

    // Update an existing itinerary
    public int updateItinerary(int id, String tripName, String destination, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_NAME, tripName);
        values.put(COLUMN_DESTINATION, destination);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);

        int rowsAffected = db.update(
                TABLE_ITINERARY,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsAffected;
    }

    // Delete an itinerary
    public int deleteItinerary(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_ITINERARY,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsDeleted;
    }
}
