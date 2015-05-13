package edu.washington.akpuri.capstone;

/**
 * Created by Julie on 3/10/15.
 */
public class SafetyZone {
    public String name;
    public String address;
    public String city;
    public int zip;
    public String state;
    private boolean isNightOutOnlyZone;

    public SafetyZone(String name, String address, String city, int zip, String state){
        this.name = name;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String returnAddress(){
        return address + " " + city + " " + state + " " + zip;
    }

}
