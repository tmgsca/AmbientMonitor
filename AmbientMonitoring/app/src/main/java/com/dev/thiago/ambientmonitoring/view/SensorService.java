package com.dev.thiago.ambientmonitoring.view;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dev.thiago.ambientmonitoring.SensorChangedEvent;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.service.MeasureService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.squareup.otto.Bus;

import org.androidannotations.annotations.EService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;

/**
 * Created by thiago on 27/02/16.
 */
@EService
public class SensorService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;

    private Sensor temperatureSensor;

    private Sensor humiditySensor;

    private Timer timer;

    private Float currentTemperature;

    private Float currentHumidity;

    private Integer roomId;

    public SensorService() {

        super(SensorService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        roomId = MeasurerUtils.getTrackedRoomId(getApplicationContext());

        registerSensorsListeners();

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMeasureData();
            }
        }, 0, 5000);
    }

    private void registerSensorsListeners() {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void sendMeasureData() {

        if (currentHumidity != null && currentTemperature != null) {

            MeasureService service = RetrofitUtils.getRetrofit().create(MeasureService.class);

            Realm realm = Realm.getInstance(getApplicationContext());

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
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.equals(temperatureSensor)) {

            currentTemperature = event.values[0];

        } else if (event.sensor.equals(humiditySensor)) {

            currentHumidity = event.values[0];
        }

        Bus bus = BusProvider.getInstance();

        bus.post(new SensorChangedEvent(currentTemperature, currentHumidity));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);
        timer.cancel();
        timer.purge();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
