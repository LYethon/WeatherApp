package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText locationEditText;
    TextView searchTextView;
    TextView[] historyTextViews = new TextView[3];

    public static final String LOCATION = "com.example.weatherapp.LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        locationEditText = findViewById(R.id.locationEditText);
        searchTextView = findViewById(R.id.searchTextView);

        // Load history elements
        historyTextViews[0] = findViewById(R.id.history1);
        historyTextViews[1] = findViewById(R.id.history2);
        historyTextViews[2] = findViewById(R.id.history3);

        // Make the search bar search
        searchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(locationEditText.getText().toString());
            }
        });

        // History can be tapped to quickly search again
        for (TextView history : historyTextViews) {
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String location = history.getText().toString();
                    if (!location.isEmpty()) {
                        search(location);
                    }
                }
            });
        }

        // Figure out how to make edit text ENTER button call the search method smhhh

    }

    public void search(String location) {
        // Searches for weather information about specified location
        if (location.isEmpty()) {
            Toast.makeText(this, "Please specify a location", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, Weather.class);
            intent.putExtra(LOCATION, location);
            startActivity(intent);
            updateHistory(location);
        }
    }

    public void updateHistory(String location) {
        // Load historical searches (up to 3)
        historyTextViews[2].setText(historyTextViews[1].getText().toString());
        historyTextViews[1].setText(historyTextViews[0].getText().toString());
        historyTextViews[0].setText(location);
    }

}