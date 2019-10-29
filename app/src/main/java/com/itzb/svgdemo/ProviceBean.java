package com.itzb.svgdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

public class ProviceBean {
    private Path mPath;
    private int drawColor;

    public ProviceBean(Path path) {
        mPath = path;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    void drawItem(Canvas canvas, Paint paint, boolean isSelect) {
        if (isSelect) {
//            绘制内部的颜色
            paint.clearShadowLayer();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED);
            canvas.drawPath(mPath, paint);
//            绘制边界
            paint.setStyle(Paint.Style.STROKE);
            int strokeColor = 0xFFD0E8F4;
            paint.setColor(strokeColor);
            canvas.drawPath(mPath, paint);
        } else {
//            绘制内部的颜色
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8, 0, 0, 0xffffff);
            canvas.drawPath(mPath, paint);
//            绘制边界
            paint.clearShadowLayer();
            paint.setColor(drawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);
        }

    }

    public boolean isTouch(float x, float y) {
        RectF rectF = new RectF();
        mPath.computeBounds(rectF, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        return region.contains((int) x, (int) y);
    }
}
