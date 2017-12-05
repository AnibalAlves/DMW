package eu.croussel.sportyfield;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    private static final String USER = "USER";
    private static final String USER_DESCR = "USER_DESCR";

    //Primary Keys NAMES
    private static final String KEY_ID = "_id";
    private static final String KEY_NUMBER = "_number";
    private static final String KEY_USERNAME = "_userName";
    private static final String KEY_EVENTID = "_eventID";

    //FIELD table columns
    private static final String KEY_LOCATION = "_location";
    private static final String KEY_LATITUDE = "_lat";
    private static final String KEY_LONGITUDE = "_long";
    private static final String KEY_PRIVATE = "_private";
    private static final String KEY_OUTDOOR = "_outdoor";
    private static final String KEY_FIELDESCRIPTION = "_description";

    //DESCR table columns
    private static final String KEY_DESCRIPTION = "_descr";
    private static final String KEY_FIELDID = "_fieldId";
    private static final String KEY_DATE = "_date";

    //USER table columns
    private static final String KEY_AGE = "_age";
    private static final String KEY_EMAIL = "_email";
    private static final String KEY_PHONE = "_phone";
    private static final String KEY_REPUTATION = "_reputation";
    private static final String KEY_FAVSPORT = "_favSport";
    private static final String KEY_TYPE = "_type";

    //USER_EVENTS columns
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_DESCR = "descr";

    //Table create statements
    //fields TABLE
    private static final String CREATE_TABLE_FIELDS = "CREATE TABLE " + FIELD + "(" + KEY_ID + " INTEGER UNIQUE PRIMARY KEY, "
            + KEY_LOCATION + " TEXT, " + KEY_LATITUDE + " DOUBLE, " + KEY_LONGITUDE + " DOUBLE, " + KEY_PRIVATE + " BOOLEAN, "
            + KEY_OUTDOOR + " BOOLEAN, " + KEY_FIELDESCRIPTION + " TEXT " + ")";
    //descrip table
    private static final String CREATE_TABLE_DESCR = "CREATE TABLE " + DESCR + "(" + KEY_NUMBER + " INTEGER PRIMARY KEY, "
            + KEY_DESCRIPTION + " TEXT, " + KEY_FIELDID + " INTEGER, " + KEY_DATE + " DATETIME " + ")";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + USER + "(" + KEY_USERNAME + " TEXT PRIMARY KEY, " +
            KEY_AGE + " INTEGER, " + KEY_EMAIL + " TEXT, " + KEY_PHONE + " INTEGER, " + KEY_REPUTATION + " INTEGER, "  + KEY_TYPE + " TEXT, " + KEY_FAVSPORT + " TEXT " + ")";

    private static final String CREATE_TABLE_USER_DESCR = "CREATE TABLE " + USER_DESCR + "(" + KEY_USER_NAME + " TEXT PRIMARY KEY, " + KEY_DESCR
            + " INTEGER " + ")";

    public DataBaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_FIELDS);
        db.execSQL(CREATE_TABLE_DESCR);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_USER_DESCR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + FIELD);
        db.execSQL("DROP TABLE IF EXISTS " + DESCR);
        db.execSQL("DROP TABLE IF EXISTS " + USER);
        db.execSQL("DROP TABLE IF EXISTS " + USER_DESCR);

        // create new tables
        onCreate(db);
    }

    /*
    * Creating a FIELD
    */
    public void createField(Field f) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION, f.getLocation());
        values.put(KEY_LATITUDE, f.getLat());
        values.put(KEY_LONGITUDE, f.getLong());
        values.put(KEY_PRIVATE, f.getPriv());
        values.put(KEY_OUTDOOR, f.getOut());
        values.put(KEY_ID, f.getId());
        values.put(KEY_FIELDESCRIPTION, f.getDescription());
        // insert row
        db.insertWithOnConflict(FIELD, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Field getField(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + FIELD + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Field f = new Field();
        f.setId(id);
        f.setLat(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
        f.setLong(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
        f.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
        f.setDescription(c.getString(c.getColumnIndex(KEY_FIELDESCRIPTION)));
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
    public void createDescr(Report d) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, d.getDescr());
        values.put(KEY_FIELDID, d.getId());
        values.put(KEY_DATE, String.valueOf(Calendar.getInstance().getTime()));
        values.put(KEY_NUMBER, d.getNumber());
        // insert row
        db.insertWithOnConflict(DESCR, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * getting all descr of that Field
     * */
    public List<Report> getAllDescri(int i) {
        List<Report> description = new ArrayList<Report>();

        String selectQuery = "SELECT  * FROM " + DESCR + " WHERE " + KEY_FIELDID + " = " + i;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Report t = new Report();
                t.setId(c.getInt(c.getColumnIndex(KEY_FIELDID)));
                t.setDescr(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                t.setNumber(c.getInt(c.getColumnIndex(KEY_NUMBER)));
                t.setDate(c.getString(c.getColumnIndex(KEY_DATE)));

                // adding to description list
                description.add(t);
            } while (c.moveToNext());
        }
        return description;
    }

    /*
    * Creating a USER
    */
    public void createUser(User u) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE,u.getType());
        values.put(KEY_USERNAME, u.getUserName());
        values.put(KEY_AGE, u.getAge());
        values.put(KEY_EMAIL, u.getEmail());
        values.put(KEY_PHONE, u.getPhone());
        values.put(KEY_REPUTATION, u.getReputatio());
        values.put(KEY_FAVSPORT, u.getFavSport());

        // insert row
        db.insertWithOnConflict(USER, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public User getUser(String uName)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + USER + " WHERE "
                + KEY_USERNAME + " = '" + uName + "'";
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        User u = new User();
        u.setUserName(uName);
        u.setAge(c.getInt(c.getColumnIndex(KEY_AGE)));
        u.setEmail(c.getString(c.getColumnIndex(KEY_EMAIL)));
        u.setReputation(c.getInt(c.getColumnIndex(KEY_REPUTATION)));
        u.setFavSport(c.getString(c.getColumnIndex(KEY_FAVSPORT)));
        u.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
        return u;
    }

    public int updateUser(User u)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AGE, u.getAge());
        values.put(KEY_EMAIL, u.getEmail());
        values.put(KEY_PHONE, u.getPhone());
        values.put(KEY_REPUTATION, u.getReputatio());
        values.put(KEY_FAVSPORT, u.getFavSport());
        values.put(KEY_TYPE, u.getType());

        // updating row
        return db.update(USER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(u.getUserName()) });
    }

    /*
    * Deleting a User
    */
    public void deleteUser(String uName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER, KEY_USERNAME + " = ?",
                new String[] {uName});
    }

    /*
    * Creating a USER_DESCR
    */
    public void createUserDescr(String uName, int desc_n) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, uName);
        values.put(KEY_AGE, desc_n);
        // insert row
        db.insertWithOnConflict(USER_DESCR, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    /*
 * Delete a USER_DESCR
 */
    public int deleteUserDescr(String uName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // updating row
        return db.delete(USER_DESCR,KEY_ID + " = ?",
                new String[] {uName});
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
