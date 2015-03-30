package fr.lassiergedeon.dontbreakthechain;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;

/**
 * Created by sebastien on 30/03/2015.
 */
public class TaskViewerFragment extends Fragment{


    ButtonFloat addButton;

    public TaskViewerFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){

        addButton = (ButtonFloat) container.findViewById(R.id.addButtonFloat);
        //addButton.setOnClickListener(new AddTaskButtonListener());

        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }
}
