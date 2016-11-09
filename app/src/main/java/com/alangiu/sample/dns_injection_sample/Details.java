package com.alangiu.sample.dns_injection_sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;


public class Details extends AppCompatActivity {

    private BarGraphSeries<DataPoint> graph;
    private final int INJECTED = 1;
    private final int NOT_INJECTED = 3;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setGraph();
    }

    private void setGraph() {
        GraphView graphView = (GraphView) findViewById(R.id.details_graph1);

        graph = new BarGraphSeries<>();
        graph.setSpacing(50);

        graph.appendData(new DataPoint(INJECTED, 5), false, 4);
        graph.appendData(new DataPoint(NOT_INJECTED, 10), false, 4);

        graph.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getX() == INJECTED) {
                    return Color.RED;
                } else {
                    return Color.GREEN;
                }
            }
        });

        graph.setDrawValuesOnTop(true);
        graph.setValuesOnTopColor(Color.WHITE);

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(4);
        graphView.addSeries(graph);
        graphView.getLegendRenderer().setVisible(false);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graphView.getGridLabelRenderer().setPadding(0);
    }
}

