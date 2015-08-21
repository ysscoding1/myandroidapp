package dnr.capitalone.com.dealandreward;

/**
 * Created by Divya on 7/27/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private Context context;
    public LocationAddress(Context a)
    {
        context = a;
    }
    public String getZipCodeFromLocation(Location location) {
        Address addr = getAddressFromLocation(location);
        return addr.getPostalCode() == null ? "" : addr.getPostalCode();
    }

    private Address getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context);
        Address address = new Address(Locale.getDefault());
        try {
            List<Address> addr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addr.size() > 0) {
                address = addr.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
