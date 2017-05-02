package com.shauryachats.grapher;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by shauryachats on 17/4/17.
 */

public class GraphicActivity extends AppCompatActivity{

    double precision;
    private ScaleGestureDetector scaleGestureDetector;

    ArrayList<EquationEvaluator> equationEvaluators = new ArrayList<EquationEvaluator>();
    public final String TAG = "GraphicActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        ArrayList<String> expressions = bundle.getStringArrayList("postfix");

        for (String expression : expressions)
        {
            equationEvaluators.add(new EquationEvaluator(expression));
        }

        precision = bundle.getDouble("precision");

        setContentView(new CanvasView(this));

    }

    public class CanvasView extends View {
        private static final long ALLOWED_TIME = 100;

        int semiheight, semiwidth;
        double scale = 1.0f;
        double centerX = 0.0f, centerY = 0.0f;

        boolean multiTouch = false;
        long timeFingersSwitched;

        float prevX, prevY;

        private static final int MIN_TEXT_GAP_FROM_GRID = 5;
        private static final int MAX_X_TEXT_GAP_FROM_GRID = 35;
        private static final int MAX_Y_TEXT_GAP_FROM_GRID = 70;

        private static final double PRECISION_WHEN_MOVING = 30;
        private static final double PRECISION_WHEN_STATIC = 200;

        double previousScaleFactor;
        double currentScaleFactor;

        public CanvasView(Context context) {
            super(context);

            /**
             * scaleGestureDetector is responsible for the two fingered zooming.
             */
            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    Log.d(TAG, "Zoom ongoing, scale" + detector.getScaleFactor());
                    currentScaleFactor = detector.getScaleFactor();

                    scale = previousScaleFactor / currentScaleFactor;

                    precision = PRECISION_WHEN_MOVING;
                    invalidate();

                    return false;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    previousScaleFactor = scale;
                    return true;
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {

                    scale = previousScaleFactor / currentScaleFactor;
                    precision = PRECISION_WHEN_STATIC;
                    invalidate();
                }
            });
        }

        /**
         *
         *  These functions convert the x, y coordinates of the graph to the X, Y coordinates of the screen.
         *
         */
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

            scaleGestureDetector.onTouchEvent(event);

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
                    precision = PRECISION_WHEN_MOVING;
                    Log.d(TAG + "***", "" + event.getX() + " " + event.getY());
                    centerX -= round((eventX - prevX)/500f * scale,3);
                    centerY += round((eventY - prevY)/500f * scale,3);
                    prevX = eventX;
                    prevY = eventY;
                    invalidate();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "Pointer action up");
                    break;
                case MotionEvent.ACTION_UP:
                    precision = PRECISION_WHEN_STATIC;
                    Log.d(TAG + "!", "Pointer up");
                    invalidate();
                    break;
                default:
                    return false;
            }

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

            drawGrid(canvas);
            drawAxes(canvas);
            //drawOrigin(canvas);

            int[] colors = {Color.RED, Color.BLUE, Color.YELLOW};
            int i = 0;

            for (EquationEvaluator e : equationEvaluators)
            {
                drawGraph(canvas, e, colors[i]);
                ++i;
            }

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                canvas.restore();
        }

        private void drawGraph(Canvas canvas, EquationEvaluator equationEvaluator, int color) {

            Paint paint = new Paint();

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(3f);

            double EPS = scale/precision;

            double prevx = 0.0, prevy = 0.0;

            double XLeftLimit = centerX - scale;
            double XRightLimit = centerX + scale;

            boolean first = false;

            for (double x = XLeftLimit; x <= XRightLimit; x += EPS) {

                double xcord, ycord, y;

                xcord = getXCoords(x);
                try
                {
                    y = equationEvaluator.eval(x);
                }
                catch (InvalidPostfixException e)
                {
                    return;
                }
                ycord = getYCoords(y);
                if (first) {
                    canvas.drawLine((float) prevx, (float) prevy, (float) xcord, (float) ycord, paint);
                } else {
                    first = true;
                }
                prevx = xcord;
                prevy = ycord;
            }
        }

        /**
         * Draws the measuring grid in the graph.
         */
        private void drawGrid(Canvas canvas) {

            Paint paint = new Paint();

            paint.setColor(Color.rgb(100, 100, 100));
            paint.setStyle(Paint.Style.STROKE);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setTextSize(30f);

            double mnx = Math.ceil(Math.log10(scale));
            double tens = Math.pow(10, mnx);
            float gap = 0;

            float absval = (float) (scale/tens);
            if (absval <= 0.3) {
                gap = (float) (tens/20);
            } else if (absval <= 0.7) {
                gap = (float) (tens/10);
            } else {
                gap = (float) (tens/5);
            }

            Log.d("Bull", "gap " + gap);
            //gap = (float) round(gap, rounder);

            Log.d("Bull", "" + mnx + " " + scale + " " + gap);

            int leftX = (int) Math.ceil((centerX - scale)/gap);
            int rightX = (int) Math.floor((centerX + scale)/gap);

            Log.d("Bull", "leftx " + leftX + " rightx " + rightX);

            //Drawing grids parallel to the Y axis.
            for (int i = leftX; i <= rightX; ++i)
            {
                float x = (float) getXCoords(i * gap);
                canvas.drawLine(x, 0.0f, x, 2*semiheight, paint);

                float y = (float) getYCoords(0);
                if (y < MAX_X_TEXT_GAP_FROM_GRID) y = MAX_X_TEXT_GAP_FROM_GRID;
                else if (y > 2*semiheight - MIN_TEXT_GAP_FROM_GRID) y = 2*semiheight - MIN_TEXT_GAP_FROM_GRID;

                if (gap >= 1)
                    canvas.drawText("" + (int)(i*gap), x + MIN_TEXT_GAP_FROM_GRID, y - MIN_TEXT_GAP_FROM_GRID, textPaint);
                else
                    canvas.drawText("" + i*gap, x + MIN_TEXT_GAP_FROM_GRID, y - MIN_TEXT_GAP_FROM_GRID, textPaint);

            }

            int upY = (int) Math.ceil((centerY - scale)/gap);
            int downY = (int) Math.floor((centerY + scale)/gap);

            Log.d("Bull", "upY " + upY + " downY " + downY);

            for (int i = upY; i <= downY; ++i)
            {
                float y = (float) getYCoords(i * gap);
                canvas.drawLine(0.0f, y, 2*semiwidth, y, paint);

                float x = (float) getXCoords(0);
                if (x < 0) x = MIN_TEXT_GAP_FROM_GRID;
                else if (x > 2*semiwidth - MAX_Y_TEXT_GAP_FROM_GRID) x = 2*semiwidth - MAX_Y_TEXT_GAP_FROM_GRID;

                if (gap >= 1)
                    canvas.drawText("" + (int)(i*gap), x + MIN_TEXT_GAP_FROM_GRID, y - MIN_TEXT_GAP_FROM_GRID, textPaint);
                else
                    canvas.drawText("" + i*gap, x + MIN_TEXT_GAP_FROM_GRID, y - MIN_TEXT_GAP_FROM_GRID, textPaint);
            }
        }

        /**
         * Draws the X and Y axis.
         * @param canvas
         */
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