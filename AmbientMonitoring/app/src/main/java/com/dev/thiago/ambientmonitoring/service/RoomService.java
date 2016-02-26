package com.dev.thiago.ambientmonitoring.service;

import com.dev.thiago.ambientmonitoring.model.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by thiago on 22/02/16.
 */
public interface RoomService {

    @GET("users/{userId}/rooms.json")
    Call<List<Room>> getRooms(@Header("Authorization") String authorization, @Path("userId") Integer userId);

    @PUT("users/{userId}/rooms/{roomId}/track.json")
    Call<Void> trackRoom(@Header("Authorization") String authorization, @Path("userId") Integer userId, @Path("roomId") Integer roomId);

    @PUT("users/{userId}/rooms/{roomId}/untrack.json")
    Call<Void> untrackRoom(@Header("Authorization") String authorization, @Path("userId") Integer userId, @Path("roomId") Integer roomId);
}
