package com.example.securepal2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {

    private Button button;
    EditText num1;
    EditText num2;
    EditText num3;
    Button button3;
    DatabaseReference PhoneNumbersDbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        num1= findViewById(R.id.num1);
        num2= findViewById(R.id.num2);
        num3= findViewById(R.id.num3);
        button3=findViewById(R.id.button3);
        PhoneNumbersDbRef = FirebaseDatabase.getInstance().getReference().child("PhnNum");
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertPhoneNumber();
            }
        });




        button = findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this , home.class);
                startActivity(intent);
                finish();
            }
        });
        
    }

    private void insertPhoneNumber() {
        String number1 = num1.getText().toString();
        String number2 = num2.getText().toString();
        String number3 = num3.getText().toString();
        Numbers numbers = new Numbers(number1 ,number2 ,number3 );
        PhoneNumbersDbRef.child(FirebaseAuth.getInstance().getUid()).setValue(numbers);
        Toast.makeText(Settings.this,"Numbers Inserted!",Toast.LENGTH_SHORT).show();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.item_done){
            message ("Done");


        }
        return super.onOptionsItemSelected(item);
    }
    public void message (String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }
}