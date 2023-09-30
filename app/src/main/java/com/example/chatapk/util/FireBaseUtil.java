package com.example.chatapk.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static boolean isLoggedIn(){
        if (currentUserId() != null){
            return true;
        }
        return false;
    }

    public  static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }



    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatroooms").document(chatroomId);
    }


    // user1 & user2 should match their user id to chat
    // this method will help user achieving it s they can chat
    public static String getChatRoomId(String userId1, String userId2){
        if (userId1.hashCode() < userId2.hashCode()){
            return userId1+"_"+userId2;
        }else {
            return userId2+"_"+userId1;
        }
    }

}
