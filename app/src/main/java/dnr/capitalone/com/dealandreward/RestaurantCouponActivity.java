package dnr.capitalone.com.dealandreward;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by RichardYan on 6/25/15.
 */

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestaurantCouponActivity extends FragmentActivity implements LocationFragment.OnFragmentInteractionListener {

    /* Maps */
    GoogleMap map;
    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);

    String zCode;
    public static FragmentManager fragmentManager;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private GoogleApiClient mGoogleApiClient;

    public String getZipCode() {
        if (getIntent().getExtras() != null) {
            zCode = getIntent().getExtras().getString("zipCodeValue");
        }
        //Toast.makeText(this, "get ZipCode:"+zCode, Toast.LENGTH_SHORT).show();
        return zCode;
    }

    public void setZipCode(String code)
    {
        zCode = code;
    }
    View buttonView;
    TextView textview;
    Typeface headerTypeface;
    ImageButton imgButton;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_coupons);
         /* Header */
        textview = (TextView) findViewById(R.id.headerText);
        headerTypeface= Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        textview.setTypeface(headerTypeface);




        // Create Inner Thread Class
        Thread background = new Thread(new Runnable() {


            private String urlString = "http://52.5.81.122:8080/retreive/coupon/restuarent/"+getZipCode();

            // After call for background.start this run method call
            public void run() {
                try {

                    InputStream in = null;
                    java.net.URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    String SetServerString = "";
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    // convert inputstream to string
                    if(in != null)
                        SetServerString = convertInputStreamToString(in);
                    else
                        SetServerString = "Did not work!";
                    threadMsg(SetServerString);
                    Log.d("SetServerString", "String is: " + SetServerString);
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
                    Log.d("Restaurant", "In restaurantcoupon aResponse is: " + aResponse);
                    if ((null != aResponse)) {

                        /*Toast.makeText(
                                getBaseContext(),
                                "Server Response: "+aResponse,
                                Toast.LENGTH_SHORT).show();
                        Log.i("msg:", aResponse);*/

                        Log.i("msg:", aResponse);
                        // Button button =(Button) findViewById(R.id.tgif);
                        Type listType = new TypeToken<List<CouponDetails>>() {}.getType();
                        ArrayList<CouponDetails> list = new Gson().fromJson(aResponse, listType);
                       // Log.d("CouponList", "List is: " + list);

                        // button.setText(list.get(0).getMerchant() + "\t\t" + list.get(0).getCouponInfo());




                        LinearLayout ll = (LinearLayout)findViewById(R.id.mapLevel);

                        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                                "dnrCouponAddress", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();

                        for (int i = 0; i < list.size(); i++) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    89);
                            if (i == 0)
                                params.topMargin = 40;
                            String backgroundColor = "";
                            if (i%2 == 0) {
                                backgroundColor = "#FAFAFA";
                            }
                            else
                            {
                                backgroundColor = "#E6E6E6";
                            }

                            View repeatedCouponButtonLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.coupon_button_frame, null);
                            repeatedCouponButtonLayout.setBackgroundColor(Color.parseColor(backgroundColor));
                            ((TextView)repeatedCouponButtonLayout.findViewById(R.id.couponButtonFrameMerchantName)).setText(list.get(i).getMerchant());
                            ((TextView)repeatedCouponButtonLayout.findViewById(R.id.couponButtonFrameMerchantName)).setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.restauranticon25, 0, 0, 0);
                            ((TextView)repeatedCouponButtonLayout.findViewById(R.id.couponButtonFrameDescription)).setText(list.get(i).getCouponInfo());
                            repeatedCouponButtonLayout.setOnClickListener(new SelectedCouponListener(list.get(i).getCouponId()));
                            ll.addView(repeatedCouponButtonLayout, params);

                            editor.putString(list.get(i).getCouponId(), list.get(i).getAddress().toString());
                            editor.putString(list.get(i).getCouponId()+"name", list.get(i).getMerchant().toString());
                            editor.putString(list.get(i).getCouponId()+"zip", list.get(i).getZipcode().toString());
                            String zipValues =  sharedPref.getString(list.get(i).getCouponId(), "60173");
                            editor.putString(list.get(i).getZipcode(), zipValues+","+list.get(i).getAddress().toString());
                        }

                        editor.commit();
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        // Define the criteria how to select the location provider
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

                        // user defines the criteria

                        criteria.setCostAllowed(false);
                        // get the best provider depending on the criteria
                        String provider = locationManager.getBestProvider(criteria, false);

                        // the last known location of this provider
                        Location location = locationManager.getLastKnownLocation(provider);

                        if (list.isEmpty()) {
                            CouponDetails c = new CouponDetails();
                            c.setMerchant("Current Location");
                            if (location != null)
                            {
                                c.setLat(String.valueOf(location.getLatitude()));
                                c.setLng(String.valueOf(location.getLongitude()));
                                Log.d("Lat", String.valueOf(location.getLatitude()));
                                Log.d("Long", String.valueOf(location.getLongitude()));
                            }
                            else
                            {
                                c.setLat(String.valueOf(0.0));
                                c.setLng(String.valueOf(0.0));
                            }
                            list.add(c);
                        }

                         /* Map */
                        fragmentManager = getFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();

                        LocationFragment lf = new LocationFragment();
                        Bundle b = new Bundle();
                        b.putSerializable("locationList", list);
                        lf.setArguments(b);
                        ft.replace(R.id.mapView, lf);

                        ft.commit();

                    }
                    else
                    {
                        Log.e("Error","No Response from Server in Restaurant Activity!");
                    }



                }
            };

        });
        // Start Thread
        background.start();  //After call start method thread called run Method



        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                "dealnrewardPrefFiles", Context.MODE_PRIVATE);


       /* mGoogleApiClient =   mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation  != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                setZipCode(addresses.get(0).getPostalCode());
                Toast.makeText(this, "new ZipCode:"+addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/

        final EditText editText = (EditText) findViewById(R.id.locationInput);

        InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(editText.getWindowToken(),0);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
               /* SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dealnrewardPrefFiles", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("zipcode", editText.getText().toString());*/
                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    /*Bundle bundle = new Bundle();
                    bundle.putString("zipCode", "60616");
                    //set Fragmentclass Arguments
                    LocationFragment fragobj = new LocationFragment();
                    fragobj.setArguments(bundle);*/
                    //zCode = "60173";

                    Intent i = new Intent(RestaurantCouponActivity.this, RestaurantCouponActivity.class);
                    i.putExtra("zipCodeValue", editText.getText().toString() );
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });
        /*map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        if (map != null) {
            Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
                    .title("Hamburg"));
            Marker kiel = map.addMarker(new MarkerOptions()
                    .position(KIEL)
                    .title("Kiel")
                    .snippet("Kiel is cool"));
                    //.icon(BitmapDescriptorFactory
                    //        .fromResource(R.drawable.ic_launcher)));
        }*/

  /*      buttonView = findViewById(R.id.tgifButton);
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//* Map *//*
                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dealnrewardPrefFiles", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("reward1", "test1");

                Intent i = new Intent(RestaurantCouponActivity.this, SelectedCoupon.class);
                *//* Map *//*
                i.putExtra("couponSelected", "tgifcoupon");
                startActivity(i);
            }
        });*/



        /* Footer */
        imgButton =(ImageButton)findViewById(R.id.shareButton);
        imgButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(imgButton));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                // imageUris.add(R.drawable.c1);

                Resources resources = getResources();
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.c1) + '/' + resources.getResourceTypeName(R.drawable.c1) + '/' + resources.getResourceEntryName(R.drawable.c1));
                imageUris.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.c1) + '/' + resources.getResourceTypeName(R.drawable.c1) + '/' + resources.getResourceEntryName(R.drawable.c1)));

                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dnrLoginPrefFiles", Context.MODE_PRIVATE);
                Map<String, ?> prefFilesMap = sharedPref.getAll();


                String userName = prefFilesMap.get("username").toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUris);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, "Download CapitalOne Deals 'n Rewards App \n http://52.5.81.122:8080/Customer.html?referalId="+userName);
                startActivity(Intent.createChooser(intent,"compatible apps:"));
            }
        });

        imgButton = (ImageButton)findViewById(R.id.moneyButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RestaurantCouponActivity.this, MoneyEarnedActivity.class);
                startActivity(i);
            }
        });

        imgButton = (ImageButton)findViewById(R.id.walletButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RestaurantCouponActivity.this, WalletActivity.class);
                startActivity(i);
            }
        });


        imgButton = (ImageButton)findViewById(R.id.homeButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), dealMainActivity.class);
                startActivity(i);
            }
        });

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coupon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        //TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Setting latitude and longitude in the TextView tv_location
        //tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );

    }
*/
/*
    private static View view;
    private static GoogleMap mMap;
    private static Double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.activity_coupon, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 26.78;
        longitude = 72.56;

        setUpMapIfNeeded(); // For setting up the MapFragment

        return view;
    }

    *//***** Sets up the map if it is possible to do so *****//*
    public static void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) CouponActivity.
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    *//**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     *//*
    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 12.0f));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap(); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    *//**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****//*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(MainActivity.fragmentManager.findFragmentById(R.id.location_map)).commit();
            mMap = null;
        }
    }*/

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            Uri dataValue = data.getData();
        }
    }


    public class SelectedCouponListener implements View.OnClickListener
    {

        String selectedCoupon;
        public SelectedCouponListener(String selectedCoupon) {
            this.selectedCoupon = selectedCoupon;
        }

        @Override
        public void onClick(View v)
        {
            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                    "dealnrewardPrefFiles", Context.MODE_PRIVATE);
            Intent i = new Intent(RestaurantCouponActivity.this, SelectedCoupon.class);
            i.putExtra("couponSelected", selectedCoupon);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("reward1", "test1");
            startActivity(i);
        }

    };


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}