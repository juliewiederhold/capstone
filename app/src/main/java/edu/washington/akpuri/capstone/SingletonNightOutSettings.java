package edu.washington.akpuri.capstone;

import java.util.ArrayList;

/**
 * Created by Julie on 4/21/15.
 */
public class SingletonNightOutSettings {

    private static SingletonNightOutSettings instance = null;
    private static SingletonQuickText quickTextInstance;
    private static SingletonUser userInstance;
    private static SingletonAppBlocking appBlockingInstance;
    private static SingletonContacts contactsInstance;

    private static ArrayList<SafetyZone> nightOutSafetyZones;
    private static ArrayList<String> nightOutQuickTexts;
    private static ArrayList<App> nightOutBlockedApps;
    private static ArrayList<Contact> nightOutBlockedContacts;

    //Empty Constructor, it's a singleton
    protected SingletonNightOutSettings(){

    }

    public static SingletonNightOutSettings getInstance(){
        if(instance == null){
            instance = new SingletonNightOutSettings();
            quickTextInstance = SingletonQuickText.getInstance();
            userInstance = SingletonUser.getInstance();
            appBlockingInstance = SingletonAppBlocking.getInstance();
            contactsInstance = SingletonContacts.getInstance();

            nightOutQuickTexts = new ArrayList<>();
            nightOutSafetyZones = new ArrayList<>();
            nightOutBlockedApps = appBlockingInstance.getBlockedApps();
            nightOutBlockedContacts = contactsInstance.getBlockedContacts();
        }

        ArrayList<SafetyZone> tempSafetyZones = userInstance.getExistingSafetyZones();
        for(int i = 0; i < tempSafetyZones.size(); i++){
            if(!nightOutSafetyZones.contains(tempSafetyZones.get(i)))
                nightOutSafetyZones.add(tempSafetyZones.get(i));
        }

        ArrayList<String> tempQuickTexts = quickTextInstance.getAllQuickTexts();
        for(int n = 0; n < tempQuickTexts.size(); n++){
            if(!nightOutQuickTexts.contains(tempQuickTexts.get(n)))
                nightOutQuickTexts.add(tempQuickTexts.get(n));
        }
        return instance;
    }

    public SingletonNightOutSettings restartInstance(){
        this.nightOutSafetyZones = new ArrayList<>();
        this.nightOutQuickTexts = new ArrayList<>();
        return instance;
    }

    public ArrayList<SafetyZone> getNightOutSafetyZones(){return this.nightOutSafetyZones;}

    public void setNightOutSafetyZones(ArrayList<SafetyZone> safetyZones){this.nightOutSafetyZones = safetyZones;}

    public ArrayList<String> getNightOutQuickTexts(){return this.nightOutQuickTexts;}

    public ArrayList<App> getNightOutBlockedApps(){return this.nightOutBlockedApps;}

    public ArrayList<Contact> getNightOutBlockedContacts(){return  this.nightOutBlockedContacts;}


}
