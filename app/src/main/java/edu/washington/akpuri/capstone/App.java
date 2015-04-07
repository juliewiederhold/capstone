package edu.washington.akpuri.capstone;

import android.util.Log;

/**
 * Created by Julie on 4/5/15.
 */
public class App {
    private String name;
    private boolean isBlocked;

    public App(String name) {
        this.name = name;
        this.isBlocked = false;
    }

    public String getName() {
        return name;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {this.isBlocked = isBlocked;}

}
