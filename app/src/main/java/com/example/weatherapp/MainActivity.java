package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            JSONObject json1 = makeRequest("St. Catharines");
            JSONObject json2 = makeRequest("Toronto");

            // Do whatever the hell we want with the json object

        } catch (IOException | InterruptedException | JSONException e) {
            System.out.println("LY Exception NEW");
            e.printStackTrace();
        }

    }

    public JSONObject makeRequest(String query) throws IOException, InterruptedException, JSONException {
        // Takes in a string as the query for the api call

        APIRunnable apiRunnable = new APIRunnable(query);
        Thread testThread = new Thread(apiRunnable);

        testThread.start();
        testThread.join();

        String response = apiRunnable.getResponse();
        return new JSONObject(response);

    }
}

class APIRunnable implements Runnable {

    private String responseString;
    private String query;

    public APIRunnable(String input) {
        query = input;
    }

    @Override
    public void run() {
        try {

            // Make request
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(String.format("https://weatherapi-com.p.rapidapi.com/forecast.json?q=%s", query))
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