package com.shauryachats.grapher;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by shauryachats on 17/4/17.
 */

public class GraphicActivity extends AppCompatActivity{

    int semiheight, semiwidth;
    double scale;
    double centerX, centerY;

    double precision;

    boolean multiTouch;
    long timeFingersSwitched;

    HashMap<Double, Double> data;

    float prevX, prevY;

    private ScaleGestureDetector scaleGestureDetector;

    EquationEvaluator equationEvaluator;

    public final String TAG = "GraphicActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        equationEvaluator = new EquationEvaluator(bundle.getStringArrayList("postfix"));
        precision = bundle.getDouble("precision");

        centerX = centerY = 0.0f;
        scale = 1.0f;

        multiTouch = false;

        data = new HashMap<Double, Double>();

        CanvasView canvas = new CanvasView(this);
        setContentView(canvas);

    }

    private class CanvasView extends View {

        private static final long ALLOWED_TIME = 100;

        public CanvasView(Context context) {
            super(context);
            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    Log.d(TAG, "Zoom ongoing, scale" + detector.getScaleFactor());
                    scale /= detector.getScaleFactor();
                    invalidate();
                    return false;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {

                }
            });
            Log.d(TAG, "ctor()");
        }

        double getXCoords(double x)
        {
            return semiwidth + (x - centerX) * (semiwidth / scale);
        }

        double getYCoords(double y)
        {
            return semiheight - (y - centerY) * (semiheight / scale);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
           // Log.d(TAG, "onTouchEvent()");
            //Log.d(TAG, "onTouchEvent()" + event.getX() + " " + event.getY());

            scaleGestureDetector.onTouchEvent(event);
            //return true;

            int eventAction = event.getAction();
            int fingersHere = event.getPointerCount();

            if (fingersHere > 1) {
                multiTouch = true;
                return true;
            }
            else if (fingersHere == 1 && multiTouch) {
                multiTouch = false;
                timeFingersSwitched = System.currentTimeMillis();
                return true;
            }

            if (fingersHere == 1 && System.currentTimeMillis() - timeFingersSwitched <= ALLOWED_TIME)
            {
                return true;
            }

            float eventX = event.getX();
            float eventY = event.getY();

            switch (eventAction)
            {
                case MotionEvent.ACTION_DOWN:
                    prevX = eventX;
                    prevY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG + "***", "" + event.getX() + " " + event.getY());
                    centerX -= round((eventX - prevX)/1000f * scale,3);
                    centerY += round((eventY - prevY)/1000f * scale,3);
                    prevX = eventX;
                    prevY = eventY;
                    invalidate();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                default:
                    return false;
            }

            //  Toast.makeText(GraphicActivity.this, "Touched!", Toast.LENGTH_SHORT).show();
            return super.onTouchEvent(event);

        }

        double round(double num, double precision)
        {
            double val = Math.pow(10.0f, precision);
            return Math.floor(num * val) / val;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.d(TAG, "Calling onDraw()");
            super.onDraw(canvas);

            semiheight = canvas.getHeight()/2;
            semiwidth = canvas.getWidth()/2;

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            // make the entire canvas white
            paint.setColor(Color.BLACK);
            canvas.drawPaint(paint);

            drawAxes(canvas);
            //drawOrigin(canvas);


            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(3f);

            double EPS = round(scale/precision, 3);

            double prevx = 0.0, prevy = 0.0;

            double XLeftLimit = round(centerX - scale,3);
            double XRightLimit = round(centerX + scale,3);

            boolean first = false;

           // drawGrid(canvas);

            Log.d(TAG + "!", "" + data.size() + " " + XLeftLimit + " " + XRightLimit + " " + EPS);

            for (double x = XLeftLimit; x <= XRightLimit; x += EPS) {

               // Log.d(TAG, "" + x);
                double xcord, ycord;
                xcord = getXCoords(x);

                if (data.containsKey(x))
                {
                    ycord = getYCoords(data.get(x));
                }
                else
                {
                    double y;
                    try {
                        y = equationEvaluator.eval(x);
                    }
                    catch (InvalidPostfixException e)
                    {
                        return;
                    }

                    ycord = getYCoords(y);
                    data.put(x, y);
                }

                if (first) {
                    canvas.drawLine((float) prevx, (float) prevy, (float) xcord, (float) ycord, paint);
                } else {
                    first = true;
                }
                prevx = xcord;
                prevy = ycord;
            }

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                canvas.restore();
        }

        private void drawGrid(Canvas canvas) {
            Paint paint = new Paint();

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));



        }

        private void drawOrigin(Canvas canvas) {
            Paint paint = new Paint();

            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTextSize(50f);

            canvas.drawText("("+round(centerX,2)+","+round(centerY,2)+")", semiwidth+20.0f, semiheight+20.0f, paint);
        }

        private void drawAxes(Canvas canvas) {
            Paint paint = new Paint();

            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);

            double originX = getXCoords(0);
            double originY = getYCoords(0);

            if (originY >= 0 && originY <= 2*semiheight)
                canvas.drawLine(0.0f, (float)originY, 2*semiwidth, (float)originY, paint);

            if (originX >= 0 && originX <= 2*semiwidth)
                canvas.drawLine((float)originX, 0.0f, (float)originX, 2*semiheight, paint);
        }
    }
}
