package com.example.tneeraja.mapsdemo;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;



class GetNearbyPlacesData extends  AsyncTask<Object, String, String> {

    static private LatLng currentlatLng;
    static private GoogleMap mMap;
    static Context context;// = MapsActivity.getBaseContext();
    ListView listView; //  for listview
    static ArrayList<HashMap<String, String>> nearbyPlaceList = new ArrayList<>();
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    MapsActivity a;


    GetNearbyPlacesData()
    {

    }


    GetNearbyPlacesData(Context context, LatLng currentlatLng, ListView listView){
        this.context=context;
        this.currentlatLng = currentlatLng;
        this.listView = listView;
    }

    @Override
    protected String doInBackground(Object... objects){
        Log.e("checking","reached nearby places");
        mMap = (GoogleMap)objects[0];
        return null;
    }

    void add_filters(boolean[] isCheckedArray){
        Filters filters = new Filters(context,currentlatLng, mMap);
        if(nearbyPlaceList.isEmpty()) {
            Toast.makeText(context,"No Nearby Parking Found", Toast.LENGTH_LONG).show();
            return;
        }
        if(isCheckedArray[0] && isCheckedArray[1] && isCheckedArray[2]){
            filters.Distance_Cost_Availability_Filter();
        }
        else if(isCheckedArray[0] && isCheckedArray[1]){
            filters.Distance_Cost_Filter();
        }
        else if(isCheckedArray[0] && isCheckedArray[2]){
            filters.Distance_Availability_Filter();
        }
        else if(isCheckedArray[1] && isCheckedArray[2]){
            filters.Cost_Availability_Filter();
        }
        else if(isCheckedArray[0]){
            filters.Distance_Filter();
        }
        else if(isCheckedArray[1]){
            filters.Cost_Filter();
        }
        else if(isCheckedArray[2]){
            filters.Availability_Filter();
        }
    }
    @Override
    protected void onPostExecute(String s){
        nearbyPlaceList = new ArrayList<>();
        listView.setAdapter(null);
        DataSnapshot dataSnapshot=MapsActivity.snapshot;
        Log.e("Checking order", "on post execute" + dataSnapshot);
        builder.include(currentlatLng);
        if(dataSnapshot != null) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                double lat = Double.parseDouble(ds.child("Lat").getValue(String.class));
                double lng = Double.parseDouble(ds.child("long").getValue(String.class));

                String address = (ds.child("Address").getValue(String.class));
                String name = (ds.child("Name").getValue(String.class));
                String cost = (ds.child("Cost").getValue(String.class));
                String availability = (ds.child("Availability").getValue(String.class));


                Location locationA = new Location("point A");

                locationA.setLatitude(lat);
                locationA.setLongitude(lng);

                Location locationB = new Location("point B");

                locationB.setLatitude(currentlatLng.latitude);
                locationB.setLongitude(currentlatLng.longitude);

                float distance = locationA.distanceTo(locationB)/1000;


               // Location.distanceBetween(currentlatLng.latitude, currentlatLng.longitude, lat, lng, results);

                Log.e("distance ",distance+"");

                if (distance <= 3.0) {
                    Log.e("Checking order", "on post execute" + distance);
                    HashMap<String,String> h=new HashMap<>();
                    h.put("lat",lat+"");
                    h.put("lng",lng+"");
                    h.put("place_name",name);
                    h.put("address",address);
                    h.put("cost",cost);
                    h.put("availability", availability);
                    h.put("distance", distance+"");
                    nearbyPlaceList.add(h);



                    show(lat, lng, name);
                }


            }
        }
        Log.e("Checkinglist",nearbyPlaceList+"");
      //  LatLngBounds bounds = new LatLngBounds();
        if(nearbyPlaceList.isEmpty()){
            Toast.makeText(context,"No Nearby Parking Found ", Toast.LENGTH_LONG).show();
        }

        if(!nearbyPlaceList.isEmpty()) {
            Toast.makeText(context, "Showing Nearby parking", Toast.LENGTH_SHORT).show();

            LatLngBounds bounds = builder.build();

            int padding = 200;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }
    }

    private void show(double lat, double lng, String placeName) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng( lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOptions);
        builder.include(latLng);
        displayRadiusList();
    }

    private void displayRadiusList() {

        SimpleAdapter simpleAdapter = new SimpleAdapter(context, nearbyPlaceList, R.layout.slidingup_listview, new String[] {"place_name", "address", "availability", "cost", "distance"}, new int[] {R.id.ListViewName, R.id.ListViewAddress,  R.id.ListViewAvailability, R.id.ListViewCost, R.id.ListViewDistance});
        listView.setAdapter(simpleAdapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){

                Object o = listView.getItemAtPosition(position);
                Log.d("Object hashmap", ""+o);

                HashMap<String, String> selectedItem = (HashMap<String, String>)o;

                Log.d("selecteditem", selectedItem+"");
                Toast.makeText(context, selectedItem.get("place_name"), Toast.LENGTH_LONG).show();

                Direction_time_display direction_time_display= new Direction_time_display(context,mMap, currentlatLng, selectedItem );
                direction_time_display.sendRequest();

            }
        });

    }


}

