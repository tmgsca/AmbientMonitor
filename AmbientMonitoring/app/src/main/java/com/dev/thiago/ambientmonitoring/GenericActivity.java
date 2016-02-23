package com.dev.thiago.ambientmonitoring;

import android.app.Activity;

/**
 * Created by thiago on 22/02/16.
 */
public class GenericActivity extends Activity {

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0) {

            getFragmentManager().popBackStack();

        } else {

            super.onBackPressed();
        }
    }
}
