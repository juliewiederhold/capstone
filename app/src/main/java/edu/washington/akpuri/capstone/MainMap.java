package edu.washington.akpuri.capstone;
import android.app.AlarmManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.lang.reflect.Array;
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
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static SingletonNightOutGroup nightOutGroup = SingletonNightOutGroup.getInstance();
    private MarkerOptions friendMarker;
    private LatLng theHub;
    private boolean inSafetyZone;

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
        nightOutGroup = SingletonNightOutGroup.getInstance();

        mMap.setMyLocationEnabled(true);

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

        ImageButton friend1 = (ImageButton) findViewById(R.id.friend1);
        friend1.setVisibility(View.INVISIBLE);
        ImageView safetyZone1 = (ImageView) findViewById(R.id.friend1_house);

        Handler friendHandler = new Handler();
        friendHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ImageButton friend1 = (ImageButton) findViewById(R.id.friend1);
                friend1.setVisibility(View.VISIBLE);
                if(userInstance.getPhone().equals("4082096381"))
                    friend1.setImageResource(R.drawable.profile_picture_nicole);
                else
                    friend1.setImageResource(R.drawable.profile_picture_julie);
            }

        }, 10000);

        final Geocoder geocoder = new Geocoder(this);

        List<SafetyZone> safetyZones = instance.getNightOutSafetyZones();




        if(!isFriendInSafetyZone(new Contact("Julie", "4082096381", 1))){
            safetyZone1.setVisibility(View.INVISIBLE);
        }

        friend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Center's the map camera on Friend 1
                mMap.moveCamera(CameraUpdateFactory.newLatLng(theHub));
            }
        });


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
                instance.setHasSetOffAlert(!instance.isHasSetOffAlert());

                alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                long when = System.currentTimeMillis();         // notification time
                Intent intent = new Intent(MainMap.this, AlertService.class);
                pendingIntent = PendingIntent.getBroadcast(MainMap.this, 0, intent, 0);

                if(instance.isHasSetOffAlert()){
                    alarmManager.setRepeating(AlarmManager.RTC, when, (AlarmManager.INTERVAL_FIFTEEN_MINUTES / 30), pendingIntent);

                    Context context = getApplicationContext();
                    CharSequence text = "Your friends have been alerted";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    alarmManager.cancel(pendingIntent);

                    Context context = getApplicationContext();
                    CharSequence text = "You have turned off your alert";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }



                return true;
            }
        });

        int totalTime = (instance.getDurationHours() * 3600000) + (instance.getDurationMinutes() * 60000);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(instance.isHasSetOffAlert()){
                    instance.setHasSetOffAlert(false);
                    alarmManager.cancel(pendingIntent);
                }
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

                                    if(instance.isHasSetOffAlert()){
                                        instance.setHasSetOffAlert(false);
                                        alarmManager.cancel(pendingIntent);
                                    }

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

        // TODO: So I put everything here, coz I can't get it to save inside the handleNewLocation method for some reason
    /*    userInstance.saveLocationToParse(40,40);
        // Get location of each member as ParseGeoPoint object
        // Where key = phone number, value = ParseGeoPoint object
        HashMap<String, ParseGeoPoint> locations = nightOutGroup.getAllLocations();
        // Get Contact objects for each member
        ArrayList<Contact> contactObjects = nightOutGroup.getGroupContacts();
        for (Contact contact : contactObjects) {
            ParseGeoPoint memberLocation = locations.get(contact.getPhone());   // Get ParseGeoPoint object
            double memberlat = memberLocation.getLatitude();                    // Get latitude
            double memberlong = memberLocation.getLongitude();                  // Get longitude
            LatLng latLng = new LatLng(memberlat, memberlong);                  // Save as LatLng
            Log.e(TAG, "Location of + " + userInstance.getPhone() + " " + memberlat + "," + memberlong);
        }*/

        // To get location for each member: locations.get(<USER PHONE NUMBER>)
    }

    private boolean isFriendInSafetyZone(Contact friend){
        //todo - we need a boolean value in parse to indicate if a friend is in their safety zone. I am thinking that once a person arrives to their safety zone, the notification is sent and the boolean value is set
        // we check here if the friend is in th safety zone to control whether or not the house appears.
        return false;
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

       final Geocoder geocoder = new Geocoder(this);
       List<Address> addresses;

    /*    for(int i = 0; i < safetyZones.size(); i++){
            try{
                addresses = geocoder.getFromLocationName(safetyZones.get(i).returnAddress(), 1);
                //locationName = geocoder.getFromLocation(currentLatitude, currentLongitude, 1).get(0).getLocality();
                if(addresses.size() > 0) {
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);

                    if (mLastLocation != null) {
                        currentLatitude = mLastLocation.getLatitude();
                        currentLongitude = mLastLocation.getLongitude();
                    }
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();

                    double num = calculateDistance(currentLongitude, currentLatitude, longitude, latitude);

                    if(num < 20){ // 20 is a guess
                        Toast toast = Toast.makeText(this, "In Safety Zone", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            } catch (IOException e){
                Log.e(TAG, "Unable connect to Geocoder", e);
            }
        } */

        for(int i = 0; i < safetyZones.size(); i++){
            try{
                addresses = geocoder.getFromLocationName(safetyZones.get(i).returnAddress(), 1);
                //locationName = geocoder.getFromLocation(currentLatitude, currentLongitude, 1).get(0).getLocality();
                if(addresses.size() > 0) {
                    double latitude= addresses.get(0).getLatitude();
                    double longitude= addresses.get(0).getLongitude();

                    double num = calculateDistance(currentLongitude, currentLatitude, longitude, latitude);

                    if(num < 20){ // 20 is a guess
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
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(currentLatitude, currentLongitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

   // TODO
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                theHub = new LatLng(47.6553169, -122.3049381);
                String name = "Unknown Location";
                List<Address> address;
                try{
                    address = geocoder.getFromLocation(47.6553169, -122.3049381, 1);
                    name = address.get(0).getAddressLine(0);
                    name = name + ", " + address.get(0).getLocality();
                    name = name + ", " + address.get(0).getAdminArea();
                    name = name + ", " + address.get(0).getCountryName();
                    name = name + ", " + address.get(0).getFeatureName();
                }catch (IOException e){
                    Log.e(TAG, "Unable connect to Geocoder", e);
                }

                friendMarker = new MarkerOptions()
                        .position(theHub);
                        //.title("Julie's location" + name);
                mMap.addMarker(friendMarker);
                if(userInstance.getPhone().equals("4082096381"))
                    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter("Nicole's Location", name));
                else
                    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter("Julie's Location", name));

                // Send currentLatitude and currentLongitude (both are existing variables defined line 342) to Parse for this user's location
                    // Can also send the LatLng of the location (defined 390), may make things easier since one variable and is what I need to move the marker
     /*           userInstance.saveLocationToParse(currentLatitude, currentLongitude);
                // Hey Julie! I don't know that this is working. It doesn't seem to do anything?

                // TODO: Pull the lat and long of each Night Out Group Member
                // Get location of each member as ParseGeoPoint object
                // Where key = phone number, value = ParseGeoPoint object
                HashMap<String, ParseGeoPoint> locations = nightOutGroup.getAllLocations();
                //// CRASHING: locations seems to be empty even though it shouldn't be :/
                Log.e(TAG, "# of locations: " + locations.size());
                // Get Contact objects for each member
                ArrayList<Contact> contactObjects = nightOutGroup.getGroupContacts();
              //  for (Contact contact : contactObjects)
               if(nightOutGroup.getGroupContacts().size() > 0){
                    String phoneNo = contactObjects.get(0).getPhone();
                    Log.e(TAG, phoneNo);
                    ParseGeoPoint memberLocation = locations.get(phoneNo);   // Get ParseGeoPoint object
                    double memberlat = memberLocation.getLatitude();                    // Get latitude
                    double memberlong = memberLocation.getLongitude();                  // Get longitude
                    LatLng friendLatLng = new LatLng(memberlat, memberlong);                  // Save as LatLng
                    Log.e(TAG, "Location of + " + userInstance.getPhone() + " " + memberlat + "," + memberlong);

                   MarkerOptions friendMarker = new MarkerOptions()
                               .position(friendLatLng);
                   mMap.addMarker(friendMarker);
                }
*/

            }

        }, 10000);
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        private String name, address;

        public MyInfoWindowAdapter(String name, String vicinity) {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.custom_info_contents, null);
            this.name = name;
            this.address = vicinity;
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.title));
            tvTitle.setText(name);
            TextView tvSnippet = ((TextView) myContentsView
                    .findViewById(R.id.snippet));
            tvSnippet.setText(address);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

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
