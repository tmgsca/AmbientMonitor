package com.dev.thiago.ambientmonitoring.view.fragment;


import android.app.ProgressDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.model.User;
import com.dev.thiago.ambientmonitoring.service.SessionService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_measurer_settings)
public class MeasurerSettingsFragment extends GenericFragment {

    ProgressDialog dialog;

    @ViewById
    TextView settingsUserName;

    @ViewById
    TextView settingsMeasurerId;

    @ViewById
    TextView settingsRoomName;

    @AfterViews
    protected void afterViews() {

        Realm realm = Realm.getInstance(getActivity());

        User user = SessionUtils.getLoggedUser(getActivity(), realm);

        settingsUserName.setText(user.getName());

        settingsMeasurerId.setText("Measurer ID: " + user.getSession().getToken());

        Integer roomId = MeasurerUtils.getTrackedRoomId(getActivity());

        Room room = realm.where(Room.class).equalTo("id", roomId).findFirst();

        settingsRoomName.setText("Room: " + room.getName());

        setTitle("Settings");

        realm.close();
    }

    @Click
    protected void settingsLogoutButtonClicked() {

        final MainActivity activity = (MainActivity) getActivity();

        SessionService service = RetrofitUtils.getRetrofit().create(SessionService.class);

        Call<Void> call = service.logout(SessionUtils.getAuthHeader(getActivity()));

        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Logging out...");

        dialog.show();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                dialog.hide();

                if (response.isSuccess()) {

                    SessionUtils.clearPreferences(activity);

                    Realm realm = Realm.getInstance(activity);

                    realm.beginTransaction();
                    realm.clear(Session.class);
                    realm.clear(User.class);
                    realm.clear(Measure.class);
                    realm.commitTransaction();
                    realm.close();

                    activity.stopSensorListener();

                    activity.showWelcomeFragment(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                dialog.hide();

                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
