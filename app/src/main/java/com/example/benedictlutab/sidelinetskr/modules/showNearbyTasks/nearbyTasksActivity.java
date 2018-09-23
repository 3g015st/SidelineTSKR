package com.example.benedictlutab.sidelinetskr.modules.showNearbyTasks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.benedictlutab.sidelinetskr.R;
import com.example.benedictlutab.sidelinetskr.helpers.apiRouteUtil;
import com.example.benedictlutab.sidelinetskr.helpers.fontStyleCrawler;
import com.example.benedictlutab.sidelinetskr.modules.tasksFeed.viewTaskDetails.taskDetailsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class nearbyTasksActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener
{
    @BindView(R.id.btnBack) Button btnBack;
    @BindView(R.id.ivDeviceLocation) ImageView ivDeviceLocation;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String FINE_LOCATION         = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION       = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final float DEFAULT_ZOOM           = 17f;
    private Boolean isLocationPermissionGranted       = false;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shownearbytaks_activity_nearby_tasks);
        ButterKnife.bind(this);

        changeFontFamily();
        getLocationPermission();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @OnClick(R.id.btnBack)
    public void onBackPressed()
    {
        this.finish();
    }

    private void getLocationPermission()
    {
        Log.e("getLocationPermission: ", "Getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                isLocationPermissionGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        isLocationPermissionGranted = false;

        switch(requestCode)
        {
            case LOCATION_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    for(int i = 0; i<grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            isLocationPermissionGranted = false;
                            Log.e("onReqPermissionsRes: ", "FAILED!");
                            return;
                        }
                    }
                    isLocationPermissionGranted = true;
                    Log.e("onReqPermissionsRes: ", "SUCCESS!");
                    // Initialize map.
                    initMap();
                }
            }
        }
    }

    private void initMap()
    {
        Log.e("initMap: ", "Initializing map...");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(nearbyTasksActivity.this);
    }

    private void changeFontFamily()
    {
        // Change Font Style.
        fontStyleCrawler fontStyleCrawler = new fontStyleCrawler(getAssets(), "fonts/avenir.otf");
        fontStyleCrawler.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }

    @OnClick(R.id.ivDeviceLocation)
    public void getDeviceLocation()
    {
        Log.e("getDeviceLocation: ", "Fetching device's current location...");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try
        {
            if(isLocationPermissionGranted)
            {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if(task.isSuccessful() && task.getResult() != null)
                        {
                            Log.e("getDeviceLocation: ", "LOCATION FOUND!");
                            Location currentLocation = (Location) task.getResult();

                            // Geo locate
                            Log.e("getDeviceLocation: ", "geoLocating now...!");
                            Geocoder geocoder  = new Geocoder(nearbyTasksActivity.this);
                            List<Address> list = new ArrayList<>();

                            try
                            {
                                list = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                            }
                            catch(IOException ex)
                            {
                                Log.e("getDeviceLocation:", ex.toString());
                            }

                            if(list.size() > 0)
                            {
                                Address address = list.get(0);
                                Log.e("getDeviceLocation: ", address.getAddressLine(0));

                                latitude  = address.getLatitude();
                                longitude = address.getLongitude();

                                // Move camera to the found location
                                if(moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM))
                                {
                                    getAllPoints();
                                }
                            }
                        }
                        else
                        {
                            Log.e("getDeviceLocation: ", "unable to find location.");
                        }
                    }
                });
            }
        }
        catch(SecurityException ex)
        {
            Log.e("getDeviceLocation(ex): ", ex.getMessage());
        }
    }

    private boolean moveCamera(LatLng latLng, float zoom)
    {
        Log.e("moveCamera: ", String.valueOf(latLng.latitude)+" , "+String.valueOf(latLng.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        return true;
    }

    private void getAllPoints()
    {
        Log.e("getAllPoints: ", "STARTED!");
        apiRouteUtil apiRouteUtil = new apiRouteUtil();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiRouteUtil.URL_GET_ALL_POINTS, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String ServerResponse)
            {
                try
                {
                    Log.e("Server Response: ", ServerResponse);
                    JSONArray jsonArray = new JSONArray(ServerResponse);
                    for(int x = 0; x < jsonArray.length(); x++)
                    {
                        final JSONObject jsonObject = jsonArray.getJSONObject(x);

                        Double LATITUDE = Double.parseDouble(jsonObject.getString("latitude"));
                        Double LONGITUDE = Double.parseDouble(jsonObject.getString("longitude"));

                        Log.e("Task's LAT-LONG: ", String.valueOf(LATITUDE) +", "+ String.valueOf(LONGITUDE));

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(LATITUDE, LONGITUDE))
                                .title(jsonObject.getString("title")).snippet("Fee: PHP " + jsonObject.getString("task_fee")).icon(BitmapDescriptorFactory.fromResource(R.drawable.main_img_pin_logo)));

                        // Set the tag to task id
                        Object taskIdData = new Object();
                        taskIdData = jsonObject.getString("task_id");
                        marker.setTag(taskIdData);

                        marker.setVisible(false);

                        Log.e("USER'S LAT LONG: ", String.valueOf(latitude) +", "+ String.valueOf(longitude));

                        // Show nearby tasks only (100 Degrees Radius)
                        if (SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude), marker.getPosition()) < 1000)
                        {
                            Log.e("isNearbyTask: ", "TRUE");
                            marker.setVisible(true);
                        }
                        else
                        {
                            promptNoNearby();
                        }

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
                        {
                            @Override
                            public void onInfoWindowClick(Marker marker)
                            {
                                String TASK_ID = String.valueOf(marker.getTag());
                                Log.e("TASK-ID: ", TASK_ID);

                                // Go to task details activity
                                Intent intent = new Intent(getApplicationContext(), taskDetailsActivity.class);
                                intent.putExtra("TASK_ID", TASK_ID);
                                startActivity(intent);
                            }
                        });
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("Catch Response: ", e.toString());
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Log.e("Error Response: ", volleyError.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Creating Map String Params.
                Map<String, String> Parameter = new HashMap<String, String>();

                Parameter.put("STATUS", "COMPLETED");

                return Parameter;
            }
        };

        // Add the StringRequest to Queue.
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Log.e("onMapReady: ", "Map - READY");
        mMap = googleMap;

        if (isLocationPermissionGranted)
        {
            getDeviceLocation();

            // Add blue dot marker.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            mMap.setMyLocationEnabled(true);
        }
    }

    private void promptNoNearby()
    {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("No nearby tasks").setContentText("We're sorry but there are no nearby tasks available :(")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                    {
                        finish();
                    }
                })
                .show();
    }
}
