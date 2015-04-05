package edu.washington.akpuri.capstone;

import java.util.ArrayList;

/**
 * Created by Julie on 4/5/15.
 */
public class SingletonAppBlocking {
    private static SingletonAppBlocking instance = null;
    private static ArrayList<App> allApps;
    private static ArrayList<App> blockedApps;
    //Empty Constructor, it's a singleton
    protected SingletonAppBlocking() {
    }

    public static SingletonAppBlocking getInstance() {
        if (instance == null) {
            instance = new SingletonAppBlocking();
            allApps = new ArrayList<>();
            blockedApps = new ArrayList<>();
        }
        return instance;
    }

    public boolean hasApps() {return (allApps.size() != 0);}

    public ArrayList<App> getAllApps() {
        return allApps;
    }

    public void addAppToAllApps(App app){allApps.add(app);}

    public void updateIsSelectedOfAppInAllApps(App app, boolean isSelected){
        for(int i = 0; i < allApps.size(); i++){
            if(app.getName().equals(allApps.get(i).getName())){
                allApps.get(i).setIsBlocked(isSelected);
            }
        }
    }

    public void setApps(ArrayList<App> apps) {
        this.allApps = apps;
    }

    public void setBlockedApps(ArrayList<App> blockedApps) {this.blockedApps = blockedApps;}

    public void addAppToBlockedApps(App app){blockedApps.add(app);}

    public ArrayList<App> getBlockedApps() {return blockedApps;}

}
