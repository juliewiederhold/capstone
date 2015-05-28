package edu.washington.akpuri.capstone;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by iguest on 5/20/15.
 */
public class SingletonNightOutGroup {

    private static final String TAG = "SingletonNightOutGroup";
    private static SingletonNightOutGroup instance = null;
    private static Map<String, Contact> groupContact;
    private static Map<String, ParseUser> groupParse;
    private static String groupName;
    private static Contact starter;
    private static ParseUser currentUser;
    private static ArrayList<String> groupMembers, groupMembersName;

    protected SingletonNightOutGroup() {
        // Empty
    }

    public static SingletonNightOutGroup getInstance() {
        if (instance == null) {
            instance = new SingletonNightOutGroup();
            groupContact = new HashMap<>();
            groupParse = new HashMap<>();
            groupMembers = new ArrayList<>();
            groupMembersName = new ArrayList<>();
            groupName = "";
        }
        return instance;
    }

    public String createGroup(String name, Contact starter) {
        this.groupName = name;
        this.starter = starter;
        this.currentUser = SingletonUser.getInstance().getCurrentUser();
        this.groupContact.put(starter.getPhone(), starter);
        this.groupParse.put(starter.getPhone(), currentUser);
        return "Created group: " + groupName;
    }

    public HashMap<String, Contact> getGroupContact() {
        return (HashMap) groupContact;
    }
    public HashMap<String, ParseUser> getGroupParse() {
        return (HashMap) groupParse;
    }

    // Add group member using phone # as key and contact object and ParseUser object as value
    public String addMember(Contact contact, ParseUser user) {
        groupContact.put(contact.getPhone(), contact);
        groupParse.put(contact.getPhone(), user);
        return "Added: " + contact.getEmail();
    }

    // Remove member from group
    public String removeMember(Contact contact) {
        groupContact.remove(contact.getPhone());
        groupParse.remove(contact.getPhone());
        return "Removed: " + contact.getEmail();
    }

    public String getGroupName(){
        return groupName;
    }

    public String clearGroup() {
        groupContact.clear();
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
        for (int i=0; i<groupMembers.size(); i++) {
            groupMembersAsString += groupMembersName.get(i) + ":" + groupMembers.get(i);
            groupMembersAsString += ",";
        }
        groupMembersAsString = groupMembersAsString.substring(0, groupMembersAsString.length()-1);
        return groupMembersAsString;
    }

}
