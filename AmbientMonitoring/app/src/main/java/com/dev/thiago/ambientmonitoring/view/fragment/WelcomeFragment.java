package com.dev.thiago.ambientmonitoring.view.fragment;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.service.SessionService;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_welcome)
public class WelcomeFragment extends GenericFragment {

    MainActivity activity;

    @Override
    public void onResume() {
        isHideActionBar = true;
        super.onResume();
    }

    @AfterViews
    void afterViews() {

        activity = (MainActivity) getActivity();

        loginPasswordEditText.setTypeface(Typeface.DEFAULT);
        loginPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());
    }

    @ViewById
    EditText loginEmailEditText;

    @ViewById
    EditText loginPasswordEditText;

    ProgressDialog dialog;

    @Click
    void loginButtonClicked() {

        SessionService sessionService = RetrofitUtils.getRetrofit().create(SessionService.class);

        String email = loginEmailEditText.getText().toString();

        String password = loginPasswordEditText.getText().toString();

        HashMap<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Call<Session> call = sessionService.login(body);

        dialog = new ProgressDialog(getActivity());

        dialog.setMessage("Logging in...");

        dialog.show();

        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {

                if (response.isSuccess()) {

                    loginRequestSuccessful(response);

                } else {

                    loginRequestFailed(response);
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {

            }
        });
    }

    void loginRequestSuccessful(Response<Session> response) {

        dialog.hide();

        Realm realm = Realm.getInstance(getActivity());

        realm.beginTransaction();

        realm.copyToRealmOrUpdate(response.body());

        realm.commitTransaction();

        activity.showWelcomeMenuFragment();
    }

    void loginRequestFailed(Response<Session> response) {

        dialog.hide();
    }
}
