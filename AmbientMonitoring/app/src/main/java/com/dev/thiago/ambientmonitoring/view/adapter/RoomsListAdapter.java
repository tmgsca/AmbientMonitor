package com.dev.thiago.ambientmonitoring.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.view.item.RoomItemView;
import com.dev.thiago.ambientmonitoring.view.item.RoomItemView_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import io.realm.Realm;

/**
 * Created by thiago on 23/02/16.
 */
@EBean
public class RoomsListAdapter extends BaseAdapter {

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

        RoomItemView roomItem;

        if (convertView == null) {

            roomItem = RoomItemView_.build(context);

        } else {

            roomItem = (RoomItemView) convertView;
        }

        roomItem.bind(rooms.get(position));

        return roomItem;
    }
}
