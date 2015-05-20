package edu.washington.akpuri.capstone;

import android.nfc.Tag;
import android.util.Log;

import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Akash on 3/11/15
 * This Singleton acts as a Global Variable holding the contacts of
 * a user and storing them for easy loading of the listview used by
 * the contacts activity. We need to store these contacts somewhere so that we
 * don't have to query the android contacts database every single time we are
 * at the contacts page because this takes too long and is too much work
 */
public class SingletonContacts {
    private static final String TAG = "SingletonContacts";
    private static SingletonContacts instance = null;
    private static ArrayList<Contact> allContacts;
    private static ArrayList<Contact> pendingContacts;          // Temporarily hold phone contacts to add to Parse.com
    private static ArrayList<Contact> sosoFriends;              // Friends/contacts currently on Parse.com
    private static ArrayList<Contact> blockedContacts;
    private static ArrayList<String> currentContacts;           // List of objectIds of user's contacts both on Parse.com and pending
    private static ArrayList<Contact> allPendingRequests;
    private static HashMap<String, ArrayList<Contact>> sosoGroups;    //

    private boolean imported;
    private boolean savedRequests;



    //Empty Constructor, it's a singleton
    protected SingletonContacts() {
    }

    public static SingletonContacts getInstance() {
        if (instance == null) {
            instance = new SingletonContacts();
            allContacts = new ArrayList<>();
            pendingContacts = new ArrayList<>();
            currentContacts = new ArrayList<>();
            allPendingRequests = new ArrayList<>();
            sosoGroups = new HashMap<>();
        }
        return instance;
    }

    public boolean hasImported(){
        return imported;
    }

    public void setHasImported(boolean imported) {
        this.imported = imported;
    }

    public boolean hasSavedRequests() {
        return savedRequests;
    }

    public void setHasSavedRequests(boolean savedRequests) {
        this.savedRequests = savedRequests;
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

    public void addContact(Contact contact) {
        allContacts.add(contact);
    }

    // Return contact
    public Contact getContact(Contact contact) {
        return allContacts.get(allContacts.indexOf(contact));
    }
    public void removeContact(Contact contact){
        allContacts.remove(contact);
    }

    public void setBlockedContacts(ArrayList<Contact> contacts) {
        this.blockedContacts = contacts;
    }

    public ArrayList<Contact> getBlockedContacts() {
        if(this.blockedContacts == null)
            this.blockedContacts = new ArrayList<>();
        return blockedContacts;
    }

    public void setPendingContacts(ArrayList<Contact> pendingContacts) {
        this.pendingContacts = pendingContacts;
    }

    public void setSosoFriends(ArrayList<Contact> sosoFriends) {
        this.sosoFriends = sosoFriends;
    }

    public ArrayList<Contact> getSosoFriends() {
        return sosoFriends;
    }

    public ArrayList<Contact> getPendingContacts() {
        return pendingContacts;
    }

    public ArrayList<String> getCurrentContacts() {
        return currentContacts;
    }

    // Adds contact with objectId to user's current contacts
    // Current contacts are contacts listed on user's Parse contacts[]
    public void setCurrentContacts(ArrayList<String> objectIds) {
        currentContacts.addAll(objectIds);
    }

    /// Adding single objects
    public void addCurrentContact(String objectId) {
        currentContacts.add(objectId);
    }

    public void addPendingContact(Contact contact) {
        pendingContacts.add(contact);
    }

    public void addSosoFriend(Contact object) {
        sosoFriends.add(object);
    }

    public void addSosoFriends(ArrayList<Contact> objects) {
        sosoFriends.addAll(objects);
    }

    // Add another So-So user's email address to pending requests
    public void addPendingRequests(Contact pendingUser) {
        allPendingRequests.add(pendingUser);
    }

    public ArrayList<Contact> getPendingRequests(){
        return allPendingRequests;
    }

    public boolean addSosoGroup(String groupName, ArrayList<Contact> group) {
        sosoGroups.put(groupName, group);
        return true;
    }

    public ArrayList<Contact> getSosoGroup(String groupName) {
        return sosoGroups.get(groupName);
    }

    public HashMap<String, ArrayList<Contact>> getSosoGroups() {
        return sosoGroups;
    }
}
