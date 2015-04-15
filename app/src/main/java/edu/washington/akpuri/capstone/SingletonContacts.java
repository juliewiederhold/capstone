package edu.washington.akpuri.capstone;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Akash on 3/11/15
 * This Singleton acts as a Global Variable holding the contacts of
 * a user and storing them for easy loading of the listview used by
 * the contacts activity. We need to store these contacts somewhere so that we
 * don't have to query the android contacts database every single time we are
 * at the contacts page because this takes too long and is too much work
 */
public class SingletonContacts {
    private static SingletonContacts instance = null;
    private static ArrayList<Contact> allContacts;
    private static ArrayList<Contact> pendingContacts;      // Temporarily hold phone contacts to add to Parse.com
    private static ArrayList<Contact> pendingFriends;       // Friends/contacts currently on Parse.com
    private static ArrayList<Contact> blockedContacts;
    private static ArrayList<String> currentContacts;       // List of objectIds of user's contacts both on Parse.com and pending

    //Empty Constructor, it's a singleton
    protected SingletonContacts() {
    }

    public static SingletonContacts getInstance() {
        if (instance == null) {
            instance = new SingletonContacts();
            allContacts = new ArrayList<Contact>();
            pendingContacts = new ArrayList<Contact>();
            currentContacts = new ArrayList<>();
        }
        return instance;
    }

    public boolean hasContacts() {
        return (allContacts.size() != 0);
    }

    public ArrayList<Contact> getAllContacts() {
        return allContacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.allContacts = contacts;
    }

    public void setBlockedContacts(ArrayList<Contact> contacts) {
        this.blockedContacts = contacts;
    }

    public ArrayList<Contact> getBlockedContacts() {
        return blockedContacts;
    }

    public void setPendingContacts(ArrayList<Contact> pendingContacts) {
        this.pendingContacts = pendingContacts;
    }

    public void setPendingFriends(ArrayList<Contact> pendingFriends) {
        this.pendingFriends = pendingFriends;
    }

    public ArrayList<Contact> getPendingFriends() {
        return pendingFriends;
    }
    public ArrayList<Contact> getPendingContacts() {
        return pendingContacts;
    }

    public ArrayList<String> getCurrentContacts() {
        return currentContacts;
    }

    // Adds contact with objectId to user's current contacts
    // Current contacts are contacts listed on user's Parse contacts[]
    public void setCurrentcontacts(String objectId) {
        currentContacts.add(objectId);
    }

    public void addPendingFriend(Contact object) {
        pendingFriends.add(object);
    }


}
