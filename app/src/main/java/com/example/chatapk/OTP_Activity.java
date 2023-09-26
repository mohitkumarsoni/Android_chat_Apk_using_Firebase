package com.example.chatapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapk.util.AndroidUtil;
import com.google.android.gms.common.util.DataUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTP_Activity extends AppCompatActivity {

    String phoneNumber;
    EditText inputEt;
    FloatingActionButton nextFab;
    ProgressBar progressBar;
    TextView resendOtpTv;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeOut = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        inputEt = findViewById(R.id.login_otp);
        nextFab = findViewById(R.id.login_next);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTv = findViewById(R.id.resend_otp_textview);

        phoneNumber = getIntent().getStringExtra("phone");

        sendOtp(phoneNumber, false);

    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        setInProgress(true);

        PhoneAuthOptions.Builder builder = new PhoneAuthOptions.Builder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOut, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(), "Verification Failed");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });
        if (isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {

    }

    private void setInProgress(boolean inProgress) {
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextFab.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            nextFab.setVisibility(View.VISIBLE);
        }
    }
}




