package com.example.a4tbrowser.model;


import androidx.fragment.app.Fragment;

public class Tab {

    private String name;
    private final Fragment fragment;

    public Tab(String name, Fragment fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
