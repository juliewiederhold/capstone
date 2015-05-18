package edu.washington.akpuri.capstone;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.os.Handler;

public class MainMap extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MainMap.class.getSimpleName();
    final SingletonContacts contactInstance = SingletonContacts.getInstance();
    public static SingletonNightOutSettings instance;
    private static ArrayList<Contact> friendsInNightOutGroup = new ArrayList<Contact>();
    final SingletonUser userInstance = SingletonUser.getInstance();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        setUpMapIfNeeded();

        instance = SingletonNightOutSettings.getInstance();
        mMap.setMyLocationEnabled(true);

        Contact temp = new Contact("Julie", "4082096381", 1);
        temp.setEmail("f@f.com");
        friendsInNightOutGroup.add(temp);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        ImageButton quickText = (ImageButton) findViewById(R.id.message_friends);
        quickText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent text = new Intent(MainMap.this, SendQuickText.class);
                startActivity(text);
            }
        });

        ImageButton alertFriends = (ImageButton) findViewById(R.id.alert_friends);
        alertFriends.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final String user = ParseUser.getCurrentUser().getString("email");

                for(int i=0; i < friendsInNightOutGroup.size(); i++) {
                    final Contact person = friendsInNightOutGroup.get(i);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
                    query.whereEqualTo("user", user);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject parseObject, ParseException e) {
                            try {
                                if (parseObject != null) {

                                    // Send push notifications
                                    ParseQuery pushQuery = userInstance.getCurrentInstallation().getQuery();
                                    pushQuery.whereEqualTo("user", person.getEmail());
                                    ParsePush push = new ParsePush();
                                    push.setQuery(pushQuery);
                                    push.setMessage(userInstance.getName() + " would like you to find her. Please go assist her NOW.");

                                    Context context = getApplicationContext();
                                    CharSequence text = "Your friends have been alerted";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();

                                    push.sendInBackground();
                                    Log.e(TAG, "sent to: " + person.getEmail());

                                } else {
                                    // Something went wrong
                                    Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                                }
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                        }
                    });
                }

                return true;
            }
        });

        int totalTime = (instance.getDurationHours() * 3600000) + (instance.getDurationMinutes() * 60000);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                LayoutInflater inflater = getLayoutInflater();

                final View fragmentView = inflater.inflate(R.layout.fragment_night_out_ends, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                builder.setView(fragmentView)
                        // Add action buttons
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            finish(); // Makes it so you can't go back to this activity

                            Intent endNightOut = new Intent(MainMap.this, MainActivity.class);
                            startActivity(endNightOut);

                            }
                        });

                builder.create();
                builder.show();
            }

        }, totalTime);

        ImageButton exit = (ImageButton) findViewById(R.id.exit_night_out);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();

                final View fragmentView = inflater.inflate(R.layout.fragment_exit_night_out, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentView.getContext());

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(fragmentView)
                        // Add action buttons
                        .setPositiveButton("End Night Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText answerText = (EditText) fragmentView.findViewById(R.id.exit_night_out_answer);

                                int answer = Integer.parseInt(answerText.getText().toString());

                                if (answer == 1088 || !answerText.getText().toString().equals("")) {

                                    finish(); // Makes it so you can't go back to this activity
                                    instance.restartInstance();

                                    Intent endNightOut = new Intent(MainMap.this, MainActivity.class);
                                    startActivity(endNightOut);

                                    answerText.setText("");

                                } else {
                                    Context context = getApplicationContext();
                                    CharSequence text = "Incorrect Answer";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                builder.create();
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    // Disables back button
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        List<SafetyZone> safetyZones = instance.getNightOutSafetyZones();

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        String locationName = "Unknown Location";
        try {
            List<Address> addressList = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                locationName = sb.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable connect to Geocoder", e);
        }

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        for(int i = 0; i < safetyZones.size(); i++){
            try{
                addresses = geocoder.getFromLocationName(safetyZones.get(i).returnAddress(), 1);
                //locationName = geocoder.getFromLocation(currentLatitude, currentLongitude, 1).get(0).getLocality();
                if(addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();

                    double num = calculateDistance(currentLongitude, currentLatitude, longitude, latitude);

                    if(num < 50){ // 50 is a guess
                        Toast toast = Toast.makeText(this, "In Safety Zone", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            } catch (IOException e){
                Log.e(TAG, "Unable connect to Geocoder", e);
            }
        }


        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
      /*  MarkerOptions options = new MarkerOptions() WILL USE ONCE WE ADD PEOPLE TO THE MAP
                .position(latLng)
                .title(locationName);
        mMap.addMarker(options);*/
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currentLatitude, currentLongitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    private double calculateDistance(double fromLong, double fromLat,
                                     double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

}
