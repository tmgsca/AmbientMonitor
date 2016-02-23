package com.dev.thiago.ambientmonitoring.service;

import com.dev.thiago.ambientmonitoring.model.Session;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by thiago on 22/02/16.
 */
public interface SessionService {

    @POST("sessions.json")
    Call<Session> login(@Body HashMap<String, String> body);

    @DELETE("sessions.json")
    Call<Void> logout(@Header("Authorization") String authorization);
}
