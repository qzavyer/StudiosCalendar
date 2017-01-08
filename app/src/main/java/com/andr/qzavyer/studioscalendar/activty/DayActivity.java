package com.andr.qzavyer.studioscalendar.activty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.andr.qzavyer.studioscalendar.R;
import com.andr.qzavyer.studioscalendar.database.DBHelper;
import com.andr.qzavyer.studioscalendar.viewextention.HorizontalScrollViewListener;
import com.andr.qzavyer.studioscalendar.viewextention.ObservableHorizontalScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayActivity extends AppCompatActivity implements HorizontalScrollViewListener {
    public final String LOG_TAG = "DayActivity";

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

    private static final int PROJECTION_ID_INDEX = 0;
    //private static final int PROJECTION_STATUS = 1;
    private static final int PROJECTION_DTSTART = 2;
    private static final int PROJECTION_DTEND = 3;
    private static final int REQUEST_LOCATION = 2;

    private TableLayout frozenHeaderTable;
    private TableLayout contentHeaderTable;
    private TableLayout frozenTable;
    private TableLayout contentTable;
    ObservableHorizontalScrollView headerScrollView;
    ObservableHorizontalScrollView contentScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        frozenTable = (TableLayout)findViewById(R.id.frozenTable);
        contentTable = (TableLayout)findViewById(R.id.contentTable);
        frozenHeaderTable = (TableLayout)findViewById(R.id.frozenTableHeader);
        contentHeaderTable = (TableLayout)findViewById(R.id.contentTableHeader);
        headerScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHeaderHorizontalScrollView);
        headerScrollView.setScrollViewListener(this);
        contentScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHorizontalScrollView);
        contentScrollView.setScrollViewListener(this);
        contentScrollView.setHorizontalScrollBarEnabled(false);
        InitializeInitialData();
    }

    @SuppressLint("DefaultLocale")
    protected void InitializeInitialData() {
        ArrayList<String[]> content = new ArrayList<>();
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;
            String calSel = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?)" +
                    " AND (" + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?)" +
                    ")";
            DBHelper dbHelper = new DBHelper(this);
            @SuppressLint("UseSparseArrays")
            Map<Integer, String> addresses = new HashMap<>();
            @SuppressLint("UseSparseArrays")
            Map<Integer, String> names = new HashMap<>();
            // подключаемся к БД
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // делаем запрос всех данных из таблицы studios, получаем Cursor
            Cursor c = db.query("studios", null, null, null, null, null, null);

            // ставим позицию курсора на первую строку выборки
            if (c.moveToFirst()) {
                // определяем номера столбцов по имени в выборке
                int nameColIndex = c.getColumnIndex("calendar");
                int titleColIndex = c.getColumnIndex("name");
                int index = 0;
                do {
                    addresses.put(index, c.getString(nameColIndex));
                    names.put(index, c.getString(titleColIndex));
                    index++;
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то выходим из цикла
                } while (c.moveToNext());
            }
            c.close();

            // матрица занятости (для каждой студии для каждого часа
            Boolean[][] busy = new Boolean[addresses.size()][24];
            for (int i = 0; i < addresses.size(); i++) {
                for (int j = 0; j < 24; j++) {
                    busy[i][j] = false;
                }
            }
            for (Map.Entry<Integer, String> entry : addresses.entrySet()) {
                String[] calArgs = new String[]{entry.getValue(), "LOCAL"
                        , entry.getValue()
                };
                String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND (" +
                        CalendarContract.Events.ACCOUNT_NAME + " = ?) AND (" +
                        CalendarContract.Events.ACCOUNT_TYPE + " = ?" +
                        ") AND (" + CalendarContract.Events.DTSTART + " >= ?) AND (" + CalendarContract.Events.DTEND + " < ?)" +
                        ")";
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
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateb = f.parse("2017-01-08");
                    Date datee = f.parse("2017-01-09");
                    String[] selectionArgs = new String[]{
                            Long.toString(calID), entry.getValue(), "com.google",
                            Long.toString(dateb.getTime()), Long.toString(datee.getTime())
                    };
                    // отправка запроса на получение событий календаря
                    @SuppressLint("Recycle")
                    Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
                    if (cur != null) {
                        while (cur.moveToNext()) {
                            Long startName = cur.getLong(PROJECTION_DTSTART);
                            Long endName = cur.getLong(PROJECTION_DTEND);
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(startName);
                            int startHour = cal.get(Calendar.HOUR_OF_DAY);
                            cal.setTimeInMillis(endName);
                            int endHour = cal.get(Calendar.HOUR_OF_DAY);
                            int endMin = cal.get(Calendar.MINUTE);
                            if (endMin > 0) {
                                endHour++;
                            }
                            if (endHour < startHour || endHour > 23) {
                                endHour = 23;
                            }
                            for (int i = startHour; i <= endHour; i++) {
                                busy[entry.getKey()][i] = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }

            for (Integer i = -1; i < 24; i++) {
                ArrayList<String> strings = new ArrayList<>();
                if (i == -1) {
                    strings.add("Время");
                } else {
                    strings.add(String.format("%02d", i) + ":00 - " + String.format("%02d", i + 1) + ":00");
                }
                for (Map.Entry<Integer, String> entry : names.entrySet()) {
                    if (i == -1) {
                        strings.add(entry.getValue());
                    } else {
                        strings.add(busy[entry.getKey()][i] ? "+" : " ");
                    }
                }
                content.add(strings.toArray(new String[0]));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        PopulateMainTable(content);
    }

    protected void PopulateMainTable(ArrayList<String[]> content) {

        TableLayout.LayoutParams frozenRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        frozenRowParams.setMargins(1, 1, 1, 1);
        frozenRowParams.weight=1;
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 1, 1, 1);
        tableRowParams.weight=1;

        TableRow frozenTableHeaderRow=null;
        TableRow contentTableHeaderRow=null;
        int maxFrozenChars = 0;
        int[] maxContentChars = new int[content.get(0).length-1];

        for (int i = 0; i < content.size(); i++){
            TableRow frozenRow = new TableRow(this);
            frozenRow.setLayoutParams(frozenRowParams);
            TextView frozenCell = new TextView(this);
            frozenCell.setText(content.get(i)[0]);
            frozenCell.setTextColor(Color.parseColor("#FF000000"));
            frozenCell.setPadding(5, 0, 5, 0);
            frozenRow.addView(frozenCell);
            if (content.get(i)[0].length() > maxFrozenChars) {
                maxFrozenChars = content.get(i)[0].length();
            }

            // The rest of them
            TableRow row = new TableRow(this);
            row.setLayoutParams(tableRowParams);
            for (int j = 1; j < content.get(0).length; j++) {
                TextView rowCell = new TextView(this);
                rowCell.setText(content.get(i)[j]);
                rowCell.setPadding(10, 0, 0, 0);
                rowCell.setGravity(Gravity.END);
                rowCell.setTextColor(Color.parseColor("#FF000000"));
                row.addView(rowCell);
                if (content.get(i)[j]!=null&&content.get(i)[j].length() > maxContentChars[j-1]) {
                    maxContentChars[j-1] = content.get(i)[j].length();
                }
            }

            if (i==0) {
                frozenTableHeaderRow=frozenRow;
                contentTableHeaderRow=row;
                frozenHeaderTable.addView(frozenRow);
                contentHeaderTable.addView(row);
            } else {
                frozenTable.addView(frozenRow);
                contentTable.addView(row);
            }
        }

        setChildTextViewWidths(frozenTableHeaderRow, new int[]{maxFrozenChars});
        setChildTextViewWidths(contentTableHeaderRow, maxContentChars);
        for (int i = 0; i < contentTable.getChildCount(); i++) {
            TableRow frozenRow = (TableRow) frozenTable.getChildAt(i);
            setChildTextViewWidths(frozenRow, new int[]{maxFrozenChars});
            TableRow row = (TableRow) contentTable.getChildAt(i);
            setChildTextViewWidths(row, maxContentChars);
        }
    }

    private void setChildTextViewWidths(TableRow row, int[] widths) {
        if (null == row) {
            return;
        }
        int cellWidthFactor = 10;
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView cell = (TextView) row.getChildAt(i);
            int replacementWidth =
                    widths[i] == 1
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 2)
                            : widths[i] < 3
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 1.7)
                            : widths[i] < 5
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 1.2)
                            : widths[i] * cellWidthFactor;
            cell.setMinimumWidth(replacementWidth);
            //cell.setMaxWidth(replacementWidth);
        }
    }


    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (scrollView==headerScrollView) {
            contentScrollView.scrollTo(x, y);
        } else if (scrollView==contentScrollView) {
            headerScrollView.scrollTo(x, y);
        }
    }
}
