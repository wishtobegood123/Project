package com.example.spacecolonypioneers.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {
    private List<BarData> barDataList;
    private Paint paint;
    private int maxValue;
    private String title;

    public static class BarData {
        public String label;
        public int value;
        public int color;

        public BarData(String label, int value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barDataList = new ArrayList<BarData>();
        paint = new Paint();
        paint.setAntiAlias(true);
        title = "";
        maxValue = 100;
    }

    public void setData(List<BarData> data, String title) {
        this.barDataList = data != null ? data : new ArrayList<BarData>();
        this.title = title != null ? title : "";
        this.maxValue = 100;
        for (BarData item : this.barDataList) {
            if (item != null && item.value > maxValue) {
                maxValue = item.value;
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (barDataList == null || barDataList.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int padding = 70;
        int chartWidth = width - padding * 2;
        int chartHeight = height - padding * 2;
        if (chartWidth <= 0 || chartHeight <= 0) return;

        paint.setColor(Color.WHITE);
        paint.setTextSize(34);
        canvas.drawText(title, padding, padding - 20, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(padding, padding, width - padding, height - padding, paint);

        paint.setStyle(Paint.Style.FILL);
        int barSpacing = 18;
        int barWidth = Math.max(24, chartWidth / barDataList.size() - barSpacing);
        for (int i = 0; i < barDataList.size(); i++) {
            BarData data = barDataList.get(i);
            if (data == null) continue;
            float barHeight = maxValue > 0 ? (float) data.value / maxValue * chartHeight : 0;
            float left = padding + i * (barWidth + barSpacing);
            float top = height - padding - barHeight;
            float right = left + barWidth;
            float bottom = height - padding;
            paint.setColor(data.color);
            canvas.drawRect(left, top, right, bottom, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            String label = "";
            if (data.label != null) {
                label = data.label.length() > 4 ? data.label.substring(0, 4) : data.label;
            }
            canvas.drawText(label, left, bottom + 24, paint);
            canvas.drawText(String.valueOf(data.value), left, top - 8, paint);
        }
    }
}
