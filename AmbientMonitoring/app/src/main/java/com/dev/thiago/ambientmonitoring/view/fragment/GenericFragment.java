package com.dev.thiago.ambientmonitoring.view.fragment;

import android.app.Fragment;

/**
 * Created by thiago on 22/02/16.
 */
public class GenericFragment extends Fragment {

    Boolean isHideActionBar;

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity().getActionBar() != null) {

            if (isHideActionBar != null && isHideActionBar) {
                getActivity().getActionBar().hide();
            } else {
                getActivity().getActionBar().show();
            }
        }
    }

    public void setTitle(String title) {
        this.getActivity().setTitle(title);
    }
}
