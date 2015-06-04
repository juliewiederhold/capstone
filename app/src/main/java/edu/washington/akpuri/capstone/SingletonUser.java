package edu.washington.akpuri.capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by Julie on 4/20/15.
 */
public class SingletonUser {
    private static final String TAG = "SingletonUser";
    private static boolean allowContactRetrieval;
    private static SingletonUser instance = null;
    private static boolean hasGoneThroughInitialSetUp;
    private static ArrayList<String> allDefaultSettings;
    private static ArrayList<SafetyZone> existingSafetyZones;
    private static ParseUser currentUser;
    private static ParseInstallation currentInstallation;
    private static String name;
    private static String phone;
    private static String username;
    private static String email;
    private static Drawable profilePicture;
    private static Contact userContact;
    private static Drawable parseProfilePicture;
    private static boolean hasPhoto;

    //Empty Constructor, it's a singleton
    protected SingletonUser(){

    }

    public static SingletonUser getInstance() {
        if (instance == null) {
            instance = new SingletonUser();
            hasPhoto = false;
            allowContactRetrieval = false;
            hasGoneThroughInitialSetUp = false;
            allDefaultSettings = new ArrayList<>();

            existingSafetyZones = new ArrayList<>();

            allDefaultSettings.add("Safety Zones");
            allDefaultSettings.add("Blocked Apps and Contacts");
            allDefaultSettings.add("Quick Texts");

            currentInstallation = ParseInstallation.getCurrentInstallation();
            currentInstallation.saveInBackground();
        }

        if(currentUser == null){
            currentUser = ParseUser.getCurrentUser(); //

            if (currentUser != null) {
                name = currentUser.get("firstname").toString() + " " + currentUser.get("lastname").toString();
                phone = currentUser.get("phone").toString();
                username = currentUser.getUsername().toString();
                email = currentUser.getEmail().toString();
                userContact = new Contact(name, phone, 0);
                userContact.setEmail(email);
            }
        }

        return instance;
    }

    public void setAllowContactRetrieval(boolean allowContactRetrieval){ this.allowContactRetrieval = allowContactRetrieval;}

    public boolean getAllowContactRetrieval(){return this.allowContactRetrieval;}

    public void setHasGoneThroughInitialSetUp(boolean hasGoneThroughInitialSetUp){this.hasGoneThroughInitialSetUp = hasGoneThroughInitialSetUp;}

    public boolean getHasGoneThroughInitialSetUp(){return this.hasGoneThroughInitialSetUp;}

    public ArrayList<String> getAllDefaultSettings(){return this.allDefaultSettings;}

    public ArrayList<SafetyZone> getExistingSafetyZones(){return this.existingSafetyZones;}

    public void setExistingSafetyZones(ArrayList<SafetyZone> existingSafetyZones) {this.existingSafetyZones = existingSafetyZones;}

    public ParseUser getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(ParseUser newUser) {
        currentUser = newUser;
    }

    public ParseInstallation getCurrentInstallation () {
        return this.currentInstallation;
    }

    public Contact getContactObject() {
        return userContact;
    }
    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }
    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return this.email;
    }

    public void setProfilePicture(Drawable pic) {this.profilePicture = pic;}

    public Drawable getProfilePicture() {
        if (this.profilePicture == null) {
            getProfilePictureFromParse();
        }
        return this.profilePicture;
    }

    public Drawable getProfilePictureFromParse() {
        if (instance.getCurrentUser().getBoolean("hasPhoto")) {
            ParseFile file = (ParseFile) instance.getCurrentUser().get("photo");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        // Get bytes
//                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bytes != null) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            parseProfilePicture = (Drawable) new BitmapDrawable(bmp);
                            // Set profile picture
                            profilePicture = parseProfilePicture;
                            hasPhoto = true;
//                            instance.getCurrentUser().put("hasPhoto", true);
//                            instance.getCurrentUser().saveInBackground(new SaveCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    Log.e(TAG, "hasPhoto = true");
//                                }
//                            });
                            Log.e(TAG, "Downloaded photo form Parse.");
                        }
                    } else {
                        // Something went wrong
                        Log.e(TAG, "Error downloading the photo from Parse.");
                    }
                }
            });
        }
        return parseProfilePicture;
    }

    // Check if user has photo saved
    public boolean hasPhoto(){
        return this.hasPhoto;
    }

    public ParseGeoPoint saveLocationToParse(double latitude, double longitude) {
        final ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        instance.getCurrentUser().put("userLocation", point);
        instance.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Saved " + point.toString());
                } else {
                    Log.e(TAG, "Error saving " + point.toString());
                    Log.e(TAG, "Error: " + e.getCode());
                }
            }
        });
        return point;
    }
}
