package com.example.kunal.weatherreport;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by Kunal on 06-Jul-17.
 */

public class DownloadWeatherData extends AsyncTask<String, Void, String>{

    Context context;

    DownloadWeatherData(Context context) {

        this.context = context;
    }

    String weatherData = "";

    @Override
    protected void onPostExecute(String weatherData) {
        super.onPostExecute(weatherData);

        JSONObject weatherObj = null;
        try {
            weatherObj = new JSONObject(weatherData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String location = "";
        try {
            location = weatherObj.getString("name");
            Log.d("Location: ", "onPostExecute: " + location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject main = null;
        try {
            main = weatherObj.getJSONObject("main");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        double temperature = 0;
        try {
            temperature = main.getDouble("temp");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int humidity = 0;
        try {
            humidity = main.getInt("humidity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double temp_min = 0;
        try {
            temp_min = main.getInt("temp_min");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double temp_max = 0;
        try {
            temp_max = main.getInt("temp_max");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject wind = null;
        try {
            wind = weatherObj.getJSONObject("wind");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double wind_speed = 0;
        try {
            wind_speed = wind.getDouble("speed");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String description = "";
        String imageIcon = "";
        String weatherType = "";
        try {
            JSONObject weatherJSON = new JSONObject(weatherData);
            JSONArray weatherReport = weatherJSON.optJSONArray("weather");
            for(int i = 0; i < weatherReport.length(); ++i) {
                JSONObject jsonObject = weatherReport.getJSONObject(i);
                description = jsonObject.getString("description");
                weatherType = jsonObject.getString("main");

                if(weatherType.equalsIgnoreCase("thunderstorm"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.thunderstorm);
                else if(weatherType.equalsIgnoreCase("drizzle"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.drizzle);
                else if(weatherType.equalsIgnoreCase("rain"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.rain);
                else if(weatherType.equalsIgnoreCase("snow"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.snow);
                else if(weatherType.equalsIgnoreCase("haze"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.haze);
                else if(weatherType.equalsIgnoreCase("clear"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.bg);
                else if(weatherType.equalsIgnoreCase("clouds"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.cloud);
                else if(weatherType.equalsIgnoreCase("extreme"))
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.storm);
                else
                    MainActivity.constraintLayout.setBackgroundResource(R.drawable.bg);

                imageIcon = jsonObject.getString("icon");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date ((1499297195+19080)*1000));
        Log.d("Time", date.toString());



        temperature -= 273;
        temp_max -= 273;
        temp_min -= 273;

        Log.d("Result", "onPostExecute: " + weatherData);
        Log.d("Temperature", "onPostExecute: " + String.valueOf(temperature) + "C");
        Log.d("Minimum temperature", "onPostExecute: " + String.valueOf(temp_min) + "C");
        Log.d("Maximum temperature", "onPostExecute: " + String.valueOf(temp_max) + "C");
        Log.d("Humidity", "onPostExecute: " + String.valueOf(humidity) + "%");
        Log.d("Wind speed", "onPostExecute: " + String.valueOf(wind_speed));
        Log.d("Weather description", "onPostExecute: " + String.valueOf(description));
        Log.d("Image", "icon: " + imageIcon);
        Log.d("Weather", weatherType);

        MainActivity.temperatureData.setText(String.format("%.0f", temperature) + "C");
        MainActivity.maxTemp.setText("max   :   " + String.format("%.0f", temp_max) + "C");
        MainActivity.minTemp.setText("min   :   " + String.format("%.0f", temp_min) + "C");
        MainActivity.windSpeed.setText("Wind speed   :   " + String.format("%.0f", wind_speed) + "m/s");
        MainActivity.humidity.setText("Humidity   :   " + String.valueOf(humidity) + "%");
        MainActivity.descriptionView.setText(description);
        MainActivity.locationView.setText(location);
        MainActivity.weatherTypeTextView.setText(weatherType);
        Picasso.with(context).load("http://openweathermap.org/img/w/" + imageIcon + ".png").into(MainActivity.imageView);
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream in = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            int data = reader.read();
            while (data != -1) {

                weatherData += (char) data;
                data = reader.read();

            }

            return weatherData;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

