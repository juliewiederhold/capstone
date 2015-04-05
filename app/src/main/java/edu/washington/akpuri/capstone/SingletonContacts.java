package edu.washington.akpuri.capstone;

import java.util.ArrayList;

/**
 * Created by Akash on 3/11/15
 * This Singleton acts as a Global Variable holding the contacts of
 * a user and storing them for easy loading of the listview used by
 * the contacts activity. We need to store these contacts somewhere so that we
 * don't have to query the android contacts database everysingle time we are
 * at the contacts page because this takes too long and is too much work
 */
public class SingletonContacts {
    private static SingletonContacts instance = null;
    private static ArrayList<Contact> allContacts;
    private static ArrayList<Contact> pendingContacts;
    private static ArrayList<Contact> blockedContacts;
    //Empty Constructor, it's a singleton
    protected SingletonContacts() {
    }

    public static SingletonContacts getInstance() {
        if (instance == null) {
            instance = new SingletonContacts();
            allContacts = new ArrayList<Contact>();
            pendingContacts = new ArrayList<Contact>();
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

    public void setBlockedContacts(ArrayList<Contact> contacts) {this.blockedContacts = contacts;}

    public ArrayList<Contact> getBlockedContacts() {return blockedContacts;}

    public void setPendingContacts(ArrayList<Contact> pendingContacts) {
        this.pendingContacts = pendingContacts;
    }

    public ArrayList<Contact> getPendingContacts() {
        return pendingContacts;
    }

}
