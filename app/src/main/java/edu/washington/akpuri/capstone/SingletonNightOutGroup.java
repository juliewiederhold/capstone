package edu.washington.akpuri.capstone;

import java.util.HashMap;

/**
 * Created by iguest on 5/20/15.
 */
public class SingletonNightOutGroup {

    private static final String TAG = "SingletonNightOutGroup";
    private static SingletonNightOutGroup instance = null;
    private static HashMap<String, Contact> group;
    private static String groupName;
    private static Contact starter;

    protected SingletonNightOutGroup() {
        // Empty
    }

    public static SingletonNightOutGroup getInstance() {
        if (instance == null) {
            instance = new SingletonNightOutGroup();
            group = new HashMap<>();
        }
        return instance;
    }

    public String createGroup(String name, Contact starter) {
        this.groupName = name;
        this.starter = starter;
        return "Created group: " + groupName;
    }

    public HashMap<String, Contact> getGroup(String groupName) {
        return group;
    }

    // Add group member using phone # as key and contact object as value
    public String addMember(Contact contact) {
        group.put(contact.getPhone(), contact);
        return "Added: " + contact.getEmail();
    }

    // Remove member from group
    public String removeMember(Contact contact) {
        group.remove(contact.getPhone());
        return "Removed: " + contact.getEmail();
    }

    public String getGroupName(SingletonNightOutGroup group){
        return groupName;
    }

    public String clearGroup() {
        group.clear();
        return "Cleared: " + groupName;
    }

}
