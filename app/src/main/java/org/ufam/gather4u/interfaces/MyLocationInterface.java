package org.ufam.gather4u.interfaces;

import android.location.Location;

import java.util.HashMap;

public interface MyLocationInterface {

    void myLocationCallback(Location l);
    void myLocationErrorCallback(HashMap<String, String> error);
}
