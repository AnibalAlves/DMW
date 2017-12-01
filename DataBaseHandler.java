package eu.croussel.sportyfield;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.audiofx.AudioEffect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by afonso on 29-11-2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {


    //Log tag
    private static final String LOG = "DataBaseHandler";

    //Version
    private static final int DATABASE_VERSION = 1;

    //DataBase name
    private static String DATABASE_NAME = "FieldsManager";

    //Tables Names
    private static final String FIELD = "FIELD";
    private static final String DESCR = "DESCR";

    //Primary Keys NAMES
    private static final String KEY_ID = "_id";
    private static final String KEY_NUMBER = "_number";

    //FIELD table columns
    private static final String KEY_LOCATION = "_location";
    private static final String KEY_LATITUDE = "_lat";
    private static final String KEY_LONGITUDE = "_long";
    private static final String KEY_PRIVATE = "_private";
    private static final String KEY_OUTDOOR = "_outdoor";

    //DESCR table columns
    private static final String KEY_DESCRIPTION = "_descr";
    private static final String KEY_FIELDID = "_fieldId";
    private static final String KEY_DATE = "_date";


    //Table create statements
    //fields TABLE
    private static final String CREATE_TABLE_FIELDS = "CREATE TABLE" + FIELD + "(" + KEY_ID + "INTEGER PRIMARY KEY"
            + KEY_LOCATION + "TEXT" + KEY_LATITUDE + "DOUBLE" + KEY_LONGITUDE + "DOUBLE" + KEY_PRIVATE + "BOOLEAN"
            + KEY_OUTDOOR + "BOOLEAN" + ")";
    //descrip table
    private static final String CREATE_TABLE_DESCR = "CREATE TABLE" + DESCR + "(" + KEY_NUMBER + "INTEGER AUTOINCREMENT PRIMARY KEY"
            + KEY_DESCRIPTION + "TEXT" + KEY_FIELDID + "INTEGER" + KEY_DATE + "DATETIME" + ")";


    public DataBaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_FIELDS);
        db.execSQL(CREATE_TABLE_DESCR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_FIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_DESCR);

        // create new tables
        onCreate(db);
    }

    /*
    * Creating a FIELD
    */
    public void createField(field f) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, f.getId());
        values.put(KEY_LOCATION, f.getLocation());
        values.put(KEY_LATITUDE, f.getLat());
        values.put(KEY_LONGITUDE, f.getLong());
        values.put(KEY_PRIVATE, f.getPriv());
        values.put(KEY_OUTDOOR, f.getOut());

        // insert row
        db.insert(CREATE_TABLE_FIELDS, null, values);
    }

    public field getField(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + FIELD + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        field f = new field();
        f.setId(id);
        f.setLat(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        f.setLong(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        f.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
        int aux = c.getColumnIndex(KEY_PRIVATE);
        if (aux==1)
            f.setPriv(true);
        else
            f.setPriv(false);
        int aux2 = c.getColumnIndex(KEY_OUTDOOR);
        if (aux2==1)
            f.setOut(true);
        else
            f.setOut(false);
        return f;
    }

    /*
    * Creating Descr
    */
    public void createDescr(descri d) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, d.getDescr());
        values.put(KEY_FIELDID, d.getId());
        values.put(KEY_DATE, String.valueOf(Calendar.getInstance().getTime()));

        // insert row
        db.insert(DESCR, null, values);
    }

    /**
     * getting all descr of that field
     * */
    public List<descri> getAllDescri(Integer id) {
        List<descri> description = new ArrayList<descri>();

        String selectQuery = "SELECT  * FROM " + DESCR + "WHERE" + KEY_FIELDID + "=" +id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                descri t = new descri();
                t.setId(id);
                t.setDescr(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                t.setNumber(c.getInt(c.getColumnIndex(KEY_NUMBER)));
                t.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                // adding to description list
                description.add(t);
            } while (c.moveToNext());
        }
        return description;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
