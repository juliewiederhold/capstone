package edu.washington.akpuri.capstone;

/**
 * Created by Akash on 3/9/2015.
 */
public class Contact {
    private String name;
    private String phone;
    private int id;

    public Contact(String name, String phone, int id) {
        this.name = name;
        this.phone = phone;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }
}
