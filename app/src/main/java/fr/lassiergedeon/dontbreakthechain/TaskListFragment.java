package fr.lassiergedeon.dontbreakthechain;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
        setHasOptionsMenu(true);

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
                    final int position = positionList[i];
                    final Task task = (Task) tasksAdapter.getItem(position);

                    switch (direction) {
                        case SwipeDirections.DIRECTION_FAR_LEFT:
                        case SwipeDirections.DIRECTION_NORMAL_LEFT:
                            // On supprime la tâche
                            adapter.remove(task);
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getActivity());
                            deleteBuilder.setTitle(R.string.deleteTask)
                                    .setMessage(getActivity().getString(R.string.deleteTaskMessage, task.getTitle()));
                            deleteBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences settings = getActivity().getSharedPreferences("fr.lassiergedeon.dontbreakthechain.TASK_" + task.getId(), getActivity().MODE_PRIVATE);
                                    settings.edit().clear().commit();
                                    db.deleteTask(task);
                                }
                            });
                            deleteBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.insert(task, position);
                                    dialog.cancel();
                                }
                            });
                            deleteBuilder.create().show();
                            break;
                        case SwipeDirections.DIRECTION_FAR_RIGHT:
                        case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                            task.markDayComplete(db);
                            adapter.remove(task);
                            Task new_task = db.getTask(task.getId());
                            adapter.insert(task, position);
                            break;
                    }
                    tasksAdapter.notifyDataSetChanged();
                }
            }
        });

        addButton = (ButtonFloat) view.findViewById(R.id.addButtonFloat);
        addButton.setBackgroundColor(getActivity().getResources().getColor(R.color.accent));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.taskName);

                final EditText input = new EditText(getActivity());
                input.setHint("Ma tâche");
                input.setPadding(30, 30, 30, 30);
                builder.setView(input);

                builder.setPositiveButton(R.string.taskAdd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskName = input.getText().toString();
                        if (!taskName.equals("")) {
                            Task t = new Task(input.getText().toString().trim());
                            int id = db.addTask(t);
                            adapter.add(db.getTask(id));
                            tasksAdapter.notifyDataSetChanged();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disableNotifications:
                for (Task t : db.getAllTasks()) {
                    int taskId = t.getId();
                    Intent intentLocal = new Intent(getActivity().getApplicationContext(), AlarmManagerHelper.class);
                    PendingIntent pendingIntentLocal = PendingIntent.getBroadcast(getActivity().getApplicationContext(), taskId, intentLocal, 0);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntentLocal);
                    pendingIntentLocal.cancel();
                    SharedPreferences settings = getActivity().getSharedPreferences("fr.lassiergedeon.dontbreakthechain.TASK_" + taskId, getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("notification_enabled", false);
                    editor.commit();
                    Toast.makeText(getActivity(), R.string.notificationDisabledText, Toast.LENGTH_SHORT).show();
                    Log.i("disablenotification", taskId + "");
                }
                return true;
            case R.id.action_enableNotifications:
                for (Task t : db.getAllTasks()) {
                    Log.i("enable", "bl bla");
                    int taskId = t.getId();
                    SharedPreferences settings = getActivity().getSharedPreferences("fr.lassiergedeon.dontbreakthechain.TASK_" + taskId, getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("notification_enabled", true);
                    editor.commit();
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity(), AlarmManagerHelper.class);
                    intent.putExtra("ringtone", t.getRingToneURI().toString());
                    intent.putExtra("task_title", t.getTitle());
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), taskId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    Calendar taskCalendar = t.getNotificationHour();
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, taskCalendar.get(Calendar.HOUR_OF_DAY));
                    c.set(Calendar.MINUTE, taskCalendar.get(Calendar.MINUTE));
                    if (c.before(Calendar.getInstance())) {
                        c.add(Calendar.DATE, 1);
                    }
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 24 * 60 * 60 * 1000, alarmIntent);
                    Toast.makeText(getActivity(), R.string.notificationEnabledText, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
