package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.squareup.seismic.ShakeDetector;

public class SuperShakeTrigger extends BaseTrigger {

    public SuperShakeTrigger(Activity context)
    {
        super (context);
    }

    @Override
    public void activateTrigger() {

        //setup shake detection using ShakeDetector library
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        ShakeDetector sd = new ShakeDetector(new ShakeDetector.Listener()
        {
            public void hearShake() {

                //you shook me!
                launchPanicIntent(getContext());

            }
        });

        sd.start(sensorManager);

    }
}
