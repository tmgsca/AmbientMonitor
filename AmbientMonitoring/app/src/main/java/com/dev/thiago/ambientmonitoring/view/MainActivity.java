package com.dev.thiago.ambientmonitoring.view;

import android.app.Fragment;

import com.dev.thiago.ambientmonitoring.GenericActivity;
import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.util.DeviceType;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.dev.thiago.ambientmonitoring.view.fragment.DashboardFragment;
import com.dev.thiago.ambientmonitoring.view.fragment.DashboardFragment_;
import com.dev.thiago.ambientmonitoring.view.fragment.RoomsFragment;
import com.dev.thiago.ambientmonitoring.view.fragment.RoomsFragment_;
import com.dev.thiago.ambientmonitoring.view.fragment.RoomsMeasuresFragment;
import com.dev.thiago.ambientmonitoring.view.fragment.RoomsMeasuresFragment_;
import com.dev.thiago.ambientmonitoring.view.fragment.WelcomeFragment;
import com.dev.thiago.ambientmonitoring.view.fragment.WelcomeFragment_;
import com.dev.thiago.ambientmonitoring.view.fragment.WelcomeMenuFragment;
import com.dev.thiago.ambientmonitoring.view.fragment.WelcomeMenuFragment_;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends GenericActivity {

    @Override
    protected void onStart() {
        super.onStart();

        setupInitialFragment();
    }

    public void showRoomsFragment() {

        RoomsFragment roomsFragment = RoomsFragment_.builder().build();

        switchFragment(roomsFragment);
    }

    public void showWelcomeFragment() {

        WelcomeFragment welcomeFragment = WelcomeFragment_.builder().build();

        getFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container, welcomeFragment).commit();
    }

    public void showWelcomeMenuFragment() {

        WelcomeMenuFragment welcomeMenuFragment = WelcomeMenuFragment_.builder().build();

        getFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container, welcomeMenuFragment).commit();
    }

    public void showRoomsMeasuresFragment() {

        RoomsMeasuresFragment roomsMeasuresFragment = RoomsMeasuresFragment_.builder().build();

        switchFragment(roomsMeasuresFragment);
    }

    public void showDashboardFragment() {

        DashboardFragment dashboardFragment = DashboardFragment_.builder().build();

        getFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container, dashboardFragment).commit();
    }

    private void setupInitialFragment() {

        if (SessionUtils.isLoggedIn(this)) {

            DeviceType deviceType = SessionUtils.getDeviceType(this);

            if (deviceType == null) {

                showWelcomeMenuFragment();

            } else if (deviceType == DeviceType.MEASURER) {

                if (MeasurerUtils.isAttached(this)) {

                    showDashboardFragment();

                } else {

                    showRoomsFragment();
                }

            } else {

                showRoomsMeasuresFragment();
            }

        } else {

            showWelcomeFragment();
        }
    }

    private void switchFragment(Fragment fragment) {

        getFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container, fragment).addToBackStack(null).commit();
    }
}
