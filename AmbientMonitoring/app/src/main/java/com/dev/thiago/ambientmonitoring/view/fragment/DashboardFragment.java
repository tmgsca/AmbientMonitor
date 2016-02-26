package com.dev.thiago.ambientmonitoring.view.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.service.MeasureService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;

@EFragment(R.layout.fragment_dashboard)
public class DashboardFragment extends GenericFragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;

    @ViewById
    TextView dashboardTemperatureTextView;

    @ViewById
    TextView dashboardHumidityTextView;

    Timer timer;

    Float currentTemperature = (float) 0;

    Float currentHumidity = (float) 0;

    Integer roomId;

    @Override
    public void onResume() {

        super.onResume();

        roomId = MeasurerUtils.getTrackedRoomId(getActivity());

        Realm realm = Realm.getInstance(getActivity());

        Room room = realm.where(Room.class).equalTo("id", roomId).findFirst();

        setTitle(room.getName());

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMeasureData();
            }
        }, 0, 5000);

        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_UI);
    }

    void sendMeasureData() {

        MeasureService service = RetrofitUtils.getRetrofit().create(MeasureService.class);

        Realm realm = Realm.getInstance(getActivity());

        Session session = realm.allObjects(Session.class).first();

        String auth = "Token token=" + session.getToken();

        Measure measure = new Measure();

        measure.setHumidity(currentHumidity);

        measure.setTemperature(currentTemperature);

        Call<Void> call = service.postMeasure(auth, session.getUser().getId(), roomId, measure);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        timer.cancel();
        timer.purge();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        if (event.sensor.equals(temperatureSensor)) {

            Float temperature = event.values[0];

            dashboardTemperatureTextView.setText(numberFormat.format(temperature) + "ºC");

            currentTemperature = temperature;

        } else {

            Float humidity = event.values[0];

            dashboardHumidityTextView.setText(numberFormat.format(humidity) + "%");

            currentHumidity = humidity;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
