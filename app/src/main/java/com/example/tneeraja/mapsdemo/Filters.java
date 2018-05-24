package com.example.tneeraja.mapsdemo;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import static com.example.tneeraja.mapsdemo.GetNearbyPlacesData.nearbyPlaceList;

public class Filters {
    HashMap<String,Double> minimumMaximum;
    static private GoogleMap mMap;
    LatLng currentlatLng;
    static Context context;

    Filters(Context context,LatLng currentLatLng, GoogleMap mMap) {
        this.context=context;
        this.currentlatLng=currentLatLng;
        this.mMap=mMap;
        minimumMaximum = new HashMap<String, Double>();
        minimumMaximum.putAll(minimumMaximumDistance());
        minimumMaximum.putAll(minimumMaximumCost());
        minimumMaximum.putAll(minimumMaximumAvailability());
    }




    private HashMap<String, Double> minimumMaximumDistance(){
        HashMap<String,String> googlePlace;
        double lat,lng,min=0,max=9999999.0;
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);


            lat = Double.parseDouble( googlePlace.get("lat"));
            lng = Double.parseDouble( googlePlace.get("lng"));
            Location locationA = new Location("Current Location");

            locationA.setLatitude(currentlatLng.latitude);
            locationA.setLongitude(currentlatLng.longitude);

            Location locationB = new Location("Destination");

            locationB.setLatitude(lat);
            locationB.setLongitude(lng);

