package com.news.app.news;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationFragment extends Fragment {
    final String TAG = "LocationFrag";
    TextView resultTv, coordinateTv;
    final int LOCATION_PERMISSION_REQUEST = 1;
    FusedLocationProviderClient client;
    double latitude, longitude;
    final String WEATHER_SERVICE_LINK = "http://api.openweathermap.org/data/2.5/weather?id=2172797&APPID=5bda833ea98063658162aac5ac577075&units=metric&";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            } else getLocation();
        } else getLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            int hasLocationPermission = getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            } else getLocation();
        } else getLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
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
            } else getLocation();
        }
    }

    private void getLocation() {

        client = LocationServices.getFusedLocationProviderClient(getContext());
        LocationRequest request = LocationRequest.create();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);

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

        if(Build.VERSION.SDK_INT >= 23 && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED);
            client.requestLocationUpdates(request,callback,null);
    }
}


