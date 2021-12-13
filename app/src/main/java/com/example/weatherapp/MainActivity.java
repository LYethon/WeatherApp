package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

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
            return;
        } else {
            Intent intent = new Intent(this, Weather.class);
            intent.putExtra(LOCATION, location);
            startActivity(intent);
        }

    }

    public JSONObject requestRealtimeWeather(String query) throws InterruptedException, JSONException {
        // Makes a request for the realtime weather using the query
        APIRunnable apiRunnable = new APIRunnable(String.format("https://weatherapi-com.p.rapidapi.com/current.json?q=%s", query));
        Thread apiThread = new Thread(apiRunnable);

        apiThread.start();
        apiThread.join();

        String response = apiRunnable.getResponse();
        return new JSONObject(response);
    }

    public JSONObject requestHistoryWeather(String query, String date) throws InterruptedException, JSONException {
        // Makes a request for the historic weather using the query and date
        APIRunnable apiRunnable = new APIRunnable(String.format("https://weatherapi-com.p.rapidapi.com/history.json?q=%s&dt=%s&lang=en", query, date));
        Thread apiThread = new Thread(apiRunnable);

        apiThread.start();
        apiThread.join();

        String response = apiRunnable.getResponse();
        return new JSONObject(response);
    }

    public JSONObject requestForecastWeather(String query, Integer days) throws InterruptedException, JSONException {
        // Makes a request for the forecast weather using the query and days
        APIRunnable apiRunnable = new APIRunnable(String.format("https://weatherapi-com.p.rapidapi.com/forecast.json?q=%s&days=%s", query, days));
        Thread apiThread = new Thread(apiRunnable);

        apiThread.start();
        apiThread.join();

        String response = apiRunnable.getResponse();
        return new JSONObject(response);
    }

}

class APIRunnable implements Runnable {

    private String responseString;
    private final String url;

    public APIRunnable(String apiUrl) {
        url = apiUrl;
    }

    @Override
    public void run() {
        try {

            // Make request
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("x-rapidapi-host", "weatherapi-com.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "f10db8bd73msh7cf4d4ab2643aa8p1a7c51jsnd5a2e8c36f5a")
                    .build();

            // Get and store response
            Response response = client.newCall(request).execute();
            responseString = response.body().string();

        } catch (Exception e) {
            System.out.println("Howdy. This shouldn't be seen.");
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return responseString;
    }

}