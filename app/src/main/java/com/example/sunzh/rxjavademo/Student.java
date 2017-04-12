package com.example.sunzh.rxjavademo;

import java.util.List;

/**
 * Created by sunzh on 2017/4/10.
 */

class Student {
    private String name;
    private List<String> courses;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public Student() {
    }

    public Student(String name, List<String> courses) {
        this.name = name;
        this.courses = courses;
    }
}
