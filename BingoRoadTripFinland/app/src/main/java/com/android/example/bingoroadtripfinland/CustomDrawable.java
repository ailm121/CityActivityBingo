package com.android.example.bingoroadtripfinland;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomDrawable extends Drawable {
    private Paint mPaint;
    private final ArrayList<String> colors;

    /**
     * Class for creating custom drawables to be used as bingo task button backgrounds
     * @param colors ArrayList List of colors that need to be included in drawable
     */
    public CustomDrawable(ArrayList<String> colors) {
        mPaint = new Paint();
        this.colors = colors;
    }

    /**
     * Creates background
     * @param canvas Canvas for drawable to be created for
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        if (colors.size() == 1) {
            setOneColor(canvas);
        } else if (colors.size() == 2) {
            setTwoColors(canvas);
        } else if (colors.size() == 3) {
            setThreeColors(canvas);
        } else {
            setFourColors(canvas);
        }
    }

    /**
     * Only one color required, identifies it and fills whole drawable with it
     * @param canvas Canvas
     */
    private void setOneColor(Canvas canvas) {

        Rect b = getBounds();
        float x = b.width();
        float y = b.height();

        if (colors.contains("RED")) {
            mPaint.setColor(Color.RED);
            canvas.drawRect(0, 0, x, y, mPaint);

        } else if (colors.contains("BLUE")) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(0, 0, x, y, mPaint);

        } else if (colors.contains("GREEN")) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(0, 0, x, y, mPaint);

        } else if (colors.contains("MAGENTA")) {
            mPaint.setColor(Color.MAGENTA);
            canvas.drawRect(0, 0, x, y, mPaint);
        }
    }

    /**
     * Two colors required, identifies them and colors drawable half and half with each
     * @param canvas Canvas
     */
    private void setTwoColors(Canvas canvas) {
        Rect b = getBounds();
        float x = b.width() / 2;
        float y = b.height();
        String firstColor = "";

        if (colors.contains("RED")) {
            mPaint.setColor(Color.RED);
            canvas.drawRect(0, 0, x, y, mPaint);
            firstColor = "RED";

        } else if (colors.contains("BLUE")) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(0, 0, x, y, mPaint);
            firstColor = "BLUE";

        } else if (colors.contains("GREEN")) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(0, 0, x, y, mPaint);
            firstColor = "GREEN";
        }

        if (colors.contains("BLUE") && !firstColor.equals("BLUE")) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(x, 0, b.width(), y, mPaint);

        } else if (colors.contains("GREEN") && !firstColor.equals("GREEN")) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(x, 0, b.width(), y, mPaint);

        } else if (colors.contains("MAGENTA")) {
            mPaint.setColor(Color.MAGENTA);
            canvas.drawRect(x, 0, b.width(), y, mPaint);
        }
    }

    /**
     * Three colors required, identifies them and fills thirds with each color
     * @param canvas Canvas
     */
    private void setThreeColors(Canvas canvas) {
        Rect b = getBounds();
        float x = b.width() / 3;
        float y = b.height();
        String firstColor = "";
        String secondColor = "";

        if (colors.contains("RED")) {
            mPaint.setColor(Color.RED);
            canvas.drawRect(0, 0, x, y, mPaint);
            firstColor = "RED";

        } else if (colors.contains("BLUE")) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(0, 0, x, y, mPaint);
            firstColor = "BLUE";
        }

        if (colors.contains("BLUE") && !firstColor.equals("BLUE")) {
            mPaint.setColor(Color.BLUE);
            canvas.drawRect(x, 0, x * 2, y, mPaint);
            secondColor = "BLUE";

        } else if (colors.contains("GREEN")) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(x, 0, x * 2, y, mPaint);
            secondColor = "GREEN";
        }

        if (colors.contains("GREEN") && !secondColor.equals("GREEN")) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(x * 2, 0, b.width(), y, mPaint);

        } else if (colors.contains("MAGENTA")) {
            mPaint.setColor(Color.MAGENTA);
            canvas.drawRect(x * 2, 0, b.width(), y, mPaint);
        }
    }

    /**
     * All four colors required, fills quarters of drawable with each
     * @param canvas Canvas
     */
    private void setFourColors(Canvas canvas) {

        Rect b = getBounds();
        float x = b.width() / 4;
        float y = b.height();

        mPaint.setColor(Color.RED);
        canvas.drawRect(0, 0, x, y, mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(x, 0, x * 2, y, mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(x * 2, 0, x * 3, y, mPaint);
        mPaint.setColor(Color.MAGENTA);
        canvas.drawRect(x * 3, 0, b.width(), y, mPaint);

    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
