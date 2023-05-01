package com.example.teachmeio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

public class DBHelper extends SQLiteOpenHelper {

    /** SQL_lite database definition */
    public static final String DATABASE_NAME = "verbs.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "verbs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ENGLISH = "english";
    public static final String COLUMN_FRENCH = "french";
    public static final String COLUMN_PRETERIT = "preterit";
    public static final String COLUMN_PAST_P = "past_p";
    public static final String COLUMN_SELECTED = "selected";
    public static final String COLUMN_NB_FAILS = "nb_fails";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

        @Override
    public void onCreate(SQLiteDatabase db) {
        // on first instantiation, create the defined database.
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ENGLISH + " TEXT," +
                COLUMN_FRENCH + " TEXT," +
                COLUMN_PRETERIT + " TEXT," +
                COLUMN_PAST_P + " TEXT," +
                COLUMN_SELECTED + " INTEGER," +
                COLUMN_NB_FAILS + " INTEGER" +
                ")");
    }

        @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public int getVerbsCount() {
        // get a readable snapshot from the database to not block database access
        SQLiteDatabase db = this.getReadableDatabase();
        // use sql aggregation to get the count value
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        // exec the query and access the data
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        // return the count.
        return count;
    }

    public int getSelectedCount(){
        // get the verbs selected count.
        int result = 0;

        // TODO: convert the arraylist to an array to not quadratically traverse the list. Index based access on arrays
        //  is faster then the get method. (performance)
        ArrayList<Verbs> res = getVerbs();
        for(int i = 0 ; i  < res.size() ; ++i)
            if(res.get(i).selected) result++;

        return result;
    }

    public ArrayList<Verbs> get_selected_ordered(){
            int nb_verb = getVerbsCount();
            Verbs tmp;

            // TODO: convert the arraylist to an array to not quadratically traverse the list. Index based access on arrays
            //  is faster then the get method. (performance)
            ArrayList<Verbs> res = new ArrayList<Verbs>();
            for(int i = 0 ; i < nb_verb ; ++i) {
                tmp = getVerb(i + 1);
                tmp.id = i + 1;
                if (tmp.selected) {
                    res.add(tmp);
                }
            }
            // sort list
            Collections.sort(res, new VerbComparator());

            return res;
        }

    public ArrayList<Integer> get_selected_ordered_id(){
        // construct a list of verbs ids in an order based on fails count ( high to low )

        // TODO: convert the arraylist to an array to not quadratically traverse the list. Index based access on arrays
        //  is faster then the get method. (performance)
        ArrayList<Verbs> res = getVerbs();
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for(int i = 0 ; i < res.size() ; ++i)
            if(res.get(i).selected)
                ids.add(res.get(i).id);

        return ids;
    }

    // Insertion of a verb into the database
    public void insertVerb(Verbs v) {
        // populate the new record values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENGLISH, v.english);
        values.put(COLUMN_FRENCH, v.french);
        values.put(COLUMN_PRETERIT, v.preterit);
        values.put(COLUMN_PAST_P, v.past_p);
        values.put(COLUMN_NB_FAILS, v.nb_fails);
        values.put(COLUMN_SELECTED, v.selected);

        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME,null ,values);
    }

    public void insertVerbs(Verbs[] verbsArray){

        // construct the bulk insert query.
        String query = "INSERT INTO "
                + TABLE_NAME
                +"(" +
                COLUMN_ENGLISH + " , "+
                COLUMN_FRENCH + " , " +
                COLUMN_PRETERIT + " , " +
                COLUMN_PAST_P + " , " +
                COLUMN_NB_FAILS + " , " +
                COLUMN_SELECTED + "  " +
                ")"
                + " VALUES ";


        // loop through the verbsArray and inject their info to the bulk sql query.
        int limit = verbsArray.length;
        for (int i = 0; i < limit  ; i++) {

            query +=
                    " ( '" +
                            verbsArray[i].english + "' , '" +
                            verbsArray[i].french.replace("'","-")  + "' , '" +
                            verbsArray[i].preterit + "' , '" +
                            verbsArray[i].past_p + "' , " +
                            verbsArray[i].nb_fails + " , " +
                            verbsArray[i].selected + "  ";

            if(i < limit - 1){
                query += " ), ";
            } else{
                query +=" ); ";
            }
        }


        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


        // get a verb by its ID
    public Verbs getVerb(int id) {
        // request a readable snapshot of db
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare the sql command.
        String[] columns = {COLUMN_ID, COLUMN_ENGLISH, COLUMN_FRENCH, COLUMN_PRETERIT, COLUMN_PAST_P, COLUMN_NB_FAILS, COLUMN_SELECTED};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };


        // execute the query.
        Cursor cursor = db.query(TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);



        // populate the verb fields before instantiating it.
        Verbs verb = null;
        if (cursor.moveToFirst()) {
            String english = "", french = "", preterit = "", past_p = "";
            int nb_fails = -1;
            boolean selected = false;

            int eng_id = cursor.getColumnIndex(COLUMN_ENGLISH);
            int fr_id = cursor.getColumnIndex(COLUMN_FRENCH);
            int pre_id = cursor.getColumnIndex(COLUMN_PRETERIT);
            int past_p_id = cursor.getColumnIndex(COLUMN_PAST_P);
            int fails_id = cursor.getColumnIndex(COLUMN_NB_FAILS);
            int sel_id = cursor.getColumnIndex(COLUMN_SELECTED);
            if(eng_id != -1 && fr_id != -1 && pre_id != -1 && past_p_id != -1 && fails_id != -1 && sel_id != -1) {
                english = cursor.getString(eng_id);
                french = cursor.getString(fr_id).replace("-","'");
                preterit = cursor.getString(pre_id);
                past_p = cursor.getString(past_p_id);
                nb_fails = cursor.getInt(fails_id);
                selected = cursor.getInt(sel_id) == 1;
            }else{
                System.out.println("error on column names");
                cursor.close();
            }
            if(english.length() < 2 || french.length() < 2 || preterit.length() < 2 || past_p.length() < 2 || nb_fails == -1){
                System.out.println("error on getting values for the verb with id = " + id);
                cursor.close();
            }


            verb = new Verbs( french,english, preterit, past_p, nb_fails, selected);
        }

        cursor.close();
        db.close();
        return verb;
    }

    public ArrayList<Verbs> getVerbs() {
        // request a readable snapshot of db
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare the columns query.
        String[] columns = {COLUMN_ID, COLUMN_ENGLISH, COLUMN_FRENCH, COLUMN_PRETERIT, COLUMN_PAST_P, COLUMN_NB_FAILS, COLUMN_SELECTED};

        // create a new verbsList
        ArrayList<Verbs> verbsList = new ArrayList<>();

        // exec the sql query.
        Cursor cursor = db.query(TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null); // retrieve cursor from database

        // collect the returned data and append the newly created verbs to the list.
        if (cursor.moveToFirst()) {
            int id = 1;
            do {
                String english = "", french = "", preterit = "", past_p = "";
                int nb_fails = -1;
                boolean selected = false;

                int eng_id = cursor.getColumnIndex(COLUMN_ENGLISH);
                int fr_id = cursor.getColumnIndex(COLUMN_FRENCH);
                int pre_id = cursor.getColumnIndex(COLUMN_PRETERIT);
                int past_p_id = cursor.getColumnIndex(COLUMN_PAST_P);
                int fails_id = cursor.getColumnIndex(COLUMN_NB_FAILS);
                int sel_id = cursor.getColumnIndex(COLUMN_SELECTED);
                if(eng_id != -1 && fr_id != -1 && pre_id != -1 && past_p_id != -1 && fails_id != -1 && sel_id != -1) {
                    english = cursor.getString(eng_id);
                    french = cursor.getString(fr_id).replace("-","'");
                    preterit = cursor.getString(pre_id);
                    past_p = cursor.getString(past_p_id);
                    nb_fails = cursor.getInt(fails_id);
                    selected = cursor.getInt(sel_id) == 1;
                }else{
                    System.out.println("error on column names");
                    cursor.close();
                    return verbsList;
                }
                if(english.length() < 2 || french.length() < 2 || preterit.length() < 2 || past_p.length() < 2 || nb_fails == -1){
                    System.out.println("error on getting values for the verb with id = " + id);
                    cursor.close();
                    return verbsList;
                }
                Verbs verb = new Verbs(french, english, preterit, past_p, nb_fails, selected);
                verb.id = id;
                // System.out.println(id + " >>> " + verb);
                verbsList.add(verb);
                id++;
                cursor.moveToNext();
            } while (id < available_verbs_view.NB_VERBS + 1);
        }
        cursor.close();
        db.close();
        return verbsList;
    }


    public int getVerbIdByEnglishString(String englishString) {

        // request a readable snapshot of the database.
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare the query.
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_ENGLISH + " LIKE ?";
        String[] selectionArgs = {"%" + englishString + "%"};

        // exec query and catch the results
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        int verbId = -1;
        int verb_id_query = -1;
        if (cursor.moveToFirst()) {
            verb_id_query = cursor.getColumnIndex(COLUMN_ID);
            if(verb_id_query == -1){
                System.out.println("error on column names");
                cursor.close();
            }else {
                verbId = cursor.getInt(verb_id_query);
            }
        }

        cursor.close();
        db.close();
        return verbId;
    }

    public int getVerbIdByFrenchString(String frenchString) {

        // request a readable snapshot of the database.
        SQLiteDatabase db = this.getReadableDatabase();

        // prepare the query.
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_FRENCH + " LIKE ?";
        String[] selectionArgs = {"%" + frenchString + "%"};

        // exec query and catch the results
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        int verbId = -1;
        int verb_id_query = -1;
        if (cursor.moveToFirst()) {
            verb_id_query = cursor.getColumnIndex(COLUMN_ID);
            if(verb_id_query == -1){
                System.out.println("error on column names");
                cursor.close();
            }else {
                verbId = cursor.getInt(verb_id_query);
            }
        }
        cursor.close();
        db.close();
        return verbId;
    }

    public void updateSelected(int id, boolean selected) {

        // construct the update query new values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_SELECTED, selected ? 1 : 0);

        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void increment_fails(int id){
        // construct our sql query.
        String sql =
                "UPDATE "+TABLE_NAME+
                " SET "+ COLUMN_NB_FAILS +" = "+ COLUMN_NB_FAILS+ " + 1 "+
                " WHERE "+ COLUMN_ID +" = "+ id +";";

        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void reset_fails(){
        // construct our sql query.
        String sql = "UPDATE "+TABLE_NAME+ " SET "+ COLUMN_NB_FAILS +" = 0;";



        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();

    }

    public void reset_selections(){
        // construct our sql query.
        String sql = "UPDATE "+TABLE_NAME+ " SET "+ COLUMN_SELECTED +" = 0 ;";

        // request a writable state of db to write to it.
        // do it at the end to prevent blocking other processes/threads db access
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


}

