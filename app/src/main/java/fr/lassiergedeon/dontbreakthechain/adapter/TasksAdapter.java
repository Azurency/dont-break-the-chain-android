package fr.lassiergedeon.dontbreakthechain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.lassiergedeon.dontbreakthechain.DBOpenHelper;
import fr.lassiergedeon.dontbreakthechain.R;
import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by Antoine on 06/04/2015.
 */
public class TasksAdapter extends ArrayAdapter<Task> {

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        TextView taskName = (TextView) convertView.findViewById(R.id.taskName);
        TextView consecutiveDays = (TextView) convertView.findViewById(R.id.consecutiveDays);

        DBOpenHelper db = new DBOpenHelper(convertView.getContext());

        taskName.setText(task.getTitle());
        consecutiveDays.setText(task.getCurrentConsecutiveDays(db) + "");

        return convertView;
    }
}
