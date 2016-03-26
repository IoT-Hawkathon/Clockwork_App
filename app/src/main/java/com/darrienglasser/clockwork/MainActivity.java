package com.darrienglasser.clockwork;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    /**
     * Database to hold alarm data.
     */
    private MyDBHandler mDatabase;

    /**
     * Textview holding makeshift action bar.
     */
    private TextView mAlarmsActiveText;

    /**
     * Grid encasing our data.
     */
    private GridView mGridView;

    /**
     * Textview displayed when there are no alarms on screen.
     */
    private TextView mAlarmsText;

    /**
     * Static context.
     */
    private static Context sContext;

    /**
     * Data used to update list, and semi-dynamically update views.
     *
     * Humorously named after the data sync issues it creates.
     */
    private AlarmData mDataSyncIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = new MyDBHandler(getApplicationContext(), null, null, 1);

        mAlarmsText = (TextView) findViewById(R.id.no_alarms_text);

        mAlarmsActiveText = (TextView) findViewById(R.id.alarms_header_text);

        sContext = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab == null) return;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePicker();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkDbExistence()) {
            mGridView = (GridView) findViewById(R.id.alarm_list);
            mGridView.setAdapter(new AlarmAdapter(mDatabase.dbToGroupList(), sContext, mDatabase));
            mAlarmsActiveText.setVisibility(View.VISIBLE);
            mAlarmsText.setVisibility(View.GONE);
        } else {
            if (mGridView != null) {
                mGridView.setVisibility(View.GONE);
                mAlarmsActiveText.setVisibility(View.GONE);
                mAlarmsText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Factory method. Pushes a timePickerDialog to the screen for data to be saved.
     */
    private void startTimePicker() {
        Calendar now = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        AlarmData data = new AlarmData(hourOfDay, minute, false);
                        if (data.getHour() == 0) {
                            data.setHour(12);
                        }
                        mDatabase.addData(data);
                        mDataSyncIssue = data;
                    }
                }, now.get(Calendar.HOUR), now.get(Calendar.MINUTE), false);
        timePickerDialog.show();

        Timer myTimer = new Timer();

        // Hell is real. 
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGridView == null || mDataSyncIssue == null) return;
                        updateViews(mDataSyncIssue);
                        mDataSyncIssue = null;
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Helper method. Determines if the database file exists.
     *
     * @return Whether or not database file exists.
     */
    private static boolean checkDbExistence() {
        // TODO: Avoid hardcoding in files names
        File dbFile = sContext.getDatabasePath("group_list.db");
        return dbFile.exists();
    }

    /**
     * Helper method that updates views.
     */
    public void updateViews(AlarmData data) {
        mGridView.invalidateViews();
        ((AlarmAdapter) (mGridView.getAdapter())).updateData(data);
    }
}
