package com.example.class_management_android;

public class Admin {
    private String adminUser;
    private String adminPass;
    private String adminID;


    public  Admin(){

    }

    public Admin(String username, String password, String id) {
        this.adminUser = username;
        this.adminPass = password;
        this.adminID = id;
    }

    public String getAdminUser() {
        return this.adminUser;
    }

    public String getAdminPass() {
        return this.adminPass;
    }

    public String getAdminID(){
        return this.adminID;
    }
}
