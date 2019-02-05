package projetstage.aldric.fr.mapsactivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import static java.util.Objects.requireNonNull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private static GoogleMap mMap;
    boolean mLocationPermissionGranted = false;

    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        requireNonNull(mapFragment).getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //get markers tag
        JSONObject recup_info = (JSONObject) marker.getTag();

        Intent intent = new Intent (this,informations_stations.class);

        Bundle bundle = new Bundle();

        bundle.putLong("Number", (Long) Objects.requireNonNull(recup_info).get("number"));
        bundle.putString("Contract Name", (String) Objects.requireNonNull(recup_info).get("contract_name"));
        bundle.putString("Adresse", (String) Objects.requireNonNull(recup_info).get("address"));
        bundle.putBoolean("Banking", (Boolean) Objects.requireNonNull(recup_info).get("banking"));
        bundle.putLong("Bike Stands", (Long) Objects.requireNonNull(recup_info).get("bike_stands"));
        bundle.putLong("Available Bike Stands", (Long) Objects.requireNonNull(recup_info).get("available_bike_stands"));
        bundle.putLong("Available Bikes", (Long) Objects.requireNonNull(recup_info).get("available_bikes"));
        bundle.putString("Status", (String) Objects.requireNonNull(recup_info).get("status"));
        bundle.putLong("Last Update", (Long) Objects.requireNonNull(recup_info).get("last_update"));
        intent.putExtras(bundle);

        startActivity(intent);
        return false;
    }

    //Creating AsyncTask in order to get info on stations
    public static class RetrieveFeedTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... urls) {

            String API_URL = "https://api.jcdecaux.com/vls/v1/stations?contract=Lyon";
            String API_KEY="19c415a6e018b292439482aa3cf912e796c400b7";

            try {
                URL url = new URL(API_URL + "&apiKey=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
                System.out.print("Error"+response);
            }

            Log.i("INFO", response);

                //Parsing JSON file
                JSONParser parse = new JSONParser();

                try {


                    JSONArray jobj = (JSONArray) parse.parse(response);

                    for (int i = 0; i < jobj.size(); i++) {

                        // getting an object form JSON tab
                        JSONObject obj = (JSONObject) jobj.get(i);

                        //Getting position object to pick up latitude and longitude
                        JSONObject position = (JSONObject) obj.get("position");

                        Double latitude = (Double) requireNonNull(position).get("lat");
                        Double longitude = (Double) requireNonNull(position).get("lng");

                        String adresse = (String) obj.get("address");
                        String ville = (String) obj.get("contract_name");

                        //add marker on map
                        Marker test = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Marker in : "+ville+ " "+adresse));
                        test.setTag(obj);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }
    }


    public void getInfoFromAPI()
    {
        RetrieveFeedTask task = new RetrieveFeedTask();
        task.execute();
    }


    private void getLocationPermission() {

        //Request location permission, so that we can get the location of the
        //device. The result of the permission request is handled by a callback,
        //onRequestPermissionsResult.

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //updateLocationUI();
    }




/*

    //test

    public void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = new Location((Location) task.getResult());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude())));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude())).title("Marker in current Location"));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
*/
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
        getInfoFromAPI();

        getLocationPermission();

        //set up onclick listener on markers
        mMap.setOnMarkerClickListener(this);
    }


}

