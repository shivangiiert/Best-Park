package com.example.tneeraja.mapsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class    MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
//PlaceSelectionListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude,longitude;
    static Place g_place;
    Context context = this;
    ListView listView;
    Location location;
    SlidingUpPanelLayout slidingUpPanelLayout;
    Object dataTransfer[] = new Object[1];
    static DataSnapshot snapshot;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference demoRef = rootRef.child("Sheet1");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_maps);
        Log.e("Checking order","CReate");

        //Initialize the Spinner
        final String[] select_qualification = {
                "Add Filters", "Distance", "Cost", "Availability"};
        Spinner spinner = findViewById(R.id.spinner_filters);

        ArrayList<StateVO> listVOs = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(select_qualification[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        MyAdapter myAdapter = new MyAdapter(MapsActivity.this, 0,
                listVOs);
        spinner.setAdapter(myAdapter);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //search bar for finding place ( with autocomplete)
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);



        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                g_place = place;
                Log.i(TAG, "Place: " + place.getName());
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getLatLng());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(markerOptions);
                LatLng queriedLocation = g_place.getLatLng();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(queriedLocation, 15.0f));

                dataTransfer[0] = mMap;
                Log.e("before getnearby!!!!!!", "before");
                showNearbyPlaces(queriedLocation);
//                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(context, queriedLocation, listView);
//                getNearbyPlacesData.execute(dataTransfer);
//                Log.e("Checking order","on create/onPlace selected");
//                Toast.makeText(MapsActivity.this, "Showing Nearby parking", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //sliding up panel for showing parking lots.
        listView = findViewById(R.id.mobile_list);


        slidingUpPanelLayout =  findViewById(R.id.sliding_layout);
        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.setAnchorPoint(0.5f); // slide up 50% then stop
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(point);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(markerOptions);

                showNearbyPlaces(point);
            }
        });

    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location1) {

        location=location1;
        //TODO: add getnearbyplaces
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        showNearbyPlaces(latLng);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        demoRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot;
                Log.e("Checking order", "Reached start");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String stringValue = ds.child("Address").getValue(String.class);

                }
                //check whether location permission is giving to app
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkLocationPermission()){


                        //check whether location is on
                        statusCheck();
                        if (isLocation())
                            check();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void check() {
        Log.e("che","print");
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(network_enabled){
            try {
                location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }catch (SecurityException e ){

            }

        }

        if(location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("lat = ", "" + latitude);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));//zoomTo(DEFAULT_ZOOM));
            if (client != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }

            showNearbyPlaces(latLng);
        }
    }

    public void statusCheck() {
        Log.e("inside statuscheck", "before is location enabled");
        if(!isLocationEnabled(this)){
            Log.e("inside", "is location enabled");
            buildAlertMessageNoGps();
        }
    }


    private void buildAlertMessageNoGps() {
        Log.e("inside", "buildalertmsg");
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(final android.content.DialogInterface dialog, final int id) {
                        startActivity(new android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(final android.content.DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
        Log.e("After alert ","msg");
    }

    boolean isLocation(){
        return isLocationEnabled(this);
    }


    public static boolean isLocationEnabled(android.content.Context context) {
        int locationMode;
        String locationProviders;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
            try {
                locationMode = android.provider.Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE);

            } catch (android.provider.Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != android.provider.Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !android.text.TextUtils.isEmpty(locationProviders);
        }


    }

    void showNearbyPlaces(LatLng latLng){
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(context, latLng, listView);
        Log.e("Checking order", "on Location changed");
        dataTransfer[0] = mMap;
        getNearbyPlacesData.execute(dataTransfer);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }



}
