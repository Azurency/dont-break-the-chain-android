package fr.lassiergedeon.dontbreakthechain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by Antoine on 18/03/2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "DB";
    private static final String DB_TASKS_TABLE_NAME = "tasks";
    private static final String DB_CHAIN_TABLE_NAME = "chain";

    //private SQLiteDatabase db;

    DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        //db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHAIN_TABLE = "CREATE TABLE " + DB_CHAIN_TABLE_NAME + " ( " +
                "idC INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idT INTEGER, " +
                "firstDate TEXT NOT NULL, " +
                "lastDate TEXT ) ";
        db.execSQL(CREATE_CHAIN_TABLE);

        String CREATE_TASKS_TABLE = "CREATE TABLE " + DB_TASKS_TABLE_NAME + " ( " +
                "idT INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "notificationHour TEXT, " +
                "ringToneURI TEXT ) ";
        db.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_CHAIN_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TASKS_TABLE_NAME);

        this.onCreate(db);
    }

    public void addTask(Task task) {
        Log.d("addTask", task.toString());

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hour = sdf.format(task.getNotificationHour().getTime());
        values.put("notificationHour", hour);
        values.put("ringToneURI", task.getRingToneURI().toString());

        db.insert(DB_TASKS_TABLE_NAME, null, values);
        db.close();
    }

    public Task getTask(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"idT", "title", "notificationHour", "ringToneURI"};

        Cursor cursor = db.query(DB_TASKS_TABLE_NAME,
                columns,
                " idT = ?",
                new String[] { String.valueOf(id) },
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(cursor.getString(2)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Task task = new Task(cursor.getInt(0), cursor.getString(1), calendar, URI.create(cursor.getString(3)));
        cursor.close();

        Log.d("getTask("+id+")", task.toString());

        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DB_TASKS_TABLE_NAME, null);

        Task task = null;
        if (cursor.moveToFirst()) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            do {
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(cursor.getString(2)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                task = new Task(cursor.getInt(0), cursor.getString(1), calendar, URI.create(cursor.getString(3)));

                tasks.add(task);
            } while (cursor.moveToNext());
        }

        Log.d("getAllTasks()", tasks.toString());

        return tasks;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hour = sdf.format(task.getNotificationHour().getTime());
        values.put("notificationHour", hour);
        values.put("ringToneURI", task.getRingToneURI().toString());

        int i = db.update(DB_TASKS_TABLE_NAME,
                values,
                " idT = ? ",
                new String[] { String.valueOf(task.getId()) });

        db.close();
        Log.d("updateTask", task.toString());
        return i;
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(DB_TASKS_TABLE_NAME,
                " idT = ? ",
                new String[] { String.valueOf(task.getId()) });

        db.close();
        Log.d("deleteTask", task.toString());
    }
}