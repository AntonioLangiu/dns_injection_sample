package com.alangiu.sample.dns_injection_sample;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.openobservatory.measurement_kit.android.LoadLibraryUtils;
import org.openobservatory.measurement_kit.android.ResourceUtils;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.DnsInjectionTest;

import java.util.LinkedList;

public class Home extends AppCompatActivity {

    private static final String INPUT_FILE = "hosts.txt";
    public LinkedList<Test> testList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeLibrary();
        copyInputFile();

        final RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        final TestAdapter testAdapter = new TestAdapter(this, testList);
        rv.setAdapter(testAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // set up the fab button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testList.add(run());
                testAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initializeLibrary() {
        LoadLibraryUtils.load_measurement_kit();
        ResourceUtils.copy_ca_bundle(this, R.raw.cacert);
        ResourceUtils.copy_geoip(this, R.raw.geoip);
        ResourceUtils.copy_geoip_asnum(this, R.raw.geoipasnum);
    }

    private void copyInputFile() {
        ResourceUtils.copy_resource(this, R.raw.hosts, INPUT_FILE);
    }

    private Test run() {
        DnsInjectionTest test = new DnsInjectionTest();
        Test testInfo = new Test();
        testInfo.id = test.set_verbosity(LogSeverity.INFO)
                .set_options("backend", "8.8.8.1") // invalid dns
                .set_options("dns/nameserver", "192.168.178.1:53")
                .set_options("net/ca_bundle_path", ResourceUtils.get_ca_bundle_path(this))
                .set_options("geoip_country_path", ResourceUtils.get_geoip_path(this))
                .set_options("geoip_asn_path", ResourceUtils.get_geoip_asnum_path(this))
                .set_input_filepath(ResourceUtils.get_path(this, INPUT_FILE))
                .set_options("no_file_report", "1")
                .run(this);
        testInfo.setRunning(this);
        return testInfo;
    }


}



