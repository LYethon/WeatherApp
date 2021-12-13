package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity {

    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        location = intent.getStringExtra(MainActivity.LOCATION);
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