package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicTrigger;

/**
 * Created by n8fr8
 */
public abstract class BaseTrigger {

    private Activity mContext;

    public BaseTrigger (Activity context)
    {
        mContext = context;

        activateTrigger();
    }

    public abstract void activateTrigger();

    public static void launchPanicIntent (Context context)
    {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        String email = prefs.getString("email",null);
        String phone = prefs.getString("phone",null);
        String subject = prefs.getString("subject","panic message");
        String message = prefs.getString("message","i triggered a panic!");

        launchIntent(context, email, phone, subject, message);
    }

    public static void launchIntent (Context context, String emailAddress, String phoneNumber, String subject, String message)
    {
        final PackageManager pm = context.getPackageManager();
        final Set<String> receiverPackageNames = PanicTrigger.getResponderActivities(context);

        Intent intent = new Intent(Panic.ACTION_TRIGGER);

        if (!TextUtils.isEmpty(emailAddress))
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                    emailAddress
            });

        if (!TextUtils.isEmpty(phoneNumber))
            intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);

        if (!TextUtils.isEmpty(subject))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (!TextUtils.isEmpty(message))
            intent.putExtra(Intent.EXTRA_TEXT,message);

        // TODO use TrustedIntents here
        List<ResolveInfo> activitiesList = pm.queryIntentActivities(intent, 0);
        Set<String> activities = new HashSet<String>();
        for (ResolveInfo resInfo : activitiesList)
            activities.add(resInfo.activityInfo.packageName);
        List<ResolveInfo> servicesList = pm.queryIntentServices(intent, 0);
        Set<String> services = new HashSet<String>();
        for (ResolveInfo resInfo : servicesList)
            services.add(resInfo.serviceInfo.packageName);
        for (String packageName : receiverPackageNames) {
            intent.setPackage(packageName);
            if (activities.contains(packageName)) {
                context.startActivity(intent);//can't do start activity unless context is an activity
            } else if (services.contains(packageName)) {
                context.startService(intent);
            }
        }
    }


    protected Context getContext ()
    {
        return mContext;
    }


}
