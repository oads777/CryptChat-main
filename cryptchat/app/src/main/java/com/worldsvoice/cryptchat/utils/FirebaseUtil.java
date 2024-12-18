package com.worldsvoice.cryptchat.utils;

import static android.content.ContentValues.TAG;

import android.nfc.Tag;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chats").document(chatroomId);
    }

    public static String getChatroomId(String userId1,String userId2){
        Log.d(TAG, "ESTOU AQUI");
        if(userId1.hashCode()<userId2.hashCode()){
            Log.d(TAG, "ESTOU sucesso");
            return userId1+"_"+userId2;
        }else{
            Log.d(TAG, "ESTOU falha");
            return userId2+"_"+userId1;
        }

    }

}