package com.example.forecastapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;
    private FloatingActionButton floatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        floatingButton = findViewById(R.id.getCoordFloatingBTN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MapsActivity.this, R.color.dark));
        }

        Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty()) {
            setLatitudeAndLongitude(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        LatLng placeInTheWorld = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(placeInTheWorld)
                .title("Marker in my place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(placeInTheWorld));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Marker in my place"));
                float mapZoom = mMap.getCameraPosition().zoom;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, mapZoom);
                mMap.animateCamera(cameraUpdate);
                setLatitudeAndLongitude(latLng.latitude, latLng.longitude);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLatitudeAndLongitude(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }
}
