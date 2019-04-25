package com.sarvesh.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 *
 */


public class MainActivity extends AppCompatActivity {

    static TextView mainTemp, mainLocation, maxTemp, minTemp, humidityTV, pressureTV;
    static ViewPropertyAnimator mainTempAnimation, mainLocAnimation;
    static EditText editText;
    boolean flag = false ;

    private int LOCATION_PERMISSION_FINE = 0;

    LocationListener locationListener;
    LocationManager locationManager;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTemp = findViewById(R.id.weatherTemp);
        mainLocation = findViewById(R.id.weatherLoc);
        maxTemp = findViewById(R.id.tempMaxVal);
        minTemp = findViewById(R.id.tempMinVal);
        humidityTV = findViewById(R.id.humidityVal);
        pressureTV = findViewById(R.id.pressureVal);
        editText = findViewById(R.id.editText);

        mainTemp.setAlpha(0);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainTempAnimation = mainTemp.animate().translationYBy(-30).setStartDelay(500).setDuration(1000).alphaBy(0.9f);
        mainLocAnimation = mainLocation.animate().translationY(-15).setStartDelay(200).setDuration(800).alphaBy(1f);

        checkLocationPermissions();

        mainTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONprocess JSONdata = new JSONprocess();
                JSONdata.execute("https://api.openweathermap.org/data/2.5/weather?q=" + getUserLocation() + "&appid=7aa999ae38af0c6d605ac86d795ca6ce");
                Toast.makeText(MainActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
            }

        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                double lat = location.getLatitude();
                double lung = location.getLongitude();

                Log.i("asdf",String.valueOf(lat)+","+String.valueOf(lung));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            JSONprocess JSONdata = new JSONprocess();
            JSONdata.execute("https://api.openweathermap.org/data/2.5/weather?q=" + getUserLocation() + "&appid=7aa999ae38af0c6d605ac86d795ca6ce");

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }

    }


    public String getUserLocation() {

        String s = "Bangalore";

        if(flag == true){
            s = editText.getText().toString();
            s = URLEncoder.encode(s);
        }

        return s;
    }


    public void getWeatherData(View v){

        flag = true;
        String s = editText.getText().toString();
        s = URLEncoder.encode(s);
        JSONprocess jsoNprocess = new JSONprocess();
        jsoNprocess.execute("https://api.openweathermap.org/data/2.5/weather?q=" + s + "&appid=fb830f7668c9f9959c4487bd6476a155");
    }

    public void checkLocationPermissions(){

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_FINE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_FINE);
        }

    }


    public void popupMenu(View view){

        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Settings :
                        Intent settingIntent = new Intent(MainActivity.this,null);
                        startActivity(settingIntent);
                        return true;
                    case R.id.About :
                        Intent aboutIntent = new Intent(MainActivity.this,null);
                        startActivity(aboutIntent);
                        return true;

                    default: return false;
                }
            }
        });

        popupMenu.show();
    }

    public static void updateView(String temp,String temp_max,String temp_min,String pressure,String humdidity,String location){

        Log.i("JSON data update",temp);
        String newTemp = String.valueOf(convert(temp,"0"));
        mainTemp.setText(newTemp + "\u00B0");
        mainTempAnimation.start();
        mainLocation.setText(location);
        mainLocAnimation.start();
        humidityTV.setText(humdidity);
        pressureTV.setText(pressure);
        maxTemp.setText(String .valueOf(convert(temp_max,"0")) + "\u00B0 C");
        minTemp.setText(String .valueOf(convert(temp_min,"0")) + "\u00B0 C");

    }

    public static int convert(String temp,String precision){

        double temps = (Double.parseDouble(temp) - 273.15);
        int temperature = 0;
        try {
             temperature = Integer.parseInt(String.format("%."+precision+"f",temps));
        }catch (Exception e){
            e.printStackTrace();
        }
        return temperature;
    }
}
