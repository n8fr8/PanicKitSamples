package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;

/**
 * Created by n8fr8 on 10/14/16.
 */
public class GeoFenceTrigger extends BaseTrigger {

    public GeoFenceTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //setup geofence

        launchIntent("email@","phone","geoalert","word");
    }
}
