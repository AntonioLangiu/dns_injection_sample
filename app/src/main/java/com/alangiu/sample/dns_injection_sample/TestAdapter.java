package com.alangiu.sample.dns_injection_sample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.openobservatory.measurement_kit.common.LogCallback;
import org.openobservatory.measurement_kit.common.LogSeverity;
import org.openobservatory.measurement_kit.nettests.TestCompleteCallback;
import org.openobservatory.measurement_kit.nettests.TestListener;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    private List<Test> tests;
    private Context ctx;

    public TestAdapter(Context ctx, List<Test> tests) {
        this.ctx = ctx;
        this.tests = tests;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View testView = inflater.inflate(R.layout.test_item, parent, false);

        return new ViewHolder(testView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Test test = tests.get(position);
        if (holder.testListener != null) {
            holder.testListener.clear_all();
        }

        holder.title.setText("Test "+test.id);
        holder.time.setText(DateFormat.format("dd/MM/yyyy hh:mm", test.timestamp).toString());

        if (test.running) {
            holder.status.setText("running...");
            holder.progress.setIndeterminate(true);
            holder.testListener = new TestListener(test.id, ctx);
            holder.testListener.on_log(new LogCallback() {
                @Override
                public void callback(long verbosity, String message) {
                    if ((verbosity & LogSeverity.JSON) != 0) {
                        try {
                            JSONObject json = new JSONObject(message);
                            if (json.getString("type").equals("progress")) {
                                holder.progress.setIndeterminate(false);
                                Double progress = json.getDouble("progress") * 100;
                                holder.progress.setProgress(progress.intValue());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).on_end(new TestCompleteCallback() {
                @Override
                public void callback() {
                    Log.d(test.id.toString(), "on_end");
                    holder.status.setText("complete");
                    holder.progress.setIndeterminate(false);
                    holder.progress.setProgress(100);
                    holder.testListener.clear_all();
                }
            });
        } else { // Not running
            holder.status.setText("complete");
            holder.progress.setIndeterminate(false);
            holder.progress.setProgress(100);
        }
    }


    @Override
    public int getItemCount() {
        return tests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView time;
        private TextView status;
        private TextView injected;
        private TextView title;
        private ProgressBar progress;

        // the principle of a recycler view is that the view are `recycled`
        // and reused mantaining always the same ViewHolder for different data.
        // When a view is binded (onBindViewHolder) the view holder can be a new
        // one or a recycled one, that contains the old references to the view.
        // This is very helpful because I can use it to hold the reference of the
        // testListener and remove the callaback every time we are reusing
        // a view for a different item.
        private TestListener testListener;

        public ViewHolder(View itemView) {
            super(itemView);
            // one of the most heavy part when creating a list view is to find every
            // time the view reference, and to avoid this we use recycler view
            // that recycle the view and the holder, so we can search for
            // the view items just the first time we need it!
            time = (TextView) itemView.findViewById(R.id.test_item_time);
            status = (TextView) itemView.findViewById(R.id.test_item_status);
            injected = (TextView) itemView.findViewById(R.id.test_item_injected);
            title = (TextView) itemView.findViewById(R.id.test_item_title);
            progress = (ProgressBar) itemView.findViewById(R.id.test_item_progress);
            cardView = (CardView) itemView.findViewById(R.id.test_item_card);
        }

    }

}
