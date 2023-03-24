package com.example.forecastapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout rlHome;
    private ProgressBar pbLoading;
    private TextView tvCityName, tvCurrentTemperature, tvCurrentPressure, tvCurrentWindSpeed, tvCurrentClouds, tvCurrentHumidity,
            tvCurrentSunrise, tvCurrentSunset, tvCurrentTempFeelsLike, tvCurrentPrecipation, tvCurrentTime;
    private TextInputEditText cityEdit;
    private ImageView ivBack, ivIcon, ivSearch, ivWindArrow, ivMapIcon, ivGPSIcon, ivSearchHistory;
    private Button shortBTN, mediumBTN, longBTN, loadAgainBTN;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String APIKey = "&appid=f96753ee20f647267d86a70f1c55987f&units=metric";
    private String currentWeatherAPI = "https://api.openweathermap.org/data/2.5/weather?";
    private String latitude, longitude;
    ActivityResultLauncher<Intent> mapsActivityResultLauncher;
    private Location location;
    private Boolean isFirstGPSUpdate = true, isFirstGPSWeatherToast = true, isFirstCorrectMainScreenLoading = false;
    private LinearLayout noInternetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        rlHome = findViewById(R.id.idRLHome);
        pbLoading = findViewById(R.id.idProgressBarLoading);
        tvCityName = findViewById(R.id.idTVCityName);
        tvCurrentTemperature = findViewById(R.id.idTVTemperature);
        tvCurrentTempFeelsLike = findViewById(R.id.idTVFeelsTempCurrent);
        tvCurrentPrecipation = findViewById(R.id.idTVPrecipationCurrent);
        tvCurrentClouds = findViewById(R.id.idTVCloudsCurrent);
        tvCurrentHumidity = findViewById(R.id.idTVHumidityCurrent);
        tvCurrentPressure = findViewById(R.id.idTVPressureCurrent);
        tvCurrentWindSpeed = findViewById(R.id.idTVWindCurrent);
        tvCurrentSunrise = findViewById(R.id.idTVSunriseCurrent);
        tvCurrentSunset = findViewById(R.id.idTVSunsetCurrent);
        cityEdit = findViewById(R.id.idEditCity);
        ivBack = findViewById(R.id.idIVBackground);
        ivIcon = findViewById(R.id.idIVIcon);
        ivSearch = findViewById(R.id.idIVSearchIcon);
        ivWindArrow = findViewById(R.id.idIVWindArrow);
        ivMapIcon = findViewById(R.id.idIVMapIcon);
        ivGPSIcon = findViewById(R.id.idIVGPSIcon);
        ivSearchHistory = findViewById(R.id.idIVSearchHistoryIcon);
        shortBTN = findViewById(R.id.idShortWeatherBTN);
        mediumBTN = findViewById(R.id.idMediumWeatherBTN);
        longBTN = findViewById(R.id.idLongWeatherBTN);
        tvCurrentTime = findViewById(R.id.idTVCurrentTime);
        loadAgainBTN = findViewById(R.id.loadAgainBTN);
        noInternetLayout = findViewById(R.id.idLLNoInternet);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            isFirstGPSUpdate = false;
            getMyKnownLocation(false);
            getCurrentWeatherInfo(null, latitude, longitude);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(isFirstGPSUpdate) {
                    isFirstGPSUpdate = false;
                    getMyKnownLocation(false);
                    getCurrentWeatherInfo(null, latitude, longitude);
                }
            }
        });
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        mapsActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bundle bundle = data.getExtras();
                            String lat = Double.toString(bundle.getDouble("latitude"));
                            String lon = Double.toString(bundle.getDouble("longitude"));
                            setLatitudeAndLongitude(lat, lon);
                            getCurrentWeatherInfo(null, lat, lon);
                        }
                    }
                });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText().toString();
                if(city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter city name correctly!", Toast.LENGTH_SHORT).show();
                }
                else {
                    setProgressBarVisible(true);
                    getCurrentWeatherInfo(city, null, null);
                }
            }
        });
        ivSearchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> recentLocalizations = MainActivity.this.getRecentLocalizations();
                String[] recentLocalizationsArray;
                if(recentLocalizations.size() > 0) {
                    recentLocalizationsArray = new String[recentLocalizations.size()];
                    for(int i=0; i<recentLocalizations.size(); i++) {
                        recentLocalizationsArray[i] = recentLocalizations.get(i);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select your previous localization")
                            .setItems(recentLocalizationsArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getCurrentWeatherInfo(recentLocalizationsArray[which], null, null);
                                }
                            });
                    builder.create().show();
                }
                else {
                    Toast.makeText(MainActivity.this, "You have not any recent localizations!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivGPSIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressBarVisible(true);
                getMyKnownLocation(true);
                getCurrentWeatherInfo(null, latitude, longitude);
                setProgressBarVisible(false);
            }
        });
        ivMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", Double.parseDouble(latitude));
                bundle.putDouble("longitude", Double.parseDouble(longitude));
                intent.putExtras(bundle);
                mapsActivityResultLauncher.launch(intent);

            }
        });
        shortBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cityName", tvCityName.getText().toString());
                bundle.putInt("forecastType", 1);
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                bundle.putString("color", "#0275d8");
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);

            }
        });
        mediumBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cityName", tvCityName.getText().toString());
                bundle.putInt("forecastType", 2);
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                bundle.putString("color", "#5cb85c");
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
        longBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cityName", tvCityName.getText().toString());
                bundle.putInt("forecastType", 3);
                bundle.putString("latitude", latitude);
                bundle.putString("longitude", longitude);
                bundle.putString("color", "#d9534f");
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
        loadAgainBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noInternetLayout.setVisibility(View.GONE);
                getCurrentWeatherInfo(null, latitude, longitude);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisions granted!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide the permissions!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getSuitableURL(String API, String cityName, String latitude, String longitude) {
        String url;
        if(cityName == null) {
            url = API+"lat="+latitude+"&lon="+longitude+APIKey;
        }
        else {
            url = API+"q="+cityName+APIKey;
        }
        return url;
    }

    private void getCurrentWeatherInfo(String cityName, String latitude, String longitude) {
        setProgressBarVisible(true);
        String url = getSuitableURL(currentWeatherAPI, cityName, latitude, longitude);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String cityName = response.getString("name");
                    if(cityName.equals("")) {
                        String lat = response.getJSONObject("coord").getString("lat");
                        String lon = response.getJSONObject("coord").getString("lon");
                        tvCityName.setText(lat + ", " + lon);
                    }
                    else {
                        tvCityName.setText(cityName);
                        addToHistoryList(cityName);
                    }
                    tvCurrentTemperature.setText(response.getJSONObject("main").getString("temp") + "°C");
                    tvCurrentTempFeelsLike.setText("Feels like: " + response.getJSONObject("main").getString("feels_like") + "°C");
                    if(response.has("rain") && response.has("snow")) {
                        String rainP = response.getJSONObject("rain").getString("1h");
                        String snowP = response.getJSONObject("snow").getString("1h");
                        double precipSum = Double.parseDouble(rainP) + Double.parseDouble(snowP);
                        tvCurrentPrecipation.setText("Precip: " + Double.toString(precipSum) + " /1h rain and snow");
                    }
                    else if(!response.has("rain") && response.has("snow")) {
                        String snowP = response.getJSONObject("snow").getString("1h");
                        tvCurrentPrecipation.setText("Precip: " + snowP + "mm /1h snow");
                    }
                    else if(response.has("rain") && !response.has("snow")) {
                        String rainP = response.getJSONObject("rain").getString("1h");
                        tvCurrentPrecipation.setText("Precip: " + rainP + "mm /1h rain");
                    }
                    else {
                        tvCurrentPrecipation.setText("Precip: 0" );
                    }
                    tvCurrentPressure.setText("Pressure: "+response.getJSONObject("main").getString("pressure")+" hPa");
                    tvCurrentHumidity.setText("Humidity: "+response.getJSONObject("main").getString("humidity")+"%");
                    tvCurrentWindSpeed.setText("Wind: "+response.getJSONObject("wind").getString("speed")+" m/s");
                    String windDeg = response.getJSONObject("wind").getString("deg");
                    ivWindArrow.setRotation(Float.parseFloat(windDeg)+180);
                    tvCurrentClouds.setText("Clouds: "+response.getJSONObject("clouds").getString("all")+"%");
                    DateFormat f = new SimpleDateFormat("HH:mm");
                    int timezonePoland = 3600 * 1000;
                    int timezone = Integer.parseInt(response.getString("timezone")) * 1000;
                    String currentDT = response.getString("dt");
                    long currentTime = Long.parseLong(currentDT)*1000 + timezone - timezonePoland;
                    Date currentTimeDate = new Date(currentTime);
                    tvCurrentTime.setText("Update weather time: " + f.format(currentTimeDate));
                    String sunrise = response.getJSONObject("sys").getString("sunrise");
                    if(!sunrise.equals("0")) {
                        long sunriseTimestamp = Long.parseLong(sunrise)*1000 + timezone - timezonePoland;
                        Date sunriseDate = new Date(sunriseTimestamp);
                        tvCurrentSunrise.setText("Sunrise: "+f.format(sunriseDate));
                    }
                    else {
                        tvCurrentSunrise.setText("");
                    }
                    String sunset = response.getJSONObject("sys").getString("sunset");
                    if(!sunset.equals("0")) {
                        long sunsetTimestamp = Long.parseLong(sunset)*1000 + timezone - timezonePoland;
                        Date sunsetDate = new Date(sunsetTimestamp);
                        tvCurrentSunset.setText("Sunset: "+f.format(sunsetDate));
                    }
                    else {
                        tvCurrentSunset.setText("");
                    }
                    String conditionIcon = response.getJSONArray("weather").getJSONObject(0).getString("icon");
                    Picasso.get().load("https://openweathermap.org/img/wn/"+conditionIcon+"@2x.png").into(ivIcon);
                    String lat = response.getJSONObject("coord").getString("lat");
                    String lon = response.getJSONObject("coord").getString("lon");
                    setLatitudeAndLongitude(lat, lon);
                    isFirstCorrectMainScreenLoading = true;
                    setNoInternetInfoVisible(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    setProgressBarVisible(false);
                    if(isFirstGPSWeatherToast) {
                        Toast.makeText(MainActivity.this, "It's your current location.", Toast.LENGTH_SHORT).show();
                        isFirstGPSWeatherToast = false;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "An unexpected error has occurred!", Toast.LENGTH_SHORT).show();
                setProgressBarVisible(false);
                if(!isFirstCorrectMainScreenLoading) {
                    setNoInternetInfoVisible(true);
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void setLatitudeAndLongitude(String lat, String lon) {
        latitude = lat;
        longitude = lon;
    }

    public void setProgressBarVisible(Boolean bool) {
        if(bool) {
            pbLoading.setVisibility(View.VISIBLE);
            rlHome.setVisibility(View.GONE);
        }
        else {
            pbLoading.setVisibility(View.GONE);
            rlHome.setVisibility(View.VISIBLE);
        }
    }

    public void setNoInternetInfoVisible(Boolean bool) {
        if(bool) {
            pbLoading.setVisibility(View.GONE);
            rlHome.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
        }
        else {
            pbLoading.setVisibility(View.GONE);
            rlHome.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);
        }
    }

    public void getMyKnownLocation(Boolean isButtonAction) {
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try {
            setLatitudeAndLongitude(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
            if(!isFirstGPSWeatherToast) {
                Toast.makeText(MainActivity.this, "It's your current location.", Toast.LENGTH_SHORT).show();
            }
        }
        catch(NullPointerException e) {
            if(isButtonAction) {
                Toast.makeText(MainActivity.this, "Please wait a moment or check if your GPS is enabled.", Toast.LENGTH_LONG).show();
            }
            else {
                setLatitudeAndLongitude("50.85", "22.49");
            }
        }
    }

    public void addToHistoryList(String city) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String whereClause = DatabaseHelper.CITY + " = ?";
        String[] whereArgs = new String[] {city};
        int deletedRowNum = getContentResolver().delete(SearchHistoryProvider.URI_CONTENT, whereClause, whereArgs);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.CITY, city);
        Uri newUri = getContentResolver().insert(SearchHistoryProvider.URI_CONTENT, cv);
    }

    public ArrayList<String> getRecentLocalizations() {
        ArrayList<String> recentLocalizations = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = getContentResolver().query(SearchHistoryProvider.URI_CONTENT, new String[]{DatabaseHelper.CITY}, null, null, DatabaseHelper.ID+" DESC");

        final int cityIndex = cursor.getColumnIndex(DatabaseHelper.CITY);

        try {
            // If moveToFirst() returns false then cursor is empty
            if (!cursor.moveToFirst()) {
                return new ArrayList<>();
            }
            do {
                // Read the values of a row in the table using the indexes acquired above
                final String cityName = cursor.getString(cityIndex);
                recentLocalizations.add(cityName);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
            db.close();
        }
        return recentLocalizations;
    }
}