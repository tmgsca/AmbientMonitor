package com.dev.thiago.ambientmonitoring.view.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.model.Measure;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.model.WeatherWrapper;
import com.dev.thiago.ambientmonitoring.service.MeasureService;
import com.dev.thiago.ambientmonitoring.service.WeatherService;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.util.WeatherUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import me.grantland.widget.AutofitHelper;
import retrofit2.Call;
import retrofit2.Response;

@EFragment(R.layout.fragment_dashboard)
public class DashboardFragment extends GenericFragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;

    @ViewById
    TextView dashboardTemperatureTextView;

    @ViewById
    TextView dashboardTemperatureDecimalTextView;

    @ViewById
    TextView dashboardHumidityTextView;

    @ViewById
    TextView dashboardWeatherCityName;

    @ViewById
    TextView dashboardWeatherCurrentWeather;

    @ViewById
    TextView dashboardWeatherCurrentTemp;

    @ViewById
    TextView dashboardWeatherCurrentHumidity;

    @ViewById
    TextView dashboardWeatherMaxTemp;

    @ViewById
    TextView dashboardWeatherMinTemp;

    @ViewById
    TextView dashboardWeatherUpdated;

    Timer timer;

    Timer weatherTimer;

    Float currentTemperature;

    Float currentHumidity;

    Integer roomId;

    @AfterViews
    void afterViews() {
        AutofitHelper.create(dashboardHumidityTextView);
    }

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

        weatherTimer = new Timer();
        weatherTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
            }
        }, 60000, 60000);

        weatherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
            }
        }, 0);

        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_UI);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void updateWeather() {

        WeatherService service = RetrofitUtils.getWeatherRertrofit().create(WeatherService.class);

        Call<WeatherWrapper> call = service.getWeather(WeatherUtils.OPEN_WEATHER_QUERY, WeatherUtils.OPEN_WEATHER_API_KEY, WeatherUtils.OPEN_WEATHER_UNIT);

        try {
            Response<WeatherWrapper> response = call.execute();

            if (response.isSuccess()) {

                updateWeatherLabels(response.body());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void updateWeatherLabels(WeatherWrapper wrapper) {

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(1);
        format.setMinimumFractionDigits(1);

        String currentTemp = format.format(wrapper.getMain().getTemp());

        dashboardWeatherCurrentTemp.setText(currentTemp + "ºC");

        String currentHumidity = format.format(wrapper.getMain().getHumidity());

        dashboardWeatherCurrentHumidity.setText(currentHumidity + "%");

        String maxTemp = format.format(wrapper.getMain().getMaxTemp());

        dashboardWeatherMaxTemp.setText(maxTemp + "ºC");

        String minTemp = format.format(wrapper.getMain().getMinTemp());

        dashboardWeatherMinTemp.setText(minTemp + "ºC");

        dashboardWeatherCityName.setText(wrapper.getName());

        if (wrapper.getWeathers() != null && wrapper.getWeathers().size() > 0) {

            dashboardWeatherCurrentWeather.setText(wrapper.getWeathers().get(0).getMain().toLowerCase());
        }

        String updatedAt = DateUtils.getRelativeDateTimeString(getActivity(), wrapper.getDate().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();


        dashboardWeatherUpdated.setText(updatedAt);
    }

    void sendMeasureData() {

        if (currentHumidity != null && currentTemperature != null) {

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
    }

    @Override
    public void onPause() {

        super.onPause();

        timer.cancel();
        timer.purge();

        weatherTimer.cancel();
        weatherTimer.purge();

        sensorManager.unregisterListener(this);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumFractionDigits(0);

        if (event.sensor.equals(temperatureSensor)) {

            Float temperature = event.values[0];

            DecimalFormat decimalFormat = new DecimalFormat("##.#");

            decimalFormat.setRoundingMode(RoundingMode.DOWN);

            Integer roundedTemp = (int) Math.floor(temperature.doubleValue());

            dashboardTemperatureTextView.setText(roundedTemp.toString() + ".");

            String decimal = String.valueOf(event.values[0]);

            try {

                decimal = decimal.substring(decimal.indexOf(".")).substring(1);

                dashboardTemperatureDecimalTextView.setText(decimal);

            } catch (Exception e) {

                dashboardTemperatureDecimalTextView.setText("0");
            }

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
