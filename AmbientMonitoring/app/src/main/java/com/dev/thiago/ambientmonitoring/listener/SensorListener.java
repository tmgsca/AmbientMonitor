package com.dev.thiago.ambientmonitoring.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dev.thiago.ambientmonitoring.SensorChangedEvent;
import com.dev.thiago.ambientmonitoring.exception.RoomNotFoundException;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.service.MeasureService;
import com.dev.thiago.ambientmonitoring.util.BusProvider;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.squareup.otto.Bus;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import retrofit2.Call;

/**
 * Created by thiago on 27/02/16.
 */
@EBean
public class SensorListener implements SensorEventListener {

    private SensorManager sensorManager;

    private Sensor temperatureSensor;

    private Sensor humiditySensor;

    private Timer timer;

    private Float currentTemperature;

    private Float currentHumidity;

    private Integer roomId;

    @RootContext
    Context context;

    @AfterInject
    protected void afterInject() {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        roomId = MeasurerUtils.getTrackedRoomId(context);
    }

    public void start() throws RoomNotFoundException {

        if (roomId != 0) {

            registerSensorsListeners();

            timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendMeasureData();
                }
            }, 0, 5000);

        } else {

            throw new RoomNotFoundException();
        }
    }

    public void stop() {

        if (sensorManager != null) {

            sensorManager.unregisterListener(this);
        }

        if (timer != null) {

            timer.cancel();

            timer.purge();
        }
    }

    private void registerSensorsListeners() {

        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Background
    protected void sendMeasureData() {

        if (currentHumidity != null && currentTemperature != null) {

            MeasureService service = RetrofitUtils.getRetrofit().create(MeasureService.class);

            String auth = SessionUtils.getAuthHeader(context);

            Measure measure = new Measure();

            measure.setHumidity(currentHumidity);

            measure.setTemperature(currentTemperature);

            Realm realm = Realm.getInstance(context);

            Call<Void> call = service.postMeasure(auth, SessionUtils.getLoggedUser(context, realm).getId(), roomId, measure);

            realm.close();

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

    @UiThread
    protected void sendBus(Float temperature, Float humidity) {

        Bus bus = BusProvider.getInstance();

        bus.post(new SensorChangedEvent(temperature, humidity));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
