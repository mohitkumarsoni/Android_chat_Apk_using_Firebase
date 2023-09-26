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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTP_Activity extends AppCompatActivity {

    EditText inputEt;
    FloatingActionButton nextFab;
    TextView resendOtpTv;
    ProgressBar progressBar;
    String phoneNumber;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeout = 60L;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    String verificationCode;




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

        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = inputEt.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                signIn(credential);
                setInProgress(true);
            }
        });

        resendOtpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phoneNumber,true);
            }
        });
    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(false);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setActivity(this)
                .setTimeout(timeout, TimeUnit.SECONDS)
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

    private void startResendTimer() {
        resendOtpTv.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeout--;
                resendOtpTv.setText("resend otp in "+timeout+" seconds");
                if (timeout<=0){
                    timeout = 60L;
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resendOtpTv.setEnabled(true);
                        }
                    });
                }
            }
        },0,1000);
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login and goto next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()){
                    Intent intent = new Intent(OTP_Activity.this, Login_username_Activity.class);
                    intent.putExtra("phone", phoneNumber);
                    startActivity(intent);

                }else {
                    AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                }
            }
        });
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




