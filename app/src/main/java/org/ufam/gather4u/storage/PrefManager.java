package org.ufam.gather4u.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import org.ufam.gather4u.models.User;


public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Gather4u";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_SESSION = "user_session";

    private String TAG = this.getClass().getSimpleName();
    private static PrefManager INSTANCE;

    private PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public static synchronized PrefManager getINSTANCE(Context context){
        if(INSTANCE==null){
            INSTANCE = new PrefManager(context);
        }
        return INSTANCE;
    }

    public void createLogin() {
        editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void removeLogin() {
        editor = pref.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean( KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    public void saveUserSession(User user){
        editor = pref.edit();
        editor.putString(KEY_USER_SESSION, new Gson().toJson(user));
        editor.commit();
    }

    public User getUserSession() {
        return new Gson().fromJson(pref.getString(KEY_USER_SESSION, null),User.class);
    }
}
