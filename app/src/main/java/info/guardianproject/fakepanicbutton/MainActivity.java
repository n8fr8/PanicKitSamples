
package info.guardianproject.fakepanicbutton;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import info.guardianproject.fakepanicbutton.triggers.BaseTrigger;
import info.guardianproject.fakepanicbutton.triggers.GeoTrigger;
import info.guardianproject.fakepanicbutton.triggers.MediaButtonTrigger;
import info.guardianproject.fakepanicbutton.triggers.PhoneNumberTrigger;
import info.guardianproject.fakepanicbutton.triggers.SuperShakeTrigger;
import info.guardianproject.fakepanicbutton.triggers.WifiTrigger;
import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicTrigger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends ListActivity {
    public static final String TAG = "FakePanicButton";

    private static final int CONTACT_PICKER_RESULT = 0x00;
    private static final int CONNECT_RESULT = 0x01;

    private EditText panicMessageEditText;
    private TextView contactTextView;

    private String requestPackageName;
    private String requestAction;

    private String displayName;
    private String phoneNumber;
    private String emailAddress = "test@test.foo";

    private BaseTrigger[] triggers;

    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PanicTrigger.checkForDisconnectIntent(this)) {
            finish();
            return;
        }

        if (PanicTrigger.checkForConnectIntent(this)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        panicMessageEditText = (EditText) findViewById(R.id.panicMessageEditText);
        contactTextView = (TextView) findViewById(R.id.contactTextView);

        Button chooseContactButton = (Button) findViewById(R.id.chooseContactButton);
        chooseContactButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pickContacts ();

            }
        });

        triggers = new BaseTrigger[5];
        triggers[0] = new SuperShakeTrigger(this);
        triggers[1] = new MediaButtonTrigger(this);
        triggers[2] = new PhoneNumberTrigger(this);
        triggers[3] = new WifiTrigger(this);
        triggers[4] = new GeoTrigger(this);


    }

    private void pickContacts ()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else
        {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(Panic.ACTION_TRIGGER), 0);
        List<ResolveInfo> services = pm.queryIntentServices(
                new Intent(Panic.ACTION_TRIGGER), 0);
        if (activities.isEmpty() && services.isEmpty())
            return;
        int size = activities.size() + services.size();
        final ArrayList<String> appLabelList = new ArrayList<String>(size);
        final ArrayList<String> packageNameList = new ArrayList<String>(size);
        final ArrayList<Drawable> iconList = new ArrayList<Drawable>(size);
        for (ResolveInfo resolveInfo : activities) {
            if (resolveInfo.activityInfo == null)
                continue;
            appLabelList.add(resolveInfo.activityInfo.loadLabel(pm).toString());
            packageNameList.add(resolveInfo.activityInfo.packageName);
            iconList.add(resolveInfo.activityInfo.loadIcon(pm));
        }
        for (ResolveInfo resolveInfo : services) {
            if (resolveInfo.serviceInfo == null)
                continue;
            appLabelList.add(resolveInfo.serviceInfo.loadLabel(pm).toString());
            packageNameList.add(resolveInfo.serviceInfo.packageName);
            iconList.add(resolveInfo.serviceInfo.loadIcon(pm));
        }

        List<ResolveInfo> connectInfos = pm.queryIntentActivities(
                new Intent(Panic.ACTION_CONNECT), 0);
        final List<String> connectPackageNameList = new ArrayList<String>(connectInfos.size());
        for (ResolveInfo resolveInfo : connectInfos) {
            if (resolveInfo.activityInfo == null)
                continue;
            connectPackageNameList.add(resolveInfo.activityInfo.packageName);
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                appLabelList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CheckedTextView checkedTextView = (CheckedTextView) super.getView(position,
                        convertView, parent);
                checkedTextView.setCompoundDrawablesWithIntrinsicBounds(iconList.get(position),
                        null, null, null);
                checkedTextView.setCompoundDrawablePadding(10);
                return checkedTextView;
            }
        });

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final Set<String> receiverPackageNames = PanicTrigger.getResponderActivities(this);

        for (int i = 0; i < packageNameList.size(); i++)
            if (receiverPackageNames.contains(packageNameList.get(i)))
                listView.setItemChecked(i, true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestPackageName = packageNameList.get(position);
                boolean checked = ((CheckedTextView) view).isChecked();
                if (connectPackageNameList.contains(requestPackageName)) {
                    if (checked) {
                        requestAction = Panic.ACTION_CONNECT;
                        // addReceiver() happens in onActivityResult()
                    } else {
                        requestAction = Panic.ACTION_DISCONNECT;
                        PanicTrigger.removeConnectedResponder(getApplicationContext(), requestPackageName);
                    }
                    Intent intent = new Intent(requestAction);
                    intent.setPackage(requestPackageName);
                    // TODO add TrustedIntents here
                    startActivityForResult(intent, CONNECT_RESULT);
                } else {
                    // no config is possible with this packageName
                    if (checked)
                        PanicTrigger.addConnectedResponder(getApplicationContext(), requestPackageName);
                    else
                        PanicTrigger.removeConnectedResponder(getApplicationContext(), requestPackageName);
                }
            }
        });

        Button panicButton = (Button) findViewById(R.id.panicButton);
        panicButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Panic.ACTION_TRIGGER);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                        emailAddress
                });
                intent.putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber);
                intent.putExtra(Intent.EXTRA_SUBJECT, "panic message");
                intent.putExtra(Intent.EXTRA_TEXT,
                        panicMessageEditText.getText().toString());
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
                        startActivityForResult(intent, 0);
                    } else if (services.contains(packageName)) {
                        startService(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case CONTACT_PICKER_RESULT:
                if (data == null)
                    return;
                Uri uri = data.getData();
                String id = uri.getLastPathSegment();
                Log.i(TAG, uri + "");

                String[] projection = {
                        Phone.DISPLAY_NAME,
                        Phone.NUMBER
                };
                Cursor cursor = getContentResolver().query(Phone.CONTENT_URI,
                        projection,
                        Phone.CONTACT_ID + " = ? ",
                        new String[] {
                            id,
                        }, null);

                if (cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                    phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    contactTextView.setText(displayName + "/" + phoneNumber);

                    setTriggerData();

                }
                break;
            case CONNECT_RESULT:
                /*
                 * Only ACTION_CONNECT needs the confirmation from
                 * onActivityResult(), listView.setOnItemClickListener handles
                 * all the other adding and removing of panic receivers.
                 */
                if (TextUtils.equals(requestAction, Panic.ACTION_CONNECT)) {
                    PanicTrigger.removeConnectedResponder(this, requestPackageName);
                }
                break;
        }
    }

    //for some triggers we have to request permissions!
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GeoTrigger.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    for (BaseTrigger trigger: triggers) {

                        //now setup the trigger again, with permissions this time
                        if (trigger instanceof GeoTrigger)
                            ((GeoTrigger)trigger).activateTrigger();

                    }


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   pickContacts();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void setTriggerData ()
    {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("email",emailAddress);
        edit.putString("phone",phoneNumber);
        edit.putString("subject","panic message");
        edit.putString("message",panicMessageEditText.getText().toString());

        edit.commit();

    }
}
