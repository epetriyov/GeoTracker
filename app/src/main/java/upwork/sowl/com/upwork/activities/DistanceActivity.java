package upwork.sowl.com.upwork.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import upwork.sowl.com.upwork.BuildConfig;
import upwork.sowl.com.upwork.PreferenceManager;
import upwork.sowl.com.upwork.R;
import upwork.sowl.com.upwork.TrackerApplication;
import upwork.sowl.com.upwork.actions.LocationUpdatedEvent;

/**
 * Created by evgenii on 6/24/17.
 */

public class DistanceActivity extends AppCompatActivity {

    private static final float KM_KOEF = 1.60934f;
    private TextView distanceText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        distanceText = (TextView) findViewById(R.id.distance);
        TrackerApplication.getInstance().getBus().register(this);
        onDistanceUpdated(produceLocationUpdatedEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TrackerApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void onDistanceUpdated(LocationUpdatedEvent event) {
        float distance = event.getDistance();
        if (BuildConfig.FLAVOR.equals("mi")) {
            distance = distance / KM_KOEF;
        }
        distanceText.setText(String.format(getString(R.string.distance_unit), String.valueOf(distance)));
    }

    private LocationUpdatedEvent produceLocationUpdatedEvent() {
        PreferenceManager preferenceManager = TrackerApplication.getInstance().getPreferenceManager();
        return new LocationUpdatedEvent(preferenceManager.getLocation(), preferenceManager.getDistance());
    }
}
