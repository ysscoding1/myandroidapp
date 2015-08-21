package dnr.capitalone.com.dealandreward;

/**
 * Created by EXB795 on 6/19/2015.
 */
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ButtonPushedOnTouchListener implements OnTouchListener
{
    final Button button;

    public ButtonPushedOnTouchListener(final Button button) {
        super();
        this.button = button;
    }
    public boolean onTouch(View v, MotionEvent event)
    {

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //button.setPressed(true);
                button.setPadding(button.getPaddingLeft(), button.getPaddingTop() + 10,
                        button.getPaddingRight(), button.getPaddingBottom());
                break;

            case MotionEvent.ACTION_MOVE:
                // touch move code
                break;

            case MotionEvent.ACTION_UP:
                //button.setPressed(false);
                button.setPadding(button.getPaddingLeft(), button.getPaddingTop() - 10,
                        button.getPaddingRight(), button.getPaddingBottom());
                break;
        }
        return false;
    }
}

