package com.sarvesh.weather;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONprocess extends AsyncTask<String,Void,String> {

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        URL url;
        HttpURLConnection connection;

        try {
            url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();
            while(data!= -1){
                char cur = (char) data;
                result += cur;
                data = reader.read();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("JSON data",result);

        return result;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


        try {

            JSONObject object = new JSONObject(s);
            String weatherInfo = object.getString("main");
            String weatherLoc = object.getString("name");

            JSONObject weatherMain = new JSONObject(weatherInfo);

            String temp = weatherMain.getString("temp");
            String temp_max = weatherMain.getString("temp_max");
            String temp_min = weatherMain.getString("temp_min");
            String pressure = weatherMain.getString("pressure");
            String humidity = weatherMain.getString("humidity");
            String location  = weatherLoc;


            Log.i("JSON data","post execute");

            MainActivity.updateView(temp,temp_max,temp_min,pressure,humidity,location);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
