package info.guardianproject.fakepanicbutton.triggers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

    private String mEmailAddress;
    private String mPhoneNumber;
    private String mSubject;
    private String mPanicMessage;

    public BaseTrigger (Activity context)
    {
        mContext = context;

        activateTrigger();
    }

    public abstract void activateTrigger();

    public void launchPanicIntent ()
    {
        launchIntent(getEmailAddress(),getPhoneNumber(),getSubject(),getPanicMessage());
    }

    public void launchIntent (String emailAddress, String phoneNumber, String subject, String message)
    {
        final PackageManager pm = mContext.getPackageManager();
        final Set<String> receiverPackageNames = PanicTrigger.getResponderActivities(mContext);

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
                mContext.startActivityForResult(intent, 0);
            } else if (services.contains(packageName)) {
                mContext.startService(intent);
            }
        }
    }


    protected Context getContext ()
    {
        return mContext;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String mEmailAddress) {
        this.mEmailAddress = mEmailAddress;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public String getPanicMessage() {
        return mPanicMessage;
    }

    public void setPanicMessage(String mPanicMessage) {
        this.mPanicMessage = mPanicMessage;
    }
}
