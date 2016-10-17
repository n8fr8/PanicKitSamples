# PanicKitSamples
Sample PanicKit Triggers / Patterns

This is a simple example app for demonstrating the PanicKit for
sending panic triggers to other apps.

https://dev.guardianproject.info/projects/panic/wiki

For an example of this interaction, check out this demo video:
https://www.youtube.com/watch?v=mS1gstS6YS8

Sample Triggers
----------------------------

PanicKit works by connecting trigger apps with receiver apps. Triggers are what create the alert that there is an emergency or panic situation. Receivers respond to this by taking an appropriate, user configured or default action.

This project demonstrates new possible triggers that could be implemented in an app like Ripple, or any app that wishes to do so.

In the "info.guardianproject.fakepanicbutton.triggers" package, you will find the following classes:

* BaseTrigger: a base class that handles launching of the "panic intent" from a set of stored preferences to trigger the responders
* GeoTrigger: Using the awesome "LOST" open-source geofencing library, this trigger sends a panic if the device moves outside of a pre-defined area (in this sample, it is Times Square NYC)
* MediaButtonTrigger: This trigger will notice multiple rapid pushes of a headset mic button or a bluetooth mic call button, and send a trigger.
* PhoneNumberTrigger (OutgoingCallReceiver): This trigger monitors phone calls, looking for a pre-defined fake "panic number".
* SuperShakeTrigger: This trigger looks for the phone being rapidly shaken. It could be expanded to wait for a series of shakes within a certain time window to avoid false positives.
* WifiTrigger: This triggers waits for the user to connect to a specific wifi network (in this sample "Starbucks"). It could also be set to trigger if the devices leaves the wifi network.

All of these samples are configured to work with the FakePanicButton sample app, which allows you to choose a contact to alert, and set a panic message. That said, these are meant to point in a direction of functionality, and have not been fully debugged or tested on all devices and OS versions.


Building with Android Studio
----------------------------

Just import this as a regular Android Studio gradle project, and it
should work as it is, and download the panic jar via gradle.

