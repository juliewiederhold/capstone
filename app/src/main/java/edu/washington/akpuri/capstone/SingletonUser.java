package edu.washington.akpuri.capstone;

import java.util.ArrayList;

/**
 * Created by Julie on 4/20/15.
 */
public class SingletonUser {
    private static boolean allowContactRetrieval;
    private static SingletonUser instance = null;
    private static boolean hasGoneThroughInitialSetUp;

    //Empty Constructor, it's a singleton
    protected SingletonUser(){

    }

    public static SingletonUser getInstance() {
        if (instance == null) {
            instance = new SingletonUser();
            allowContactRetrieval = false;
            hasGoneThroughInitialSetUp = false;
        }
        return instance;
    }

    public void setAllowContactRetrieval(boolean allowContactRetrieval){ this.allowContactRetrieval = allowContactRetrieval;}

    public boolean getAllowContactRetrieval(){return this.allowContactRetrieval;}

    public void setHasGoneThroughInitialSetUp(boolean hasGoneThroughInitialSetUp){this.hasGoneThroughInitialSetUp = hasGoneThroughInitialSetUp;}

    public boolean getHasGoneThroughInitialSetUp(){return this.hasGoneThroughInitialSetUp;}
}
