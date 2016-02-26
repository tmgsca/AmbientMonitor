package com.dev.thiago.ambientmonitoring.service;

import com.dev.thiago.ambientmonitoring.model.WeatherWrapper;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by thiago on 26/02/16.
 */
public interface WeatherService {

    @GET("weather")
    Call<WeatherWrapper> getWeather(@Query("q") String query, @Query("appid") String appId, @Query("units") String units);
}
