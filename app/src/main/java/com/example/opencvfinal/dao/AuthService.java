package com.example.opencvfinal.dao;

import com.example.opencvfinal.auth.AuthRequest;
import com.example.opencvfinal.auth.JWTToken;
import com.example.opencvfinal.entities.AdminUserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AuthService {
    @POST("authenticate")
    Call<JWTToken> authenticate(@Body AuthRequest authRequest);
}
