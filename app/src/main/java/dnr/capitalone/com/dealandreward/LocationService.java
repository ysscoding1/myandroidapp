package dnr.capitalone.com.dealandreward;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.location.LocationManager.*;

/**
 * Created by Divya on 7/27/2015.
 */
public class LocationService extends Service
{
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 30 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    NotificationCompat.Builder wearNotificaiton = null;
    NotificationManagerCompat notificationManager = null;
    //NotificationManagerCom notificationManager;
    NotificationCompat.Builder notif;
    Intent intent;
    int counter = 0;
    String zipcode = "60173";

    public String getZipCode() {

        return zipcode;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        // Define the criteria how to select the location provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        wearNotificaiton = new NotificationCompat.Builder(getApplicationContext())
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.wallet)
                .setWhen(System.currentTimeMillis())
                .setTicker("Wallet Icon")
                .setContentTitle("Nearest Coupons")
                .setContentText("Hello")
                .setGroup("COUPONLIST");


        // Create a NotificationCompat.Builder to build a standard notification
// then extend it with the WearableExtender
     /*   notif = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Coupons Available in your area")
                .setContentText("coupon details")
                .setSmallIcon(R.drawable.wallet)
                .extend(wearableExtender)
                .build();
*/
        // Extend the notification builder with the second page
                        /*Notification notification = wearNotificaiton
                                .extend(new NotificationCompat.WearableExtender().build();*/

        // Issue the notification
        // Get an instance of the NotificationManager service



         notificationManager =
                NotificationManagerCompat.from(getApplicationContext());

