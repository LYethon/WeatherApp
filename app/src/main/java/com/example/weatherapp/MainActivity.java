package com.example.weatherapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            testApi();
        } catch (IOException e) {
            System.out.println("LY Exception NEW");
            e.printStackTrace();
        }
    }

    public void testApi() throws IOException {

        Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Part A");
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url("https://weatherapi-com.p.rapidapi.com/forecast.json?q=Saint Catharines")
                            .get()
                            .addHeader("x-rapidapi-host", "weatherapi-com.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "f10db8bd73msh7cf4d4ab2643aa8p1a7c51jsnd5a2e8c36f5a")
                            .build();

                    System.out.println("Part B");

                    Response response = client.newCall(request).execute();
                    System.out.println("Part C");
                    System.out.println(response.body().string());
                } catch (Exception e) {
                    System.out.println("Howdy. This shouldn't be seen.");
                    e.printStackTrace();
                }
            }
        });

        testThread.start();
    }
}