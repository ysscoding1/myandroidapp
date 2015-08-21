package dnr.capitalone.com.dealandreward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by RichardYan on 6/27/15.
 */
public class MoneyEarnedActivity extends Activity {

    TextView textView;
    ImageButton imgButton;
    RelativeLayout totalEarnedLayout;
    String firstLevelRewardsAmount;
    String secondLevelRewardsAmount;
    String thirdLevelRewardsAmount;
    String totalDollarAmount;
    String totalCentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_info);

        // Create Inner Thread Class
        Thread background = new Thread(new Runnable() {

            private String urlString = "http://52.5.81.122:8080/retreive/coupon/restuarent/";
            private String jsonString = "";
            private String SetServerString = "";
            private String email = "";


            // After call for background.start this run method call
            public void run() {
                SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                        "dnrLoginPrefFiles", Context.MODE_PRIVATE);

                email = sharedPref.getString("username", "bkadali@gmail.com");
                try {
                    if (email.equals("") != true) {
                        String urlString = "http://52.5.81.122:8080/rewards/totalrewards/" + email;
                        java.net.URL url = new URL(urlString);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        String SetServerString = "";
                        InputStream in = null;
                        in = new BufferedInputStream(urlConnection.getInputStream());
                        //Log.d("MyApp", "Input Stream is: " + convertInputStreamToString(in));
                        // convert inputstream to string
                        if(in != null) {
                            SetServerString = convertInputStreamToString(in);
                        }
                        else {
                            SetServerString = "Did not work!";
                        }
                        Log.d("BeforeReplaced", SetServerString);
                        in.close();
                        threadMsg(SetServerString);
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
                    //final String decimalFormatString = "##.00";
                    double totalAmount;
                    String aResponse = msg.getData().getString("message");
                    Log.d("aResponse", "In Handler aResponse is: " + aResponse);
                    if ((null != aResponse)) {
                        Type responseType = new TypeToken<UserDetails>() {}.getType();
                        UserDetails userDetails = new Gson().fromJson(aResponse, responseType);
                        Log.d("UserDetails", userDetails.toString());
                        double rewardsAmount = Double.valueOf(userDetails.getRewardsAmount());
                        double directReferralRewards = Double.valueOf(userDetails.getDirectReferralRewards());
                        double subrefRewards = Double.valueOf(userDetails.getSubrefRewards());

                        totalAmount = rewardsAmount + directReferralRewards + subrefRewards;

                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
                        String[] amounts = (currencyFormatter.format(totalAmount)).split("\\.");
                        firstLevelRewardsAmount = (currencyFormatter.format(rewardsAmount));
                        secondLevelRewardsAmount = (currencyFormatter.format(directReferralRewards));
                        thirdLevelRewardsAmount = (currencyFormatter.format(subrefRewards));
                        totalDollarAmount = amounts[0].replace("$", "");
                        totalCentAmount = amounts[1];
                        Log.d("TotalAmount", String.valueOf(totalAmount) + Arrays.toString(amounts) + currencyFormatter.format(totalAmount));
                        displayUserAmount(totalDollarAmount, totalCentAmount, firstLevelRewardsAmount,
                                secondLevelRewardsAmount, thirdLevelRewardsAmount);

                    }
                    else {
                        Log.e("Error", "No Response From Server in Money Earned Activity");
                    }
                }
            };
        });
        // Start Thread
        background.start();





        /*imgButton = (ImageButton) findViewById(R.id.totalEarnedInfoButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MoneyEarnedActivity.this, RewardsInfoActivity.class);
                Bundle extras = new Bundle();
                extras.putString("totalDollarAmount", totalDollarAmount);
                extras.putString("totalCentAmount", totalCentAmount);
                extras.putString("firstLevelRewardsAmount", firstLevelRewardsAmount);
                extras.putString("secondLevelRewardsAmount", secondLevelRewardsAmount);
                extras.putString("thirdLevelRewardsAmount", thirdLevelRewardsAmount);
                i.putExtras(extras);
                startActivity(i);
            }
        });*/
    };

    public String[] calculateDollarAndCentAmount (String firstAmount, String secondAmount, String thirdAmount) {
        String[] firstSplit= firstAmount.split(".");
        String[] secondSplit= firstAmount.split(".");
        String[] thirdSplit= firstAmount.split(".");
        int carryOver = 0;
        String totalCentAmount = "";
        String totalDollarAmount = "";
        int addedCentAmount = Integer.valueOf(firstSplit[1]) + Integer.valueOf(secondSplit[1]) + Integer.valueOf(thirdSplit[1]);
        int addedDollarAmount = Integer.valueOf(firstSplit[0]) + Integer.valueOf(secondSplit[0]) + Integer.valueOf(thirdSplit[0]);
        if(addedCentAmount >= 100) {
            carryOver = addedCentAmount / 100;
            totalCentAmount = String.valueOf(addedCentAmount % 100);
        }
        addedDollarAmount += carryOver;
        totalDollarAmount = NumberFormat.getNumberInstance(Locale.US).format(addedDollarAmount);
        String[] totalAmounts = {totalDollarAmount, totalCentAmount};
        return totalAmounts;
    }
    public void displayUserAmount(String dollarAmount, String centAmount, String firstLevelRewardsAmount,
                                  String secondLevelRewardsAmount, String thirdLevelRewardsAmount){

        totalEarnedLayout = (RelativeLayout) findViewById(R.id.totalEarnedLayout);
        ((TextView) totalEarnedLayout.findViewById(R.id.earnedDollarAmount)).setText(dollarAmount);
        ((TextView) totalEarnedLayout.findViewById(R.id.earnedCentAmount)).setText(centAmount);

        ((TextView) findViewById(R.id.firstLevelRewardsAmount)).setText(firstLevelRewardsAmount);
        ((TextView) findViewById(R.id.secondLevelRewardsAmount)).setText(secondLevelRewardsAmount);
        ((TextView) findViewById(R.id.thirdLevelRewardsAmount)).setText(thirdLevelRewardsAmount);
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
}
