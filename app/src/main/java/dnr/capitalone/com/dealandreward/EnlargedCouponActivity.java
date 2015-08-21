package dnr.capitalone.com.dealandreward;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by RichardYan on 8/3/15.
 */
public class EnlargedCouponActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarged_coupon);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            String urlString = extras.getString("enlargedCoupon");
            ImageView imgView = (ImageView) findViewById(R.id.enlargedImage);
            new DownloadImageTask(imgView)
                    .execute(urlString);
        }
    }


}
