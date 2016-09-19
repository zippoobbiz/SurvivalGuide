package com.assignment.xiaoduo.survivalguide.configurations;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by xiaoduo on 6/18/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "cJkzCJHwpJ0i8JR3NaC4hz7ISL2aC7qBPzvbBuot", "LjGg3bPFjDREcTQ5ohcWmAT4dYxNXX6tzu8nGeqG");
        ParseObject testObject = new ParseObject("TestObject");

        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
