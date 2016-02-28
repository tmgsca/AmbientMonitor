package com.dev.thiago.ambientmonitoring.view.fragment;

import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.thiago.ambientmonitoring.R;
import com.dev.thiago.ambientmonitoring.SensorChangedEvent;
import com.dev.thiago.ambientmonitoring.enums.Season;
import com.dev.thiago.ambientmonitoring.model.Room;
import com.dev.thiago.ambientmonitoring.model.SeasonParameters;
import com.dev.thiago.ambientmonitoring.model.WeatherWrapper;
import com.dev.thiago.ambientmonitoring.service.WeatherService;
import com.dev.thiago.ambientmonitoring.util.BusProvider;
import com.dev.thiago.ambientmonitoring.util.DeviceType;
import com.dev.thiago.ambientmonitoring.util.MeasurerUtils;
import com.dev.thiago.ambientmonitoring.util.RetrofitUtils;
import com.dev.thiago.ambientmonitoring.util.SeasonUtils;
import com.dev.thiago.ambientmonitoring.util.SessionUtils;
import com.dev.thiago.ambientmonitoring.util.WeatherUtils;
import com.dev.thiago.ambientmonitoring.view.MainActivity;
import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import me.grantland.widget.AutofitHelper;
import retrofit2.Call;
import retrofit2.Response;

@EFragment(R.layout.fragment_dashboard)
@OptionsMenu(R.menu.menu_rooms)
public class DashboardFragment extends GenericFragment {

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

    @ViewById
    ImageView dashboardTemperatureImageView;

    @ViewById
    ImageView dashboardHumidityImageView;

    Timer timer;

    Timer weatherTimer;

    Float currentTemperature;

    Float currentHumidity;

    Integer roomId;

    @AfterViews
    void afterViews() {

        SessionUtils.setDeviceType(getActivity(), DeviceType.MEASURER);

        AutofitHelper.create(dashboardHumidityTextView);

        setFragmentTitle();
    }

    @Override
    public void onResume() {

        super.onResume();

        BusProvider.getInstance().register(this);

        resumeTimers();

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {

        super.onPause();

        BusProvider.getInstance().unregister(this);

        timer.cancel();
        timer.purge();

        weatherTimer.cancel();
        weatherTimer.purge();

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setFragmentTitle() {

        Realm realm = Realm.getInstance(getActivity());

        Room room = getRoom(realm);

        if (room != null) {

            setTitle(room.getName());

            realm.close();

        }
    }

    private Room getRoom(Realm realm) {

        roomId = MeasurerUtils.getTrackedRoomId(getActivity());

        return realm.where(Room.class).equalTo("id", roomId).findFirst();
    }

    private void resumeTimers() {
        timer = new Timer();

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
    protected void updateWeatherLabels(WeatherWrapper wrapper) {

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
    }

    private void updateColorsByMeasuredData() {

        Realm realm = Realm.getInstance(getActivity());

        Room room = getRoom(realm);

        SeasonParameters summerParameters = getSummerParameters(room);

        SeasonParameters winterParameters = getWinterParameters(room);

        Integer temperatureColor;

        Integer humidityColor;

        switch (SeasonUtils.getSeason()) {

            case SUMMER:

                if (currentTemperature != null)
                    temperatureColor = summerParameters.getColorByTemperature(currentTemperature, getActivity());

                if (currentHumidity != null)
                    humidityColor = summerParameters.getColorByHumidity(currentHumidity, getActivity());

            case WINTER:

                if (currentTemperature != null)
                    temperatureColor = summerParameters.getColorByTemperature(currentTemperature, getActivity());

                if (currentHumidity != null)
                    humidityColor = summerParameters.getColorByHumidity(currentHumidity, getActivity());

            default:

                temperatureColor = ContextCompat.getColor(getActivity(), R.color.normal_color);

                humidityColor = ContextCompat.getColor(getActivity(), R.color.normal_color);
        }

        dashboardHumidityImageView.setColorFilter(humidityColor);

        dashboardTemperatureImageView.setColorFilter(temperatureColor);

        realm.close();
    }

    private SeasonParameters getSummerParameters(Room room) {

        SeasonParameters parameters = new SeasonParameters(Season.SUMMER);

        parameters.setMaxTemp(room.getMaxSummerTemp());

        parameters.setMinTemp(room.getMinSummerTemp());

        parameters.setMaxHumidity(room.getMaxSummerHumidity());

        parameters.setMinHumidity(room.getMinSummerHumidity());

        return parameters;
    }

    private SeasonParameters getWinterParameters(Room room) {

        SeasonParameters parameters = new SeasonParameters(Season.WINTER);

        parameters.setMaxTemp(room.getMaxWinterTemp());

        parameters.setMinTemp(room.getMinWinterTemp());

        parameters.setMaxHumidity(room.getMaxWinterHumidity());

        parameters.setMinHumidity(room.getMinWinterHumidity());

        return parameters;
    }

    @Subscribe
    public void onSensorChanged(SensorChangedEvent event) {

        if (event.getTemperature() != null) {

            Float temperature = event.getTemperature();

            Integer roundedTemp = (int) Math.floor(temperature.doubleValue());

            dashboardTemperatureTextView.setText(roundedTemp.toString() + ".");

            String decimal = String.valueOf(event.getTemperature());

            try {

                decimal = decimal.substring(decimal.indexOf(".")).substring(1, 2);

                dashboardTemperatureDecimalTextView.setText(decimal);

            } catch (Exception e) {

                dashboardTemperatureDecimalTextView.setText("0");
            }

            currentTemperature = temperature;
        }

        if (event.getHumidity() != null) {

            NumberFormat numberFormat = NumberFormat.getNumberInstance();

            numberFormat.setMaximumFractionDigits(0);

            numberFormat.setMinimumFractionDigits(0);

            Float humidity = event.getHumidity();

            dashboardHumidityTextView.setText(numberFormat.format(humidity) + "%");

            currentHumidity = humidity;
        }

        updateColorsByMeasuredData();
    }


    @OptionsItem(R.id.action_settings)
    void showSettings() {

        MainActivity activity = (MainActivity) getActivity();

        activity.showSettingsFragment(true);
    }

}
