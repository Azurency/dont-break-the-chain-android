package fr.lassiergedeon.dontbreakthechain;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by Antoine on 06/04/2015.
 */
public class TaskViewerFragment extends Fragment {

    private Task currentTask;
    private DBOpenHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_task, container, false);

        // on récupère l'id de la tâche passé avec le putExtra
        Intent launchingIntent = getActivity().getIntent();
        int task_id = launchingIntent.getIntExtra("task_id", 0);
        ((TextView) view.findViewById(R.id.textView2)).setText("" + task_id);

        // on récupère la tâche associé à l'id
        db = new DBOpenHelper(getActivity());
        List<Task> t = db.getAllTasks();
        currentTask = db.getTask(task_id);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(currentTask.getTitle());

        return view;
    }
}
