package com.alangiu.sample.dns_injection_sample;

import android.content.Context;

import org.openobservatory.measurement_kit.common.LogCallback;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.EntryCallback;
import org.openobservatory.measurement_kit.nettests.TestCompleteCallback;
import org.openobservatory.measurement_kit.nettests.TestListener;

import java.util.Date;
import java.util.LinkedList;

public class Test {

    public Integer id;
    public Long timestamp;
    public boolean running;

    public String log = ""; // String to concat logs

    public LinkedList<String> entryList = new LinkedList<>();

    public Test() {
        this.timestamp = new Date().getTime();
    }


    public void setRunning(Context ctx) {
        this.running = true;
        final TestListener testListener = new TestListener(id, ctx);
        testListener.on_log(new LogCallback() {
            @Override
            public void callback(long verbosity, String message) {
                if ((verbosity & LogSeverity.JSON) == 0){
                    log = log.concat("\n");
                    log = log.concat(message);
                } // if json do nothing
            }
        }).on_entry(new EntryCallback() {
            @Override
            public void callback(String entry) {
                 entryList.add(entry);
            }
        }).on_end(new TestCompleteCallback() {
            @Override
            public void callback() {
                running = false;
                testListener.clear_all();
            }
        });
    }
}
