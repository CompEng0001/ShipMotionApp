# ShipMotionApp

## Intro
An App that is designed to provide proof of concept that a ships motion can captured on a mobile device. The context provided is to detect ships carrying bulk solids in particular those that are prone to liquefaction. 

## Operation

App is to be installed on a device with Android OS and a minSdkVersion 23. 

APK is available here -> [CargoStabilityApp.apk](App/CargoStabilityApp.apk)

## Visual Guide 

 **Splash Screen**

<img align="center" width="300" heigh="600" src="Cargo Stability Warning System Splash Screen.jpg">   

 **Data Acquisition**

 * Here you can see that the app measures Azimuth, pitch and Roll to show the orientation and rotation of the device. 
 * Sample Time can be changed as whole numbers in milliseconds (ms) eg 1000 == 1sec or 200 == 0.2 sec and so forth.

 > That the sample time is limited to the fastest as 0 seconds, however, the hardware logs events between 0 and 200000 microseconds (us)

<img align="center"  width="300" heigh="600" src="Cargo Stability Warning System Defualt sampletime.jpg">  

