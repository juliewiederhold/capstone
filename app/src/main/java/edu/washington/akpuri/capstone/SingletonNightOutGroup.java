package edu.washington.akpuri.capstone;


import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by iguest on 5/20/15.
 */
public class SingletonNightOutGroup {


    private static final String TAG = "SingletonNightOutGroup";
    private static SingletonNightOutGroup instance = new SingletonNightOutGroup();
    private static Map<String, Contact> groupContact = new HashMap<>();
    private static ArrayList<Contact> groupContacts = new ArrayList<>();
    private static Map<String, ParseUser> groupParse = new HashMap<>();
    private static Map<String, ParseGeoPoint> groupLocation = new HashMap<>();  // phone number, location
    private static String groupName = "";
    private static Contact starter;
    private static ParseUser currentUser;
    private static ArrayList<String> groupMembers = new ArrayList<>();
    private static ArrayList<String> groupMembersName = new ArrayList<>();
    private static ArrayList<String> memberIds = new ArrayList<>();
    private static boolean hasBeenCreated = false;;

    protected SingletonNightOutGroup() {
        // Empty
    }

    public static SingletonNightOutGroup getInstance() {
//        if (instance == null) {
////            instance = new SingletonNightOutGroup();
//            groupContact = new HashMap<>();
//            groupContacts = new ArrayList<>();
//            groupParse = new HashMap<>();
//            groupMembers = new ArrayList<>();
//            groupMembersName = new ArrayList<>();
//            groupName = "";
//            memberIds = new ArrayList<>();
//            hasBeenCreated = false;
//            groupLocation = new HashMap<>();
//        }
        return instance;
    }

    public String createGroup(String name, Contact starter) {
        this.groupName = name;
        this.starter = starter;
        this.currentUser = SingletonUser.getInstance().getCurrentUser();
        this.groupContact.put(starter.getPhone(), starter);
        this.groupContacts.add(starter);
        this.groupParse.put(starter.getPhone(), currentUser);
        this.hasBeenCreated = true;
        return "Created group: " + groupName;
    }

    /**
     * @return Contact object for each member of the group
     */
    public HashMap<String, Contact> getGroupContact() {
        return (HashMap) groupContact;
    }

    public ArrayList<Contact> getGroupContacts() {
        return groupContacts;
    }
    /**
     *
     * @return ParseUser object of each member of the group
     */
    public HashMap<String, ParseUser> getGroupParse() {
        return (HashMap) groupParse;
    }

    /**
     * Add group member using phone # as key and contact object and ParseUser object as value
     */
    public String addMember(Contact contact, ParseUser user) {
        groupContact.put(contact.getPhone(), contact);
        groupContacts.add(contact);
        groupParse.put(contact.getPhone(), user);
        memberIds.add(user.getObjectId());
        Log.e(TAG, "Added " + user.getObjectId());
        return "Added: " + contact.getEmail();
    }

    /**
     *
     * @return ParseUser object IDs for each member of the group
     */
    public ArrayList<String> getMemberIds(){
        return memberIds;
    }

    /**
     * // Remove member from group
     * @param contact
     * @return
     */
    public String removeMember(Contact contact) {
        groupContact.remove(contact.getPhone());
        groupContacts.remove(contact); // dunno if this works
        groupParse.remove(contact.getPhone());
        return "Removed: " + contact.getEmail();
    }

    public String getGroupName(){
        return groupName;
    }

    public String clearGroup() {
        groupContact.clear();
        groupContacts.clear();
        groupParse.clear();
        instance = null;
        return "Cleared: " + groupName;
    }

    public ArrayList<String> getMembers(){
        Iterator<Map.Entry<String, Contact>> iterator = groupContact.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Contact> entry = iterator.next();
            groupMembers.add(entry.getKey());
            groupMembersName.add(entry.getValue().getName());
        }
        return groupMembers;
    }



    // Return name of members
    public String getMembersName(){
        String groupMembersAsString = "";
        for (int i=0; i<groupMembers.size(); i++) {
//            groupMembersAsString += groupMembersName.get(i) + " (" + groupMembers.get(i) + ")";
            groupMembersAsString += groupMembersName.get(i);
            groupMembersAsString += ",";
        }
        groupMembersAsString = groupMembersAsString.substring(0, groupMembersAsString.length()-1);
        return groupMembersAsString;
    }

    // Return phone numbers of members
    public String getMembersPhone() {
        String groupMembersAsString = "";
        for (int i=0; i<groupMembers.size(); i++) {
            groupMembersAsString += groupMembers.get(i);
            groupMembersAsString += ",";
        }
        groupMembersAsString = groupMembersAsString.substring(0, groupMembersAsString.length()-1);
        return groupMembersAsString;
    }

    // Return phone numbers of members
    public String getMembersAsString() {
        String groupMembersAsString = "";

        if(groupMembers.size() > 0){ // In case they don't add anyone to the group
            for (int i=0; i<groupMembers.size(); i++) {
                groupMembersAsString += groupMembersName.get(i) + ":" + groupMembers.get(i);
                groupMembersAsString += ",";
            }
            groupMembersAsString = groupMembersAsString.substring(0, groupMembersAsString.length()-1);
        }

        return groupMembersAsString;
    }

    public HashMap<String, ParseGeoPoint> getAllLocations() {

        for (final Contact contact : groupContacts ) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereContains("phone", contact.getPhone());
            query.getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        groupLocation.put(contact.getPhone(), (ParseGeoPoint) parseUser.get("userLocation"));
                    }
                }
            });
        }

        return (HashMap) groupLocation;
    }

}
