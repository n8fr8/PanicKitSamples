package info.guardianproject.fakepanicbutton.triggers;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class PhoneNumberTrigger extends BaseTrigger {

    private final static String PHONE_NUMBER_TRIGGER = "5551212";

    public PhoneNumberTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        getContext().registerReceiver(new OutgoingCallReceiver(),intentFilter);
    }

    public class OutgoingCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if (phoneNumber.equals(PHONE_NUMBER_TRIGGER))
                launchPanicIntent();

        }
    }
}
