package com.artem.uofmcampusmap.buildings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

public class DrawingPathView extends View {

    private ArrayList<Line> pathsToDraw;
    private int linePos;
    private int posWithinRoute;
    private Paint paint;

    public DrawingPathView(Context context)
    {
        super(context);
        initialize();
    }

    public DrawingPathView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public DrawingPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize()
    {
        linePos = 0;
        pathsToDraw = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int sourceX;
        int sourceY;
        int destX;
        int destY;

        if(linePos >= 0 && linePos < pathsToDraw.size())
        {
            for (int i = linePos; i < pathsToDraw.size(); i++) {
                Line line = pathsToDraw.get(i);

                DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
                float dpi = metrics.densityDpi / 160f;
                sourceX = (int) (line.getSourceX() * dpi);
                sourceY = (int) (line.getSourceY() * dpi);
                destX = (int) (line.getDestX() * dpi);
                destY = (int) (line.getDestY() * dpi);

                canvas.drawLine(sourceX, sourceY, destX, destY, paint);

                if(i == linePos)
                {
                    drawCurrLocation(sourceX, sourceY, canvas);
                }
            }
        }
        else
        {
            //Used for when there are no routes to display
            //Just prevents any potential bugs with lines remaining
            Paint clear = new Paint();
            clear.setColor(Color.TRANSPARENT);
            canvas.drawLine(0, 0, 0, 0, clear);
        }
    }

    //Draws an X where the user currently is, with an offset of 3 up, down, left, and right
    private void drawCurrLocation(int sourceX, int sourceY, Canvas canvas)
    {
        Paint locatPaint = new Paint();
        locatPaint.setColor(Color.GREEN);
        locatPaint.setStyle(Paint.Style.STROKE);
        locatPaint.setStrokeWidth(6);

        final int LINE_OFFSET = 9;

        int topLeftX = sourceX - LINE_OFFSET;
        int bottomRightX = sourceX + LINE_OFFSET;
        int topLeftY = sourceY + LINE_OFFSET;
        int bottomRightY = sourceY - LINE_OFFSET;

        canvas.drawLine(topLeftX, topLeftY, bottomRightX, bottomRightY, locatPaint);

        int topRightX = sourceX + LINE_OFFSET;
        int bottomLeftX = sourceX - LINE_OFFSET;
        int topRightY = sourceY + LINE_OFFSET;
        int bottomLeftY = sourceY - LINE_OFFSET;

        canvas.drawLine(topRightX, topRightY, bottomLeftX, bottomLeftY, locatPaint);
    }

    public void addPathToEnd(Line pathToAdd)
    {
        pathsToDraw.add(pathToAdd);
    }

    public void addPathToStart(Line pathToAdd)
    {
        pathsToDraw.add(0, pathToAdd);
    }

    public void updatePathPos(int pathPos)
    {
        this.linePos = pathPos - posWithinRoute;
    }

    public void setPosWithinRoute(int posWithinRoute)
    {
        this.posWithinRoute = posWithinRoute;
    }
}
