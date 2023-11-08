package com.example.securepal2;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.DownloadManager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.Manifest;

import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.os.Looper;

import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class home extends AppCompatActivity {


    DatabaseReference PhoneNumbersDbRef;

    FusedLocationProviderClient fusedLocationProviderClient;

    private final static int REQUEST_CODE=100;

     Button button, setting_button;
     TextView loc ;


    // this string declared globally
    public  static  String mainAddress;
    String s1;
    String s2;
    String s3;

   Button panic;



    public static String PERMISSION;


    public static List<String> numberArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        setting_button = findViewById(R.id.settings);
        button = findViewById(R.id.button4);
        loc= findViewById(R.id.location);
        panic = findViewById(R.id.panic);



        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mainAddress!=null&&numberArray!=null){
                    for (String number : numberArray) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null, mainAddress, null, null);
                            Toast.makeText(home.this, number, Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });






        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this , Settings.class);
                startActivity(intent);
                finish();
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this , Login.class);
                startActivity(intent);
                finish();
            }
        });




        FirebaseDatabase.getInstance().getReference().child("PhnNum").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s1 = snapshot.child("number1").getValue(String.class);
                String s2 = snapshot.child("number2").getValue(String.class);
                String s3 = snapshot.child("number3").getValue(String.class);

               numberArray = new ArrayList<>();
               numberArray.add(s1);
               numberArray.add(s2);
               numberArray.add(s3);

                Toast.makeText(home.this, "Numbers Retrieved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }







}