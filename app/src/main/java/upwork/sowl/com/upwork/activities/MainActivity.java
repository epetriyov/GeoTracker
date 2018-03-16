package upwork.sowl.com.upwork.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ResolvableApiException;
import com.squareup.otto.Subscribe;

import upwork.sowl.com.upwork.BuildConfig;
import upwork.sowl.com.upwork.R;
import upwork.sowl.com.upwork.TrackerApplication;
import upwork.sowl.com.upwork.TrackingService;
import upwork.sowl.com.upwork.actions.OpenSettigsAction;
import upwork.sowl.com.upwork.actions.TrackErrorEvent;
import upwork.sowl.com.upwork.actions.TrackingStartedEvent;

public class MainActivity extends AppCompatActivity implements ConfirmTrackingDialog.ConfirmListener {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CONFIRM_TAG = "confirm_tracking";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String EXTRA_REQUEST_UPDATE = "extra_request_updates";

    private boolean mRequestingLocationUpdates = false;
    private Button btnTracking;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(EXTRA_REQUEST_UPDATE, mRequestingLocationUpdates);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTracking = (Button) findViewById(R.id.btn_tracking);
        btnTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRequestingLocationUpdates) {
                    getSupportFragmentManager().beginTransaction()
                            .add(new ConfirmTrackingDialog(), CONFIRM_TAG)
                            .commit();
                } else {
                    stopTracking();
                    updateButtonText();
                }
            }
        });
        findViewById(R.id.btn_distance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DistanceActivity.class));
            }
        });
        findViewById(R.id.btn_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
            }
        });
        if (savedInstanceState != null) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(EXTRA_REQUEST_UPDATE, false);
        }
        updateButtonText();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startTracking();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startTracking();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        TrackerApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackerApplication.getInstance().getBus().unregister(this);
    }

    @Subscribe
    public void openResolutionsScreen(OpenSettigsAction event) {
        try {
            ResolvableApiException rae = (ResolvableApiException) event.getException();
            rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sie) {
            Log.i(TAG, "PendingIntent unable to execute request.");
        }
    }

    @Subscribe
    public void onLocationRequestStarted(TrackingStartedEvent event) {
        mRequestingLocationUpdates = true;
        updateButtonText();
    }

    @Subscribe
    public void onRequestLocationError(TrackErrorEvent event) {
        Snackbar.make(
                findViewById(android.R.id.content),
                R.string.location_request_error,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private void updateButtonText() {
        if (mRequestingLocationUpdates) {
            btnTracking.setText(R.string.stop_tracking);
        } else {
            btnTracking.setText(R.string.start_tracking);
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onConfirmed() {
        if (checkPermissions()) {
            startTracking();
        } else {
            requestPermissions();
        }
    }

    private void startTracking() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.setAction(TrackingService.START);
        startService(serviceIntent);
    }

    private void stopTracking() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.setAction(TrackingService.STOP);
        startService(serviceIntent);
        mRequestingLocationUpdates = false;
    }
}
