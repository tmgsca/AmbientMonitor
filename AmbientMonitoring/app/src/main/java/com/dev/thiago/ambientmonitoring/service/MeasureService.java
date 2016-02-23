package com.dev.thiago.ambientmonitoring.service;

import com.dev.thiago.ambientmonitoring.model.Measure;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by thiago on 22/02/16.
 */
public interface MeasureService {

    @GET("users/{userId}/rooms/{roomId}/measures.json")
    Call<List<Measure>> getMeasures(@Header("Authorization") String authorization,
                                    @Path("userId") Integer userId,
                                    @Path("roomId") Integer roomId);

    @GET("users/{userId}/rooms/{roomId}/measures/last.json")
    Call<Measure> getLastMeasures(@Header("Authorization") String authorization,
                                  @Path("userId") Integer userId,
                                  @Path("roomId") Integer roomId);

    @POST("users/{userId}/rooms/{roomId}/measures.json")
    Call<Void> postMeasure(@Header("Authorization") String authorization,
                           @Path("userId") Integer userId,
                           @Path("roomId") Integer roomId,
                           @Body Measure measure);
}
