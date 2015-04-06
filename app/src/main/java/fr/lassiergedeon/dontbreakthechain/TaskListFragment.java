package fr.lassiergedeon.dontbreakthechain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import fr.lassiergedeon.dontbreakthechain.adapter.TasksAdapter;
import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by sebastien on 30/03/2015.
 */
public class TaskListFragment extends Fragment {

    private ListView taskListView;
    private TasksAdapter adapter;
    private SwipeActionAdapter tasksAdapter;
    private ButtonFloat addButton;
    DBOpenHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.list_tasks, container, false);

        db = new DBOpenHelper(getActivity());

        final ArrayList<Task> taches = new ArrayList<>(db.getAllTasks());
        adapter = new TasksAdapter(getActivity(), taches);
        tasksAdapter = new SwipeActionAdapter(adapter);
        taskListView = (ListView) view.findViewById(R.id.tasksListView);
        tasksAdapter.setListView(taskListView);
        taskListView.setAdapter(tasksAdapter);
        tasksAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.task_item_bg_left)
                    .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.task_item_bg_right)
                    .addBackground(SwipeDirections.DIRECTION_FAR_LEFT, R.layout.task_item_bg_left)
                    .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT, R.layout.task_item_bg_right);

        tasksAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
            @Override
            public boolean hasActions(int position) {
                return true;
            }

            @Override
            public boolean shouldDismiss(int position, int direction) {
                return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
            }

            @Override
            public void onSwipe(int[] positionList, int[] directionList) {
                Log.d("onSwipe()", "swipe");
                for (int i = 0; i < positionList.length; i++) {
                    int direction = directionList[i];
                    int position = positionList[i];
                    String dir = "";

                    switch (direction) {
                        case SwipeDirections.DIRECTION_FAR_LEFT:
                            dir = "far left";
                            break;
                        case SwipeDirections.DIRECTION_FAR_RIGHT:
                            dir = "far right";
                            break;
                        case SwipeDirections.DIRECTION_NORMAL_LEFT:
                            dir = "left";
                            break;
                        case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                            dir = "right";
                            break;
                    }
                    Toast.makeText(getActivity(),
                            dir + " swipe position " + tasksAdapter.getItem(position),
                            Toast.LENGTH_SHORT).show();
                    tasksAdapter.notifyDataSetChanged();
                }
            }
        });

        addButton = (ButtonFloat) view.findViewById(R.id.addButtonFloat);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.taskName);

                final EditText input = new EditText(getActivity());
                builder.setView(input);

                builder.setPositiveButton(R.string.taskAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = input.getText().toString();
                        if (!taskName.equals("")) {
                            Task t = new Task(input.getText().toString().trim(), Calendar.getInstance(), URI.create("unknow"));
                            int id = db.addTask(t);
                            adapter.add(db.getTask(id));
                            tasksAdapter.notifyDataSetChanged();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton(R.string.taskCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetail = new Intent(getActivity().getApplicationContext(), TaskViewerActivity.class);
                showDetail.putExtra("task_id", ((Task) taskListView.getItemAtPosition(position)).getId());
                startActivity(showDetail);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        return view;
    }
}
