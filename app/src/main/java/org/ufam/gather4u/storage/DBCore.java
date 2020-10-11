package org.ufam.gather4u.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

public class DBCore extends SQLiteOpenHelper {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private Gson mGson;

    public DBCore(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
        this.mGson = new Gson();
    }

//    public DBCore(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//        this.mContext = context;
//        this.mGson = new Gson();
//    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.e(TAG,"on create started");

        sqLiteDatabase.execSQL(
                "create table user(" +
                        "_id integer primary key autoincrement," +
                        "registration text," +
                        "user_json text);" +

                "create table pais(" +
                        "_id integer primary key autoincrement," +
                        "pais text );" +

                "create table estado(" +
                        "_id integer primary key autoincrement," +
                        "estado text," +
                        "idpais integer );" +

                "create table cidade(" +
                        "_id integer primary key autoincrement," +
                        "cidade text," +
                        "idestado integer );" +

                "create table bairro(" +
                        "_id integer primary key autoincrement," +
                        "bairro text);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO: 13/03/17  aqui devem ser executados os backups das tabelas, sempre que houver uma atualização de versão do banco

        Log.w(TAG,"ERROR TRUE NOT OCURRED");
        try{
            sqLiteDatabase.execSQL("drop table if exists user");
            sqLiteDatabase.execSQL("drop table if exists cidade");
            sqLiteDatabase.execSQL("drop table if exists bairro");
        }catch (Exception e){
            e.printStackTrace();
        }
        onCreate(sqLiteDatabase);
        Log.w(TAG,"on upgrade finished");
    }
}
