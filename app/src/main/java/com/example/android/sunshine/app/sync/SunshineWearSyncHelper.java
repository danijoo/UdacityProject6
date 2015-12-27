package com.example.android.sunshine.app.sync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SunshineWearSyncHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String KEY_HIGHEST_TEMP = "highestTemp";
    public static final String KEY_LOWEST_TEMP = "lowestTemp";
    public static final String KEY_WEATHER_ICON = "weatherIcon";

    private GoogleApiClient mApiClient;
    private PutDataRequest mDataRequest;

    public void updateWear(Context context, int highestTemp, int lowestTemp, int weatherIconResId) {
        mDataRequest = buildRequest(context, highestTemp, lowestTemp, weatherIconResId);
        mApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mApiClient.connect();
    }

    /**
     * Build the DataRequest
     * @param highestTemp daily highest temperature
     * @param lowestTemp daily lowest Temperature
     * @return PutDataRequest with the given values in it
     */
    private PutDataRequest buildRequest(Context context, int highestTemp, int lowestTemp, int weatherIconResId) {
        PutDataMapRequest mapRequest = PutDataMapRequest.create("/dailyTemp");
        DataMap dMap = mapRequest.getDataMap();
        dMap.putInt(KEY_HIGHEST_TEMP, highestTemp);
        dMap.putInt(KEY_LOWEST_TEMP, lowestTemp);

        try {
            Bitmap weatherIcon = BitmapFactory.decodeResource(context.getResources(), weatherIconResId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            weatherIcon.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            byte[] byteArray = stream.toByteArray();
            Asset asset = Asset.createFromBytes(byteArray);
            dMap.putAsset(KEY_WEATHER_ICON, asset);
        } catch (IOException e) {
            // should now happen
        }

        return mapRequest.asPutDataRequest();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("SunshineWearSyncHelper", "Api connected.");
        Wearable.DataApi.putDataItem(mApiClient, mDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d("SunshineWearSyncHelper", "Data item synced: " + dataItemResult.getDataItem().getUri());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("SunshineWearSyncHelper", "Api connection suspended. " + i );
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("SunshineWearSyncHelper", "Api connection failed. " + connectionResult);
    }
}
