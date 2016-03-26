package com.darrienglasser.clockwork;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

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
     * Extras
     */
    private String apikey = "your-apikey-here";
    private String url_ocrdocument = "https://api.idolondemand.com/1/api/sync/ocrdocument/v1";
    private String filesrc="images/plos_SemanticParticularityMeasure.jpg";


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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent AwsIntent = new Intent(MainActivity.this, easyAWS.class);
                startService(AwsIntent);
            }
        });

        t.run();

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
                final android.os.Handler handler = new android.os.Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(
                                        getApplicationContext(), AlarmTriggeredActivity.class);
                                startActivity(intent);
                            }
                        }, 10000);
                    }
                };
                runnable.run();
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
                        AlarmData data = new AlarmData(hourOfDay, minute, true);
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
//
//    public void post1(){
//
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//
//        try {
//            HttpPost httppost = new HttpPost(url_ocrdocument);
//
//            File f = new File(fileSrc);
//            FileBody fileBody = new FileBody(f);
//            StringBody apikeyStringBody = new StringBody(apikey, ContentType.TEXT_PLAIN);
//
//            HttpEntity reqEntity = MultipartEntityBuilder.create()
//                    .addPart("file", fileBody)
//                    .addPart("apikey", apikeyStringBody)
//                    .build();
//
//            httppost.setEntity(reqEntity);
//
//            CloseableHttpResponse response = null;
//
//            try {
//                response = httpclient.execute(httppost);
//
//                System.out.println(response.getStatusLine());
//                HttpEntity entity = response.getEntity();
//                if (entity != null) {
//                    entity.writeTo(System.out);
//                }
//
//            }catch(ClientProtocolException cpe){
//                cpe.printStackTrace();
//            }catch(IOException ioe){
//                ioe.printStackTrace();
//            } finally {
//                try {
//                    response.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } finally {
//            try {
//                httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
