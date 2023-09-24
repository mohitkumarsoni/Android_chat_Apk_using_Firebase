package com.example.chatapk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class OTP_Activity extends AppCompatActivity {
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phoneNumber = getIntent().getExtras().getString("phone");
        Toast.makeText(this, ""+phoneNumber, Toast.LENGTH_LONG).show();

    }
}