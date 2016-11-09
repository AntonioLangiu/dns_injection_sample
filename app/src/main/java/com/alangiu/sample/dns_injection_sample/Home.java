package com.alangiu.sample.dns_injection_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.openobservatory.measurement_kit.android.LoadLibraryUtils;
import org.openobservatory.measurement_kit.android.ResourceUtils;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeLibrary();
    }

    private void initializeLibrary() {
        LoadLibraryUtils.load_measurement_kit();
        ResourceUtils.copy_ca_bundle(this, R.raw.cacert);
        ResourceUtils.copy_geoip(this, R.raw.geoip);
        ResourceUtils.copy_geoip_asnum(this, R.raw.geoipasnum);
    }
}
