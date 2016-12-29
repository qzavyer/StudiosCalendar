package com.andr.qzavyer.studioscalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayActivity extends AppCompatActivity {
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,                            // 0
            CalendarContract.Events.STATUS,                         // 1
            CalendarContract.Events.DTSTART,                        // 2
            CalendarContract.Events.DTEND                           // 3
    };

    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_STATUS = 1;
    private static final int PROJECTION_DTSTART = 2;
    private static final int PROJECTION_DTEND = 3;
    private static final int REQUEST_LOCATION = 2;

    LinearLayout dayMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        String calSel = "(" + CalendarContract.Calendars.NAME + " = ?)";
        DBHelper dbHelper = new DBHelper(this);
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> addresses = new HashMap<>();
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы studios, получаем Cursor
        Cursor c = db.query("studios", null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("calendar");
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                addresses.put(c.getInt(idColIndex), c.getString(nameColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то выходим из цикла
            } while (c.moveToNext());
        }
        c.close();

        for(Map.Entry<Integer, String> entry : addresses.entrySet()) {
            String[] calArgs = new String[]{entry.getValue()};
            String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND (" +
                    CalendarContract.Events.ACCOUNT_NAME + " = ?))";
            // Проверка разрешения чтения календаря, если нет, запрашиваем разрешение
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        REQUEST_LOCATION);
                return;
            }
            try {
                // отправка запроса на получение календаря
                @SuppressLint("Recycle")
                Cursor calCur = cr.query(calUri, CALENDAR_PROJECTION, calSel, calArgs, null);
                long calID = 0;
                if (calCur != null) {
                    while (calCur.moveToNext()) {
                        // Получаем ИД календаря
                        calID = calCur.getLong(PROJECTION_ID_INDEX);
                    }
                }
                String[] selectionArgs = new String[]{Long.toString(calID), entry.getValue()};
                @SuppressLint("Recycle")
                Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
                if (cur != null) {
                    while (cur.moveToNext()) {
                        long eidName = 0;
                        String endName = null;
                        String statusName = null;
                        String startName = null;
                        // Get the field values
                        eidName = cur.getLong(PROJECTION_ID_INDEX);
                        statusName = cur.getString(PROJECTION_STATUS);
                        startName = cur.getString(PROJECTION_DTSTART);
                        endName = cur.getString(PROJECTION_DTEND);
                    }
                }
            } catch (Exception e) {
                String msg = e.getMessage();
            }
        }
        dayMain = (LinearLayout)findViewById(R.id.dayMain);
        Date today = new Date();
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

        for(String point : data){
            TextView headView = new TextView(this);
            headView.setText(point);
            dayMain.addView(headView);
        }
    }
}
