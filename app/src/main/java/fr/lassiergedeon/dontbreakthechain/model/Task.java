package fr.lassiergedeon.dontbreakthechain.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.lassiergedeon.dontbreakthechain.DBOpenHelper;

/**
 * Created by Antoine on 18/03/2015.
 */
public class Task {

    public static final SimpleDateFormat TASK_NOTIFICATION_DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private int id;
    private String title;
    private Calendar notificationHour;
    private Uri ringToneURI;

    public Task() {}

    public Task(String title) {
        super();
        this.title = title;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        this.notificationHour = c;
        ringToneURI = Settings.System.DEFAULT_NOTIFICATION_URI;
    }

    public Task(String title, Calendar notificationHour, Uri ringToneURI) {
        super();
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public Task(int id, String title, Calendar notificationHour, Uri ringToneURI) {
        super();
        this.id = id;
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public void markDayComplete(DBOpenHelper dbo){
        Chain c = this.getLastChain(dbo);
        Date dateActuel = new Date();
        String s = Chain.DATE_FORMATTER.format(dateActuel);
        if (hasCurrentChain(dbo)) {
            c.setLastDate(s);
            dbo.updateChain(c);
        } else {
            dbo.addChain(new Chain(this.id, s, s));
        }
    }

    public boolean hasCurrentChain(DBOpenHelper db) {
        Chain lastChain = getLastChain(db);
        if (lastChain == null)
            return false;
        Date dateActuelle = new Date();
        Date derniereDate = null;
        try {
            derniereDate = Chain.DATE_FORMATTER.parse(lastChain.getLastDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ((int) ((dateActuelle.getTime() - derniereDate.getTime()) / (1000 * 60 * 60 * 24)) <= 1);
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

    public Chain getLastChain(DBOpenHelper dbo){
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
        if (!hasCurrentChain(dbo))
            return 0;
        SQLiteDatabase db = dbo.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chain where idT = "+ this.id
                +" and idC = (select MAX(idC) from chain where idT = "+this.id+" ) ", null);
        int nbJours = 0;
        if (cursor.moveToFirst()) {
            SimpleDateFormat sdf = Chain.DATE_FORMATTER;
            try {
                Date dd = sdf.parse(cursor.getString(2));
                Date df = sdf.parse(cursor.getString(3));
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(dd);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(df);

                while (!cal1.after(cal2)) {
                    nbJours++;
                    cal1.add(Calendar.DATE, 1);
                }
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
            SimpleDateFormat sdf = Chain.DATE_FORMATTER;
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
                if( longestChain <= calendarF.getTimeInMillis() - calendarD.getTimeInMillis() ){
                    longestChain = calendarF.getTimeInMillis() - calendarD.getTimeInMillis();
                    idCLongest = cursor.getInt(0);
                }
            } while (cursor.moveToNext());
        }
        Log.d("getLongestChain()", idCLongest+"" );
        if (idCLongest == -1)
            return null;
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

    public String getNotificationHourAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(notificationHour.getTime());
    }

    public Uri getRingToneURI() {
        return ringToneURI;
    }

    public void setRingToneURI(Uri ringToneURI) {
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
