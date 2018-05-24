package com.example.tneeraja.mapsdemo;

/*
 * Created by insomnia on 4/16/18.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.tneeraja.mapsdemo.Modules.DirectionFinder;
import com.example.tneeraja.mapsdemo.Modules.DirectionFinderListener;
import com.example.tneeraja.mapsdemo.Modules.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Direction_time_display extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,DirectionFinderListener,
        LocationListener
{

    static  String duration1="",distance1="";
    private LatLng originLatLng,destination_latLng;
    private static GoogleMap mMap;
    private List<Polyline> polylinePaths;
    private HashMap<String,String> destination;
    static Context context;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    Direction_time_display(Context context,GoogleMap mMap, LatLng currentLatLng, HashMap<String, String> destination){
        this.mMap=mMap;
        this.context=context;
        this.originLatLng = currentLatLng;
        this.destination = destination;
        double des_longitude = Double.parseDouble( destination.get("lng"));
        double des_latitude = Double.parseDouble( destination.get("lat"));
        destination_latLng = new LatLng (des_latitude,des_longitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void sendRequest() {




        try {
            new DirectionFinder( this, originLatLng, destination_latLng).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }


    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes)
    {
        polylinePaths = new ArrayList<>();
        //originMarkers = new ArrayList<>();
      //  destinationMarkers = new ArrayList<>();

            mMap.clear();

        for (Route route : routes) {

            Log.e("Accessed","");
            duration1 =route.duration.text+"";
            distance1 =route.distance.text+"";

            Log.e("Display",""+duration1+""+distance1);
            TextView txtView =  ((Activity)context).findViewById(R.id.textViewDuration);
            txtView.setText(""+duration1);
            TextView txtView1 =  ((Activity)context).findViewById(R.id.textViewDistance);
            txtView1.setText(""+distance1);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destination_latLng);
            markerOptions.title(destination.get("place_name"));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //   Log.e("latlng", lat + " " + lng + " "+ );
            //getAddress(lat, lng, context);
            builder.include(markerOptions.getPosition());
            mMap.addMarker(markerOptions);

            MarkerOptions markerOptions_origin = new MarkerOptions();
            markerOptions_origin.position(originLatLng);
            markerOptions_origin.title("Current Location");
            markerOptions_origin.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            builder.include(markerOptions_origin.getPosition());
            mMap.addMarker(markerOptions_origin);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                builder.include(route.points.get(i));//.getPosition());
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
        LatLngBounds bounds = builder.build();
        int padding = 200; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }



}
