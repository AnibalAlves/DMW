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
    private static final String IDMAX = "ID_MAX";

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
    private static final String KEY_IMAGE = "_image";

    //DESCR table columns
    private static final String KEY_DESCRIPTION = "_descr";
    private static final String KEY_FIELDID = "_fieldId";
    private static final String KEY_DATE = "_date";
    private static final String KEY_UNAMEREPORT = "_userName";
    private static final String KEY_REPIMAGE = "_reportImage";

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

    //MAX IDs
    private static final String KEY_IDMAXFIELD = "idMax";

    //Table create statements
    //fields TABLE
    private static final String CREATE_TABLE_FIELDS = "CREATE TABLE " + FIELD + "(" + KEY_ID + " INTEGER UNIQUE PRIMARY KEY, "
            + KEY_LOCATION + " TEXT, " + KEY_LATITUDE + " DOUBLE, " + KEY_LONGITUDE + " DOUBLE, " + KEY_PRIVATE + " BOOLEAN, "
            + KEY_OUTDOOR + " BOOLEAN, " + KEY_FIELDESCRIPTION + " TEXT, " + KEY_IMAGE + " BLOB" + ")";
    //descrip table
    private static final String CREATE_TABLE_DESCR = "CREATE TABLE " + DESCR + "(" + KEY_NUMBER + " INTEGER PRIMARY KEY, "
            + KEY_DESCRIPTION + " TEXT, " + KEY_REPIMAGE + " BLOB, " + KEY_UNAMEREPORT + " TEXT, " + KEY_FIELDID + " INTEGER, " + KEY_DATE + " DATETIME " + ")";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + USER + "(" + KEY_USERNAME + " TEXT PRIMARY KEY, " +
            KEY_AGE + " INTEGER, " + KEY_EMAIL + " TEXT, " + KEY_PHONE + " INTEGER, " + KEY_REPUTATION + " INTEGER, "  + KEY_TYPE + " TEXT, " + KEY_FAVSPORT + " TEXT " + ")";

    private static final String CREATE_TABLE_USER_DESCR = "CREATE TABLE " + USER_DESCR + "(" + KEY_USER_NAME + " TEXT PRIMARY KEY, " + KEY_DESCR
            + " INTEGER " + ")";

    private static final String CREATE_TABLE_IDMAX = "CREATE TABLE " + IDMAX + "(" + KEY_IDMAXFIELD + " INTEGER)";

    public DataBaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public int fieldIdMax = 1;
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_FIELDS);
        db.execSQL(CREATE_TABLE_DESCR);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_USER_DESCR);
        db.execSQL(CREATE_TABLE_IDMAX);
        init_IdMax(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + FIELD);
        db.execSQL("DROP TABLE IF EXISTS " + DESCR);
        db.execSQL("DROP TABLE IF EXISTS " + USER);
        db.execSQL("DROP TABLE IF EXISTS " + USER_DESCR);
        db.execSQL("DROP TABLE IF EXISTS " + IDMAX);

        // create new tables
        onCreate(db);
    }


    public void clearDb() {
        SQLiteDatabase db = this.getWritableDatabase();
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + FIELD);
        db.execSQL("DROP TABLE IF EXISTS " + DESCR);
        db.execSQL("DROP TABLE IF EXISTS " + USER);
        db.execSQL("DROP TABLE IF EXISTS " + USER_DESCR);
        db.execSQL("DROP TABLE IF EXISTS " + IDMAX);
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
        values.put(KEY_ID, updateFieldIdMax());
        values.put(KEY_FIELDESCRIPTION, f.getDescription());
        values.put(KEY_IMAGE, f.getImage());
        // insert row
        db.insertWithOnConflict(FIELD, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void init_IdMax(SQLiteDatabase db){
//        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDMAXFIELD, 2);
        // insert row
        db.insertWithOnConflict(IDMAX, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    private int getFieldIdMax(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM "+ IDMAX;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        return c.getInt(c.getColumnIndex(KEY_IDMAXFIELD));
    }

    private int updateFieldIdMax(){
        SQLiteDatabase db = this.getWritableDatabase();
        int newId = getFieldIdMax() + 1;
        String updateQuery = "UPDATE "+ IDMAX +" SET "+ KEY_IDMAXFIELD+" = "+newId ;
        db.execSQL(updateQuery);
        return newId;
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
        f.setPriv(c.getColumnIndex(KEY_PRIVATE) == 1);
        f.setOut(c.getColumnIndex(KEY_OUTDOOR) == 1);
        f.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
        return f;
    }
    /**
     * getting all descr of that Field
     * */
    public List<Field> getAllFields() {
        List<Field> fields = new ArrayList<Field>();

        String selectQuery = "SELECT  * FROM " + FIELD ;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Field f = new Field();
                f.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                f.setLat(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                f.setLong(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                f.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                f.setDescription(c.getString(c.getColumnIndex(KEY_FIELDESCRIPTION)));
                f.setPriv(c.getColumnIndex(KEY_PRIVATE) == 1);
                f.setOut(c.getColumnIndex(KEY_OUTDOOR) == 1);
                f.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
                // adding to description list
                fields.add(f);
            } while (c.moveToNext());
        }
        return fields;
    }
    public List<Field> getAllFieldsWithFilter(Filter filter) {

        String fieldType = filter.getFieldType();
        if(fieldType == null){
            return getAllFields();
        }
        Boolean isOutdoor = filter.getOutdoor();
        Boolean isIndoor = filter.getIndoor();
        Boolean isPrivate = filter.getPrivate();
        Boolean isPublic = filter.getPublic();
        String selectQuery = "SELECT  * FROM " + FIELD + " WHERE "
                + KEY_FIELDESCRIPTION + " = '" + fieldType + "'";
        if(isOutdoor && !isIndoor) selectQuery = selectQuery.concat(" AND " + KEY_OUTDOOR + " = 1");
        if(isIndoor && !isOutdoor) selectQuery = selectQuery.concat(" AND " + KEY_OUTDOOR + " = 0");
        if(isPrivate && !isPublic) selectQuery = selectQuery.concat(" AND " + KEY_PRIVATE + " = 1");
        if(!isPrivate && isPublic) selectQuery = selectQuery.concat(" AND " + KEY_PRIVATE + " = 0");


        List<Field> fields = new ArrayList<Field>();

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Field f = new Field();
                f.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                f.setLat(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
                f.setLong(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
                f.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                f.setDescription(c.getString(c.getColumnIndex(KEY_FIELDESCRIPTION)));
                f.setPriv(c.getColumnIndex(KEY_PRIVATE) == 1);
                f.setOut(c.getColumnIndex(KEY_OUTDOOR) == 1);
                f.setImage(c.getBlob(c.getColumnIndex(KEY_IMAGE)));
                // adding to description list
                fields.add(f);
            } while (c.moveToNext());
        }
        return fields;
    }

    /*
    * Creating Report
    */
    public void createReport(Report d) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, d.getDescr());
        values.put(KEY_FIELDID, d.getId());
        values.put(KEY_DATE, String.valueOf(Calendar.getInstance().getTime()));
        values.put(KEY_UNAMEREPORT,d.getUserName());
        values.put(KEY_REPIMAGE, d.getRepImage());
        // insert row
        db.insertWithOnConflict(DESCR, null, values,SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * getting all Reports of that Field
     * */
    public List<Report> getAllReport(int i) {
        ArrayList<Report> reports = new ArrayList<>();

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
                t.setUserName(c.getString(c.getColumnIndex(KEY_UNAMEREPORT)));
                t.setRepImage(c.getBlob(c.getColumnIndex(KEY_REPIMAGE)));
                // adding to description list
                reports.add(t);
            } while (c.moveToNext());
        }
        return reports;
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
        return db.update(USER, values, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(u.getUserName()) });
    }

    public int updateUserRep(String uName, Integer i)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REPUTATION, i);
        // updating row
        return db.update(USER, values, KEY_USERNAME + " = ?",
                new String[] { String.valueOf(uName) });
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
