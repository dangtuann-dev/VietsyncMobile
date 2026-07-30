package com.app.learning.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import com.app.learning.data.model.LearningSessionModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeatmapCalendarView extends View {

    private Paint cellPaint;
    private Paint textPaint;
    private Map<String, Integer> dateDurationMap = new HashMap<>();

    private int numWeeks = 13; // ~90 days
    private int numDaysPerWeek = 7;
    private float cellSize = 36f;
    private float cellSpacing = 8f;
    private float cornerRadius = 6f;

    // Color gradient for heatmap
    private int colorEmpty = Color.parseColor("#E0E0E0");
    private int colorLevel1 = Color.parseColor("#C6E48B");
    private int colorLevel2 = Color.parseColor("#7BC96F");
    private int colorLevel3 = Color.parseColor("#239A3B");
    private int colorLevel4 = Color.parseColor("#196127");

    public HeatmapCalendarView(Context context) {
        super(context);
        init();
    }

    public HeatmapCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeatmapCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cellPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(24f);
    }

    public void setSessionData(List<LearningSessionModel> sessions) {
        dateDurationMap.clear();
        if (sessions != null) {
            for (LearningSessionModel s : sessions) {
                dateDurationMap.put(s.getDate(), s.getDurationMinutes());
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = numWeeks * (cellSize + cellSpacing) + getPaddingLeft() + getPaddingRight();
        float height = numDaysPerWeek * (cellSize + cellSpacing) + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startX = getPaddingLeft();
        float startY = getPaddingTop();

        // Simulated rendering of past 90 days grid (13 weeks x 7 days)
        int dayCounter = 0;

        for (int w = 0; w < numWeeks; w++) {
            for (int d = 0; d < numDaysPerWeek; d++) {
                float left = startX + w * (cellSize + cellSpacing);
                float top = startY + d * (cellSize + cellSpacing);
                float right = left + cellSize;
                float bottom = top + cellSize;

                // Pick color level based on dummy/passed data
                int minutes = getMinutesForIndex(dayCounter);
                cellPaint.setColor(getColorForMinutes(minutes));

                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, cellPaint);

                dayCounter++;
            }
        }
    }

    private int getMinutesForIndex(int index) {
        if (index % 7 == 0) return 0;
        if (index % 5 == 0) return 15;
        if (index % 3 == 0) return 45;
        if (index % 2 == 0) return 75;
        return 20;
    }

    private int getColorForMinutes(int minutes) {
        if (minutes <= 0) return colorEmpty;
        if (minutes <= 20) return colorLevel1;
        if (minutes <= 45) return colorLevel2;
        if (minutes <= 75) return colorLevel3;
        return colorLevel4;
    }
}
