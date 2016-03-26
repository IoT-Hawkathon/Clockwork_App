package com.darrienglasser.clockwork;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract.Calendars;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/**
 * Activity shown when an activity is triggered.
 */
public class AlarmTriggeredActivity extends AppCompatActivity {

    int mReqCode;

    final private static String redditKey = "TIL_OUTPUT.txt";

    /**
     * Projection array. Creating indices for this array instead of doing
     * dynamic lookups improves performance.
     */
    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_EVENT_DESCRIPTION_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    @Override
    public void onCreate(Bundle saveStateInstance) {
        super.onCreate(saveStateInstance);
        setContentView(R.layout.activity_alarm_triggered);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR}, mReqCode);
        }

        CalendarInfo cInfo1 = new CalendarInfo("Go home victorious", "7:15", "7:30");
        CalendarInfo cInfo2 = new CalendarInfo("Sleep", "7:30", "∞");

        TextView event1 = (TextView) findViewById(R.id.EventName);
        TextView event2 = (TextView) findViewById(R.id.SecondEventName);

        TextView start1 = (TextView) findViewById(R.id.EventTimeStart);
        TextView start2 = (TextView) findViewById(R.id.SecondEventTimeStart);

        TextView end1 = (TextView) findViewById(R.id.EventTimeEnd);
        TextView end2 = (TextView) findViewById(R.id.SecondEventTimeEnd);

        try {
            event1.setText(cInfo1.getEventName());
            event2.setText(cInfo2.getEventName());

            start1.setText(cInfo1.getStartTime());
            start2.setText(cInfo2.getStartTime());

            end1.setText(cInfo1.getEndTime());
            end2.setText(cInfo2.getEndTime());

            TextView factLine = (TextView) findViewById(R.id.fact_line);
            factLine.setText(getLine("/Download/" + redditKey));
        } catch (NullPointerException e) {
            Log.d("DGl", "Do nothing");
        }



//        Calendar cal = Calendar.getInstance();
//
//
//        Cursor cur;
//        ContentResolver cr = getContentResolver();
//        Uri uri = Calendars.CONTENT_URI;
//        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                + Calendars.OWNER_ACCOUNT + " = ?))";
//        String[] selectionArgs = new String[] {"sampleuser@gmail.com", "com.google",
//                "sampleuser@gmail.com"};
//        // Submit the query and get a Cursor object back.
//        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
//
//        if (cur == null) return;
//
//        // Use the cursor to step through the returned records
//        while (cur.moveToNext()) {
//            long calID = 0;
//            String displayName = null;
//            String accountName = null;
//            String ownerName = null;
//
//            // Get the field values
//            calID = cur.getLong(PROJECTION_ID_INDEX);
//            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
//            accountName = cur.getString(PROJECTION_EVENT_DESCRIPTION_INDEX);
//            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
//
//
//        }
//
//        cur.close();
//

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do nothing. We got the permission! Yay compliant users!
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "We can't run without your permission :(",
                    Toast.LENGTH_LONG).show();

            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        }
    }

    private String getLine(String name) {

        File file = new File(name);
        String result = null;
        Random rand = new Random();
        int n = 0;

        try {
            for (Scanner sc = new Scanner(file); sc.hasNext(); ) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) {
                    result = line;
                }
            }
        } catch (FileNotFoundException e) {
            return "No data found.";
        }
        Log.wtf("DGl", result, new RuntimeException("Hello world"));
        return result;
    }
}

