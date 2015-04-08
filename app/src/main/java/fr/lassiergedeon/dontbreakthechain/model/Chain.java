package fr.lassiergedeon.dontbreakthechain.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Antoine on 18/03/2015.
 */
public class Chain {
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("d/M/y");
    private int id;
    private int idTask;
    private String firstDate;
    private String lastDate;

    public Chain(int idTask, String firstDate, String lastDate) {
        this.idTask = idTask;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    public Chain(int id, int idTask, String firstDate, String lastDate) {
        this.id = id;
        this.idTask = idTask;
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    public Chain(int id, int idTask, String firstDate) {
        this.id = id;
        this.idTask = idTask;
        this.firstDate = firstDate;
        this.lastDate = "";
    }

    public int getNbJours(){
        int nbJours = 0;
        try {
            Date dd = DATE_FORMATTER.parse(firstDate);
            Date df = DATE_FORMATTER.parse(lastDate);
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
        return nbJours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public List<Date> getDateList() {
        List<Date> dates = new ArrayList<>();

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = DATE_FORMATTER.parse(firstDate);
            date2 = DATE_FORMATTER.parse(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }

        return dates;
    }

    @Override
    public String toString() {
        return "Chain{" +
                "id=" + id +
                ", idTask=" + idTask +
                ", firstDate='" + firstDate + '\'' +
                ", lastDate='" + lastDate + '\'' +
                '}';
    }
}
