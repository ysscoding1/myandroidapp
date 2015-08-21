package dnr.capitalone.com.dealandreward;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ZGP046 on 7/22/2015.
 */
public class RewardRedeemActivity extends Activity {
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_redeem);
        String clickedCouponID = "";

        Intent i = getIntent();

        Bundle extras = getIntent().getExtras();
        String urlString = "http://52.5.81.122:8080/retreive/image/grocery/";
        if (extras != null) {
            clickedCouponID = i.getStringExtra("clickedCouponID");
            urlString += clickedCouponID;
            ImageView imgView = (ImageView) findViewById(R.id.couponstoRedeem);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    1000,
                    500);
            imgView.setLayoutParams(params);
            new DownloadImageTask(imgView)
                    .execute(urlString);
        }

        Button button = (Button) findViewById(R.id.clipCouponButton);
        button.setOnClickListener(new SCListener(clickedCouponID));

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
                Intent i = new Intent(RewardRedeemActivity.this, MoneyEarnedActivity.class);
                startActivity(i);
            }
        });

        imgButton = (ImageButton)findViewById(R.id.walletButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RewardRedeemActivity.this, WalletActivity.class);
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


    public class SCListener implements View.OnClickListener {

        String clickedCouponID;

        public SCListener(String clickedCouponID) {
            this.clickedCouponID = clickedCouponID;
        }

        @Override
        public void onClick(View v) {
            try {


                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                String email = sharedPref.getString("username", "bkadali@gmail.com");

                // Create Inner Thread Class
                Thread background = new Thread(new UIRedeemThread(clickedCouponID, email));
                background.start();
            /*
            String SetServerString = "";
            in = new BufferedInputStream(urlConnection.getInputStream());*/
            } catch (Throwable t) {
                // just end the background thread
                Log.i("Animation", "Thread  exception " + t);
            }
        }

    }

    public class UIRedeemThread implements Runnable {
        private LayoutInflater inflater;
        private String couponId;
        private String email;

        public UIRedeemThread(String _couponId, String _email) {
            this.couponId = _couponId;
            this.email = _email;
        }


        // After call for background.start this run method call
        public void run() {


            //call the redeem service
            String urlString = "http://52.5.81.122:8080/redeem/coupon/" + couponId + "/" + email;

            try{
                java.net.URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("myURL response code", String.valueOf(urlConnection.getResponseCode()));
                Log.d("myURL", urlString);
            }catch (Throwable t) {
                // just end the background thread
                Log.i("Animation", "Thread  exception " + t);
            }


        }
    }
}
