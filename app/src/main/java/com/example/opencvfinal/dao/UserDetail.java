package com.example.opencvfinal.dao;

import com.example.opencvfinal.entities.AdminUserDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserDetail {

    @GET("users/{login}")
    Call<AdminUserDTO> getUserDetails(@Path("login") String login);
}
