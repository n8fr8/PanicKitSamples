package info.guardianproject.fakepanicbutton.triggers;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class PhoneNumberTrigger extends BaseTrigger {

    public final static String PHONE_NUMBER_TRIGGER = "1234";

    public PhoneNumberTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        OutgoingCallReceiver ocr = new OutgoingCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        getContext().registerReceiver(ocr,intentFilter);

    }


}
