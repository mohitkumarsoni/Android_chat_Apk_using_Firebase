package com.example.chatapk.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    String chatRoomId;
    List<String> userIds;
    Timestamp lastMessageTimeStamp;
    String lastMessageSenderId;

    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lastMessageTimeStamp, String lastMessageSenderId) {
        this.chatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public ChatRoomModel() {
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Timestamp lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
