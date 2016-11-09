package com.alangiu.sample.dns_injection_sample;

import android.app.Application;

import java.util.LinkedList;

public class MyApplication extends Application {
    public LinkedList<Test> testList;

    @Override
    public void onCreate() {
        super.onCreate();
        testList = new LinkedList<>();
    }
}
