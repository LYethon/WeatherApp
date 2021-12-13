package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText locationEditText;
    TextView searchTextView;

    public static final String LOCATION = "com.example.weatherapp.LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        locationEditText = (EditText) findViewById(R.id.locationEditText);
        searchTextView = (TextView) findViewById(R.id.searchTextView);

        searchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(view);
            }
        });

        // Figure out how to make edit text ENTER button call the search method smhhh

    }

    public void search(View view) {
        // Searches for weather information about specified location
        String location = locationEditText.getText().toString();

        if (location.isEmpty()) {
            Toast.makeText(this, "Please specify a location", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, Weather.class);
            intent.putExtra(LOCATION, location);
            startActivity(intent);
        }
    }

}