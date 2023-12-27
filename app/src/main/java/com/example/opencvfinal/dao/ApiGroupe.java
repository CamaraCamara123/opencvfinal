package com.example.opencvfinal.dao;

import com.example.opencvfinal.entities.Groupe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiGroupe {
    @GET("all")
    Call<List<Groupe>> findAll();

    @GET("{id}")
    Call<Groupe> getGroupeById(@Path("id") Long id);
}
