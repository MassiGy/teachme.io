package com.example.teachmeio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.teachmeio.TabVerb;
import com.example.teachmeio.Verbs;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "verbs_db";

    // Verbs table name and columns
    private static final String TABLE_VERBS = "verbs";
    private static final String COLUMN_VERB_ID = "verb_id";
    private static final String COLUMN_VERB_FRENCH = "verb_french";
    private static final String COLUMN_VERB_ENGLISH = "verb_english";
    private static final String COLUMN_VERB_PRETERIT = "verb_preterit";
    private static final String COLUMN_VERB_PAST_P = "verb_past_p";
    private static final String COLUMN_VERB_SELECTED = "verb_selected";
    private static final String COLUMN_VERB_FAILS = "verb_fails";

    // Create verbs table query
    private static final String CREATE_VERBS_TABLE = "CREATE TABLE " + TABLE_VERBS + "("
            + COLUMN_VERB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_VERB_FRENCH + " TEXT,"
            + COLUMN_VERB_ENGLISH + " TEXT,"
            + COLUMN_VERB_PRETERIT + " TEXT,"
            + COLUMN_VERB_PAST_P + " TEXT,"
            + COLUMN_VERB_SELECTED + " INTEGER,"
            + COLUMN_VERB_FAILS + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_VERBS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERBS);

        // Create tables again
        onCreate(db);
    }

    // Verbs table CRUD operations
    public void addVerb(Verbs verb) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_VERB_FRENCH, verb.french);
        values.put(COLUMN_VERB_ENGLISH, verb.english);
        values.put(COLUMN_VERB_PRETERIT, verb.preterit);
        values.put(COLUMN_VERB_PAST_P, verb.past_p);
        values.put(COLUMN_VERB_SELECTED, verb.selected ? 1 : 0);
        values.put(COLUMN_VERB_FAILS, verb.nb_fails);

        db.insert(TABLE_VERBS, null, values);
        db.close();
    }

    public Verbs getVerb(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VERBS, new String[] {
                        COLUMN_VERB_ID,
                        COLUMN_VERB_FRENCH,
                        COLUMN_VERB_ENGLISH,
                        COLUMN_VERB_PRETERIT,
                        COLUMN_VERB_PAST_P,
                        COLUMN_VERB_SELECTED,
                        COLUMN_VERB_FAILS
                }, COLUMN_VERB_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Verbs verb = new Verbs(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
        );
        verb.selected = cursor.getInt(5) == 1;
        verb.nb_fails = cursor.getInt(6);

        cursor.close();
        db.close();

        return verb;
    }

    // Getting all verbs
    public List<Verbs> getAllVerbs() {
        List<Verbs> verbList = new ArrayList<>();

        // Select all query
        String selectQuery = "SELECT  * FROM " + TabVerb.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to the list
        if (cursor.moveToFirst()) {
            do {
                Verbs verb = new Verbs();
               // verb.setId(cursor.getInt(cursor.getColumnIndex(Verbs.COLUMN_ID)));
                verb.french = cursor.getString(cursor.getColumnIndex(TabVerb.COLUMN_FRENCH));
                verb.english = cursor.getString(cursor.getColumnIndex(TabVerb.COLUMN_ENGLISH));
                verb.preterit = cursor.getString(cursor.getColumnIndex(TabVerb.COLUMN_PRETERIT));
                verb.past_p = cursor.getString(cursor.getColumnIndex(TabVerb.COLUMN_PAST_P));
                verb.nb_fails = cursor.getInt(cursor.getColumnIndex(TabVerb.COLUMN_NUMBER_FAILS));
                verb.selected = cursor.getInt(cursor.getColumnIndex(TabVerb.COLUMN_SELECTED)) == 1;

                verbList.add(verb);
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        cursor.close();
        db.close();

        // Return verb list
        return verbList;
    }
}

