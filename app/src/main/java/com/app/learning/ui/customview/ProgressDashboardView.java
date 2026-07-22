package com.app.learning.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressDashboardView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPaint;
    
    private RectF rectF;
    private float progress = 0f; // 0 to 100
    private float strokeWidth = 20f;
    
    public ProgressDashboardView(Context context) {
        super(context);
        init();
    }

    public ProgressDashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);
        
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.BLUE);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = strokeWidth / 2;
        rectF.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background circle
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, (getWidth() / 2f) - (strokeWidth / 2f), backgroundPaint);
        
        // Draw progress arc
        float angle = (progress / 100f) * 360f;
        canvas.drawArc(rectF, -90, angle, false, progressPaint);
        
        // Draw text in the middle
        String text = (int)progress + "%";
        float textY = (getHeight() / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f);
        canvas.drawText(text, getWidth() / 2f, textY, textPaint);
    }
    
    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        invalidate(); // Redraw view
    }
}
