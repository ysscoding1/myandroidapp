package dnr.capitalone.com.dealandreward;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by RichardYan on 7/21/15.
 */
public class RewardsInfoActivity extends Activity {
    TextView textView;
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_info);

        Intent i = getIntent();
        String totalDollarAmount = i.getStringExtra("totalDollarAmount");
        String totalCentAmount = i.getStringExtra("totalCentAmount");
        String firstLevelRewardsAmount = i.getStringExtra("firstLevelRewardsAmount");
        String secondLevelRewardsAmount = i.getStringExtra("secondLevelRewardsAmount");
        String thirdLevelRewardsAmount = i.getStringExtra("thirdLevelRewardsAmount");

        textView = (TextView) findViewById(R.id.earnedDollarAmount);
        textView.setText(totalDollarAmount);

        textView = (TextView) findViewById(R.id.earnedCentAmount);
        textView.setText(totalCentAmount);

        textView = (TextView) findViewById(R.id.firstLevelRewardsAmount);
        textView.setText(firstLevelRewardsAmount);

        textView = (TextView) findViewById(R.id.secondLevelRewardsAmount);
        textView.setText(secondLevelRewardsAmount);

        textView = (TextView) findViewById(R.id.thirdLevelRewardsAmount);
        textView.setText(thirdLevelRewardsAmount);

        imgButton = (ImageButton) findViewById(R.id.totalEarnedInfoButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RewardsInfoActivity.this, MoneyEarnedActivity.class);
                startActivity(i);
            }
        });
    }
}
