package edu.washington.akpuri.capstone;

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

    //Empty Constructor, it's a singleton
    protected SingletonContacts() {

    }

    public static SingletonContacts getInstance() {
        if (instance == null) {
            instance = new SingletonContacts();
        }
        return instance;
    }

}
