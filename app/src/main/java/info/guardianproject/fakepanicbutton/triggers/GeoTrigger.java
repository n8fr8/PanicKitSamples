package info.guardianproject.fakepanicbutton.triggers;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.mapzen.android.lost.api.Geofence;
import com.mapzen.android.lost.api.GeofencingRequest;
import com.mapzen.android.lost.api.LocationServices;
import com.mapzen.android.lost.api.LostApiClient;

/**
 * Created by n8fr8
 * GeoTrigger using the MapZen LOST library: https://github.com/mapzen/lost/blob/master/docs/geofences.md
 */
public class GeoTrigger extends BaseTrigger {

    private LostApiClient lostApiClient;
    public final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9999;

    public GeoTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {



        lostApiClient = new LostApiClient.Builder(getContext()).addConnectionCallbacks(new LostApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected() {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions((Activity)getContext(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                        // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                else
                {

                    setupGeoFence();
                }

            }

            @Override
            public void onConnectionSuspended() {

            }
        }).build();
        lostApiClient.connect();



    }

    private void setupGeoFence ()
    {

        //setup geofence for Times Square area
        String requestId = "geof1-timesSquare";
        double latitude = 40.758896;
        double longitude = -73.985130;
        float radius = 0.0001f;

        Geofence geofence = new Geofence.Builder()
                .setRequestId(requestId)
                .setCircularRegion(latitude, longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();

        Intent serviceIntent = new Intent(getContext().getApplicationContext(), GeofenceIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, serviceIntent, 0);

        try {
            LocationServices.GeofencingApi.addGeofences(lostApiClient, request, pendingIntent);
        }
        catch (SecurityException se)
        {
            Log.e("GeoTrigger","Permission not granted",se);
        }
    }

    public class GeofenceIntentService extends Service
    {
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            //geofence triggered
            launchPanicIntent(getContext());

            return super.onStartCommand(intent, flags, startId);
        }
    }
}
