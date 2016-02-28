package com.dev.thiago.ambientmonitoring.view.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.widget.ListView;
import android.widget.Toast;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.service.RoomService;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;
import com.dev.thiago.ambientmonitoring.view.adapter.RoomsMeasurementsListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_rooms_measures)
public class RoomsMeasuresFragment extends Fragment {

    @Bean
    RoomsMeasurementsListAdapter adapter;

    @ViewById
    ListView roomsListView;

    MainActivity activity;

    ProgressDialog dialog;

    Timer timer;

    @AfterViews
    void afterViews() {

        activity = (MainActivity) getActivity();

        roomsListView.setAdapter(adapter);

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                requestRooms();
            }
        }, 0, 5000);
    }

    @UiThread
    void requestRooms() {

        RoomService service = RetrofitUtils.getRetrofit().create(RoomService.class);

        Realm realm = Realm.getInstance(getActivity());

        Session session = realm.allObjects(Session.class).first();

        realm.close();

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

        realm.close();

        adapter.notifyDataSetChanged();
    }

    void requestRoomsFailed(Response<List<Room>> response) {

        dialog.hide();

        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    @ItemClick
    void roomsListView(Room room) {

    }

    @Override
    public void onPause() {
        super.onPause();

        timer.cancel();
        timer.purge();
    }
}
