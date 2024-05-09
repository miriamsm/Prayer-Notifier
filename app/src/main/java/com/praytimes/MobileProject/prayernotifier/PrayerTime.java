package com.praytimes.MobileProject.prayernotifier;

public class PrayerTime {

    String name;
    String time;
    boolean next;

    public  PrayerTime(){

    }

    public PrayerTime(String name, String time,boolean next) {
        this.name = name;
        this.time = time;
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }
}