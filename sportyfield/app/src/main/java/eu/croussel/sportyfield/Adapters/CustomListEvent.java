package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.R;

/**
 * Created by root on 05/02/18.
 */

public class CustomListEvent extends ArrayAdapter<String> {
    private final Activity context;
    private final List<SimplifiedEvent> _events ;
    private HashMap<String, Integer> hash;
    private Integer[] mThumbIds = {
            R.drawable.pictobasketball01,
            R.drawable.pictoarcherey01,
            R.drawable.pictoathletism01,
            R.drawable.pictobadminton01,
            R.drawable.pictobaseball01,
            R.drawable.pictobowling01,
            R.drawable.pictoboxe01,
            R.drawable.pictocurling01,
            R.drawable.pictodiving01,
            R.drawable.pictofish01,
            R.drawable.pictofitness01,
            R.drawable.pictogolf01,
            R.drawable.pictogym01,
            R.drawable.pictohockey01,
            R.drawable.pictojudo01,
            R.drawable.pictokayak01,
            R.drawable.pictopingpong01,
            R.drawable.pictorugby01,
            R.drawable.pictorunning01,
            R.drawable.pictosocker01,
            R.drawable.pictoswim01,
            R.drawable.pictotennis01,
            R.drawable.pictovolleyball01
    };

    public CustomListEvent(Activity context, List<SimplifiedEvent> events) {
        super(context, R.layout.list_single, new String[events.size()]);
        hash =  new HashMap<>();
        hash.put("Basketball",0);
        hash.put("Archery",1);
        hash.put("Athletism",2);
        hash.put("Badminton",3);
        hash.put("Baseball",4);
        hash.put("Bowling",5);
        hash.put("Boxe",6);
        hash.put("Curling",7);
        hash.put("Diving",8);
        hash.put("Fishing",9);
        hash.put("Fitness",10);
        hash.put("Golf",11);
        hash.put("Gymnastic",12);
        hash.put("Hockey",13);
        hash.put("Judo",14);
        hash.put("Kayak",15);
        hash.put("Pingpong",16);
        hash.put("Rugby",17);
        hash.put("Running",18);
        hash.put("Football",19);
        hash.put("Swimming",20);
        hash.put("Tennis",21);
        hash.put("Volleyball",22);

        this._events = events;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.event_row_layout, null, true);

        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        TextView thirdLine = (TextView) rowView.findViewById(R.id.thirdLine);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);
        SimplifiedEvent e = _events.get(position);
        firstLine.setText(e.getEventName());
        secondLine.setText(DateFormat.format("yyyy/MM/dd - hh:mm",e.getEventDate()));
        thirdLine.setText(e.getEventDescription());
        if(hash.containsKey(e.get_eventSport()))
            image.setImageResource(mThumbIds[hash.get(e.get_eventSport())]);

        return rowView;
    }


    }
