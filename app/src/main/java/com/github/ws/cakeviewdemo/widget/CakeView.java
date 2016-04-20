package com.github.ws.cakeviewdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;

import com.github.ws.cakeviewdemo.bean.BaseMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 4/20 0020.
 */
public class CakeView extends View {

    private Context ctx;
    private DecimalFormat format;
    private SparseArray<BaseMessage> sparseArray;

    private Paint arcPaint;
    private Paint linePaint;
    private Paint textPaint;

    private float centerX;
    private float centerY;
    private float radius;
    private float total;
    private float startAngle;
    private float textAngle;

    private List<PointF[]> pointList;
    private List<PointF> textList;

    public CakeView(Context context) {
        this(context, null);
    }

    public CakeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.ctx = context;
        this.pointList = new ArrayList<>();
        this.textList = new ArrayList<>();
        this.format = new DecimalFormat("##0.00");
        this.sparseArray = new SparseArray<>();

        this.arcPaint = new Paint();
        this.arcPaint.setAntiAlias(true);
        this.arcPaint.setDither(true);
        this.arcPaint.setStyle(Paint.Style.STROKE);

        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeWidth(dip2px(ctx, 2));
        this.linePaint.setColor(Color.parseColor("#FFFFFF"));

        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setDither(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width;
        int height;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
            width = Math.min(heightSpecSize, Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = Math.min(widthSpecSize, Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]));
        } else if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = height = Math.min(getScreenSize(ctx)[0], getScreenSize(ctx)[1]);
        } else {
            width = widthSpecSize;
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(centerX, centerY) * 0.72f;
        arcPaint.setStrokeWidth(radius / 3 * 2);
        textPaint.setTextSize(radius / 7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        if (sparseArray != null) {
            for (int i = 0; i < sparseArray.size(); i++) {
                arcPaint.setColor(sparseArray.get(sparseArray.keyAt(i)).color);
                canvas.drawArc(mRectF, startAngle, sparseArray.get(sparseArray.keyAt(i)).percent / total * 360f, false, arcPaint);

                float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
                float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));

                float startX = (float) (centerX + (radius - arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
                float startY = (float) (centerY + (radius - arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));

                PointF startPoint = new PointF(startX, startY);
                PointF stopPoint = new PointF(stopX, stopY);

                pointList.add(new PointF[]{startPoint, stopPoint});

                textAngle = startAngle + sparseArray.get(sparseArray.keyAt(i)).percent / total * 360f / 2;

                float textPointX = (float) (centerX + radius * Math.cos(Math.toRadians(textAngle)));
                float textPointY = (float) (centerY + radius * Math.sin(Math.toRadians(textAngle)));

                PointF textPoint = new PointF(textPointX, textPointY);

                textList.add(textPoint);

                startAngle += sparseArray.get(sparseArray.keyAt(i)).percent / total * 360f;

            }

            for (PointF[] fp : pointList) {
                canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
            }

            for (int i = 0; i < textList.size(); i++) {
                textPaint.setTextAlign(Paint.Align.CENTER);
                String text = sparseArray.get(sparseArray.keyAt(i)).content;
                canvas.drawText(text, textList.get(i).x, textList.get(i).y, textPaint);

                Paint.FontMetrics fm = textPaint.getFontMetrics();
                canvas.drawText(format.format(sparseArray.get(sparseArray.keyAt(i)).percent * 100 / total) + "%", textList.get(i).x, textList.get(i).y + (fm.descent - fm.ascent), textPaint);
            }

        }
    }

    /**
     * 设置饼的宽度
     *
     * @param width
     */
    public void setCakeStrokeWidth(int width) {
        arcPaint.setStrokeWidth(dip2px(ctx, width));
    }

    /**
     * 设置饼的数据
     *
     * @param sparseArray
     */
    public void setCakeData(SparseArray<BaseMessage> sparseArray) {
        if (sparseArray == null) {
            return;
        }
        for (int i = 0; i < sparseArray.size(); i++) {
            total += sparseArray.get(sparseArray.keyAt(i)).percent;
        }
        this.sparseArray = sparseArray;
        invalidate();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }
}
