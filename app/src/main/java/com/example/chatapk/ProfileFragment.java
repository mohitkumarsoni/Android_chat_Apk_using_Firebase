package com.example.chatapk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatapk.model.UserModel;
import com.example.chatapk.util.AndroidUtil;
import com.example.chatapk.util.FireBaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileFragment extends Fragment {
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    FloatingActionButton updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;


    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        phoneInput = view.findViewById(R.id.profile_phone);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.profile_logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener(v -> {
            updateBtnClicked();
        });

        logoutBtn.setOnClickListener(v -> {

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FireBaseUtil.logout();
                    Intent intent = new Intent(getContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });

        });

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    private void updateBtnClicked() {
        String newUsername = usernameInput.getText().toString();
        if (newUsername.isEmpty()|| newUsername.length() < 3 ){
            usernameInput.setError("minimum username length is 3 characters");
            return;
        }
        currentUserModel.setUsername(newUsername);

        // to remove keyboard from screen as updateBtnClicked
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(),0);
        }catch (Exception e){
            Log.d("PROFILE KEYBOARD ERROR",e.toString());
        }

        setInProgress(true);

        if (selectedImageUri!=null) {
            //if image is present than it will upload with image
            FireBaseUtil.getCurrentProfilePicStorageReference().putFile(selectedImageUri)
                    .addOnCompleteListener(task ->{
                        updateToFirestore();
                    });
        }else {
            //if image is not present than it will upload without image
            updateToFirestore();
        }

    }

    private void updateToFirestore(){
        FireBaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                AndroidUtil.showToast(getContext(), "Updated Successfully");
            }else {
                AndroidUtil.showToast(getContext(), "Updating Failed");
            }
        });
    }

    private void getUserData() {
        setInProgress(true);
        FireBaseUtil.getCurrentProfilePicStorageReference().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePic(getContext(), uri, profilePic);
                            }
                        });
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
        });
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}