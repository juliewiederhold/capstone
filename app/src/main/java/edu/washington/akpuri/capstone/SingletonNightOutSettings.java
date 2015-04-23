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
            nightOutBlockedApps = new ArrayList<>();
            nightOutBlockedContacts = new ArrayList<>();

             initializeBlockedApps();
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

        initializeBlockedApps();

        ArrayList<Contact> tempContact = contactsInstance.getBlockedContacts();
        for(int x = 0; x < tempContact.size(); x++){
            if(!nightOutBlockedContacts.contains(tempContact.get(x)))
                nightOutBlockedContacts.add(tempContact.get(x));
        }
        return instance;
    }

    private static void initializeBlockedApps(){
        ArrayList<App> tempApp = appBlockingInstance.getAllApps();
        for(int a = 0; a < tempApp.size(); a++){
            if(!nightOutBlockedApps.contains(tempApp.get(a))){
                App temp = tempApp.get(a);
                App copy = new App(temp.getName(), temp.isBlocked());
                nightOutBlockedApps.add(copy);
            }
        }
    }

    public SingletonNightOutSettings restartInstance(){
        this.nightOutSafetyZones = new ArrayList<>();
        this.nightOutQuickTexts = new ArrayList<>();
        this.nightOutBlockedContacts = new ArrayList<>();
        return instance;
    }

    public ArrayList<SafetyZone> getNightOutSafetyZones(){return this.nightOutSafetyZones;}

    public ArrayList<String> getNightOutQuickTexts(){return this.nightOutQuickTexts;}

    public ArrayList<App> getNightOutBlockedApps(){return this.nightOutBlockedApps;}

    public ArrayList<Contact> getNightOutBlockedContacts(){return  this.nightOutBlockedContacts;}

    public void setNightOutBlockedContacts(ArrayList<Contact> list){ this.nightOutBlockedContacts = list;}


}
