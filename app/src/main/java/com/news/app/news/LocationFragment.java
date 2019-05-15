package com.news.app.news;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationFragment extends Fragment {
    final String TAG = "LocationFrag";
    TextView resultTv, coordinateTv;
    final int LOCATION_PERMISSION_REQUEST = 1;
    final int REQUEST_CHECK_SETTINGS = 2;
    FusedLocationProviderClient client;
    double latitude, longitude;
    final String WEATHER_SERVICE_LINK = "http://api.openweathermap.org/data/2.5/weather?id=2172797&APPID=5bda833ea98063658162aac5ac577075&units=metric&";//lat=32.08337216&lon=34.77137702";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_frag, container, false);
        resultTv = rootView.findViewById(R.id.result_tv);
        coordinateTv = rootView.findViewById(R.id.coordinate_tv);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(hasLocationPermission()){
            getLocation();
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Attention!").setMessage("You must give the Location permission in order to see the weather")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                            startActivity(intent);
                        }
                    }).show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            for(int permissionResult : grantResults) {
                if(permissionResult != PackageManager.PERMISSION_GRANTED) return;
            }
            getLocation();;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void checkSettings(LocationRequest request) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getContext()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {

        client = LocationServices.getFusedLocationProviderClient(getContext());
        LocationRequest request = LocationRequest.create();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        checkSettings(request);

        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                coordinateTv.setText(location.getLatitude() + " , " + location.getLongitude());

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                RequestQueue queue = Volley.newRequestQueue(getContext());
                StringRequest request1= new StringRequest(WEATHER_SERVICE_LINK + "lat=" + latitude + "&lon=" + longitude, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            StringBuffer sb = new StringBuffer();
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray weatherArr = rootObject.getJSONArray("weather");
                            if(weatherArr.length()>0){
                                JSONObject bestResult = weatherArr.getJSONObject(0);
                                String mainWeather = bestResult.getString("description");
                                sb.append(mainWeather+"\n");
                            }
                            JSONObject mainObject = rootObject.getJSONObject("main");
                            sb.append("Current temp:" + mainObject.getDouble( "temp") + ", Max temp: "+ mainObject.getInt("temp_max") + ", Min temp: " + mainObject.getInt("temp_min") + "\n");
                            sb.append(rootObject.getString("name"));

                            resultTv.setText(sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                queue.add(request1);
                queue.start();
            }
        };

        client.requestLocationUpdates(request,callback,null);
    }
}


