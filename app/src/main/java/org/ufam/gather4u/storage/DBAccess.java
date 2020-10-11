package org.ufam.gather4u.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.ufam.gather4u.application.Constants;
import org.ufam.gather4u.models.Residuo;
import org.ufam.gather4u.models.User;

import java.util.ArrayList;

public class DBAccess {

    private SQLiteDatabase db;
    private final String TAG = this.getClass().getSimpleName();
    private Gson mGson;
    private static DBAccess instance;

    private DBAccess(Context context) {
        DBCore auxdb = new DBCore(context, Constants.NAME_DB,null,Constants.VERSION_DB);
        db = auxdb.getWritableDatabase();
        mGson = new Gson();
    }

    public static DBAccess getInstance(Context context){
        if (instance == null) {
            instance = new DBAccess(context);
        }
        return instance;
    }

    public boolean insertUser(User user){
        try {
            JSONObject joUser = new JSONObject(mGson.toJson(user));
            ContentValues values = new ContentValues();
            values.put("user_json",joUser.toString());
            values.put("login",user.getLogin());
            db.insert("user",null,values);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<User> getAllUsers(){
        Cursor cursor= db.rawQuery("select * from user", null);

        ArrayList<User> list = new ArrayList<>();

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            int count = 0;
            do {
                User user;
                String stringUser = cursor.getString(2);
                user = mGson.fromJson(stringUser,User.class);
                user.set_id(cursor.getInt(0));
                list.add(user);
                Log.e(TAG, "count :" +count);
                count++;
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<User> getUserByLogin(String login){

        String[] a = new String[1];
        a[0]       = '%'+login +'%';
        Cursor cursor = db.rawQuery("select * from user where login LIKE '?'", a);
//        String query = "select * from user where registration like '%" +registration+ "%';";
//        Log.w(TAG,query);
//        Cursor cursor= db.rawQuery(query, null);
        ArrayList<User> list = new ArrayList<>();

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                User user = new User();
                String stringUser = cursor.getString(2);
                user = mGson.fromJson(stringUser,User.class);
                user.set_id(cursor.getInt(0));
                list.add(user);

            }while (cursor.moveToNext());
        }
        cursor.close();
        Log.e(TAG, list.toString());
        return list;
    }

    public boolean deleteOneUSer(int id){

        String where = "_id = ?  ";
        String[] whereArgs = {String.valueOf(id)};
        db.delete("user",where, whereArgs);

        return true;
    }

    public boolean updateOneUser(User user){
        ContentValues values = new ContentValues();
        values.put("user_json", new Gson().toJson(user));
        String where;
        where = "_id = ?";
        String[] whereArgs = {String.valueOf(user.get_id())};
        return db.update("user", values, where, whereArgs) > 0;
    }

    public boolean insertResiduo(Residuo resid){
        try {
            JSONObject joResid = new JSONObject(mGson.toJson(resid));
            ContentValues values = new ContentValues();
            values.put("residuo_json", joResid.toString());
            values.put("residuo",resid.getNome());
            db.insert("residuos",null,values);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Residuo> getResiduos() {

        Cursor cursor = db.rawQuery("select * from residuos", null);

        ArrayList<Residuo> list = null;

        if(cursor.getCount()>0){

            list = new ArrayList<>();

            cursor.moveToFirst();
            int count = 0;
            do {

                Residuo resid;
                String stringResid = cursor.getString(2);
                resid = mGson.fromJson(stringResid,Residuo.class);

                resid.setIdResiduo(cursor.getInt(0));

                resid.setNome(cursor.getString(1));

                resid.setDescricao(cursor.getString(2));

                resid.setCor(cursor.getString(3));

                resid.setPesoPontuacao(cursor.getString(4));

                list.add(resid);

                Log.e(TAG, "count :" +count);
                count++;

            }while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

}
