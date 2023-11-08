package com.example.securepal2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;

public class MainActivity extends AppCompatActivity implements ShakeDetector.OnShakeListener{
    private ShakeDetector shakeDetector;
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    LocationRequest locationRequest;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    FusedLocationProviderClient fusedLocationProviderClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shakeDetector = new ShakeDetector(this, this);

        auth = FirebaseAuth.getInstance();

        button = findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });





        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();


        getLocation();

    }
    @Override
    public void onShake() {

        // Show an alert dialog when a shake is detected
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Shake Detected");
        builder.setMessage("You shook your phone!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("01313246872",null,"I am in Danger...Please HELP ME!",null,null);
            }
        });
        builder.show();



    }
    @Override
    protected void onPause() {
        super.onPause();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(shakeDetector);
    }



    private boolean isGPSEnabled(){
        LocationManager locationManager = null;
        boolean is = false;
        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        is = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return is;
    }


    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            if (isGPSEnabled()) {
                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                        .removeLocationUpdates(this);
                                if(locationResult != null && locationResult.getLocations().size() > 0){
                                    int index = locationResult.getLocations().size()-1;
                                    double latitude = locationResult.getLocations().get(index).getLatitude();
                                    double longitude = locationResult.getLocations().get(index).getLongitude();

                                    Geocoder geocoder =  new Geocoder(MainActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try{
                                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        String Address  = " I am in Danger,I need Help! " +
                                                "My Address is: "+addresses.get(0).getAddressLine(0)+"\nlatitude: "+latitude
                                                +"\nlongitude: "+longitude+"\nPlease search this on the map";

                                        home.mainAddress= Address;


                                    } catch (IOException e){
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }, Looper.getMainLooper());
            } else {
                Toast.makeText(this, "Turn on GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            Toast.makeText(this, "Need SMS Permission", Toast.LENGTH_SHORT).show();
        } else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            Toast.makeText(this, "Need location Permission", Toast.LENGTH_SHORT).show();
        }
    }



}