        // user defines the criteria

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, false);
        previousBestLocation = locationManager.getLastKnownLocation(provider);
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 4000, 0, listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.i("TestData", "starting service");
      //  Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 4000, 0, listener);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            //Toast.makeText(getApplicationContext(), "current best locatione " , Toast.LENGTH_SHORT).show();
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

       // Toast.makeText(getApplicationContext(), "time delta: "+ timeDelta , Toast.LENGTH_SHORT).show();

       // Toast.makeText(getApplicationContext(), "time delta isSignificantlyNewer: "+ isSignificantlyNewer , Toast.LENGTH_SHORT).show();

       // Toast.makeText(getApplicationContext(), "time delta isSignificantlyOlder: "+ isSignificantlyOlder , Toast.LENGTH_SHORT).show();

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        //Toast.makeText(getApplicationContext(), "accuracyDelta: "+ accuracyDelta , Toast.LENGTH_SHORT).show();


        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
           // Log.i("**************************************", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                //Toast.makeText(getApplicationContext(), "location change " , Toast.LENGTH_SHORT).show();
                LocationAddress l;
                l = new LocationAddress(getApplicationContext());

                final String zipcode = "60173";// l.getZipCodeFromLocation(loc);
                //Toast.makeText(getApplicationContext(), "location change zipcode: "+ zipcode , Toast.LENGTH_SHORT).show();

                // Define the criteria how to select the location provider
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

                // user defines the criteria

                criteria.setCostAllowed(false);

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                String provider = locationManager.getBestProvider(criteria, false);
                previousBestLocation = locationManager.getLastKnownLocation(provider);

                SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                        "dnrLoginPrefFiles", Context.MODE_PRIVATE);
                String email = sharedPref1.getString("username", "bkadali@gmail.com");
                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                        "walletPrefFiles", Context.MODE_PRIVATE);
                Map<String, ?> prefFilesMap = sharedPref.getAll();
                String couponIDs;


                if (prefFilesMap.isEmpty() != true) {
                    //Toast.makeText(getApplicationContext(), "pref is not null: "+ zipcode , Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPref2 = getBaseContext().getSharedPreferences(
                            "dnrCouponAddress", Context.MODE_PRIVATE);
                    boolean flag = false;
                    if (sharedPref2.getAll().isEmpty() != true)
                    {
                        //Toast.makeText(getApplicationContext(), "pref2 is not null: "+ zipcode , Toast.LENGTH_SHORT).show();
                        couponIDs = (String) prefFilesMap.get("couponIDs");
                        Toast.makeText(getApplicationContext(), "coupon ids: "+ couponIDs , Toast.LENGTH_SHORT).show();
                        String[] ids = couponIDs.split(",");

                        Set<String> stringSet = new HashSet<>(Arrays.asList(ids));
                        String[] filteredArray = stringSet.toArray(new String[0]);

                        String couponsDetails = "You have a coupon for restaurants:";
                        for(String s : filteredArray)
                        {
                            String add =  sharedPref2.getString(s, "1615 Golf Rd, Schaumurg, IL");
                            String name = sharedPref2.getString(s+"name", "TGIF");
                            String zip = sharedPref2.getString(s+"zip", "60173");

                            if (zip.equals(zipcode)) {
                                couponsDetails += name + " at ";
                                couponsDetails += add;
                                flag = true;
                            }
                        }

                        if (flag) {
                            wearNotificaiton.setContentText(couponsDetails);
                            wearNotificaiton.setStyle(new NotificationCompat.BigTextStyle().bigText(couponsDetails));
                            notificationManager.notify(01, wearNotificaiton.build());
                            flag = false;
                        }
                    }

                }

                Thread background = new Thread(new Runnable() {

                    private String urlString = "http://52.5.81.122:8080/retreive/coupon/";
                    private String jsonString = "";
                    private String SetServerString = "";
                    // After call for background.start this run method call
                    public void run() {
                        try {

                            Log.i("run",  "location change check address" + zipcode );
                            Toast.makeText(getApplicationContext(), "location change check address" + zipcode , Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                                    "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                            String email = sharedPref1.getString("username", "bkadali@gmail.com");

                            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                                    "walletPrefFiles", Context.MODE_PRIVATE);
                            Map<String, ?> prefFilesMap = sharedPref.getAll();
                            String couponIDs;


                            if (prefFilesMap.isEmpty() != true) {
                                Log.d("MyApp", "Inside!");
                                couponIDs = (String) prefFilesMap.get("couponIDs");
                                Log.d("CouponIDSet", couponIDs);
                                Set<String> couponIDSet = new HashSet<String>(Arrays.asList(couponIDs.split(",")));
                                int i = 0;
                                int end = couponIDSet.size();
                                InputStream in = null;
                                List<String> couponDetailsList = new ArrayList<String>();
                                for(String couponID: couponIDSet) {
                                    urlString = "http://52.5.81.122:8080/retreive/coupon/" + couponID;
                                    URL url = new URL(urlString);
                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                    String SetServerString = "";
                                    in = new BufferedInputStream(urlConnection.getInputStream());
                                    //Log.d("MyApp", "Input Stream is: " + convertInputStreamToString(in));
                                    // convert inputstream to string
                                    if(in != null)
                                        SetServerString = convertInputStreamToString(in);
                                    else
                                        SetServerString = "Did not work!";
                                    Log.d("BeforeReplaced", SetServerString);
                                    couponDetailsList.add(SetServerString);
                                }
                                jsonString = Arrays.toString(couponDetailsList.toArray());
                                Log.d("Wallet", "reached");
                                Log.d("JsonString", jsonString);
                                Log.d("SetServerString", "In Wallet SetServerString is : " + SetServerString);
                                in.close();
                                threadMsg(jsonString);
                            }
                        } catch (Throwable t) {
                            // just end the background thread
                            Log.i("Animation", "Thread  exception " + t);
                        }
                    }

                    private void threadMsg(String msg) {
                        if (!msg.equals(null) && !msg.equals("")) {
                            Message msgObj = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("message", msg);
                            msgObj.setData(b);
                            handler.sendMessage(msgObj);
                        }
                    }

                    // Define the Handler that receives messages from the thread and update the progress
                    private final Handler handler = new Handler() {
                        public void handleMessage(Message msg) {
                            String aResponse = msg.getData().getString("message");
                            Log.d("aResponse", "In Handler aResponse is: " + aResponse);
                            if ((null != aResponse)) {
                                Type listType = new TypeToken<List<CouponDetails>>() {
                                }.getType();
                                ArrayList<CouponDetails> list = new Gson().fromJson(aResponse, listType);
                                ArrayList<CouponDetails> testAddress = new ArrayList<CouponDetails>();
                                String couponsDetails = "";
                                for (int i = 0; i < list.size(); i++) {
                                    //   displayCoupon(list.get(i).getCouponInfo(), list.get(i).getMerchant(), i, list.get(i).getCouponId());

                                    if (list.get(i).getZipcode() != null && list.get(i).getZipcode().equals(getZipCode())) {
                                        testAddress.add(list.get(i));
                                        couponsDetails += "You have a coupon for the restuarent: " + list.get(i).getMerchant() + " at Address: " +
                                                list.get(i).getAddress() + " " + list.get(i).getZipcode() + ", for a coupon off " + list.get(i).getCouponInfo() + ".";
                                    }
                                }

                                if (testAddress.size() > 0) {
                                    wearNotificaiton.setContentText(couponsDetails);
                                    notificationManager.notify(01, wearNotificaiton.build());
                                }
                            } else {
                                Log.e("Error", "No Response From Server in Location check");
                            }
                        }
                    };
                });
                background.start();

               // Toast.makeText(getApplicationContext(), "End calling " + zipcode , Toast.LENGTH_SHORT).show();
                // Create Inner Thread Class
                //Thread background = new Thread(new UIThread(zipcode));
                // Start Thread
                //background.start();
                /*String jsonString;
                String urlString;
                String SetServerString="";
                try {

                    Log.i("run",  "location change check address" + zipcode );
                    Toast.makeText(getApplicationContext(), "location change check address" + zipcode , Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                            "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                    String email = sharedPref1.getString("username", "bkadali@gmail.com");

                    SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                            "walletPrefFiles", Context.MODE_PRIVATE);
                    Map<String, ?> prefFilesMap = sharedPref.getAll();
                    String couponIDs;
                    Toast.makeText(getApplicationContext(), "location change user" + email , Toast.LENGTH_SHORT).show();


                    if (prefFilesMap.isEmpty() != true) {

                       // wearNotificaiton.setContentText("heello test notif");
                       // notificationManager.notify(01, wearNotificaiton.build());

                        Log.d("MyApp", "Inside!");
                        couponIDs = (String) prefFilesMap.get("couponIDs");
                        Toast.makeText(getApplicationContext(), "couponIds" + couponIDs , Toast.LENGTH_SHORT).show();
                        Log.d("CouponIDSet", couponIDs);
                        Set<String> couponIDSet = new HashSet<String>(Arrays.asList(couponIDs.split(",")));
                        int i = 0;
                        int end = couponIDSet.size();
                        InputStream in = null;
                        List<String> couponDetailsList = new ArrayList<String>();
                        for(String couponID: couponIDSet) {
                            Toast.makeText(getApplicationContext(), "calling url with id" + couponID , Toast.LENGTH_SHORT).show();
                            urlString = "http://52.5.81.122:8080/retreive/coupon/" + couponID;
                            Toast.makeText(getApplicationContext(), "url: " + urlString , Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "" + couponID , Toast.LENGTH_SHORT).show();
                            java.net.URL url = new URL(urlString);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                            SetServerString = "";
                            in = new BufferedInputStream(urlConnection.getInputStream());
                            //Log.d("MyApp", "Input Stream is: " + convertInputStreamToString(in));
                            // convert inputstream to string
                            if(in != null)
                                SetServerString = convertInputStreamToString(in);
                            else
                                SetServerString = "Did not work!";
                            Log.d("BeforeReplaced", SetServerString);
                            Toast.makeText(getApplicationContext(), "server strings" + SetServerString , Toast.LENGTH_SHORT).show();
                            couponDetailsList.add(SetServerString);
                        }
                        jsonString = Arrays.toString(couponDetailsList.toArray());
                        Log.d("Wallet", "reached");
                        Log.d("JsonString", jsonString);
                        Toast.makeText(getApplicationContext(), "JsonString" + jsonString , Toast.LENGTH_SHORT).show();
                        Log.d("SetServerString", "In Wallet SetServerString is : " + SetServerString);
                        in.close();


                        Log.d("aResponse", "In Handler aResponse is: " + SetServerString);
                        if ((null != SetServerString)) {
                            Type listType = new TypeToken<List<CouponDetails>>() {}.getType();
                            ArrayList<CouponDetails> list = new Gson().fromJson(SetServerString, listType);
                            ArrayList<CouponDetails> testAddress = new ArrayList<CouponDetails>();
                            String couponsDetails = "";
                            for (int k = 0; k < list.size(); k++) {
                                //   displayCoupon(list.get(i).getCouponInfo(), list.get(i).getMerchant(), i, list.get(i).getCouponId());

                                if (list.get(k).getZipcode() != null && list.get(k).getZipcode().equals(zipcode) )
                                {
                                    testAddress.add(list.get(k));
                                    couponsDetails+="You have a coupon for the restuarent: "+ list.get(k).getMerchant() + " at Address: "+
                                            list.get(k).getAddress() +" " + list.get(k).getZipcode() + ", for a coupon off " + list.get(k).getCouponInfo()+ ".";
                                    Toast.makeText(getApplicationContext(), "Copouns msg" + couponsDetails , Toast.LENGTH_SHORT).show();
                                }
                            }



                            if (testAddress.size() > 0)
                            {
                                wearNotificaiton.setContentText(couponsDetails);
                                notificationManager.notify(01, wearNotificaiton.build());
                            }
                        }
                        else {
                            Log.e("Error", "No Response From Server in Location check");
                        }

                 //       threadMsg(jsonString);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "no detailson pref" , Toast.LENGTH_SHORT).show();

                    }
                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
*/

            }
        }



        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }


        public class UIThread implements Runnable{

            private String zipCode = "";
            public UIThread(String zCode) {
                zipCode = zCode;
            }

            private String urlString = "http://52.5.81.122:8080/retreive/coupon/";
            private String jsonString = "";
            private String SetServerString = "";
            // After call for background.start this run method call
            public void run() {
                try {

                   // Log.i("run",  "location change check address" + zipCode );
                    Toast.makeText(getApplicationContext(), "start of the  thread" + zipCode , Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                            "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                    String email = sharedPref1.getString("username", "bkadali@gmail.com");

                    SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                            "walletPrefFiles", Context.MODE_PRIVATE);
                    Map<String, ?> prefFilesMap = sharedPref.getAll();
                    String couponIDs;


                    if (prefFilesMap.isEmpty() != true) {
                        Log.d("MyApp", "Inside!");
                        couponIDs = (String) prefFilesMap.get("couponIDs");
                        Log.d("CouponIDSet", couponIDs);
                        Set<String> couponIDSet = new HashSet<String>(Arrays.asList(couponIDs.split(",")));
                        int i = 0;
                        int end = couponIDSet.size();
                        InputStream in = null;
                        List<String> couponDetailsList = new ArrayList<String>();
                        for(String couponID: couponIDSet) {
                            urlString = "http://52.5.81.122:8080/retreive/coupon/" + couponID;
                            URL url = new URL(urlString);
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                            String SetServerString = "";
                            in = new BufferedInputStream(urlConnection.getInputStream());
                            //Log.d("MyApp", "Input Stream is: " + convertInputStreamToString(in));
                            // convert inputstream to string
                            if(in != null)
                                SetServerString = convertInputStreamToString(in);
                            else
                                SetServerString = "Did not work!";
                            Log.d("BeforeReplaced", SetServerString);
                            couponDetailsList.add(SetServerString);
                        }
                        jsonString = Arrays.toString(couponDetailsList.toArray());
                        Log.d("Wallet", "reached");
                        Log.d("JsonString", jsonString);
                        Log.d("SetServerString", "In Wallet SetServerString is : " + SetServerString);
                        in.close();
                        threadMsg(jsonString);
                    }
                } catch (Throwable t) {
                    // just end the background thread
                    Log.i("Animation", "Thread  exception " + t);
                }
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    String aResponse = msg.getData().getString("message");
                    Log.d("aResponse", "In Handler aResponse is: " + aResponse);
                    if ((null != aResponse)) {
                        Type listType = new TypeToken<List<CouponDetails>>() {}.getType();
                        ArrayList<CouponDetails> list = new Gson().fromJson(aResponse, listType);
                        ArrayList<CouponDetails> testAddress = new ArrayList<CouponDetails>();
                        String couponsDetails = "";
                        for (int i = 0; i < list.size(); i++) {
                            //   displayCoupon(list.get(i).getCouponInfo(), list.get(i).getMerchant(), i, list.get(i).getCouponId());

                            if (list.get(i).getZipcode() != null && list.get(i).getZipcode().equals(zipCode) )
                            {
                                testAddress.add(list.get(i));
                                couponsDetails+="You have a coupon for the restuarent: "+ list.get(i).getMerchant() + " at Address: "+
                                        list.get(i).getAddress() +" " + list.get(i).getZipcode() + ", for a coupon off " + list.get(i).getCouponInfo()+ ".";
                            }
                        }

                        if (testAddress.size() > 0)
                        {
                            wearNotificaiton.setContentText(couponsDetails);
                            notificationManager.notify(01, wearNotificaiton.build());
                            /*NotificationCompat.Builder wearNotificaiton = new NotificationCompat.Builder(getApplicationContext())
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.wallet)
                                    .setWhen(System.currentTimeMillis())
                                    .setTicker("Wallet Icon")
                                    .setContentTitle("Text 1")
                                    .setContentText("Hello")
                                    .setGroup("COUPONLIST");


                            NotificationCompat.WearableExtender wearableExtender =
                                    new NotificationCompat.WearableExtender()
                                            .setHintHideIcon(true);

                            // Create a NotificationCompat.Builder to build a standard notification
// then extend it with the WearableExtender
                            Notification notif = new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle("Coupons Available in your area")
                                    .setContentText(couponsDetails)
                                    .setSmallIcon(R.drawable.wallet)
                                    .extend(wearableExtender)
                                    .build();

                            // Extend the notification builder with the second page
                        *//*Notification notification = wearNotificaiton
                                .extend(new NotificationCompat.WearableExtender().build();*//*

                            // Issue the notification
                            // Get an instance of the NotificationManager service
                            NotificationManagerCompat notificationManager =
                                    NotificationManagerCompat.from(getApplicationContext());
                            notificationManager =
                                    NotificationManagerCompat.from(getApplicationContext());
                            notificationManager.notify(01, notif);*/
                        }
                    }
                    else {
                        Log.e("Error", "No Response From Server in Location check");
                    }
                }
            };
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }


    }
}