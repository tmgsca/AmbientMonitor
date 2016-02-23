package com.dev.thiago.ambientmonitoring.view.item;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Room;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by thiago on 23/02/16.
 */
@EViewGroup(R.layout.item_room)
public class RoomItemView extends LinearLayout {

    @ViewById
    TextView itemRoomNameTextView;

    @ViewById
    TextView itemRoomNameTrackedTextView;

    public void bind(Room room) {

        itemRoomNameTextView.setText(room.getName());

        itemRoomNameTrackedTextView.setText(room.getIsTracked() ? "Tracking" : "Not tracking");
    }

    public RoomItemView(Context context) {
        super(context);
    }
}
