package com.artem.uofmcampusmap.buildings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Artem on 2017-06-26.
 */

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

        if(linePos >= 0 && linePos < pathsToDraw.size())
        {
            for (int i = linePos; i < pathsToDraw.size(); i++) {
                Line line = pathsToDraw.get(i);
                canvas.drawLine(line.getSourceX(), line.getSourceY(), line.getDestX(), line.getDestY(), paint);
            }
        }
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
