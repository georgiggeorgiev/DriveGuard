package com.joro.driveguard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class WarningController
{
    private static final String TAG = "WarningController";

    private final Context context;

    private CountDownTimer timer;
    private boolean isTimerRunning;

    private MediaPlayer warningBeep;

    private LocationManager locationManager;
    private final LocationListener locationListener;

    public WarningController(Context context)
    {
        this.context = context;

        isTimerRunning = false;

        timer = new CountDownTimer(3000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                // Do nothing with ticks, warnings are only called on timer finish
            }

            @Override
            public void onFinish()
            {
                onTimerFinish();
            }
        };

        warningBeep = MediaPlayer.create(context, R.raw.warning_beep);
        warningBeep.setLooping(true);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                onLocationUpdate(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        };
    }

    public void update(boolean areBothEyesClosed)
    {
//        Log.d(TAG, String.valueOf(areBothEyesClosed));
        if (areBothEyesClosed)
        {
            if (!isTimerRunning)
            {
                onTimerStart();
            }
        } else
        {
            if (isTimerRunning)
            {
                onTimerCancel();
            }
        }
    }

    private void onTimerFinish()
    {
        isTimerRunning = false;
        if (!warningBeep.isPlaying())
        {
            warningBeep.seekTo(0);
            warningBeep.start();
            requestLocationUpdate();
        }
    }

    private void onTimerCancel()
    {
        timer.cancel();
        isTimerRunning = false;
        if (warningBeep.isPlaying())
        {
            warningBeep.pause();
        }
    }

    private void onTimerStart()
    {
        timer.start();
        isTimerRunning = true;
    }

    private void requestLocationUpdate()
    {
        // TODO Properly request permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "Location permission error");
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    private void onLocationUpdate(Location location)
    {
        updateTracker(location);
    }

    private void updateTracker(Location location)
    {
        // This method will fail silently if an exception is encountered as to not distract the user

        final LocalDateTime localDateTime = LocalDateTime.now();
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        final String userPhoneNumber = Constants.userPhoneNumber;
        if (userPhoneNumber == null || userPhoneNumber.isEmpty())
        {
            return;
        }
        final String userFirstName = Constants.userFirstName;
        if (userFirstName == null || userFirstName.isEmpty())
        {
            return;
        }
        final String APIKey = Constants.APIKey;
        if (APIKey == null || APIKey.isEmpty())
        {
            return;
        }

        final JSONObject jsonObject = new JSONObject();

        try
        {
            jsonObject.put("localDateTime", localDateTime);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("userPhoneNumber", userPhoneNumber);
            jsonObject.put("userFirstName", userFirstName);
            jsonObject.put("APIKey", APIKey);

            final String url = Constants.serverUrl + "tracker";

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response ->
                    {
                        Log.d(TAG, "Response received");
                    },
                    error ->
                    {
                        // TODO Will currently always throw error because response on server side is always empty. Normal behavior is unaffected
                        Log.d(TAG, error.toString());
                    }
            );

            requestQueue.add(jsonObjectRequest);
        }
        catch (JSONException e)
        {
            Log.d(TAG, "JSON Error");
        }
    }
}
