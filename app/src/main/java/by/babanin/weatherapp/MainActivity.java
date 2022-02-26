package by.babanin.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import by.babanin.weatherapp.service.WeatherService;

public class MainActivity extends AppCompatActivity {

    private EditText cityNameField;
    private Button weatherButton;
    private TextView weatherResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameField = findViewById(R.id.cityNameField);
        weatherButton = findViewById(R.id.weatherButton);
        weatherResult = findViewById(R.id.weatherResult);

        weatherButton.setOnClickListener(view -> {
            String cityName = cityNameField.getText().toString().trim();
            if (cityName.isEmpty()) {
                Toast.makeText(this, R.string.city_name_is_empty, Toast.LENGTH_LONG).show();
            } else {
                new WeatherService(weatherResult).execute(cityName);
            }
        });
    }
}