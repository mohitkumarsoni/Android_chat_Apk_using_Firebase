package com.example.chatapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchUserActivity extends AppCompatActivity {
    ImageButton back_btn,search_btn;
    RecyclerView recycler_view;
    EditText search_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        back_btn = findViewById(R.id.back_btn);
        search_btn = findViewById(R.id.search_user_btn);
        recycler_view = findViewById(R.id.search_user_recycler_view);
        search_input = findViewById(R.id.search_user_name_input);

        // focus will be set upon EditText , so keypad will automatically appear
        search_input.requestFocus();

        back_btn.setOnClickListener(v -> onBackPressed());

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = search_input.getText().toString();
                if (searchTerm.isEmpty() || searchTerm.length()<3){
                    search_input.setError("Invalid Username");
                    return;
                }

                setupSearchRecyclerView(searchTerm);

            }
        });

    }

    private void setupSearchRecyclerView(String searchTerm) {

    }
}