package info.guardianproject.fakepanicbutton.triggers;


import android.app.Activity;

public class WifiTrigger extends BaseTrigger {

    public WifiTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //setup geofence

        launchIntent("email@","phone","geoalert","word");
    }
}