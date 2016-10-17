package info.guardianproject.fakepanicbutton.triggers;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;

import org.w3c.dom.Text;

public class WifiTrigger extends BaseTrigger {

    private final static String WIFI_SSID_TRIGGER = "Starbucks";

    public WifiTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //register for network state change events
        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getContext().registerReceiver(new NetworkStatusReceiver(),intentFilter);

    }

    public class NetworkStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo netInfo = intent.getParcelableExtra (WifiManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.TYPE_WIFI == netInfo.getType ()
                    && netInfo.isConnected()) {

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();

                //Check if I am connected to the "trigger" SSID, and if so send an alert!

                if (!TextUtils.isEmpty(ssid)
                    && ssid.equals(WIFI_SSID_TRIGGER))
                {
                    launchPanicIntent(getContext());
                }
            }

        }



    }
}