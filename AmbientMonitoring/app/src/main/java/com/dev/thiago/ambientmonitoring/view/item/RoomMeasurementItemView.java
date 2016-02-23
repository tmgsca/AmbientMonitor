package com.dev.thiago.ambientmonitoring.view.item;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by thiago on 23/02/16.
 */
@EViewGroup(R.layout.item_room_measurement)
public class RoomMeasurementItemView extends LinearLayout {

    @ViewById
    TextView itemRoomNameTextView;

    @ViewById
    TextView itemRoomMeasurementTemperatureTextView;

    @ViewById
    TextView itemRoomMeasurementHumidityTextView;

    public void bind(Room room, Measure measure) {

        itemRoomNameTextView.setText(room.getName());

        if (measure != null) {

            itemRoomMeasurementTemperatureTextView.setText(measure.getTemperature() + "ºC");

            itemRoomMeasurementHumidityTextView.setText(measure.getHumidity() + "%");
        }
    }

    public RoomMeasurementItemView(Context context) {
        super(context);
    }
}
