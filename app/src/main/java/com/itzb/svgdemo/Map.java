package com.itzb.svgdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Map extends View {
    private List<ProviceBean> provices;
    private Paint paint;
    private ProviceBean select;
    private RectF totalRect;
    private float scale = 1.0f;//缩放比例，让地图在屏幕全屏显示
    private Context mContext;

    public Map(Context context) {
        this(context, null);
    }

    public Map(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public Map(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        provices = new ArrayList();
        paint = new Paint();
        paint.setAntiAlias(true);
        provices = new ArrayList<>();
        new LoadThread().start();//读取数据
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //        获取到当前控件宽高值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (totalRect != null) {
            scale = width / totalRect.width();
        }

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(scale, scale);
        for (ProviceBean provice : provices) {
            provice.drawItem(canvas, paint, select == provice ? true : false);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (ProviceBean provice : provices) {
            boolean isTouch = provice.isTouch(event.getX() / scale, event.getY() / scale);
            if (isTouch) {
                select = provice;
                invalidate();
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    private class LoadThread extends Thread {
        private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1};

        @Override
        public void run() {
            super.run();
            final InputStream inputStream = mContext.getResources().openRawResource(R.raw.map_china);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  //取得DocumentBuilderFactory实例
            DocumentBuilder builder = null; //从factory获取DocumentBuilder实例
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);   //解析输入流 得到Document实例
                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");
                float left = -1;
                float right = -1;
                float top = -1;
                float bottom = -1;
                List<ProviceBean> list = new ArrayList<>();
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("d");
                    Path path = PathParser.createPathFromPathData(pathData);
                    ProviceBean proviceItem = new ProviceBean(path);
                    proviceItem.setDrawColor(colorArray[i % 3]);
                    RectF rect = new RectF();
                    path.computeBounds(rect, true);
                    left = left == -1 ? rect.left : Math.min(left, rect.left);
                    right = right == -1 ? rect.right : Math.max(right, rect.right);
                    top = top == -1 ? rect.top : Math.min(top, rect.top);
                    bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
                    list.add(proviceItem);
                }
                provices = list;
                totalRect = new RectF(left, top, right, bottom);
//                刷新界面
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout();
                        invalidate();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
