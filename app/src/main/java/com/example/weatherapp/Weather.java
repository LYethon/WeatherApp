package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity {

    String location;
    TextView info, temperature, feelsLike, wind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().hide();

        // Pull location from main activity
        Intent intent = getIntent();
        location = intent.getStringExtra(MainActivity.LOCATION);

        // Store layout elements
        info = findViewById(R.id.info);
        temperature = findViewById(R.id.temperature);
        feelsLike = findViewById(R.id.feelsLike);
        wind = findViewById(R.id.wind);

        try {
            JSONObject realtime = requestRealtimeWeather(location);

            if (realtime.has("error")) {
                Toast.makeText(this, "Invalid location. Please go back and try again.", Toast.LENGTH_LONG).show();
                return;
            }

            // Pull data from JSON object
            JSONObject location = realtime.getJSONObject("location");
            JSONObject current = realtime.getJSONObject("current");

            String city = location.getString("name");
            String region = location.getString("region");

            Double temperatureVal = current.getDouble("temp_c");
            Double feelsLikeVal = current.getDouble("feelslike_c");
            Double windSpeedVal = current.getDouble("wind_kph");
            String windDirectionVal = current.getString("wind_dir");

            // Update elements
            info.setText(String.format("%s, %s", city, region));
            temperature.setText(String.format("%d°C", Math.round(temperatureVal)));
            feelsLike.setText(String.format("Feels like %d°C", Math.round(feelsLikeVal)));
            wind.setText(String.format("Wind: %d km/h (%s)", Math.round(windSpeedVal), windDirectionVal));

        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
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