package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Weather extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        String location = intent.getStringExtra(MainActivity.LOCATION);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(location);
    }

}