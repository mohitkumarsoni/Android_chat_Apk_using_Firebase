package com.example.chatapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.chatapk.model.UserModel;
import com.example.chatapk.util.AndroidUtil;
import com.example.chatapk.util.FireBaseUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FireBaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
            //from notification to chat activity
            String userId = getIntent().getExtras().getString("userId");
            FireBaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            Intent intent = new Intent(this, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,model);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

        }else {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (FireBaseUtil.isLoggedIn()) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, LoginPhoneActivity.class));
                            }
                            finish();
                        }
                    }, 700);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}