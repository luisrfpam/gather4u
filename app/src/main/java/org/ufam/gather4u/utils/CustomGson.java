package org.ufam.gather4u.utils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CustomGson {

    public static String ToJson(Object clazz)
    {
        return new Gson().toJson(clazz);
    }

    public static JSONObject ToJSONObject(Class clazz){
        try {
            return new JSONObject( ToJson(clazz) );
        }
        catch (JSONException ex)
        {
        }
        return null;
    }

    public static Object FromJson(String json, Class[] clazz){
        return new Gson().fromJson(json, clazz.getClass());
    }

    /**
     * Get the underlying class for a type, or null if the type is a variable
     * type.
     *
     * @param type the type
     * @return the underlying class
     */
    public static Class<?> getClass(Type type)
    {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
