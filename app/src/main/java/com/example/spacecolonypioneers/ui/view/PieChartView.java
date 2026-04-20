package com.example.spacecolonypioneers.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PieChartView extends View {
    private List<PieData> pieDataList;
    private Paint paint;
    private String title;
    private int totalValue;

    public static class PieData {
        public String label;
        public int value;
        public int color;

        public PieData(String label, int value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pieDataList = new ArrayList<PieData>();
        paint = new Paint();
        paint.setAntiAlias(true);
        title = "";
        totalValue = 0;
    }

    public void setData(List<PieData> data, String title) {
        this.pieDataList = data != null ? data : new ArrayList<PieData>();
        this.title = title != null ? title : "";
        totalValue = 0;
        for (PieData item : this.pieDataList) {
            if (item != null) totalValue += item.value;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pieDataList == null || pieDataList.isEmpty() || totalValue <= 0) return;

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2 - 10;
        int radius = Math.min(width, height) / 4;

        paint.setColor(Color.WHITE);
        paint.setTextSize(34);
        canvas.drawText(title, centerX - paint.measureText(title) / 2, 48, paint);

        RectF rect = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float startAngle = -90f;
        for (PieData data : pieDataList) {
            if (data == null || data.value <= 0) continue;
            float sweep = (float) data.value / totalValue * 360f;
            paint.setColor(data.color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(rect, startAngle, sweep, true, paint);
            startAngle += sweep;
        }

        paint.setTextSize(22);
        int legendY = centerY + radius + 40;
        for (int i = 0; i < pieDataList.size(); i++) {
            PieData data = pieDataList.get(i);
            if (data == null) continue;
            float percentage = totalValue > 0 ? (float) data.value / totalValue * 100f : 0f;
            paint.setColor(data.color);
            canvas.drawRect(40, legendY + i * 34 - 16, 68, legendY + i * 34 + 8, paint);
            paint.setColor(Color.WHITE);
            String label = data.label != null ? data.label : "Unknown";
            String legend = String.format(Locale.getDefault(), "%s: %d (%.1f%%)", label, data.value, percentage);
            canvas.drawText(legend, 80, legendY + i * 34, paint);
        }
    }
}
