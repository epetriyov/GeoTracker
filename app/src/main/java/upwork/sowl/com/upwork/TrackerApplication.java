package upwork.sowl.com.upwork;

import android.app.Application;

import com.squareup.otto.Bus;

/**
 * Created by evgenii on 6/24/17.
 */

public class TrackerApplication extends Application {

    private static TrackerApplication instance;

    private Bus bus;

    private PreferenceManager preferenceManager;

    public static TrackerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new Bus();
        preferenceManager = new PreferenceManager(this);
    }

    public Bus getBus() {
        return bus;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
