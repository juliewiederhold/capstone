package edu.washington.akpuri.capstone;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Julie on 4/20/15.
 */
public class SingletonUser {
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

    //Empty Constructor, it's a singleton
    protected SingletonUser(){

    }

    public static SingletonUser getInstance() {
        if (instance == null) {
            instance = new SingletonUser();
            allowContactRetrieval = false;
            hasGoneThroughInitialSetUp = true;
            allDefaultSettings = new ArrayList<>();

            existingSafetyZones = new ArrayList<>();

            allDefaultSettings.add("Safety Zones");
            allDefaultSettings.add("Blocked Apps and Contacts");
            allDefaultSettings.add("Quick Texts");

            currentUser = ParseUser.getCurrentUser();
            name = currentUser.get("firstname").toString() + " " + currentUser.get("lastname").toString();
            phone = currentUser.get("phone").toString();
            username = currentUser.getUsername().toString();
            email = currentUser.getEmail().toString();

            currentInstallation = ParseInstallation.getCurrentInstallation();
            currentInstallation.saveInBackground();
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

    public ParseInstallation getCurrentInstallation () {
        return this.currentInstallation;
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

}
