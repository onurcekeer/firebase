package com.example.onur.firebaseexample;


public class User {
    private String Username;
    private String Status;
    private String Profil;

    public User(String Username,String Status,String Profil){
        super();
        this.Username = Username;
        this.Status = Status;
        this.Profil = Profil;
    }

    public String getUsername() {
        return Username;
    }

    public String getStatus() {
        return Status;
    }

    public String getProfil() {
        return Profil;
    }

}

