package com.example.teachmeio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

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
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }



    // Insertion of a verb into the database
        public void insertVerb(Verbs v) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ENGLISH, v.english);
            values.put(COLUMN_FRENCH, v.french);
            values.put(COLUMN_PRETERIT, v.preterit);
            values.put(COLUMN_PAST_P, v.past_p);
            values.put(COLUMN_NB_FAILS, v.nb_fails);
            values.put(COLUMN_SELECTED, v.selected);
            db.insert(TABLE_NAME,null ,values);
        }


        // get a verb by its ID
        public Verbs getVerb(int id) {
            SQLiteDatabase db = this.getReadableDatabase();

            String[] columns = {COLUMN_ID, COLUMN_ENGLISH, COLUMN_FRENCH, COLUMN_PRETERIT, COLUMN_PAST_P, COLUMN_NB_FAILS, COLUMN_SELECTED};
            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = { String.valueOf(id) };

            Cursor cursor = db.query(TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);



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
                    french = cursor.getString(fr_id);
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

    public int getVerbIdByEnglishString(String englishString) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_ENGLISH + " LIKE ?";
        String[] selectionArgs = {"%" + englishString + "%"};
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
        return verbId;
    }

    public int getVerbIdByFrenchString(String frenchString) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_FRENCH + " LIKE ?";
        String[] selectionArgs = {"%" + frenchString + "%"};
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
        return verbId;
    }

    public void updateSelected(int id, boolean selected) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SELECTED, selected ? 1 : 0);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }




}

