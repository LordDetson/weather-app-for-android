package by.babanin.weatherapp.service;

import android.os.AsyncTask;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import by.babanin.weatherapp.domain.GeoCode;

public class WeatherService extends AsyncTask<String, String, String> {

    private static final String API_KEY = "601623878fd556799ead8cf7fb6dcafb";

    private static final String GEOCODING_ENDPOINT_FORMAT = "https://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s";
    private static final String CURRENT_WEATHER_DATA_ENDPOINT_FORMAT = "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&appid=%s";

    private final TextView resultTextView;

    public WeatherService(TextView resultTextView) {
        this.resultTextView = resultTextView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        resultTextView.setText("Processing...");
    }

    @Override
    protected String doInBackground(String... strings) {
        String cityName = strings[0];
        GeoCode geoCode = getGeoCode(cityName);
        if (geoCode != null) {
            Double temperature = getTemperature(geoCode);
            if (temperature != null) {
                return "Temperature: " + temperature + " ℃";
            }
        }
        return null;
    }

    @Nullable
    private Double getTemperature(GeoCode geoCode) {
        if (geoCode != null) {
            URL weatherDataRequest = createWeatherDataRequest(geoCode);
            if (weatherDataRequest != null) {
                InputStream inputStream = sendRequest(weatherDataRequest);
                if (inputStream != null) {
                    return readTemperature(inputStream);
                }
            }
        }
        return null;
    }

    @Nullable
    private GeoCode getGeoCode(String cityName) {
        URL geocodingRequest = createGeocodingRequest(cityName);
        if (geocodingRequest != null) {
            InputStream inputStream = sendRequest(geocodingRequest);
            if (inputStream != null) {
                return readGeoCode(inputStream);
            }
        }
        return null;
    }

    @Nullable
    private URL createGeocodingRequest(String cityName) {
        URL geocodingUrl = null;
        try {
            geocodingUrl = new URL(String.format(GEOCODING_ENDPOINT_FORMAT, cityName, API_KEY));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return geocodingUrl;
    }

    private URL createWeatherDataRequest(GeoCode geoCode) {
        URL currentWeatherDataUrl = null;
        try {
            currentWeatherDataUrl = new URL(String.format(CURRENT_WEATHER_DATA_ENDPOINT_FORMAT, geoCode.getLatitude(), geoCode.getLongitude(), API_KEY));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return currentWeatherDataUrl;
    }

    @Nullable
    private InputStream sendRequest(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Nullable
    private GeoCode readGeoCode(InputStream inputStream) {
        String response = getResponse(inputStream);
        if (response != null && !response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                double lat = jsonObject.getDouble("lat");
                double lon = jsonObject.getDouble("lon");
                return new GeoCode(lat, lon);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Double readTemperature(InputStream inputStream) {
        String response = getResponse(inputStream);
        if (response != null && !response.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getJSONObject("main").getDouble("temp");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    private String getResponse(InputStream inputStream) {
        try (BufferedReader resultReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = resultReader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null && !result.isEmpty()) {
            resultTextView.setText(result);
        } else {
            resultTextView.setText("Error");
        }
    }
}
