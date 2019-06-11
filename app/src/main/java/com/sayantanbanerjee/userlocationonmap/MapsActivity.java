package com.sayantanbanerjee.userlocationonmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.M;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    boolean flag = false;


    LocationListener locationListener;
    protected FusedLocationProviderClient fusedLocationClient;
    float a;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                Log.i("LOCATION :", location.toString());
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());





                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                    if(listAddresses!= null && listAddresses.size() >0){


                        String address = "You are at: ";
                        String address2= "";

                        if(listAddresses.get(0).getSubLocality() != null)
                        {
                            address += listAddresses.get(0).getSubLocality() + ", ";
                        }
                        if(listAddresses.get(0).getLocality() != null)
                        {
                            address += listAddresses.get(0).getLocality();
                        }
                        if(listAddresses.get(0).getAdminArea() != null)
                        {
                            address2 += listAddresses.get(0).getAdminArea() + ", ";
                        }
                        if(listAddresses.get(0).getCountryName() != null)
                        {
                            address2 += listAddresses.get(0).getCountryName();
                        }
                        Log.i("HERE: ",address);


                        mMap.addMarker(new MarkerOptions().position(newLocation).title(address).snippet(address2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        if(flag == true)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                        }
                        else
                        {


                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                            flag = true;
                        }




                    }
                    else
                    {
                        mMap.addMarker(new MarkerOptions().position(newLocation).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        if(flag == true)
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                        }
                        else
                        {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                            flag = true;
                        }
                    }
                }catch (Exception e)
                {
                    mMap.addMarker(new MarkerOptions().position(newLocation).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    if(flag == true)
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                    }
                    else
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation,17));
                        flag = true;
                    }
                    e.printStackTrace();
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            // Logic to handle location object
                            if (location != null) {

                                mMap.clear();
                                LatLng newL = new LatLng(location.getLatitude(), location.getLongitude());

                                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try{
                                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                                    if(listAddresses!= null && listAddresses.size() >0){
                                        String address = "You are at: ";
                                        String address2= "";

                                        if(listAddresses.get(0).getSubLocality() != null)
                                        {
                                            address += listAddresses.get(0).getSubLocality() + ", ";
                                        }
                                        if(listAddresses.get(0).getLocality() != null)
                                        {
                                            address += listAddresses.get(0).getLocality();
                                        }
                                        if(listAddresses.get(0).getAdminArea() != null)
                                        {
                                            address2 += listAddresses.get(0).getAdminArea() + ", ";
                                        }
                                        if(listAddresses.get(0).getCountryName() != null)
                                        {
                                            address2 += listAddresses.get(0).getCountryName();
                                        }
                                        Log.i("HERE: ",address);
                                        flag = true;

                                        mMap.addMarker(new MarkerOptions().position(newL).title(address).snippet(address2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newL,17));

                                    }
                                    else
                                    {
                                        flag = true;
                                        mMap.addMarker(new MarkerOptions().position(newL).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newL,17));
                                    }

                                }catch (Exception e)
                                {
                                    flag = true;
                                    mMap.addMarker(new MarkerOptions().position(newL).title("You are here! No Address found").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newL,17));
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                // Got last known location. In some rare situations this can be null.
                            }
                        }
                    });



        }


    }
}
