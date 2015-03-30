package fr.lassiergedeon.dontbreakthechain;

import android.util.Log;
import android.view.View;
import android.view.View.*;

/**
 * Created by sebastien on 30/03/2015.
 */
public class AddTaskButtonListener implements OnClickListener{

    public AddTaskButtonListener(){
        super();
    }

    @Override
    public void onClick(View v) {
        Log.d("debug", "prout");
    }
}
