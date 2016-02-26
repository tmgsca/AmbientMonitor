package com.dev.thiago.ambientmonitoring.view.item;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.NumberFormat;

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

            NumberFormat numberFormat = NumberFormat.getNumberInstance();

            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);

            itemRoomMeasurementTemperatureTextView.setText(numberFormat.format(measure.getTemperature()) + "ºC");

            itemRoomMeasurementHumidityTextView.setText(numberFormat.format(measure.getHumidity()) + "%");
        }
    }

    public RoomMeasurementItemView(Context context) {
        super(context);
    }
}
