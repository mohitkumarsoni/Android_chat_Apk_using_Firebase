package com.example.chatapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chatapk.model.UserModel;
import com.example.chatapk.util.FireBaseUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

public class Login_username_Activity extends AppCompatActivity {
    EditText usernameInput;
    FloatingActionButton letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);
        
        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        
        phoneNumber = getIntent().getStringExtra("phone");
        getUsername();
        
        letMeInBtn.setOnClickListener(v -> setUsername());
        
    }

    private void setUsername() {
        String username = usernameInput.getText().toString();
        if (username.isEmpty()|| username.length() < 3 ){
            usernameInput.setError("minimum username length is 3 characters");
            return;
        }

        setInProgress(true);
        if (userModel != null){
            userModel.setUsername(username);
        }else {
            userModel = new UserModel(phoneNumber,username, Timestamp.now());
        }

        FireBaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                Intent intent = new Intent(Login_username_Activity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

    }

    private void getUsername() {
        setInProgress(true);
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null){
                    usernameInput.setText(userModel.getUsername());
                }
            }
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}









