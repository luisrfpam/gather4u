package org.ufam.gather4u.utils;

public class Conv {

    public static Double ToDouble(String val){
        if (val != null){
            if (val.trim().length() > 0){
                return Double.parseDouble(val);
            }
        }
        return 0.0;
    }

}
