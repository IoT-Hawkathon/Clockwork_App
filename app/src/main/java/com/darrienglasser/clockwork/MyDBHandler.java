package com.darrienglasser.clockwork;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    /** SQLite Database version. */
    private static final int DATABASE_VERSION = 1;

    /** File name of database. */
    private static final String DATABASE_NAME = "group_list.db";

    /** Name of table. */
    public static final String TABLE_PRODUCTS = "alarms";

    /** Column ID.  */
    private static final String COLUMN_ID = "_id";

    /** Column hour. */
    private static final String COLUMN_HOUR = "alarmHour";

    /** Column number of groups. */
    private static final String COLUMN_MINUTE = "alarmMinute";

    /** Column number of subgroups. */
    private static final String COLUMN_TOGGLE = "alarmToggle";

    public MyDBHandler(
            Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_HOUR + " TEXT, "
                + COLUMN_MINUTE + " TEXT, "
                + COLUMN_TOGGLE + " TEXT"
                + ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    /** Add new row to database. */
    public void addData(AlarmData data) {
        // TODO: Set groupData value to COLUMN_ID, and actually use it

        ContentValues values = new ContentValues();
        values.put(COLUMN_HOUR, data.getHour());
        values.put(COLUMN_MINUTE, data.getMinute());
        values.put(COLUMN_TOGGLE, data.isToggled());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    /** Delete row from database. */
    public void deleteProduct(String groupID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(
                "DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_ID + "=\"" + groupID + "\";");
    }

    /**
     * Destroys database entirely.
     */
    public void destroy() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PRODUCTS, null, null);
    }

    public ArrayList<AlarmData> dbToGroupList() {
        ArrayList<AlarmData> dataList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String nameQuery = "SELECT " + COLUMN_HOUR + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        String numGroupsQuery =
                "SELECT " + COLUMN_MINUTE + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        String numSGroupsQuery =
                "SELECT " + COLUMN_TOGGLE + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor cHour = db.rawQuery(nameQuery, null);
        Cursor cMinute = db.rawQuery(numGroupsQuery, null);
        Cursor cToggle = db.rawQuery(numSGroupsQuery, null);

        cHour.moveToFirst();
        cMinute.moveToFirst();
        cToggle.moveToFirst();

        // All values must be synchronized, because they are written as an object, so we only need
        // to check one.
        while (!cHour.isAfterLast()) {
            if (cHour.getString(cHour.getColumnIndex(COLUMN_HOUR)) != null) {
                AlarmData tmpData = new AlarmData(
                        cHour.getInt(cHour.getColumnIndex(COLUMN_HOUR)),
                        cMinute.getInt(cMinute.getColumnIndex(COLUMN_MINUTE)),
                        Boolean.parseBoolean(
                                cToggle.getString(cToggle.getColumnIndex(COLUMN_TOGGLE))));

                dataList.add(tmpData);
            }
            cHour.moveToNext();
            cMinute.moveToNext();
            cToggle.moveToNext();
        }

        cHour.close();
        cMinute.close();
        cToggle.close();
        return dataList;
    }

    /**
     * Returns the last item in the database.
     *
     * @return Last item in the database.
     */
    public AlarmData getLastData() {
        ArrayList<AlarmData> dataList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();

        String nameQuery = "SELECT " + COLUMN_HOUR + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        String numGroupsQuery =
                "SELECT " + COLUMN_MINUTE + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        String numSGroupsQuery =
                "SELECT " + COLUMN_TOGGLE + " FROM " + TABLE_PRODUCTS + " WHERE 1";

        Cursor cHour = db.rawQuery(nameQuery, null);
        Cursor cMinute = db.rawQuery(numGroupsQuery, null);
        Cursor cToggle = db.rawQuery(numSGroupsQuery, null);

        cHour.moveToLast();
        cMinute.moveToLast();
        cToggle.moveToLast();

        AlarmData data = new AlarmData(cHour.getInt(cHour.getColumnIndex(COLUMN_HOUR)),
                cMinute.getInt(cMinute.getColumnIndex(COLUMN_MINUTE)),
                Boolean.parseBoolean(
                        cToggle.getString(cToggle.getColumnIndex(COLUMN_TOGGLE))));

        cHour.close();
        cMinute.close();
        cToggle.close();
        return data;
    }
}