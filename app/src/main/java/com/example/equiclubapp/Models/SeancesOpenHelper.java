package com.example.equiclubapp.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.equiclubapp.CalendarActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeancesOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "seancesManager";
    private static final String TABLE_SEANCE = "seances";
    private static final String KEY_ID = "id";
    private static final String KEY_GRP = "seance_Grp";
    private static final String KEY_CLIENT = "client";
    private static final String KEY_MONITOR = "monitor";
    private static final String KEY_DATE = "date";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_ISDONE = "isDone";
    private static final String KEY_PAYMENT = "payment";
    private static final String KEY_COMMENT = "payment";

    public SeancesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SEANCE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_GRP + " INTEGER,"
                + KEY_CLIENT + " INTEGER," + KEY_MONITOR + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_DURATION + " INTEGER,"
                + KEY_ISDONE + " INTEGER," + KEY_PAYMENT + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        //Log.e(SeancesOpenHelper.class.getSimpleName(),"CREATE_CONTACTS_TABLE : " + CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEANCE);

        // Create tables again
        onCreate(db);
        //Log.e(SeancesOpenHelper.class.getSimpleName(),"Upgrade_CONTACTS_TABLE");
    }

    // code to add the new contact
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addSeance(Seance seance, String monitor) {
        //Log.e(SeancesOpenHelper.class.getSimpleName()," try addSeance" + seance.getSeanceId());
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("INSERT INTO "+ TABLE_SEANCE + "("+ KEY_ID +"," + KEY_GRP +"," + KEY_CLIENT +","
                + KEY_MONITOR +"," + KEY_DATE +"," + KEY_DURATION +"," + KEY_ISDONE+"," +
                KEY_PAYMENT  +") VALUES ("+seance.getSeanceId() +","+ seance.getSeanceGrpId()
                +","+ seance.getClientId() +",\""+ monitor +"\",\""+
                seance.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME) +"\","+
                seance.getDurationMinut() +","+ ((seance.getDone()) ? 1 : 0)
                +","+ seance.getPaymentId()+")");
        //Log.e(SeancesOpenHelper.class.getSimpleName(),"addSeance");
    }

    // code to get the single contact
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Seance getSeance(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEANCE, new String[] { KEY_ID, KEY_GRP, KEY_CLIENT,
                        KEY_MONITOR, KEY_DATE, KEY_DURATION, KEY_ISDONE, KEY_PAYMENT,KEY_COMMENT },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Seance seance = new Seance(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)),
                Integer.parseInt(cursor.getString(2)), 0,
                LocalDateTime.parse(cursor.getString(4), DateTimeFormatter.ISO_DATE_TIME),
                Integer.parseInt(cursor.getString(5)),
                (Integer.parseInt(cursor.getString(6)) == 1),
                Integer.parseInt(cursor.getString(7)), cursor.getString(3));
        // return contact
        return seance;
    }

    // code to get all contacts in a list view
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Seance> getAllSeances() {
        List<Seance> seancesList = new ArrayList<Seance>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEANCE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Seance seance = new Seance();
                seance.setSeanceId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                seance.setSeanceGrpId(cursor.getInt(cursor.getColumnIndex(KEY_GRP)));
                seance.setClientId(cursor.getInt(cursor.getColumnIndex(KEY_CLIENT)));
                seance.setComments(cursor.getString(cursor.getColumnIndex(KEY_MONITOR)));
                seance.setStartDate(LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(KEY_DATE)), DateTimeFormatter.ISO_DATE_TIME));
                seance.setDurationMinut(cursor.getInt(cursor.getColumnIndex(KEY_DURATION)));
                seance.setDone((cursor.getInt(cursor.getColumnIndex(KEY_ISDONE)) == 1));
                seance.setPaymentId(cursor.getInt(cursor.getColumnIndex(KEY_PAYMENT)));

                // Adding contact to list
                seancesList.add(seance);
            } while (cursor.moveToNext());
        }

        // return contact list
        return seancesList;
    }

    //Seance par mois et annee du client
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Seance> getAllSeances(int id, int mois, int annee) {
        List<Seance> seancesList = new ArrayList<Seance>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEANCE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int idClient = cursor.getInt(cursor.getColumnIndex(KEY_CLIENT));
                LocalDateTime startDate = LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(KEY_DATE)), DateTimeFormatter.ISO_DATE_TIME);
                int sMois = startDate.getMonthValue() ;
                int sYear = startDate.getYear();
                if(idClient == id && mois == sMois && annee == sYear) {
                    Seance seance = new Seance();
                    seance.setSeanceId(idClient);
                    seance.setSeanceGrpId(cursor.getInt(cursor.getColumnIndex(KEY_GRP)));
                    seance.setClientId(cursor.getInt(cursor.getColumnIndex(KEY_CLIENT)));
                    seance.setComments(cursor.getString(cursor.getColumnIndex(KEY_MONITOR)));
                    seance.setStartDate(startDate);
                    seance.setDurationMinut(cursor.getInt(cursor.getColumnIndex(KEY_DURATION)));
                    seance.setDone((cursor.getInt(cursor.getColumnIndex(KEY_ISDONE)) == 1));
                    seance.setPaymentId(cursor.getInt(cursor.getColumnIndex(KEY_PAYMENT)));

                    seancesList.add(seance);
                }
            } while (cursor.moveToNext());
        }

        // return contact list
        //Log.e(SeancesOpenHelper.class.getSimpleName(),"getAllSeances mounth year : " + seancesList);
        return seancesList;
    }

    // code to update the single contact
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int updateSeance(Seance seance, String monitor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GRP, seance.getSeanceGrpId());
        values.put(KEY_CLIENT, seance.getClientId());
        values.put(KEY_MONITOR, monitor);
        values.put(KEY_DATE, seance.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME));
        values.put(KEY_DURATION, seance.getDurationMinut());
        values.put(KEY_ISDONE, (seance.getDone()) ? 1 : 0);
        values.put(KEY_PAYMENT, seance.getPaymentId());

        // updating row
        return db.update(TABLE_SEANCE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(seance.getSeanceId()) });
    }

    // Deleting single contact
    public void deleteSeance(Seance seance) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEANCE, KEY_ID + " = ?",
                new String[] { String.valueOf(seance.getSeanceId()) });
        db.close();
    }

    // Getting contacts Count
    public int getSeancesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SEANCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
