package com.example.class_management_android.model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;

    public Chat(String sender, String receiver, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Chat(){

    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
