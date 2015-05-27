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
    private static ArrayList<String> groupMembers;

    protected SingletonNightOutGroup() {
        // Empty
    }

    public static SingletonNightOutGroup getInstance() {
        if (instance == null) {
            instance = new SingletonNightOutGroup();
            groupContact = new HashMap<>();
            groupParse = new HashMap<>();
            groupMembers = new ArrayList<>();
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

    // Add group member using phone # as key and contact object as value
    public String addMemberContact(Contact contact) {
        groupContact.put(contact.getPhone(), contact);
        return "Added: " + contact.getEmail();
    }

    public String addMemberParse(Contact contact, ParseUser user) {
        groupParse.put(contact.getPhone(), user);
        return "Added: " + contact.getEmail();
    }

    // Remove member from group
    public String removeMember(Contact contact) {
        groupContact.remove(contact.getPhone());
        groupParse.remove(contact.getPhone());
        return "Removed: " + contact.getEmail();
    }

    public String getGroupName(SingletonNightOutGroup group){
        return groupName;
    }

    public String clearGroup() {
        groupContact.clear();
        groupParse.clear();
        return "Cleared: " + groupName;
    }

    public ArrayList<String> getMembers(){
        Iterator<Map.Entry<String, Contact>> iterator = groupContact.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Contact> entry = iterator.next();
            groupMembers.add(entry.getKey());
        }
        return groupMembers;
    }

    public String getMembersAsString(){
        String groupMembersAsString = "";
        for (int i=0; i<groupMembers.size(); i++) {
            groupMembersAsString += groupMembers.get(i);
            groupMembersAsString += ",";
        }
        groupMembersAsString = groupMembersAsString.substring(0, groupMembersAsString.length()-1);
        return groupMembersAsString;
    }

}