            float distance = locationA.distanceTo(locationB);
            if(distance<min)
            {
                min=distance;
            }
            if(distance>max)
            {
                max=distance;
            }

        }
        HashMap<String, Double> minMax = new HashMap<>();
        minMax.put("minimumDistance", min);
        minMax.put("maximumDistance", max);
        return minMax;
    }

     private HashMap<String,Double> minimumMaximumCost(){
        double cost,min_cost=99999999.0;
        double max_cost=0.0;
        HashMap<String, String> googlePlace;
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);


            cost = Double.parseDouble( googlePlace.get("cost"));

            if(cost<min_cost)
            {
                min_cost=cost;
            }
            if(cost>max_cost)
            {
                max_cost=cost;
            }
        }
        HashMap<String, Double> minMax = new HashMap<>();
        minMax.put("minimumCost", min_cost);
        minMax.put("maximumCost", max_cost);
        return minMax;

    }

    private HashMap<String,Double> minimumMaximumAvailability (){
        double availability,max_availability=0.0;
        double min_availability=9999999999.0;
        HashMap<String, String> googlePlace;
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);

            availability = Double.parseDouble( googlePlace.get("availability"));

            if(availability>max_availability)
            {
                max_availability=availability;
            }
            if(availability<min_availability)
            {
                min_availability=availability;
            }
        }
        HashMap<String, Double> minMax = new HashMap<>();
        minMax.put("minimumAvailability", min_availability);
        minMax.put("maximumAvailability", max_availability);
        return minMax;

    }



    public  void Distance_Filter()
    {
        double min=9999999999.0;
        double max=0.0;
        double lat,lng;
        HashMap<String, String> googlePlace;
        HashMap<String, String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);


            lat = Double.parseDouble( googlePlace.get("lat"));
            lng = Double.parseDouble( googlePlace.get("lng"));
            Location locationA = new Location("Current Location");

            locationA.setLatitude(currentlatLng.latitude);
            locationA.setLongitude(currentlatLng.longitude);

            Location locationB = new Location("Destination");

            locationB.setLatitude(lat);
            locationB.setLongitude(lng);

            float distance = locationA.distanceTo(locationB);
            if(distance<min)
            {
                min_googlePlace = googlePlace;
                min=distance;
            }
            if(distance>max)
            {
                max=distance;
            }

        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng,min_googlePlace);
        display.sendRequest();

    }

    public  void Cost_Filter()
    {
        double cost,min_cost=99999999.0;
        double max_cost=0.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);


            cost = Double.parseDouble( googlePlace.get("cost"));

            if(cost<min_cost)
            {
                min_googlePlace=googlePlace;
                min_cost=cost;
            }
            if(cost>max_cost)
            {
                max_cost=cost;
            }
        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, min_googlePlace);
        display.sendRequest();

    }

    public  void Availability_Filter()
    {
        double availability,max_availability=0.0;
        double min_availability=9999999999.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> max_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);

            availability = Double.parseDouble( googlePlace.get("availability"));

            if(availability>max_availability)
            {
                max_googlePlace=googlePlace;
                max_availability=availability;
            }
            if(availability<min_availability)
            {
                min_availability=availability;
            }
        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, max_googlePlace);
        display.sendRequest();

    }

    public  void Distance_Cost_Filter()
    {
        double cost, lat, lng, distance;
        double normalized_distance;
        double normalized_cost;
        double weightage;
        double min_weightage=99999999.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);

            lat = Double.parseDouble( googlePlace.get("lat"));
            lng = Double.parseDouble( googlePlace.get("lng"));
            Location locationA = new Location("Current Location");

            locationA.setLatitude(currentlatLng.latitude);
            locationA.setLongitude(currentlatLng.longitude);

            Location locationB = new Location("Destination");

            locationB.setLatitude(lat);
            locationB.setLongitude(lng);

            distance = locationA.distanceTo(locationB);

            cost = Double.parseDouble( googlePlace.get("cost"));

            normalized_distance=(distance-minimumMaximum.get("minimumDistance"))/(minimumMaximum.get("maximumDistance")-minimumMaximum.get("minimumDistance"));
            normalized_cost=(cost-minimumMaximum.get("minimumCost"))/(minimumMaximum.get("maximumCost")-minimumMaximum.get("minimumCost"));

            weightage=0.5*normalized_distance+0.5*normalized_cost;
            if(weightage<min_weightage)
            {
                min_googlePlace=googlePlace;
                min_weightage=weightage;
            }
        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, min_googlePlace);
        display.sendRequest();
    }
    public  void Distance_Availability_Filter()
    {
        double availability,lat, lng, distance;
        double normalized_distance;
        double normalized_availability;
        double weightage;
        double min_weightage=99999999.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);

            lat = Double.parseDouble( googlePlace.get("lat"));
            lng = Double.parseDouble( googlePlace.get("lng"));
            Location locationA = new Location("Current Location");

            locationA.setLatitude(currentlatLng.latitude);
            locationA.setLongitude(currentlatLng.longitude);

            Location locationB = new Location("Destination");

            locationB.setLatitude(lat);
            locationB.setLongitude(lng);

            distance = locationA.distanceTo(locationB);

            availability = Double.parseDouble( googlePlace.get("availability"));

            normalized_distance=(distance-minimumMaximum.get("minimumDistance"))/(minimumMaximum.get("maximumDistance")-minimumMaximum.get("minimumDistance"));
            normalized_availability=(availability-minimumMaximum.get("minimumAvailability"))/(minimumMaximum.get("maximumAvailability")-minimumMaximum.get("minimumAvailability"));


            weightage=0.5*normalized_distance+0.5*(1-normalized_availability);
            Log.e("Weightage", weightage+"");
            Log.e("Distance", distance+"");
            Log.e("Avai", availability+"");

            if(weightage<min_weightage)
            {
                min_googlePlace=googlePlace;
                min_weightage=weightage;
            }
        }

    //    Log.e("Weightage", weightage+"");
    //    Log.e("min weightage place", min_googlePlace+"");
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, min_googlePlace);
        display.sendRequest();
    }
    public  void Cost_Availability_Filter()
    {
        double cost, availability;
        double normalized_availability;
        double normalized_cost;
        double weightage;
        double min_weightage=99999999.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);
            cost = Double.parseDouble( googlePlace.get("cost"));
            availability=Double.parseDouble(googlePlace.get("availability"));

            normalized_cost=(cost-minimumMaximum.get("minimumCost"))/(minimumMaximum.get("maximumCost")-minimumMaximum.get("minimumCost"));
            normalized_availability=(availability-minimumMaximum.get("minimumAvailability"))/(minimumMaximum.get("maximumAvailability")-minimumMaximum.get("minimumAvailability"));


            weightage=0.5*(1-normalized_availability)+0.5*normalized_cost;
            if(weightage<min_weightage)
            {
                min_googlePlace=googlePlace;
                min_weightage=weightage;
            }
        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, min_googlePlace);
        display.sendRequest();
    }
    public  void Distance_Cost_Availability_Filter()
    {
        double cost,lat, lng, availability,distance;
        double normalized_availability;
        double normalized_cost;
        double normalized_distance;
        double weightage;
        double min_weightage=99999999.0;
        HashMap<String, String> googlePlace;
        HashMap<String,String> min_googlePlace = new HashMap<>();
        Log.e("nearByplaceList", nearbyPlaceList+"");
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            googlePlace = nearbyPlaceList.get(i);

            lat = Double.parseDouble( googlePlace.get("lat"));
            lng = Double.parseDouble( googlePlace.get("lng"));
            Location locationA = new Location("Current Location");

            locationA.setLatitude(currentlatLng.latitude);
            locationA.setLongitude(currentlatLng.longitude);

            Location locationB = new Location("Destination");

            locationB.setLatitude(lat);
            locationB.setLongitude(lng);

            distance = locationA.distanceTo(locationB);

            //distance=Double.parseDouble(googlePlace.get("distance"));
            cost = Double.parseDouble( googlePlace.get("cost"));
            availability=Double.parseDouble(googlePlace.get("availability"));


            normalized_cost=(cost-minimumMaximum.get("minimumCost"))/(minimumMaximum.get("maximumCost")-minimumMaximum.get("minimumCost"));
            normalized_availability=(availability-minimumMaximum.get("minimumAvailability"))/(minimumMaximum.get("maximumAvailability")-minimumMaximum.get("minimumAvailability"));
            normalized_distance=(distance-minimumMaximum.get("minimumDistance"))/(minimumMaximum.get("maximumDistance")-minimumMaximum.get("minimumDistance"));
            weightage=0.33*(1-normalized_availability)+0.33*(normalized_cost)+0.33*(normalized_distance);
            if(weightage<min_weightage)
            {
                min_googlePlace=googlePlace;
                min_weightage=weightage;
            }
        }
        Direction_time_display display = new Direction_time_display(context,mMap,currentlatLng, min_googlePlace);
        display.sendRequest();
    }

}
