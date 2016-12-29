package com.andr.qzavyer.studioscalendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DayActivity extends AppCompatActivity {
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,                            // 0
            CalendarContract.Events.ACCOUNT_NAME,                   // 1
            CalendarContract.Events.CALENDAR_DISPLAY_NAME,          // 2
            CalendarContract.Events.OWNER_ACCOUNT,                  // 3
            CalendarContract.Events.STATUS,                         // 4
            CalendarContract.Events.DTSTART,                        // 5
            CalendarContract.Events.CALENDAR_ID,                    // 6
    };

    public static final String[] CAL_PROJ = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_STATUS = 4;
    private static final int PROJECTION_DTSTART = 5;
    private static final int PROJECTION_CALENDARID = 6;
    private static final int REQUEST_LOCATION = 2;

    LinearLayout dayMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        String calSel = "((" +
                CalendarContract.Calendars.NAME + " = ?"+
                "))";
        String[] calArgs = new String[]{"<account>@gmail.com"};
        String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND (" +
                CalendarContract.Events.ACCOUNT_NAME + " = ?)"+
                ")";
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    REQUEST_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try{
            Cursor calCur = cr.query(calUri, CAL_PROJ, calSel, calArgs, null);
            long calID = 0;
            while (calCur.moveToNext()) {

                String name;

                // Get the field values
                calID = calCur.getLong(PROJECTION_ID_INDEX);
                name = calCur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                long id = calID/2;
            }
            String[] selectionArgs = new String[]{Long.toString(calID),"<account>@gmail.com"};
            Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            while (cur.moveToNext()) {
                String displayName = null;
                String accountName = null;
                String ownerName = null;
                String statusName = null;
                String startName = null;

                // Get the field values
                accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                statusName = cur.getString(PROJECTION_STATUS);
                startName = cur.getString(PROJECTION_DTSTART);
                displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            }
        }catch (Exception e){
            String msg = e.getMessage();
        }
        dayMain = (LinearLayout)findViewById(R.id.dayMain);
        String[] data = {
                "00:00 - 01:00",
                "01:00 - 02:00",
                "02:00 - 03:00",
                "03:00 - 04:00",
                "04:00 - 05:00",
                "05:00 - 06:00",
                "06:00 - 07:00",
                "07:00 - 08:00",
                "08:00 - 09:00",
                "09:00 - 10:00",
                "10:00 - 11:00",
                "11:00 - 12:00",
                "12:00 - 13:00",
                "13:00 - 14:00",
                "14:00 - 15:00",
                "15:00 - 16:00",
                "16:00 - 17:00",
                "17:00 - 18:00",
                "18:00 - 19:00",
                "19:00 - 20:00",
                "20:00 - 21:00",
                "21:00 - 22:00",
                "22:00 - 23:00",
                "23:00 - 00:00"};

        LinearLayout item = new LinearLayout(this);
        for(String point : data){
            TextView headView = new TextView(this);
            headView.setText(point);
            dayMain.addView(headView);
        }

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.gridcell, R.id.tvText, data);
        daysGrid.setAdapter(adapter);*/
        adjustGridView();



    }
    private void adjustGridView() {
        /*daysGrid.setNumColumns(2);
        daysGrid.setColumnWidth(100);*/
    }
}
