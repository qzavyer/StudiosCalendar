package com.andr.qzavyer.studioscalendar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;
        btOk = (Button) findViewById(R.id.btOk);
        View.OnClickListener okClick = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, DayActivity.class);
                startActivity(intent);
            }
        };
        btOk.setOnClickListener(okClick);
    }
}
