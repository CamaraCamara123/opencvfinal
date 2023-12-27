package com.example.opencvfinal.auth;

import java.io.Serializable;

public class JWTToken implements Serializable {
    private String id_token;

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String idToken) {
        this.id_token = id_token;
    }
}
