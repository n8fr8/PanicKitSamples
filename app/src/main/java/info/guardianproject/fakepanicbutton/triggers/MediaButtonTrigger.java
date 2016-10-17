package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.Date;

public class MediaButtonTrigger extends BaseTrigger {

    private static int mTriggerCount = 0;
    private final static int TRIGGER_THRESHOLD = 3;

    private static long mLastTriggerTime = -1;

    public MediaButtonTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //if a headset button or a bluetooth "call" button is pressed, trigger this

        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        MediaButtonIntentReceiver r = new MediaButtonIntentReceiver();
        getContext().registerReceiver(r, filter);


    }

    public class MediaButtonIntentReceiver extends BroadcastReceiver {

        public MediaButtonIntentReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }

            int action = event.getAction();
            if (action == KeyEvent.ACTION_DOWN) {

                //check for 3 rapidly pressed key events

                long triggerTime = new Date().getTime();

                //if the trigger is the first one, or happened with a second of the last one, then count it
                if (mLastTriggerTime == -1 || ((triggerTime - mLastTriggerTime)<1000))
                    mTriggerCount++;

                mLastTriggerTime = triggerTime;

                if (mTriggerCount > TRIGGER_THRESHOLD) {
                    launchPanicIntent(context);
                    mTriggerCount = 0;
                }


            }
            abortBroadcast();
        }
    }
}
