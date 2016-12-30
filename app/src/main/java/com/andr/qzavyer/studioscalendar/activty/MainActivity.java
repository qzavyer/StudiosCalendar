package com.andr.qzavyer.studioscalendar.activty;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.andr.qzavyer.studioscalendar.R;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "MainActivity";
    Button btOk;
    final Context activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btOk = (Button) findViewById(R.id.btOk);
        View.OnClickListener okClick = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(activityContext, DayActivity.class);
                startActivity(intent);
            }
        };
        btOk.setOnClickListener(okClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, "Добавить");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==1){
            Intent intent = new Intent(activityContext, AddActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
