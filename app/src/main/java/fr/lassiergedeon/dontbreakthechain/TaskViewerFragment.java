package fr.lassiergedeon.dontbreakthechain;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.lassiergedeon.dontbreakthechain.model.Chain;
import fr.lassiergedeon.dontbreakthechain.model.Task;

/**
 * Created by Antoine on 06/04/2015.
 */
public class TaskViewerFragment extends Fragment {

    private Task currentTask;
    private DBOpenHelper db;
    private CaldroidFragment calendarFragment;
    private List<Chain> chains;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_task, container, false);
        setHasOptionsMenu(true);

        // on récupère l'id de la tâche passé avec le putExtra
        Intent launchingIntent = getActivity().getIntent();
        int task_id = launchingIntent.getIntExtra("task_id", 0);

        // on récupère la tâche associé à l'id
        db = new DBOpenHelper(getActivity());
        currentTask = db.getTask(task_id);

        // on récupère les chaines associées à l'id de la tâche
        chains = db.getAllChainsForTaskId(task_id);

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(currentTask.getTitle());

        // On met en place le calendrier
        calendarFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            calendarFragment.restoreStatesFromKey(savedInstanceState, "CALDROID_SAVED_STATE");
        } else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
            calendarFragment.setArguments(args);
        }

        // Met en place le calendrier
        setRessourcesForChains();

        // On attache le calendrier au fragment
        FragmentTransaction transaction = ((ActionBarActivity) getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.taskCalendar, calendarFragment);
        transaction.commit();

        // Mise en place des statistiques
        TextView currentChainLengthTextView = (TextView) view.findViewById(R.id.actualChainLengthTextView);
        TextView longuestChainLengthTextView = (TextView) view.findViewById(R.id.longuestChainLengthTextView);
        Chain longuestChain = currentTask.getLongestChain(db);

        currentChainLengthTextView.setText(Html.fromHtml("<b>" + currentTask.getCurrentConsecutiveDays(db)
                                                         + " " + getString(R.string.days) + "</b>"));
        longuestChainLengthTextView.setText(Html.fromHtml("<b>" + (longuestChain != null ? longuestChain.getNbJours() : 0)
                                                          + " " + getString(R.string.days) + "</b>"));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (calendarFragment != null) {
            calendarFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_task_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manage_notification:
                Intent showSetting = new Intent(getActivity().getApplicationContext(), TaskSettingActivity.class);
                showSetting.putExtra("task_id", currentTask.getId());
                startActivity(showSetting);
                getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_top);
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setRessourcesForChains() {
        // On met en surbrillance les dates des chains
        for (Chain chain : chains) {
            List<Date> dates = chain.getDateList();
            for (Date date : dates) {
                calendarFragment.setBackgroundResourceForDate(R.color.blue, date);
            }
        }
    }
}
