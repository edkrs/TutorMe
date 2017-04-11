package com.accipio.tutorme;


import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by rachel on 2016-11-03.
 */
public class Tutor {
    private String id;
    private String name;
    private String desc;
    private ArrayList<String> courses;
    private float rating;
    private int status;
    private String rate;

    public Tutor(String id, String name, String desc, ArrayList<String> courses, float rating, int status, String rate) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.courses = courses;
        this.rating = rating;
        this.status = status;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void addCourse(String courseAdd){
        this.courses.add(courseAdd);
    }

    public float getRating() {
        return rating;
    }

    public int getStatus() {
        return status;
    }

    public String getRate() { return rate; }
}
