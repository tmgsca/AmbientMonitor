package com.dev.thiago.ambientmonitoring.view.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.ListView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.service.RoomService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;
import com.dev.thiago.ambientmonitoring.view.adapter.RoomsListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_rooms)
public class RoomsFragment extends GenericFragment implements DialogInterface.OnClickListener {

    @ViewById
    ListView roomsListView;

    ProgressDialog dialog;

    @Bean
    RoomsListAdapter adapter;

    MainActivity activity;

    Room clickedRoom;

    @AfterViews
    void afterViews() {

        activity = (MainActivity) getActivity();

        setTitle("Choose a room to track");

        roomsListView.setAdapter(adapter);

        requestRooms();
    }

    private void requestRooms() {

        RoomService service = RetrofitUtils.getRetrofit().create(RoomService.class);

        Realm realm = Realm.getInstance(getActivity());

        Session session = realm.allObjects(Session.class).first();

        String auth = "Token token=" + session.getToken();

        Integer userId = session.getUser().getId();

        Call<List<Room>> call = service.getRooms(auth, userId);

        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Loading rooms...");

        if (adapter == null || adapter.getCount() == 0) {

            dialog.show();
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

        dialog.hide();

        Realm realm = Realm.getInstance(getActivity());

        realm.beginTransaction();

        realm.copyToRealmOrUpdate(response.body());

        realm.commitTransaction();

        adapter.notifyDataSetChanged();
    }

    void requestRoomsFailed(Response<List<Room>> response) {

        dialog.hide();
    }

    @ItemClick
    void roomsListView(Room room) {

        clickedRoom = room;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to start tracking " + room.getName() + "?");
        builder.setNegativeButton("Cancel", this);
        builder.setPositiveButton("Yes", this);
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {

            case DialogInterface.BUTTON_POSITIVE:

                MeasurerUtils.setIsAttached(activity, true);
                MeasurerUtils.setTrackedRoomId(activity, clickedRoom.getId());

                activity.showDashboardFragment();

            case DialogInterface.BUTTON_NEGATIVE:

                dialog.dismiss();
        }
    }
}
