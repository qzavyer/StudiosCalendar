package com.andr.qzavyer.studioscalendar.activty;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.andr.qzavyer.studioscalendar.database.DBHelper;
import com.andr.qzavyer.studioscalendar.R;

public class AddActivity extends AppCompatActivity {

    final String LOG_TAG = "AddActivity";

    Button btOk;
    Button btCancel;
    EditText nameEdit;
    EditText addressEdit;
    EditText phoneEdit;
    EditText emailEdit;
    EditText webEdit;
    EditText calendarEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        final Context context = this;
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        addressEdit = (EditText) findViewById(R.id.addressEdit);
        phoneEdit = (EditText) findViewById(R.id.phoneEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        webEdit = (EditText) findViewById(R.id.webEdit);
        calendarEdit = (EditText) findViewById(R.id.calendarEdit);
        btOk = (Button) findViewById(R.id.btOk);
        View.OnClickListener okClick = new View.OnClickListener() {
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                String name = nameEdit.getText().toString();
                String address = addressEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String web = webEdit.getText().toString();
                String calendar = calendarEdit.getText().toString();
                cv.put("name", name);
                cv.put("address", address);
                cv.put("phone", phone);
                cv.put("email", email);
                cv.put("web", web);
                cv.put("calendar", calendar);
                DBHelper dbHelper = new DBHelper(context);
                // подключаемся к БД
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert("studios", null, cv);
                dbHelper.close();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        };
        btOk.setOnClickListener(okClick);
        btCancel = (Button) findViewById(R.id.btCancel);
        View.OnClickListener cancelClick = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        };
        btCancel.setOnClickListener(cancelClick);
        //https://calendar.google.com/calendar/embed?src=qzavyer%40gmail.com&ctz=Asia/Yekaterinburg
        //https://calendar.google.com/calendar/embed?src=manekenph%40gmail.com
        //https://calendar.google.com/calendar/ical/manekenph%40gmail.com/public/basic.ics
    }
}