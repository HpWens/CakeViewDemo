# CakeViewDemo

我们在设计图表的时候，有可能就会用到饼图。今天浏览网页无意发现了饼图，于是就想自己也设计一个，说干就干。以为很简单，还是有点费事，有想过放弃。但半途而废不是我的风格，就坚持写了下去。最后花了几个小时写出来了，还是有点小小的成绩感。饼图可以随你定制，来看看效果图：

![cake](http://img.blog.csdn.net/20160420234348449)

我们一起来看看设计流程：

##onDraw

如果你对自定义`View`还不是很了解，请阅读[Android自定义View基础篇系列](http://blog.csdn.net/u012551350/article/details/50913391)这里就略过`onMeasure`，直接来讲一下`onDraw`。如果你自定义View高手，也请略过本文。

首先我们得根据每块饼占整个圆的比例，来画圆弧。
```
  RectF mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
  
  canvas.drawArc(mRectF, startAngle, mList.get(i).percent / total * 360f, false, arcPaint);
```
这里得讲讲`RectF`与`Rect`区别，他们同样都是画矩形，区别在于`F`（浮点数）上面，`RectF`坐标点是`float`(浮点型)，`Rect`坐标点是`int`(整形)。那么`PointF`与`Point`一个道理。

`mList.get(i).percent` 表示每块饼的数值，`total` 表示所有饼数值之和。画完前一块饼圆弧，接着上一块圆弧的弧度接着画下一块圆弧。那么`startAngle`就是前面圆弧累和。

```
  startAngle += mList.get(i).percent / total * 360f;
```

每块的圆弧就画好了，接下来画每块圆弧间隔的直线，`  canvas.drawLine(startX,startY,stopX,stopY,mPaint);`变量分别有开始坐标和结束坐标。

```
        float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
        float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        float startX = (float) (centerX + (radius - arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (centerY + (radius - arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        PointF startPoint = new PointF(startX, startY);
        PointF stopPoint = new PointF(stopX, stopY);
```
加号表示外接点的坐标，减号表示内接点坐标。

```
        for (PointF[] fp : pointFs) {
            canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
        }
```
这样间隔直线就画好了，最后完成文字的绘画，绘画文字的点位于每块圆弧的中心点，那么主要就是计算每块圆弧的中心点坐标。首先获取每块圆弧中心点距离原始点的弧度：
```
 textAngle = startAngle + mList.get(i).percent / total * 360f / 2;   
```
知道弧度了，就可以获取坐标点了：

```
        float textPointX = (float) (centerX + radius * Math.cos(Math.toRadians(textAngle)));
        float textPointY = (float) (centerY + radius * Math.sin(Math.toRadians(textAngle)));
```
然后就是绘制文本了：

```
        for (int i = 0; i < textList.size(); i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            String text = mList.get(i).content;
            canvas.drawText(text, textList.get(i).x, textList.get(i).y, textPaint);
            }
```
最后就是绘制符号了，位于文字的正下方，那么我们需要知道文本的高度：

```
 Paint.FontMetrics fm = textPaint.getFontMetrics();
 int height=fm.descent - fm.ascent;
  canvas.drawText(format.format(mList.get(i).percent * 100 / total) + "%", textList.get(i).x, textList.get(i).y + height, textPaint);
```
到这里大体上就完成了饼图的制作，是不是很容易。下面我贴下源码，感兴趣的同学可以自行烤到自己项目中，动一动你的手指5分钟集成饼图：

```
package com.github.ws.cakeviewdemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
    private List<BaseMessage> mList;

    private Paint arcPaint;
    private Paint linePaint;
    private Paint textPaint;

    private float centerX;
    private float centerY;
    private float radius;
    private float total;
    private float startAngle;
    private float textAngle;

    private List<PointF[]> lineList;
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
        this.lineList = new ArrayList<>();
        this.textList = new ArrayList<>();
        this.mList = new ArrayList<>();
        this.format = new DecimalFormat("##0.00");

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
        radius = Math.min(centerX, centerY) * 0.725f;
        arcPaint.setStrokeWidth(radius / 3 * 2);
        textPaint.setTextSize(radius / 7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textList.clear();
        lineList.clear();
        lineList = new ArrayList<>();
        textList = new ArrayList<>();

        if (mList != null) {
            RectF mRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            for (int i = 0; i < mList.size(); i++) {
                arcPaint.setColor(mList.get(i).color);
                canvas.drawArc(mRectF, startAngle, mList.get(i).percent / total * 360f, false, arcPaint);

                lineList.add(getLinePointFs());//获取直线 开始坐标 结束坐标

                textAngle = startAngle + mList.get(i).percent / total * 360f / 2;
                textList.add(getTextPointF());   //获取文本文本

                startAngle += mList.get(i).percent / total * 360f;
            }
            //绘制间隔空白线
            drawSpacingLine(canvas, lineList);
            //绘制文字
            drawText(canvas);
        }
        
    }

    /**
     * 获取文本文本
     *
     * @return
     */
    private PointF getTextPointF() {
        float textPointX = (float) (centerX + radius * Math.cos(Math.toRadians(textAngle)));
        float textPointY = (float) (centerY + radius * Math.sin(Math.toRadians(textAngle)));
        return new PointF(textPointX, textPointY);
    }

    /**
     * 获取直线 开始坐标 结束坐标
     */
    private PointF[] getLinePointFs() {
        float stopX = (float) (centerX + (radius + arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
        float stopY = (float) (centerY + (radius + arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        float startX = (float) (centerX + (radius - arcPaint.getStrokeWidth() / 2) * Math.cos(Math.toRadians(startAngle)));
        float startY = (float) (centerY + (radius - arcPaint.getStrokeWidth() / 2) * Math.sin(Math.toRadians(startAngle)));
        PointF startPoint = new PointF(startX, startY);
        PointF stopPoint = new PointF(stopX, stopY);
        return new PointF[]{startPoint, stopPoint};
    }

    /**
     * 画间隔线
     *
     * @param canvas
     */
    private void drawSpacingLine(Canvas canvas, List<PointF[]> pointFs) {
        for (PointF[] fp : pointFs) {
            canvas.drawLine(fp[0].x, fp[0].y, fp[1].x, fp[1].y, linePaint);
        }
    }

    /**
     * 画文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        for (int i = 0; i < textList.size(); i++) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            String text = mList.get(i).content;
            canvas.drawText(text, textList.get(i).x, textList.get(i).y, textPaint);

            Paint.FontMetrics fm = textPaint.getFontMetrics();
            canvas.drawText(format.format(mList.get(i).percent * 100 / total) + "%", textList.get(i).x, textList.get(i).y + (fm.descent - fm.ascent), textPaint);
        }
    }

    /**
     * 设置间隔线的颜色
     *
     * @param color
     */
    public void setSpacingLineColor(int color) {
        linePaint.setColor(color);
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    /**
     * 设置开始角度
     *
     * @param startAngle
     */
    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
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
     * @param mList
     */
    public void setCakeData(List<BaseMessage> mList) {
        total = 0;
        if (mList == null) {
            return;
        }
        for (int i = 0; i < mList.size(); i++) {
            total += mList.get(i).percent;
        }
        this.mList.clear();
        this.mList = mList;
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

```

有什么宝贵的意见请给我留言。


