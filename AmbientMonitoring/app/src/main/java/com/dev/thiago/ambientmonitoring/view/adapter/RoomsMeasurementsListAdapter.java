package com.dev.thiago.ambientmonitoring.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.view.item.RoomMeasurementItemView;
import com.dev.thiago.ambientmonitoring.view.item.RoomMeasurementItemView_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import io.realm.Realm;

/**
 * Created by thiago on 23/02/16.
 */
@EBean
public class RoomsMeasurementsListAdapter extends BaseAdapter {

    List<Room> rooms;

    @RootContext
    Context context;

    @AfterInject
    public void initAdapter() {

        Realm realm = Realm.getInstance(context);

        rooms = realm.allObjects(Room.class);
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RoomMeasurementItemView roomItem;

        if (convertView == null) {

            roomItem = RoomMeasurementItemView_.build(context);

        } else {

            roomItem = (RoomMeasurementItemView) convertView;
        }

        Realm realm = Realm.getInstance(context);

        Measure measure = null;

        Room room = rooms.get(position);

        if (rooms.size() > 0) {

            measure = room.getMeasures().last();
        }

        roomItem.bind(rooms.get(position), measure);

        return roomItem;
    }
}
