package com.example.chatapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapk.Adapter.ChatRecyclerAdapter;
import com.example.chatapk.model.ChatMessageModel;
import com.example.chatapk.model.ChatRoomModel;
import com.example.chatapk.model.UserModel;
import com.example.chatapk.util.AndroidUtil;
import com.example.chatapk.util.FireBaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    UserModel otherUser;
    String chatRoomId;
    ImageView imageView;
    ChatRoomModel chatRoomModel;
    EditText messageInput;
    ImageButton sendMessageButton, backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get userModel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        // mention user id to create uniqueId for generating chatroom
        // to start chat unique id is required & (should be matched)
        chatRoomId = FireBaseUtil.getChatRoomId(FireBaseUtil.currentUserId(), otherUser.getUserId());

        // find views
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageButton = findViewById(R.id.message_send_button);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        FireBaseUtil.getOtherProfilePicStorageReference(otherUser.getUserId()).getDownloadUrl()
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()){
                                Uri uri = t.getResult();
                                AndroidUtil.setProfilePic(getApplicationContext(), uri, imageView);
                            }
                        });

        backBtn.setOnClickListener(v -> onBackPressed());
        
        otherUsername.setText(otherUser.getUsername());
        
        sendMessageButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        // will get chatroom model or create new to begin chats
        getOrCreateChatRoomModel();

        // setting up recyclerView to show chat conversations
        setupChatRecyclerView();

    }

    private void setupChatRecyclerView() {
        Query query = FireBaseUtil.getChatRoomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);     // recyclerView will start in reverse order, like chat apps
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FireBaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FireBaseUtil.currentUserId(), Timestamp.now());
        FireBaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            messageInput.setText("");
                            sendNotification(message);
                        }
                    }
                });
    }

    private void sendNotification(String message) {
        // require current username, message, currentUserId, otherUserToken....
        FireBaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentUser.getUsername());
                    notificationObj.put("body", message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);

                }catch (Exception e){

                }
            }
        });
    }

    private void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAArQPB9xI:APA91bH_QzGRE9K_hL5u4hDuQXE7_xe32jVV6QaguTJavlD_NadsN-z7W9m3ieMEc-_A_JkHM8OxTmGtvZpumS0-FoPsT27ePneyz21cYFgWN5FgsoGjGZrQ_ObXYN5urNVskILDOQbC")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

    private void getOrCreateChatRoomModel() {
        FireBaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null){
                    //if you were not chatting earlier i.e first time chatting
                    chatRoomModel = new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FireBaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );

                    FireBaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}