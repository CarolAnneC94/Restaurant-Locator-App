package com.trees.locationrestaurants;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.internal.experience.ExperienceEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {
    ArrayList<String> name;
    ArrayList<String> address;
    ArrayList<String> district;
    ArrayList<String> contact;
    ArrayList<String> status;
    ArrayList<String> remakrs;
    ArrayList<String> loc_lat;
    ArrayList<String> loc_lng;
    ArrayList<String> id;
    ArrayList<Bitmap> image;
    List<ArrayList<String>> sort_loc;
    SpotsDialog progressDialog;
    private static final long MIN_TIME = 400;
    private static final long MIN_DISTANCE = 1000;
    private static CircleOptions circleOptions;
    private CircleOptions circleOptions1;
    public static GoogleMap mMap;
    private static MarkerOptions mo;
    private static Marker marker;
    private static Marker marker1;
    public boolean firstRun = true;
    LocationManager locationManager;
    Button logout;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new SpotsDialog(MainActivity.this, R.style.Custom);

        if (!CheckPermissions()) {
            SpecificPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //float[] distance = new float[2];
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("Current Location");
        if (!isLocationEnabled()){
            showAlert(1);
        }
        else{
            progressDialog.show();
        }
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        if(!isNetworkAvailable()){
            showAlertNetwork();
        }
        if(!isLocationEnabled()){
            showAlertLocation();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    private void showAlertNetwork() {
        String message, title, btnText;
            message = "Please connect your device to internet";
            title = "Connection Failed";
            btnText = "Grant";

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        finish();
                    }
                });
        dialog.show();
    }

    private void showAlertLocation() {
        String message, title, btnText;

        message = "Please enable the location";
        title = "Location Error";
        btnText = "Grant";

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        finish();
                    }
                });
        dialog.show();
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
    }
    public void allLocations(double lat, double lng) {
        name = new ArrayList<String>();
        address = new ArrayList<String>();
        district = new ArrayList<String>();
        contact = new ArrayList<String>();
        status = new ArrayList<String>();
        remakrs = new ArrayList<String>();
        loc_lat = new ArrayList<String>();
        loc_lng = new ArrayList<String>();
        image = new ArrayList<Bitmap>();

        OkHttpClient client = new OkHttpClient();
//        String url = "https://reqres.in/api/users?page=2";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&location="+String.valueOf(lat)+","+String.valueOf(lng)+"&radius=5000&type=restaurant&key=AIzaSyDwHTdvrd-10oRHfS1Zq_-tyIOm5KHonuI";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    Log.d("pleasecheck", myResponse);
                    try {
                        firstRun = false;
                        JSONObject json = new JSONObject(myResponse);
                        String getCarInfor = json.getString("results");
                        JSONArray jsonArray = new JSONArray(getCarInfor);

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            name.add(jsonObject.getString("name"));
                            try {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("opening_hours");
                                if (jsonObject1.getBoolean("open_now")) {
                                    status.add("Open");
                                } else {
                                    status.add("Close");
                                }
                            }
                            catch (Exception ex){
                                status.add("Open");
                            }
                            JSONObject jsonObject2 = jsonObject.getJSONObject("geometry");
                            JSONObject jsonObject3 = jsonObject2.getJSONObject("location");
                            loc_lat.add(jsonObject3.getString("lat"));
                            loc_lng.add(jsonObject3.getString("lng"));

                        }
                            progressDialog.cancel();

                        DisplayMarkers();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void DisplayMarkers(){
        MainActivity.this.runOnUiThread(new Runnable(){
            public void run(){
                for (int i = 0; i < name.size(); i++) {
                    Log.e("check", loc_lat.get(i) + "," + loc_lng.get(i));
                    LatLng latLng = new LatLng(Double.parseDouble(loc_lat.get(i)), Double.parseDouble(loc_lng.get(i)));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Name: " + name.get(i));
                    markerOptions.snippet("Status: " + status.get(i));
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.addMarker(markerOptions);
                }
            }
        });


    }
    public void c1(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");

    }
    public void ChangeMapType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void onZoom(View view) {
        if (view.getId() == R.id.Bzoomin) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }

        if (view.getId() == R.id.Bzoomout) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mo);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng latLng) {
                final android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Please Confirm");
                alertDialog.setMessage("Do you want to add place here?");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(MainActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MainActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MainActivity.this);
                snippet.setTextColor(Color.GRAY);
                title.setTypeface(null, Typeface.BOLD);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        if(firstRun) {
//            allLocations();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

        if (circleOptions == null) {
            Toast toast5 = Toast.makeText(getApplicationContext(), "LOCATOR BEGIN", Toast.LENGTH_LONG);
            toast5.show();
        }

        else {
            Marker locationMarker = mMap.addMarker(mo);
            locationMarker.showInfoWindow();
            float[] distance = new float[2];

            Location.distanceBetween(marker.getPosition().latitude,
                    marker.getPosition().longitude, circleOptions.getCenter().latitude,
                    circleOptions.getCenter().longitude, distance);
                    Toast toast3 = Toast.makeText(getApplicationContext(), "distance[0]", Toast.LENGTH_LONG);
                    toast3.show();
        }
        allLocations(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
    private boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void SpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void searchLocation(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                allLocations(address.getLatitude(), address.getLongitude());
               Toast.makeText(getApplicationContext(),address.getLatitude()+" "+address.getLongitude(), Toast.LENGTH_LONG).show();
            }
            catch (Exception ex){
            }
        }
    }
}

