package upwork.sowl.com.upwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import upwork.sowl.com.upwork.PreferenceManager;
import upwork.sowl.com.upwork.R;
import upwork.sowl.com.upwork.TrackerApplication;
import upwork.sowl.com.upwork.actions.LocationUpdatedEvent;

/**
 * Created by evgenii on 6/24/17.
 */

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TrackerApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TrackerApplication.getInstance().getBus().unregister(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        onDistanceUpdated(produceLocationUpdatedEvent());
    }

    @Subscribe
    public void onDistanceUpdated(LocationUpdatedEvent event) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(event.getLocation()).title(getString(R.string.your_location)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(event.getLocation()));
        }
    }

    private LocationUpdatedEvent produceLocationUpdatedEvent() {
        PreferenceManager preferenceManager = TrackerApplication.getInstance().getPreferenceManager();
        return new LocationUpdatedEvent(preferenceManager.getLocation(), preferenceManager.getDistance());
    }

}
