package com.cs528.parkingassist.Util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

public class Utils {
    public static Location getBestLastKnownLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;

        for (String provider : providers) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (bestLocation == null || location != null
                        && location.getElapsedRealtimeNanos() > bestLocation.getElapsedRealtimeNanos()
                        && location.getAccuracy() > bestLocation.getAccuracy())
                    bestLocation = location;
            } catch (SecurityException ignored) {
            }
        }

        return bestLocation;
    }
}
