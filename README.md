Android44BugDemo-START_STICKY
=============================

Sample code to demonstrate an Android 4.4 bug related to Services. The bug appears to only appear in 4.4.1 and 4.4.2 (perhaps earlier versions, but the reports are unreliable and there's less evidence to back that up). At the time of this writing, two distinct fixes have been submitted to AOSP.

To be used as described in [Title](link)

NOTE: This project was built with Android Studio v0.4.6

Steps to replicate the bug:

1. Build this project and deploy it to a device running 4.4.1 / 4.4.2
2. Run the app
3. Tap 'Start'
4. Press the Home button
5. Enter the multi-tasking screen (however your device supports doing that)
6. Swipe away / dismiss this app

Expected result on most versions of Android: The toast messages should continue hitting the screen every few seconds.

Expected result on *4.4.1 and 4.4.2*: The toast messages will stop appearing on the screen and never return without intervention.