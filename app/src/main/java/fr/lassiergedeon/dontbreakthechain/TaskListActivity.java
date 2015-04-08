package fr.lassiergedeon.dontbreakthechain;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.lassiergedeon.dontbreakthechain.model.Chain;
import fr.lassiergedeon.dontbreakthechain.model.Task;



public class TaskListActivity extends ActionBarActivity {

    DBOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        /*if (savedInstanceState == null) {
            Fragment f = new TaskListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }*/

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        db = new DBOpenHelper(this);
        //int id = db.addTask(new Task("Faire du sport"));
        //db.addChain(new Chain(id, "06/09/2014", "07/04/2015"));
        //db.addChain(new Chain(253, "12/03/2015", "30/03/2015"));
        //db.addChain(new Chain(253, "02/04/2015", "05/04/2015"));

        /*for (Task t : db.getAllTasks()) {
            db.deleteTask(t);
        }

        db.addTask(new Task("test1", Calendar.getInstance(), URI.create("blabla")));
        db.addTask(new Task("test2", Calendar.getInstance(), URI.create("unautre")));
        db.addTask(new Task("test3", Calendar.getInstance(), URI.create("opopop")));

        List<Task> tasks = db.getAllTasks();

        db.deleteTask(tasks.get(0));

        db.getAllTasks();

        db.addChain(new Chain(1, "20/10/2014", "05/12/2014"));
        db.addChain(new Chain(2, "03/08/2014", "12/11/2014"));
        db.addChain(new Chain(2, "01/01/2015", "12/02/2015"));
        db.addChain(new Chain(1, "15/12/2014", "23/03/2015"));

        List<Chain> chains = db.getAllChains();

        db.deleteChain(chains.get(0));

        db.getAllChains();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }
}
