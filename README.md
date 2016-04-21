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
到这里大体上就完成了饼图的制作，是不是很容易。感兴趣的同学可以自行烤到自己项目中，动一动你的手指5分钟集成饼图：

有什么宝贵的意见请给我留言。


