package com.example.kunal.weatherreport;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static TextView temperatureData;
    static TextView maxTemp;
    static TextView minTemp;
    static TextView windSpeed;
    static TextView humidity;
    static TextView locationView;
    static TextView descriptionView;
    static ImageView imageView;
    static TextView weatherTypeTextView;
    static ConstraintLayout constraintLayout;

    LocationManager locationManager;

    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureData = (TextView) findViewById(R.id.temperatureTextView);
        maxTemp = (TextView) findViewById(R.id.maxTextView);
        minTemp = (TextView) findViewById(R.id.minTextView);
        windSpeed = (TextView) findViewById(R.id.windSpeedTextview);
        humidity = (TextView) findViewById(R.id.humidityTextView);
        locationView = (TextView) findViewById(R.id.locationTextView);
        descriptionView = (TextView) findViewById(R.id.weatherDescriptionTextView);
        weatherTypeTextView = (TextView) findViewById(R.id.weatherTypeTextView);
        imageView = (ImageView) findViewById(R.id.imageIconView);
        constraintLayout = (ConstraintLayout) findViewById(R.id.myLayout);

        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {


            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    Log.i("Location", location.toString());
                    DownloadWeatherData downloadWeatherData = new DownloadWeatherData(getApplicationContext());
                    downloadWeatherData.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(location.getLatitude()) + "&lon=" + String.valueOf(location.getLongitude()) + "&appid=2e4c52cf734bc4bf1fccb1526cfae5d3");
//                downloadWeatherData.execute("http://api.openweathermap.org/data/2.5/weather?lat=22.472947&lon=88.367053&appid=2e4c52cf734bc4bf1fccb1526cfae5d3");

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                    if (provider.equals("gps")) {
                        Toast.makeText(getApplicationContext(), "GPS is off", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    Log.i("lm_disabled", provider);

                }

            };

            // If device is running SDK < 23

            if (Build.VERSION.SDK_INT < 23) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // ask for permission

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


                } else {

                    // we have permission!

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if(lastKnownLocation == null) {
                        Toast.makeText(MainActivity.this, "Turn on GPS", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.w("last known location", "onCreate: " +lastKnownLocation.toString());
                        DownloadWeatherData downloadWeatherData = new DownloadWeatherData(getApplicationContext());
                        downloadWeatherData.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + String.valueOf(lastKnownLocation.getLatitude()) + "&lon=" + String.valueOf(lastKnownLocation.getLongitude()) + "&appid=2e4c52cf734bc4bf1fccb1526cfae5d3");
                    }

                }

            }

        } else {
            Toast.makeText(this, "Please check internet connection and try again", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

}
