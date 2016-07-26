package com.devdivr.awarenesstest.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.devdivr.awarenesstest.R;
import com.devdivr.awarenesstest.helper.AwarenessHelper;
import com.devdivr.awarenesstest.network.NetworkTask;
import com.devdivr.awarenesstest.model.DaumAddress;
import com.devdivr.awarenesstest.network.Callback;
import com.devdivr.awarenesstest.network.Url;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQ_PERMISSION_LOCATION = 1;

    private GoogleApiClient googleApiClient;

    private TextView tvActivity, tvLocation, tvAddress, tvPlaces, tvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                setRefreshText();
                getDetectedActivity();
                getLocation();
                getPlaces();
                getWeather();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDetectedActivity();
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "GoogleApi suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast.makeText(MainActivity.this, "GoogleApi not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQ_PERMISSION_LOCATION:

                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            getLocation();
                            getPlaces();
                            getWeather();
                        } else {
                            checkPermissions();
                        }
                    }
                }
                break;
        }
    }

    private void initViews() {
        tvActivity = (TextView) findViewById(R.id.tv_activity);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvPlaces = (TextView) findViewById(R.id.tv_places);
        tvWeather = (TextView) findViewById(R.id.tv_weather);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_LOCATION);
        } else {
            getLocation();
            getPlaces();
            getWeather();
        }
    }

    private void getDetectedActivity() {

        Awareness.SnapshotApi.getDetectedActivity(googleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get the current activity.");
                            tvActivity.setText("Could not get the current activity.");
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();
                        String activity = String.format("Type : %s, Confidence %d",
                                AwarenessHelper.getActivityType(probableActivity.getType()), probableActivity.getConfidence());
                        Log.i(TAG, activity);
                        tvActivity.setText(activity);
                    }
                });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Awareness.SnapshotApi.getLocation(googleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get location.");
                            tvLocation.setText("Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        String coordinates = String.format("Latitude %.2f, Longitude %.2f", location.getLatitude(), location.getLongitude());
                        Log.i(TAG, coordinates);
                        tvLocation.setText(coordinates);
                        getAddressByLocation(location);
                    }
                });
    }

    private void getPlaces() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Awareness.SnapshotApi.getPlaces(googleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get places.");
                            tvPlaces.setText("Could not get places.");
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                        if (placeLikelihoodList != null) {
                            // Show the top 5 possible location results.
                            for (int i = 0; i < 5; i++) {
                                PlaceLikelihood p = placeLikelihoodList.get(i);
                                String place = p.getPlace().getName().toString() + ", likelihood: " + p.getLikelihood();
                                Log.i(TAG, place);
                                sb.append(place);
                                sb.append(i < 5 ? ", " : "");
                            }
                            tvPlaces.setText(sb.toString());
                        } else {
                            tvPlaces.setText("Nothing");
                        }
                    }
                });
    }

    private void getWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Awareness.SnapshotApi.getWeather(googleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get weather.");
                            tvWeather.setText("Could not get weather.");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        String weatherInfo = AwarenessHelper.getWeatherInfo(weather);
                        Log.i(TAG, "Weather: " + weatherInfo);
                        tvWeather.setText("" + weatherInfo);
                    }
                });
    }

    private void getAddressByLocation(Location location) {
        String url = Url.getDaumApiUrl(getString(R.string.daum_api_key), location.getLongitude(), location.getLatitude());
        new NetworkTask<>(DaumAddress.class, callback).execute(url);
    }

    private Callback<DaumAddress> callback = new Callback<DaumAddress>() {
        @Override
        public void onSuccess(DaumAddress address) {
            tvAddress.setText(address.getAddress());
        }

        @Override
        public void onFailure() {
            tvAddress.setText("Could not get address");
        }

        @Override
        public void onComplete() {}
    };

    private void setRefreshText() {
        tvActivity.setText(R.string.getting_activity);
        tvLocation.setText(R.string.getting_location);
        tvAddress.setText(R.string.getting_address);
        tvPlaces.setText(R.string.getting_places);
        tvWeather.setText(R.string.getting_weather);
    }
}
