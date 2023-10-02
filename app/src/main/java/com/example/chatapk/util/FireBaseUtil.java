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

/*

1. CollectionReference : is used when you need a collection of result from FirebaseFirestore database. (Example: list of user present in database)
    It can be used to read, write, and delete data from the collection. For example, you could use a CollectionReference to get a list of all the users in your database,
     or to write a new user to the database.



2.  DocumentReference : is used when you need a detail of particular data/user from FirebaseFirestore database (Example: id of particular user/ group)
     It can be used to read, write, and delete the data in the document. For example, you could use a DocumentReference to get the profile information of a particular user,
     or to update the user's profile information.

 */
