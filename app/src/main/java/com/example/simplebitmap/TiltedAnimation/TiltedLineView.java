package com.example.simplebitmap.TiltedAnimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TiltedLineView extends View {
    private Paint linePaint,pPaint,npaint,bPaint,paint;
    private int centerX;
    private int centerY;
    private float tiltAngle = 0.0f;


    public TiltedLineView(Context context) {
        super(context);
        init();
    }

    public TiltedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TiltedLineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY);
        linePaint.setStrokeWidth(13);
        pPaint = new Paint();
        pPaint.setColor(Color.BLACK);
        pPaint.setStrokeWidth(15);
        npaint = new Paint();
        npaint.setColor(Color.WHITE);
        bPaint = new Paint();
        bPaint.setColor(Color.GRAY);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int x = centerX-centerX%5;
        canvas.drawCircle(x, centerY,30,pPaint);
        canvas.drawLine(x,centerY+10,x,centerY+123,pPaint);
        canvas.drawLine(x,centerY+50,x-30,centerY+100,pPaint);
        canvas.drawCircle(x-30,centerY+100,8,pPaint);
        canvas.drawLine(x-30,centerY+100,x-40,centerY+150,pPaint);
        canvas.drawLine(x,centerY+123,x-30,centerY+230,pPaint);
        canvas.drawLine(x,centerY+123,x+50,centerY+190,pPaint);
        canvas.drawCircle(x+50,centerY+190,8,pPaint);
        canvas.drawLine(x+50,centerY+190,x+40,centerY+230,pPaint);
        canvas.save();
        canvas.rotate(tiltAngle, x+118, centerY+230);
        Path path = new Path();
        RectF rect = new RectF(x+88,centerY-110,x+156,centerY-70);
        path.addArc(rect, 0f, -180f);
        canvas.drawRect(x+88,centerY-85,x+156,centerY-60,bPaint);
        canvas.drawRect(x+98,centerY-75,x+144,centerY-70,paint);
        canvas.drawPath(path, bPaint);
        canvas.drawLine(x+120,centerY-70,x+120,centerY+230,linePaint);
        canvas.restore();
        canvas.drawLine(x,centerY+50,x+60-tiltAngle,centerY+100,pPaint);
        canvas.drawCircle(x+60-tiltAngle,centerY+100,8,pPaint);
        canvas.drawLine(x+60-tiltAngle,centerY+100,x+125+tiltAngle,centerY+100+tiltAngle,pPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    public void startAnimation(float x) {
        ValueAnimator animator = ValueAnimator.ofFloat(-05.0f, 10.0f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tiltAngle = (float) animation.getAnimatedValue();
                Log.d("tiltAngle",""+tiltAngle);
                invalidate();
            }
        });
        animator.start();
    }
}

