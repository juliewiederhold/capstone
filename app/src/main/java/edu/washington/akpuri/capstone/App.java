package edu.washington.akpuri.capstone;

import android.util.Log;

/**
 * Created by Julie on 4/5/15.
 */
public class App {
    private String name;
    private boolean selected;
    private boolean isBlocked;

    public App(String name) {
        this.name = name;
        this.selected = false;
        this.isBlocked = false;
    }

    public String getName() {
        return name;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
