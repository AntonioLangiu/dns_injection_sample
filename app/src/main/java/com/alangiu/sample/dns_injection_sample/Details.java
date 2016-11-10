package com.alangiu.sample.dns_injection_sample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.nettests.EntryCallback;
import org.openobservatory.measurement_kit.nettests.TestCompleteCallback;
import org.openobservatory.measurement_kit.nettests.TestListener;

import java.util.LinkedList;


public class Details extends AppCompatActivity {

    private BarGraphSeries<DataPoint> graph = null;
    private final int INJECTED = 3;
    private final int NOT_INJECTED = 6;

    private EntryAdapter entryAdapter = null;

    private MyApplication myApplication = null;
    private Test test = null;

    private Integer injectedEntry = 0;
    private Integer notInjectedEntry = 0;

    private TestListener testListener = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initializeData();
        setGraph();
        setRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        testListener.clear_all();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        setListener();
    }


    private void initializeData() {
        Integer position = getIntent().getExtras().getInt("position");
        if (position < 0) {
            Log.e("details", "invalid position");
            finish();
        }
        myApplication = (MyApplication) getApplicationContext();
        test = myApplication.testList.get(position);
    }

    private void setRecyclerView() {
        final RecyclerView rv = (RecyclerView) findViewById(R.id.details_recycler_view);
        entryAdapter = new EntryAdapter(this, test.entryList);
        rv.setAdapter(entryAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setGraph() {
        GraphView graphView = (GraphView) findViewById(R.id.details_graph1);

        graph = new BarGraphSeries<>();
        graph.setSpacing(80);

        graph.appendData(new DataPoint(INJECTED, injectedEntry), false, 4);
        graph.appendData(new DataPoint(NOT_INJECTED, notInjectedEntry), false, 4);

        graph.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getX() == INJECTED) {
                    return ContextCompat.getColor(getApplicationContext(), R.color.red);
                } else {
                    return ContextCompat.getColor(getApplicationContext(), R.color.green);

                }
            }
        });

        graph.setDrawValuesOnTop(true);
        graph.setValuesOnTopColor(Color.WHITE);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(8);
        graphView.addSeries(graph);
        graphView.getLegendRenderer().setVisible(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graphView.getGridLabelRenderer().setPadding(0);
    }

    private void getData() {
        int i = 0;
        String item;
        for (i = 0; i<test.entryList.size(); i++) {
            item = test.entryList.get(i);
            updateGraphWithEntry(item);
        }
    }

    private void setListener() {
        testListener = new TestListener(test.id, this);
        testListener.on_entry(new EntryCallback() {
            @Override
            public void callback(String entry) {
                Log.d("on_Entry", entry);
                entryAdapter.notifyDataSetChanged();
                updateGraphWithEntry(entry);
            }
        }).on_end(new TestCompleteCallback() {
            @Override
            public void callback() {
                testListener.clear_all();
            }
        });
    }

    public void updateGraphWithEntry(String entry) {
        Entry entryParsed = new Entry(entry);
        if (entryParsed.injected) {
            injectedEntry++;
            graph.appendData(new DataPoint(INJECTED, injectedEntry), false, 4);
        } else {
            notInjectedEntry++;
            graph.appendData(new DataPoint(NOT_INJECTED, notInjectedEntry), false, 4);
        }
    }

    public void backPressed(View view) {
        finish();
    }

    private class Entry {
        public String input = "";
        public Boolean injected = false;

        public Entry(String entry) {
            /// XXX remember to check the version
            try {
                JSONObject json = new JSONObject(entry);
                this.input = json.getString("input");
                this.injected = json.getJSONObject("test_keys").getBoolean("injected");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {

        public LinkedList<String> entryList;
        public Context ctx;

        public EntryAdapter(Context ctx, LinkedList<String> entryList) {
            this.entryList = entryList;
            this.ctx = ctx;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View testView = inflater.inflate(R.layout.dns_injection_item, parent, false);
            return new ViewHolder(testView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // this function takes the json of the entry and it fills the view
            String entry = test.entryList.get(position);
            Entry entryParsed = new Entry(entry);
            holder.site.setText(entryParsed.input);
            if (entryParsed.injected) {
                holder.injected.setText("Injected");
                holder.injected.setBackgroundColor(ContextCompat.getColor(ctx, R.color.red));
            }else {
                holder.injected.setText("Not Injected");
                holder.injected.setBackgroundColor(ContextCompat.getColor(ctx, R.color.green));
            }


        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {


            public TextView site = null;
            public TextView injected = null;

            public ViewHolder(View itemView) {
                super(itemView);
                site = (TextView) itemView.findViewById(R.id.dns_injection_item_site);
                injected = (TextView) itemView.findViewById(R.id.dns_injection_item_value);
            }
        }
    }

}

