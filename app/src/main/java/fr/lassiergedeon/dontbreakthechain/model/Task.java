package fr.lassiergedeon.dontbreakthechain.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.lassiergedeon.dontbreakthechain.DBOpenHelper;

/**
 * Created by Antoine on 18/03/2015.
 */
public class Task {
    private int id;
    private String title;
    private Calendar notificationHour;
    private URI ringToneURI;

    public Task() {}

    public Task(String title, Calendar notificationHour, URI ringToneURI) {
        super();
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public Task(int id, String title, Calendar notificationHour, URI ringToneURI) {
        super();
        this.id = id;
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public void markDayComplete(DBOpenHelper dbo){
        Chain c = this.getCurrentChain(dbo);
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/y");
        Date derniereDate = null;
        try {
            derniereDate = sdf.parse(c.getLastDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dateActuel = new Date();
        String s = dateActuel.getDate()+"/"+( 1 + dateActuel.getMonth())+"/"+(1900 + dateActuel.getYear());
        if( (int)( (dateActuel.getTime() - derniereDate.getTime()) / (1000 * 60 * 60 * 24)) > 1 ) {
            dbo.addChain(new Chain(this.id, s, s));
        }else{
            c.setLastDate(s);
            dbo.updateChain(c);
        }

    }

    public ArrayList<Chain> getChains(DBOpenHelper dbo){
        ArrayList<Chain> chains = new ArrayList<Chain>();

        SQLiteDatabase db = dbo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chain WHERE idT = "+this.id, null);

        Chain chain = null;
        if (cursor.moveToFirst()) {
            do {
                chain = new Chain(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
                chains.add(chain);
            } while (cursor.moveToNext());
        }

        cursor.close();
        Log.d("getAllChains()", chains.toString());

        return chains;
    }

    public Chain getCurrentChain(DBOpenHelper dbo){
        SQLiteDatabase db = dbo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chain where idT = "+ this.id
                +" and idC = (select MAX(idC) from chain where idT = "+this.id+" ) ", null);
        Chain c = null;
        if (cursor.moveToFirst()) {
            c = new Chain(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        }
        return c;
    }

    /**
     * @param dbo
     * @return le nombre de jours de la chaine actuelle
     */
    public int getCurrentConsecutiveDays(DBOpenHelper dbo){
        SQLiteDatabase db = dbo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chain where idT = "+ this.id
                +" and idC = (select MAX(idC) from chain where idT = "+this.id+" ) ", null);
        int nbJours = 0;
        if (cursor.moveToFirst()) {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/y");
            try {
                Date dd = sdf.parse(cursor.getString(2));
                Date df = sdf.parse(cursor.getString(3));
                nbJours = (int)( (df.getTime() - dd.getTime()) / (1000 * 60 * 60 * 24));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.d("getConsecutiveDays(", nbJours+"");
        return nbJours;
    }

    /**
     * @param dbo
     * @return la plus longue chaine
     */
    public Chain getLongestChain(DBOpenHelper dbo){
        SQLiteDatabase db = dbo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chain where idT = ?", new String[]{""+this.id});
        int idCLongest = -1;
        long longestChain = 0;
        if (cursor.moveToFirst()) {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/y");
            do {
                Calendar calendarD = Calendar.getInstance();
                Calendar calendarF = Calendar.getInstance();
                try {
                    calendarD.setTime(sdf.parse(cursor.getString(2)));
                    calendarF.setTime(sdf.parse(cursor.getString(3)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Log.d("getLongestChain()", calendarF.getTimeInMillis() - calendarD.getTimeInMillis()+"" );
                if( longestChain < calendarF.getTimeInMillis() - calendarD.getTimeInMillis() ){
                    longestChain = calendarF.getTimeInMillis() - calendarD.getTimeInMillis();
                    idCLongest = cursor.getInt(0);
                }
            } while (cursor.moveToNext());
        }
        Log.d("getLongestChain()", idCLongest+"" );
        return dbo.getChain(idCLongest);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(Calendar notificationHour) {
        this.notificationHour = notificationHour;
    }

    public URI getRingToneURI() {
        return ringToneURI;
    }

    public void setRingToneURI(URI ringToneURI) {
        this.ringToneURI = ringToneURI;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", notificationHour=" + notificationHour +
                ", ringToneURI=" + ringToneURI +
                '}';
    }
}
