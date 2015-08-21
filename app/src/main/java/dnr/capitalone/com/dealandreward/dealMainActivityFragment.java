package dnr.capitalone.com.dealandreward;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class dealMainActivityFragment extends Fragment {

    public dealMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_deal_main, container, false);
        return inflater.inflate(R.layout.profile_deal_main, container, false);
    }
}
