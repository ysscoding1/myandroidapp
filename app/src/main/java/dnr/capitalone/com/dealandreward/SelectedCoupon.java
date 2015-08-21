package dnr.capitalone.com.dealandreward;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;


public class SelectedCoupon extends ActionBarActivity {
    ImageView imageView;
    ImageButton imgButton;
    LinearLayout linearLayout;
    LinearLayout mainLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_coupons);
        String value = null;

        Bundle extras = getIntent().getExtras();
        String urlString = "http://52.5.81.122:8080/retreive/image/grocery/";
        if (extras!=null)
        {
            value = extras.getString("couponSelected");
            urlString+=value;
            ImageView imgView = (ImageView) findViewById(R.id.couponstodisplay);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    1000,
                    500);
            imgView.setLayoutParams(params);
            new DownloadImageTask(imgView)
                    .execute(urlString);


            // imgView.setImageResource(getResources().getIdentifier(value,"drawable", getPackageName()));
        }
        mainLinearLayout = (LinearLayout) findViewById(R.id.mainLevel);

        linearLayout = (LinearLayout) findViewById(R.id.clipCouponButton);
        linearLayout.setOnClickListener(new SCListener(value));


        imgButton =(ImageButton) findViewById(R.id.walletButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectedCoupon.this, WalletActivity.class);
                startActivity(i);
            }
        });

        imgButton =(ImageButton) findViewById(R.id.homeButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectedCoupon.this, dealMainActivity.class);
                startActivity(i);
            }
        });

        imgButton =(ImageButton)findViewById(R.id.shareButton);
        imgButton.setOnTouchListener(new ButtonHighlighterOnTouchListener(imgButton));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  Toast.makeText(getApplicationContext(), "You download is resumed", Toast.LENGTH_LONG).show();*/

               /* Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "http://www.url.com");
                startActivity(Intent.createChooser(i, "Share URL"));*/

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

        ImageView imgView = (ImageView) findViewById(R.id.couponstodisplay);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectedCoupon.this, EnlargedCouponActivity.class);
                Bundle extras = getIntent().getExtras();
                String urlString = "http://52.5.81.122:8080/retreive/image/grocery/";
                String value = "";
                if (extras!=null) {
                    value = extras.getString("couponSelected");
                }
                i.putExtra("enlargedCoupon", urlString+value);
                startActivity(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selected_coupon, menu);
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

    public class SCListener implements View.OnClickListener
    {

        String selectedCoupon;
        public SCListener(String selectedCoupon) {
            this.selectedCoupon = selectedCoupon;
        }

        @Override
        public void onClick(View v)
        {
            SharedPreferences sharedPref1 = getBaseContext().getSharedPreferences(
                    "dnrLoginPrefFiles", Context.MODE_PRIVATE);

            String email = sharedPref1.getString("username", "bkadali@gmail.com");
            //  Toast.makeText(getApplicationContext(), "Getting:" , Toast.LENGTH_SHORT);
            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(email +
                    "walletPrefFiles", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            Map<String, ?> prefFilesMap = sharedPref.getAll();
            if (prefFilesMap.isEmpty() == true) {
                editor.putString("couponIDs", selectedCoupon);
            }
            else {
                String existingIds = (String) prefFilesMap.get("couponIDs");
                editor.putString("couponIDs", existingIds + "," + selectedCoupon);
                Log.d("MyApp", "Existing IDs: " + existingIds);
            }
            /*String existingIds = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("walletPrefFiles", "");

            if (existingIds.isEmpty()) {
                editor.putString("couponIDs", selectedCoupon);
            }
            else {

                editor.putString("couponIDs", existingIds + "," + selectedCoupon);
            }*/
            editor.commit();

            //String allIds = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("walletPrefFiles", "");
            //Toast.makeText(getApplicationContext(), "All Ids: "+ allIds , Toast.LENGTH_SHORT);
        }

    }
}
