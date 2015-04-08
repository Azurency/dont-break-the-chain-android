package fr.lassiergedeon.dontbreakthechain;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by Antoine on 07/04/2015.
 */
public class TaskSettingFragment extends Fragment {

    private Task currentTask;
    private DBOpenHelper db;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private boolean hasNotificationEnabled;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_task, container, false);
        setHasOptionsMenu(true);

        // on récupère l'id de la tâche passé avec le putExtra
        Intent launchingIntent = getActivity().getIntent();
        int task_id = launchingIntent.getIntExtra("task_id", 0);

        // on récupère la tâche associé à l'id
        db = new DBOpenHelper(getActivity());
        currentTask = db.getTask(task_id);

        // mise en place du texte dans les informations
        TextView informations = (TextView) view.findViewById(R.id.taskSettingInformation);
        informations.setText(getString(R.string.taskSettingsInformation, currentTask.getTitle()));

        // on créer ou récupère les préférences de la tache
        final SharedPreferences preferences = getActivity().getSharedPreferences("fr.lassiergedeon.dontbreakthechain.TASK_" + task_id, getActivity().MODE_PRIVATE);

        boolean notificationEnabled = preferences.getBoolean("notification_enabled", false);
        hasNotificationEnabled = notificationEnabled;
        final Calendar notificationHour = currentTask.getNotificationHour();

        final TextView notificationHourTextView = (TextView) view.findViewById(R.id.notificationHour);
        notificationHourTextView.setText(Task.TASK_NOTIFICATION_DATE_FORMAT.format(notificationHour.getTime()));

        // on met à jour les champs de préférence
        Switch enabledSwitch = (Switch) view.findViewById(R.id.enableNotificationSwitch);
        enabledSwitch.setChecked(notificationEnabled);
        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notification_enabled", isChecked);
                hasNotificationEnabled = isChecked;
                if (isChecked) {
                    updateAlarmManager();
                } else {
                    cancelAlarmManager();
                }
                editor.commit();
            }
        });

        Button changeNotificationHour = (Button) view.findViewById(R.id.changeHour);
        changeNotificationHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        currentTask.setNotificationHour(c);
                        db.updateTask(currentTask);
                        updateAlarmManager();
                        notificationHourTextView.setText(Task.TASK_NOTIFICATION_DATE_FORMAT.format(c.getTime()));
                    }
                }, notificationHour.get(Calendar.HOUR_OF_DAY), notificationHour.get(Calendar.MINUTE), true);
                timePickerDialog.setTitle(R.string.hourSelect);
                timePickerDialog.show();
            }
        });

        /*TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(notificationHour.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(notificationHour.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                currentTask.setNotificationHour(c);
                db.updateTask(currentTask);
                updateAlarmManager();
            }
        });*/

        Button changeRington = (Button) view.findViewById(R.id.changeRingtone);
        changeRington.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri notificationRingtone = currentTask.getRingToneURI();
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.selectTone));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, notificationRingtone);
                startActivityForResult(intent, 5);
            }
        });

        return view;
    }

    private void updateAlarmManager() {
        if (!hasNotificationEnabled)
            return;
        cancelAlarmManager();
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmManagerHelper.class);
        intent.putExtra("ringtone", currentTask.getRingToneURI().toString());
        intent.putExtra("task_title", currentTask.getTitle());
        alarmIntent = PendingIntent.getBroadcast(getActivity(), currentTask.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar taskCalendar = currentTask.getNotificationHour();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, taskCalendar.get(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, taskCalendar.get(Calendar.MINUTE));
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 24 * 60 * 60 * 1000, alarmIntent);
    }

    private void cancelAlarmManager() {
        if (!hasNotificationEnabled)
            return;
        if (alarmManager != null)
            alarmManager.cancel(alarmIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                currentTask.setRingToneURI(uri);
                Log.i("changedRing", uri.getPath());
                db.updateTask(currentTask);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_colapse:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
