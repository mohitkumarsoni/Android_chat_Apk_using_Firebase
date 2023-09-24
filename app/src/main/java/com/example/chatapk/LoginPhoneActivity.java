package com.example.chatapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbb20.CountryCodePicker;

public class LoginPhoneActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    FloatingActionButton sendOtpBtn;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        progressBar.setVisibility(View.GONE);

        sendOtpBtn.setOnClickListener((v) -> {
            if (!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Invalid Phone number");
                return;
            }
            Toast.makeText(this, ""+countryCodePicker.getFullNumberWithPlus(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginPhoneActivity.this, OTP_Activity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });
    }
}