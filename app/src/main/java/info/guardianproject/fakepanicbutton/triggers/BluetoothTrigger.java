package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;

public class BluetoothTrigger extends BaseTrigger {

    public BluetoothTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //setup bluetooth detect

        launchIntent("email@","phone","geoalert","word");
    }
}
