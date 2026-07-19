package com.app.learning.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.vietsyncmobile.R;

public class RatingBarView extends LinearLayout {
    private ProgressBar[] progressBars = new ProgressBar[5];

    public RatingBarView(Context context) {
        super(context);
        init(context, null);
    }

    public RatingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RatingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.view_rating_distribution, this);
        progressBars[0] = findViewById(R.id.pb_5_star);
        progressBars[1] = findViewById(R.id.pb_4_star);
        progressBars[2] = findViewById(R.id.pb_3_star);
        progressBars[3] = findViewById(R.id.pb_2_star);
        progressBars[4] = findViewById(R.id.pb_1_star);
    }

    public void setDistribution(int[] counts) {
        if (counts == null || counts.length != 5) return;
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        for (int i = 0; i < 5; i++) {
            if (total == 0) {
                progressBars[i].setProgress(0);
            } else {
                int percent = (int) ((counts[i] * 100f) / total);
                progressBars[i].setProgress(percent);
            }
        }
    }
}
