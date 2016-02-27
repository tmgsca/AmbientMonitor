package com.dev.thiago.ambientmonitoring.view;

import android.app.Fragment;
import android.app.FragmentTransaction;

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
import org.androidannotations.annotations.OptionsMenu;

@OptionsMenu(R.menu.menu_rooms)
@EActivity(R.layout.activity_main)
public class MainActivity extends GenericActivity {

    @Override
    protected void onStart() {
        super.onStart();

        setupInitialFragment();

    }

    public void showRoomsFragment(Boolean isAddBackstack) {

        RoomsFragment roomsFragment = RoomsFragment_.builder().build();

        switchFragment(roomsFragment, isAddBackstack);
    }

    public void showWelcomeFragment(Boolean isAddBackstack) {

        WelcomeFragment welcomeFragment = WelcomeFragment_.builder().build();

        switchFragment(welcomeFragment, isAddBackstack);
    }

    public void showWelcomeMenuFragment(Boolean isAddBackstack) {

        WelcomeMenuFragment welcomeMenuFragment = WelcomeMenuFragment_.builder().build();

        switchFragment(welcomeMenuFragment, isAddBackstack);
    }

    public void showRoomsMeasuresFragment(Boolean isAddBackstack) {

        RoomsMeasuresFragment roomsMeasuresFragment = RoomsMeasuresFragment_.builder().build();

        switchFragment(roomsMeasuresFragment, isAddBackstack);
    }

    public void showDashboardFragment(Boolean isAddBackstack) {

        DashboardFragment dashboardFragment = DashboardFragment_.builder().build();

        switchFragment(dashboardFragment, isAddBackstack);
    }

    private void setupInitialFragment() {

        if (SessionUtils.isLoggedIn(this)) {

            DeviceType deviceType = SessionUtils.getDeviceType(this);

            if (deviceType == null) {

                showWelcomeMenuFragment(false);

            } else if (deviceType == DeviceType.MEASURER) {

                if (MeasurerUtils.isAttached(this)) {

                    showDashboardFragment(false);

                } else {

                    showRoomsFragment(false);
                }

            } else {

                showRoomsMeasuresFragment(false);
            }

        } else {

            showWelcomeFragment(false);
        }
    }

    private void switchFragment(Fragment fragment, Boolean isAddBackstack) {

        FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container, fragment);

        if (isAddBackstack) {

            ft.addToBackStack(null);
        }

        ft.commit();
    }
}
