package com.example.forecastapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForecastActivity extends AppCompatActivity {
    private String APIKey = "&appid=f96753ee20f647267d86a70f1c55987f&units=metric";
    private String forecastFor5DaysAPI = "https://api.openweathermap.org/data/2.5/forecast?";
    private String forecastFor16DaysAPI = "https://api.openweathermap.org/data/2.5/forecast/daily?";
    private String forecastFor30DaysAPI = "https://pro.openweathermap.org/data/2.5/forecast/climate?";
    private TextView cityName, forecastDesrciption;
    private RecyclerView rvForecast;
    private ArrayList<ForecastRecyclerView> forecastListForListView;
    private ForecastRecyclerViewAdapter forecastRVAdapter;
    private ProgressBar pbLoadingForecast;
    private RelativeLayout rlForecast;
    private Button chartsBTN;
    private String toolbarColor;
    private ArrayList<ChartData> chartDataList;
    private int forecastType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarForecast);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cityName = findViewById(R.id.idTVForecastCityName);
        forecastDesrciption = findViewById(R.id.idTVForecastDescribe);
        rvForecast = findViewById(R.id.idRVForecast);
        pbLoadingForecast = findViewById(R.id.idProgressBarLoadingForecast);
        rlForecast = findViewById(R.id.idRLForecast);
        forecastListForListView = new ArrayList<>();
        forecastRVAdapter = new ForecastRecyclerViewAdapter(this, forecastListForListView);
        rvForecast.setAdapter(forecastRVAdapter);
        chartsBTN = findViewById(R.id.seeChartsBTN);

        Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty()) {
            toolbarColor = bundle.getString("color");
            toolbar.setBackgroundColor(Color.parseColor(toolbarColor));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(toolbarColor));
            }
            chartsBTN.setBackgroundColor(Color.parseColor(toolbarColor));
            cityName.setText(bundle.getString("cityName"));
            forecastType = bundle.getInt("forecastType");
            String latitude = bundle.getString("latitude");
            String longitude = bundle.getString("longitude");
            switch(forecastType) {
                case 1:
                    forecastDesrciption.setText("Hourly forecast for 5 days");
                    getForecastToListView(latitude, longitude, 1);
                break;
                case 2:
                    forecastDesrciption.setText("Daily forecast for 16 days");
                    getForecastToListView(latitude, longitude, 2);
                    break;
                case 3:
                    forecastDesrciption.setText("Forecast for 30 days");
                    getForecastToListView(latitude, longitude, 3);
                break;
                default:
                    finish();
                break;
            }
        }

        chartsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForecastActivity.this, ChartActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("color", toolbarColor);
                bundle2.putParcelableArrayList("chartData", chartDataList);
                bundle2.putInt("forecastType", forecastType);
                intent.putExtras(bundle2);
                ForecastActivity.this.startActivity(intent);
            }
        });
    }

    public String getProperlyURL(String lat, String lon, int forecastType) {
        String url = "";
        if(forecastType == 1) {
            url = forecastFor5DaysAPI+"lat="+lat+"&lon="+lon+APIKey;
        }
        else if(forecastType == 2) {
            url = forecastFor16DaysAPI+"lat="+lat+"&lon="+lon+"&cnt=16"+APIKey;
        }
        else if(forecastType == 3) {
            url = forecastFor30DaysAPI+"lat="+lat+"&lon="+lon+APIKey;
        }
        return url;
    }

    public void getForecastToListView(String lat, String lon, int forecastType) {
        String url = getProperlyURL(lat, lon, forecastType);
        RequestQueue queue = Volley.newRequestQueue(ForecastActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(forecastType == 1) loadShortForecast(response, forecastType);
                else if(forecastType == 2) loadMediumAndLongForecast(response, forecastType);
                else if(forecastType == 3) loadMediumAndLongForecast(response, forecastType);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForecastActivity.this, "An unexpected error has occurred!", Toast.LENGTH_SHORT).show();
                setProgressBarVisible(false);
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void setProgressBarVisible(Boolean bool) {
        if(bool) {
            pbLoadingForecast.setVisibility(View.VISIBLE);
            rlForecast.setVisibility(View.GONE);
        }
        else {
            pbLoadingForecast.setVisibility(View.GONE);
            rlForecast.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadShortForecast(JSONObject response, int forecastType) {
        try {
            chartDataList = new ArrayList<>();
            JSONArray forecastJSONArray = response.getJSONArray("list");
            for(int i=0; i<forecastJSONArray.length(); i++) {
                String textTime = forecastJSONArray.getJSONObject(i).getString("dt_txt");
                String temp = forecastJSONArray.getJSONObject(i).getJSONObject("main").getString("temp");
                String pressure = forecastJSONArray.getJSONObject(i).getJSONObject("main").getString("pressure");
                String humidity = forecastJSONArray.getJSONObject(i).getJSONObject("main").getString("humidity");
                String icon = forecastJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                String clouds = forecastJSONArray.getJSONObject(i).getJSONObject("clouds").getString("all");
                String wind = forecastJSONArray.getJSONObject(i).getJSONObject("wind").getString("speed");
                String windDeg = forecastJSONArray.getJSONObject(i).getJSONObject("wind").getString("deg");
                String rain = "0";
                String snow = "0";
                if(forecastJSONArray.getJSONObject(i).has("rain")) {
                    rain = forecastJSONArray.getJSONObject(i).getJSONObject("rain").getString("3h");
                }
                if(forecastJSONArray.getJSONObject(i).has("snow")) {
                    snow = forecastJSONArray.getJSONObject(i).getJSONObject("snow").getString("3h");
                }
                ForecastRecyclerView forecastRV = new ForecastRecyclerView(textTime, temp, pressure, humidity, rain, snow, icon, clouds, wind, windDeg, forecastType);
                forecastListForListView.add(forecastRV);
                chartDataList.add(new ChartData(textTime, Float.parseFloat(temp), Float.parseFloat(rain), Float.parseFloat(snow), Float.parseFloat(clouds), Float.parseFloat(wind)));
            }
            forecastRVAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            setProgressBarVisible(false);
        }
    }

    public void loadMediumAndLongForecast(JSONObject response, int forecastType) {
        try {
            chartDataList = new ArrayList<>();
            JSONArray forecastJSONArray = response.getJSONArray("list");
            for(int i=0; i<forecastJSONArray.length(); i++) {
                String unixTime = forecastJSONArray.getJSONObject(i).getString("dt");
                DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                int timezonePoland = 3600 * 1000;
                int timezone = Integer.parseInt(response.getJSONObject("city").getString("timezone")) * 1000;
                long unixTimeL = Long.parseLong(unixTime)*1000 + timezone - timezonePoland;
                Date timeDate = new Date(unixTimeL);
                String dateString = f.format(timeDate);
                //w miejsce godziny wstawię wschod i zachod slonca
                f = new SimpleDateFormat("HH:mm");
                String sunrise = forecastJSONArray.getJSONObject(i).getString("sunrise");
                long sunriseL = Long.parseLong(sunrise)*1000 + timezone - timezonePoland;
                Date sunriseDate = new Date(sunriseL);
                String sunriseString = f.format(sunriseDate);
                String sunset = forecastJSONArray.getJSONObject(i).getString("sunset");
                long sunsetL = Long.parseLong(sunset)*1000 + timezone - timezonePoland;
                Date sunsetDate = new Date(sunsetL);
                String sunsetString = f.format(sunsetDate);
                String dayTemp =  forecastJSONArray.getJSONObject(i).getJSONObject("temp").getString("day");
                String nightTemp =  forecastJSONArray.getJSONObject(i).getJSONObject("temp").getString("night");
                String pressure = forecastJSONArray.getJSONObject(i).getString("pressure");
                String humidity = forecastJSONArray.getJSONObject(i).getString("humidity");
                String rain = "0";
                String snow = "0";
                if(forecastJSONArray.getJSONObject(i).has("rain")) {
                    rain = forecastJSONArray.getJSONObject(i).getString("rain");
                }
                if(forecastJSONArray.getJSONObject(i).has("snow")) {
                    snow = forecastJSONArray.getJSONObject(i).getString("snow");
                }
                String icon = forecastJSONArray.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon");
                String clouds = forecastJSONArray.getJSONObject(i).getString("clouds");
                String windSpeed = forecastJSONArray.getJSONObject(i).getString("speed");
                String windDeg = forecastJSONArray.getJSONObject(i).getString("deg");

                ForecastRecyclerView forecastRV = new ForecastRecyclerView(dateString, sunriseString, sunsetString, dayTemp, nightTemp, pressure, humidity, rain, snow, icon, clouds, windSpeed, windDeg, forecastType);
                forecastListForListView.add(forecastRV);
                chartDataList.add(new ChartData(dateString, Float.parseFloat(dayTemp), Float.parseFloat(nightTemp), Float.parseFloat(rain), Float.parseFloat(snow), Float.parseFloat(clouds), Float.parseFloat(windSpeed)));
            }
            forecastRVAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            setProgressBarVisible(false);
        }
    }
}