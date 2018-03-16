package upwork.sowl.com.upwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by evgenii on 6/24/17.
 */

public class PreferenceManager {

    private static final String PREFERENCE_FILE = "upwork.sowl.com.upwork.PREFERENCE_FILE_KEY";
    private static final String DISTANCE = "distance";
    private static final String LAT = "lat";
    private static final String LONG = "long";

    private final SharedPreferences mSharedPref;

    PreferenceManager(final Context context) {
        mSharedPref = context.getSharedPreferences(
                PREFERENCE_FILE, Context.MODE_PRIVATE);
    }

    public float getDistance() {
        return mSharedPref.getFloat(DISTANCE, 0);
    }

    public void updateLocation(double latitude, double longitude) {
        float newDistance = 0;
        LatLng oldLocation = getLocation();
        if (oldLocation != null) {
            newDistance = getDistance() + getDistancBetweenTwoPoints(oldLocation.latitude,
                    oldLocation.longitude, latitude, longitude);
        }
        mSharedPref.edit()
                .putString(LAT, String.valueOf(latitude))
                .putString(LONG, String.valueOf(longitude))
                .putFloat(DISTANCE, newDistance)
                .apply();
    }

    private float getDistancBetweenTwoPoints(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1,
                lat2, lon2, distance);
        return distance[0] * 0.001f;
    }

    public LatLng getLocation() {
        String lat = mSharedPref.getString(LAT, null);
        String lng = mSharedPref.getString(LONG, null);
        if (lat != null && lng != null) {
            return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        }
        return null;
    }
}

