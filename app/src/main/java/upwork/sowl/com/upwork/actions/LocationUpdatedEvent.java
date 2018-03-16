package upwork.sowl.com.upwork.actions;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by evgenii on 6/24/17.
 */

public class LocationUpdatedEvent {

    private LatLng location;

    private float distance;

    public LocationUpdatedEvent(LatLng location, float distance) {
        this.location = location;
        this.distance = distance;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
