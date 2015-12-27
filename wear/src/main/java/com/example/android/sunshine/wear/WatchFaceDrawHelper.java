package com.example.android.sunshine.wear;

import android.content.res.Resources;
import android.graphics.Bitmap;
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
    private Paint mIconPaint;

    float mTimeXOffset;
    float mTimeYOffset;
    float mDateXOffset;
    float mDateYOffset;
    float mTempYOffset;
    float mWeatherIconXOffset;
    float mWeatherIconYOffset;
    float mTempHighXOffset;
    float mTempLowXOffset;

    public WatchFaceDrawHelper(Resources resources, boolean isRound) {
        mTimeYOffset = resources.getDimension(R.dimen.time_y_offset);
        mDateYOffset = resources.getDimension(R.dimen.date_y_offset);
        mTempYOffset = resources.getDimension(R.dimen.temp_y_offset);
        mWeatherIconYOffset = resources.getDimension(R.dimen.weather_icon_y_offset);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(resources.getColor(R.color.background));

        mIconPaint = new Paint();
//        mIconPaint.setColor(resources.getColor(android.R.color.transparent));

        timeTextPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));
        mDateTextPaint = createTextPaint(resources.getColor(R.color.textcolor_secondary));

        mHighestTempPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));
        mHighestTempPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mLowestTempPaint = createTextPaint(resources.getColor(R.color.textcolor_primary));

        mTimeXOffset = resources.getDimension(isRound
                ? R.dimen.time_x_offset_round : R.dimen.time_x_offset);
        mDateXOffset = resources.getDimension(isRound
                ? R.dimen.date_x_offset_round : R.dimen.date_x_offset);
        mWeatherIconXOffset = resources.getDimension(isRound
                ? R.dimen.weather_icon_x_offset_round : R.dimen.weather_icon_x_offset);
        mTempHighXOffset = resources.getDimension(isRound
                ? R.dimen.temp_high_x_offset_round : R.dimen.temp_high_x_offset);
        mTempLowXOffset = resources.getDimension(isRound
                ? R.dimen.temp_low_x_offset_round : R.dimen.temp_low_x_offset);

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

    public void draw(Canvas canvas, Rect bounds, Time time, int highestTemp, int lowestTemp, Bitmap weatherIcon) {
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
        canvas.drawText(timeText, mTimeXOffset, mTimeYOffset, timeTextPaint);

        // Draw current date
        String dateText = time.format(DATE_FORMAT);
        canvas.drawText(dateText, mDateXOffset, mDateYOffset, mDateTextPaint);

        // Draw weather
        if(!(highestTemp == 0 && lowestTemp == 0)) {
            String currentHigh = String.format(TEMP_FORMAT, highestTemp);
            canvas.drawText(currentHigh, mTempHighXOffset, mTempYOffset, mHighestTempPaint);
            String currentLow = String.format(TEMP_FORMAT, lowestTemp);
            canvas.drawText(currentLow, mTempLowXOffset, mTempYOffset, mLowestTempPaint);
        }

        if(weatherIcon != null)
            canvas.drawBitmap(weatherIcon, mWeatherIconXOffset, mWeatherIconYOffset, mIconPaint);
    }
}
