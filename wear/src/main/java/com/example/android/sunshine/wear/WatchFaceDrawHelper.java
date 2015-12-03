package com.example.android.sunshine.wear;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;

public class WatchFaceDrawHelper {

    public static final String TIME_FORMAT = "%d:%02d";
    public static final String TIME_FORMAT_AMBIENT = "%d:%02d:%02d";
    public static final String DATE_FORMAT = "%a, %b %m %Y";
    public static final String TEMP_FORMAT= "%1$s°C";

    private boolean isInAmbientMode = false;
    private boolean isLowBitAmbient = false;

    private Paint mBackgroundPaint;
    private Paint timeTextPaint;
    private Paint mDateTextPaint;
    private Paint mHighestTempPaint;
    private Paint mLowestTempPaint;

    float timeXOffset;
    float timeYOffset;
    float mDateXOffset;
    float mDateYOffset;
    float mWeatherYOffset;
    float mWeatherXOffset;
    float mWeatherXOffset2;

    public WatchFaceDrawHelper(Resources resources, boolean isRound) {
        timeYOffset = resources.getDimension(R.dimen.time_y_offset);
        mDateYOffset = resources.getDimension(R.dimen.date_y_offset);
        mWeatherYOffset = resources.getDimension(R.dimen.weather_y_offset);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(resources.getColor(R.color.background));

        timeTextPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));
        mDateTextPaint = createTextPaint(resources.getColor(R.color.textcolor_secondary));

        mHighestTempPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));
        mHighestTempPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mLowestTempPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));

        timeXOffset = resources.getDimension(isRound
                ? R.dimen.time_x_offset_round : R.dimen.time_x_offset);
        mDateXOffset = resources.getDimension(isRound
                ? R.dimen.date_x_offset_round : R.dimen.date_x_offset);
        mWeatherXOffset = resources.getDimension(isRound
                ? R.dimen.weather_x_offset_round : R.dimen.weather_x_offset);
        mWeatherXOffset2 = resources.getDimension(isRound
                ? R.dimen.weather_x_offset_round_2 : R.dimen.weather_x_offset_2);

        float timeTextSize = resources.getDimension(isRound
                ? R.dimen.time_text_size_round : R.dimen.time_text_size);
        timeTextPaint.setTextSize(timeTextSize);

        float dateTextSize = resources.getDimension(isRound
                ? R.dimen.date_text_size_round : R.dimen.date_text_size);
        mDateTextPaint.setTextSize(dateTextSize);

        float weatherTextSize = resources.getDimension(isRound
                ? R.dimen.weather_text_size_round : R.dimen.weather_text_size);
        mHighestTempPaint.setTextSize(weatherTextSize);

        float weatherTextSize2 = resources.getDimension(isRound
                ? R.dimen.weather_text_size_round_2 : R.dimen.weather_text_size_2);
        mLowestTempPaint.setTextSize(weatherTextSize2);
    }

    private Paint createTextPaint(int textColor) {
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        paint.setAntiAlias(true);
        return paint;
    }

    public void setInAmbientMode(boolean ambientMode) {
        this.isInAmbientMode = ambientMode;
        if(isLowBitAmbient)
            timeTextPaint.setAntiAlias(!isInAmbientMode);
    }

    public void setIsLowBitAmbient(boolean isLowBitAmbient) {
        this.isLowBitAmbient = isLowBitAmbient;
    }

    public void draw(Canvas canvas, Rect bounds, Time time, int highestTemp, int lowestTemp) {
        // Draw the background.
        if (isInAmbientMode) {
            canvas.drawColor(Color.BLACK);
        } else {
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
        }

        time.setToNow();
        // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
        String timeText = isInAmbientMode
                ? String.format(TIME_FORMAT, time.hour, time.minute)
                : String.format(TIME_FORMAT_AMBIENT, time.hour, time.minute, time.second);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timeTextPaint);

        // Draw current date
        String dateText = time.format(DATE_FORMAT);
        canvas.drawText(dateText, mDateXOffset, mDateYOffset, mDateTextPaint);

        // Draw weather
        String currentHigh = String.format(TEMP_FORMAT, highestTemp);
        canvas.drawText(currentHigh, mWeatherXOffset, mWeatherYOffset, mHighestTempPaint);
        String currentLow = String.format(TEMP_FORMAT, lowestTemp);
        canvas.drawText(currentLow, mWeatherXOffset + mWeatherXOffset2, mWeatherYOffset, mLowestTempPaint);

    }
}
