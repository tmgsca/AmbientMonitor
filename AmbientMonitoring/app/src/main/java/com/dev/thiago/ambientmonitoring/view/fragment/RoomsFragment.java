package com.dev.thiago.ambientmonitoring.view.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.Toast;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.service.RoomService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;
import com.dev.thiago.ambientmonitoring.view.adapter.RoomsListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_rooms)
public class RoomsFragment extends GenericFragment implements DialogInterface.OnClickListener {

    @ViewById
    ListView roomsListView;

    ProgressDialog progressDialog;

    @Bean
    RoomsListAdapter adapter;

    MainActivity activity;

    Room clickedRoom;

    @AfterViews
    void afterViews() {

        activity = (MainActivity) getActivity();

        setTitle("Choose a room to attach");

        roomsListView.setAdapter(adapter);

        requestRooms();
    }

    private void requestRooms() {

        RoomService service = RetrofitUtils.getRetrofit().create(RoomService.class);

        String auth = SessionUtils.getAuthHeader(getActivity());

        Integer userId = SessionUtils.getLoggedUser(getActivity()).getId();

        Call<List<Room>> call = service.getRooms(auth, userId);

        progressDialog = new ProgressDialog(getActivity());

        progressDialog.setMessage("Loading rooms...");

        if (adapter == null || adapter.getCount() == 0) {

            progressDialog.show();
        }

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {

                if (response.isSuccess()) {

                    requestRoomsSuccessful(response);

                } else {

                    requestRoomsFailed(response);
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {

            }
        });
    }

    void requestRoomsSuccessful(Response<List<Room>> response) {

        progressDialog.hide();

        Realm realm = Realm.getInstance(getActivity());

        realm.beginTransaction();

        realm.copyToRealmOrUpdate(response.body());

        realm.commitTransaction();

        adapter.notifyDataSetChanged();
    }

    void requestRoomsFailed(Response<List<Room>> response) {

        progressDialog.hide();
    }

    @ItemClick
    void roomsListView(Room room) {

        clickedRoom = room;

        String message;

        if (room.getIsTracked()) {
            message = room.getName() + " is already attached to another device. Do you wish to remove the other device and attach to the current one?";
        } else {
            message = "Do you want to start tracking " + room.getName() + "?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", this);
        builder.setPositiveButton("Yes", this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:

                dialog.dismiss();

                progressDialog.setMessage("Waiting for server...");

                progressDialog.show();

                attach(clickedRoom.getIsTracked(), clickedRoom.getId());

            case DialogInterface.BUTTON_NEGATIVE:

                dialog.dismiss();
        }
    }

    @Background
    void attach(Boolean detach, Integer roomId) {

        final RoomService service = RetrofitUtils.getRetrofit().create(RoomService.class);
        final String auth = SessionUtils.getAuthHeader(getActivity());
        final Integer userId = SessionUtils.getLoggedUser(getActivity()).getId();

        if (detach) {

            final Boolean untrackIsSuccess = untrackRoom(service, auth, userId, roomId);

            if (!untrackIsSuccess) {

                requestFailed("Could not detach the other device from the room");

                return;
            }
        }

        if (trackRoom(service, auth, userId, roomId)) {

            showDashboardFragment();

        } else {

            requestFailed("Could not attach this device to the room");
        }
    }

    @UiThread
    void requestFailed(String message) {

        progressDialog.dismiss();

        if (message != null)
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void showDashboardFragment() {

        MeasurerUtils.setIsAttached(activity, true);

        MeasurerUtils.setTrackedRoomId(activity, clickedRoom.getId());

        progressDialog.dismiss();

        activity.showDashboardFragment();
    }

    private Boolean untrackRoom(RoomService service, String auth, Integer userId, Integer roomId) {

        Call<Void> call = service.untrackRoom(auth, userId, roomId);

        try {

            Response<Void> response = call.execute();

            return response.isSuccess();

        } catch (IOException e) {

            e.printStackTrace();

            return false;
        }
    }

    private Boolean trackRoom(RoomService service, String auth, Integer userId, Integer roomId) {

        Call<Void> call = service.trackRoom(auth, userId, roomId);

        try {

            Response<Void> response = call.execute();

            return response.isSuccess();

        } catch (IOException e) {

            e.printStackTrace();

            return false;
        }
    }
}
