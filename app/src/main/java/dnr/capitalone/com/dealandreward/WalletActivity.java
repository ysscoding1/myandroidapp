package dnr.capitalone.com.dealandreward;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Color;

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
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WalletActivity extends ActionBarActivity {

    Drawable drawable;
    Button button, buttonToAdd;
    ScaleDrawable sd;
    ImageButton imgButton;
    LinearLayout mainLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_wallet);

        // Create Inner Thread Class
        Thread background = new Thread(new Runnable() {

            private String urlString = "http://52.5.81.122:8080/retreive/coupon/";
            private String jsonString = "";
            private String SetServerString = "";
            // After call for background.start this run method call
            public void run() {
                try {
                    SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                            "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                    String email = sharedPref1.getString("username", "bkadali@gmail.com");

                    SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                            "walletPrefFiles", Context.MODE_PRIVATE);
                    Map <String, ?> prefFilesMap = sharedPref.getAll();
                    String couponIDs;
                    mainLinearLayout = (LinearLayout) findViewById(R.id.mainLevelWallet);
                    buttonToAdd = new Button(getBaseContext());

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
                            java.net.URL url = new URL(urlString);
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
                            /*SetServerString = SetServerString.replaceAll("[\\[\\]]", "");
                            Log.d("Replaced", SetServerString);
                            if(i == 0 && i == (end-1)){
                                jsonString += "[" + SetServerString + "]";
                            }
                            else if (i == 0) {
                                jsonString += "[" + SetServerString + ",";
                            }
                            else if (i == (end-1)) {
                                jsonString += SetServerString + "]";
                            }
                            else {
                                jsonString += SetServerString + ",";
                            }*/
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

                       /* SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                                "walletPrefFiles", Context.MODE_PRIVATE);
                        Map <String, ?> prefFilesMap = sharedPref.getAll();
                        String couponIDs = (String) prefFilesMap.get("couponIDs");
                        Set<String> couponIDSet = new HashSet<String>(Arrays.asList(couponIDs.split(",")));*/

                        for (int i = 0; i < list.size(); i++) {
                            displayCoupon(list.get(i).getCouponInfo(), list.get(i).getMerchant(), i, list.get(i).getCouponId());
                        }
                    }
                    else {
                        Log.e("Error", "No Response From Server in Wallet");
                    }
                }
            };
        });
        // Start Thread
        background.start();

        //setContentView(R.layout.activity_wallet);

        /* Body */
        /*drawable = getResources().getDrawable(R.drawable.hm_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.25),
                (int) (drawable.getIntrinsicHeight() * 0.25));
        sd = new ScaleDrawable(drawable, 0, 70, 70);
        button = (Button) findViewById(R.id.hmButton);
        button.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example

        drawable = getResources().getDrawable(R.drawable.starbucks_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.25),
                (int) (drawable.getIntrinsicHeight() * 0.25));
        sd = new ScaleDrawable(drawable, 0, 70, 70);
        button = (Button) findViewById(R.id.starbucksButton);
        button.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example

        drawable = getResources().getDrawable(R.drawable.walmart_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.25),
                (int) (drawable.getIntrinsicHeight() * 0.25));
        sd = new ScaleDrawable(drawable, 0, 70, 70);
        button = (Button) findViewById(R.id.walmartButton);
        button.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example

        drawable = getResources().getDrawable(R.drawable.pizzahut_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.25),
                (int) (drawable.getIntrinsicHeight() * 0.25));
        sd = new ScaleDrawable(drawable, 0, 70, 70);
        button = (Button) findViewById(R.id.pizzahutButton);
        button.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example

        drawable = getResources().getDrawable(R.drawable.bestbuy_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.25),
                (int) (drawable.getIntrinsicHeight() * 0.25));
        sd = new ScaleDrawable(drawable, 0, 70, 70);
        button = (Button) findViewById(R.id.bestbuyButton);
        button.setCompoundDrawables(sd.getDrawable(), null, null, null); //set drawableLeft for example
        */

        /* Footer */
        imgButton =(ImageButton)findViewById(R.id.shareButton);
        imgButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(imgButton));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  Toast.makeText(getApplicationContext(), "You download is resumed", Toast.LENGTH_LONG).show();*/
                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                // imageUris.add(R.drawable.c1);

                Resources resources = getResources();
                Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.c1) + '/' + resources.getResourceTypeName(R.drawable.c1) + '/' + resources.getResourceEntryName(R.drawable.c1));
                imageUris.add(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.c1) + '/' + resources.getResourceTypeName(R.drawable.c1) + '/' + resources.getResourceEntryName(R.drawable.c1)));

                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                String userName = sharedPref.getString("username", "bkadali@gmail.com");

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
                Intent i = new Intent(WalletActivity.this, MoneyEarnedActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallet, menu);
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

    public void displayCoupon(String description, String merchant, int orderIndex, String couponID){
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLevelWallet);

        Button buttonToAdd = new Button(this);
        String color = "bestbuy";
        String textColor = "#FFFFFF";
        if(merchant.equals("TGIF")) {
            color = "white";
            textColor = "#000000";
        }
        int backgroundID = getResources().getIdentifier(color + "_coupon_top", "drawable", getPackageName());
        Log.d("displayCoupon", "description is: " + description + " merchant is: " + merchant + " order is: " + orderIndex);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1000, 200);
        params.gravity=Gravity.CENTER;
        params.topMargin=15;
        buttonToAdd.setLayoutParams(params);
        buttonToAdd.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        buttonToAdd.setBackgroundResource(backgroundID);
        buttonToAdd.setText(merchant + ": " + description);
        buttonToAdd.setGravity(Gravity.RIGHT);
        buttonToAdd.setTextSize(20);
        buttonToAdd.setAllCaps(false);
        buttonToAdd.setTextColor(Color.parseColor(textColor));
        String merchantLogo = merchant.replace(" ", "").replace("'", "").replace(".","").toLowerCase();
        Log.d("merchantLogo", merchantLogo);
        int logoID = getResources().getIdentifier(merchantLogo + "_logo", "drawable", getPackageName());
        Log.d("LOGOID", String.valueOf(logoID));
        if(logoID == 0)
            logoID = getResources().getIdentifier("starbucks_icon", "drawable", getPackageName());

        Drawable logo = getResources().getDrawable(logoID);
        Bitmap bitmap = ((BitmapDrawable) logo).getBitmap();
        Drawable scaledLogo = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));

        buttonToAdd.setCompoundDrawablesRelativeWithIntrinsicBounds(scaledLogo, null, null, null);
        buttonToAdd.setOnClickListener(new SelectedCouponListener(couponID));
        mainLinearLayout.addView(buttonToAdd, orderIndex);

    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        //inputStream.close();
        return result;
    }

    public class SelectedCouponListener implements View.OnClickListener
    {

        String couponID;
        public SelectedCouponListener(String couponID) {
            this.couponID = couponID;
        }

        @Override
        public void onClick(View v)
        {
            /*String urlString = "http://52.5.81.122:8080/retreive/image/grocery/";
            urlString += couponID;
            int couponImageID = getResources().getIdentifier(couponID, "id", getPackageName());
            Log.d("CouponID", String.valueOf(couponImageID));
            ImageView imgView = (ImageView) findViewById(couponImageID);
            new DownloadImageTask(imgView)
                    .execute(urlString);*/
            Intent i = new Intent(WalletActivity.this, RewardRedeemActivity.class);
            i.putExtra("clickedCouponID", couponID);
            startActivity(i);
        }

    };
}
