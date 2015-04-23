package edu.washington.akpuri.capstone;

import android.util.Log;


/**
 * Created by Akash on 3/9/2015.
 */

public class Contact {
    private String name;
    private String phone;
    private int id;
    private boolean hasSoSo;
    private boolean isPending;
    private boolean selected;
    private boolean hasBeenAdded;
    private String objectId;


    public Contact(String name, String phone, int id) {
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.objectId = "";  // for now; updated after adding to parse?
        this.hasSoSo = false;
        this.selected = false;
        this.hasBeenAdded = false;
        this.isPending = true;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        if (phone.equals("") || phone.equals(null)) {
            Log.i("Contact", "No phone number existed or was entered for contact: " + name);
            return "NO PHONE NUMBER PRESENT";
        } else {
            return phone;
        }
    }

    public int getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean hasBeenAdded() {
        return hasBeenAdded;
    }

    public void setHasBeenAdded(boolean hasBeenAdded) {
        this.hasBeenAdded = hasBeenAdded;
    }

    public void setIsPending(boolean isPending) {
        this.isPending = isPending;
    }

    public boolean isPending(){
        return isPending;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
