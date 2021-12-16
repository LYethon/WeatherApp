package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity {

    LinearLayout current;
    String location;
    TextView info, date, temperature, feelsLike, wind, precipitation, humidity, visibility;
    ImageView icon;
    List<TextView> forecasts = new ArrayList<>();
    List<ImageView> subIcons = new ArrayList<>();
    Boolean isMetric = true;

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
        date = findViewById(R.id.date);
        temperature = findViewById(R.id.temperature);
        feelsLike = findViewById(R.id.feelsLike);
        wind = findViewById(R.id.wind);
        precipitation = findViewById(R.id.precipitation);
        humidity = findViewById(R.id.humidity);
        visibility = findViewById(R.id.visibility);
        icon = findViewById(R.id.icon);

        // Load all 7 forecast elements
        forecasts.add(findViewById(R.id.forecast1));
        forecasts.add(findViewById(R.id.forecast2));
        forecasts.add(findViewById(R.id.forecast3));
        forecasts.add(findViewById(R.id.forecast4));
        forecasts.add(findViewById(R.id.forecast5));
        forecasts.add(findViewById(R.id.forecast6));
        forecasts.add(findViewById(R.id.forecast7));

        //Load all 7 icon elements
        subIcons.add(findViewById(R.id.icon1));
        subIcons.add(findViewById(R.id.icon2));
        subIcons.add(findViewById(R.id.icon3));
        subIcons.add(findViewById(R.id.icon4));
        subIcons.add(findViewById(R.id.icon5));
        subIcons.add(findViewById(R.id.icon6));
        subIcons.add(findViewById(R.id.icon7));

        try {
            JSONObject forecastJSON = requestForecastWeather(location, 8); // Since it includes the current date

            // If response returned an error, handle it
            if (forecastJSON.has("error")) {
                Toast.makeText(this, "Invalid location. Please go back and try again.", Toast.LENGTH_LONG).show();
                return;
            }

            // Set on click listener for linear layout
            current = findViewById(R.id.current);
            current.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isMetric) {
                            isMetric = false;
                            setImperialValues(forecastJSON);
                        } else {
                            isMetric = true;
                            setMetricValues(forecastJSON);
                        }
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Set values according to specified units
            if (isMetric) {
                setMetricValues(forecastJSON);
            } else {
                setImperialValues(forecastJSON);
            }

            // Location info (non weather related)
            String city = forecastJSON.getJSONObject("location").getString("name");
            String region = forecastJSON.getJSONObject("location").getString("region");
            String day = forecastJSON.getJSONObject("location").getString("localtime").substring(0, 10);
            info.setText(String.format("%s, %s", city, region));
            date.setText(day);

        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMetricValues(JSONObject json) throws JSONException, InterruptedException {
        // Pulls and sets metric info from the forecast endpoint JSON object
        JSONObject current = json.getJSONObject("current");
        JSONObject forecast = json.getJSONObject("forecast");
        JSONArray forecastArray = forecast.getJSONArray("forecastday");

        // Pull values from current
        double temperatureVal = current.getDouble("temp_c");
        double feelsLikeVal = current.getDouble("feelslike_c");
        double windSpeedVal = current.getDouble("wind_kph");
        String windDirectionVal = current.getString("wind_dir");
        Integer precipitationVal = current.getInt("precip_mm");
        Integer humidityVal = current.getInt("humidity");
        Integer visibilityVal = current.getInt("vis_km");
        JSONObject currentCondition = current.getJSONObject("condition");

        String iconUrl = "http://cdn.weatherapi.com/weather/128x128/" + currentCondition.getString("icon").substring(35);

        icon.setImageDrawable(getIcon(iconUrl));

        // Update current day values
        temperature.setText(String.format(Locale.CANADA, "%d°C", Math.round(temperatureVal)));
        feelsLike.setText(String.format(Locale.CANADA, "Feels like %d°C", Math.round(feelsLikeVal)));
        wind.setText(String.format(Locale.CANADA, "Wind: %d km/h (%s)", Math.round(windSpeedVal), windDirectionVal));
        precipitation.setText(String.format(Locale.CANADA, "Precipitation: %dmm", precipitationVal));
        humidity.setText(String.format(Locale.CANADA, "Humidity: %d%%", humidityVal));
        visibility.setText(String.format(Locale.CANADA, "Visibility: %dkm", visibilityVal));

        // Update up to 7 of the forecast text views
        for (int i = 1; i < forecastArray.length(); i++) {
            TextView tempTextView = forecasts.get(i - 1);
            ImageView tempImageView = subIcons.get(i - 1);

            JSONObject day = forecastArray.getJSONObject(i);
            String date = day.getString("date").substring(0, 10);
            double temperature  = day.getJSONObject("day").getDouble("avgtemp_c");
            JSONObject subCondition = day.getJSONObject("day").getJSONObject("condition");
            String subIconUrl = "http:" + subCondition.getString("icon");

            tempImageView.setImageDrawable(getIcon(subIconUrl));

            tempTextView.setText(String.format(Locale.CANADA, "%s\n%d°C", date, Math.round(temperature)));
        }

    }

    public void setImperialValues(JSONObject json) throws JSONException {
        // Pulls and sets imperial info from the forecast endpoint JSON object
        JSONObject current = json.getJSONObject("current");
        JSONObject forecast = json.getJSONObject("forecast");
        JSONArray forecastArray = forecast.getJSONArray("forecastday");

        // Pull values from current
        double temperatureVal = current.getDouble("temp_f");
        double feelsLikeVal = current.getDouble("feelslike_f");
        double windSpeedVal = current.getDouble("wind_mph");
        String windDirectionVal = current.getString("wind_dir");
        Integer precipitationVal = current.getInt("precip_in");
        Integer humidityVal = current.getInt("humidity");
        Integer visibilityVal = current.getInt("vis_miles");
        JSONObject currentCondition = current.getJSONObject("condition");
        String iconUrl = currentCondition.getString("icon");

        // Update current day values
        temperature.setText(String.format(Locale.CANADA, "%d°F", Math.round(temperatureVal)));
        feelsLike.setText(String.format(Locale.CANADA, "Feels like %d°F", Math.round(feelsLikeVal)));
        wind.setText(String.format(Locale.CANADA, "Wind: %d mph (%s)", Math.round(windSpeedVal), windDirectionVal));
        precipitation.setText(String.format(Locale.CANADA, "Precipitation: %din", precipitationVal));
        humidity.setText(String.format(Locale.CANADA, "Humidity: %d%%", humidityVal));
        visibility.setText(String.format(Locale.CANADA, "Visibility: %dmi", visibilityVal));

        // Update up to 7 of the forecast text views
        for (int i = 1; i < forecastArray.length(); i++) {
            TextView tempTextView = forecasts.get(i - 1);

            JSONObject day = forecastArray.getJSONObject(i);
            String date = day.getString("date").substring(0, 10);
            double temperature  = day.getJSONObject("day").getDouble("avgtemp_f");

            tempTextView.setText(String.format(Locale.CANADA, "%s\n%d°F", date, Math.round(temperature)));
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

    public Drawable getIcon(String url) throws InterruptedException, JSONException {
        IconRunnable iconRunnable = new IconRunnable(url);
        Thread iconThread = new Thread(iconRunnable);

        iconThread.start();
        iconThread.join();

        return iconRunnable.getResult();
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

class IconRunnable implements Runnable {
    private String url;
    private Drawable result;

    public IconRunnable(String u) {
        url = u;
    }

    @Override
    public void run() {
        try {
            InputStream input = (InputStream) new URL(url).getContent();
            result = Drawable.createFromStream(input, "source");
        } catch (Exception e) {
            System.out.println("ICON ERROR");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public Drawable getResult() { return result; }
}