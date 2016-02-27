package com.dev.thiago.ambientmonitoring.view.fragment;


import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.util.DeviceType;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_welcome_menu)
public class WelcomeMenuFragment extends GenericFragment {

    MainActivity activity;

    @AfterViews
    void afterViews() {
        activity = (MainActivity) getActivity();
        setTitle("What is this device?");
    }

    @Click
    void clientButtonClicked() {

        SessionUtils.setDeviceType(activity, DeviceType.CLIENT);

        activity.showRoomsMeasuresFragment(true);
    }

    @Click
    void measurerButtonClicked() {

        SessionUtils.setDeviceType(activity, DeviceType.MEASURER);

        activity.showRoomsFragment(true);
    }
}
