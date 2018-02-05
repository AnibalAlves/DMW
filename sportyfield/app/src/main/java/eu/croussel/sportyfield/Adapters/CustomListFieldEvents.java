package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.R;

/**
 * Created by afonso on 05-02-2018.
 */

public class CustomListFieldEvents extends ArrayAdapter<String> {

    private final Activity context;
    private final List<SimplifiedEvent> events ;


    public CustomListFieldEvents(Activity context, List<SimplifiedEvent> eve) {
        super(context, R.layout.list_single, new String[eve.size()]);
        this.context = context;
        this.events=eve;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_events, null, true);
        System.out.println("Inside the customListFieldEvents: " + events);

        TextView name = rowView.findViewById(R.id.name);
        name.setText(events.get(position).getEventName().toString());

        TextView date = rowView.findViewById(R.id.date);
        date.setText(android.text.format.DateFormat.format("yyyy.MM.dd - HH:MM",events.get(position).getEventDate()).toString());

        TextView description = rowView.findViewById(R.id.eventDescription);
        description.setText(events.get(position).getEventDescription());

        Button joinEvent = rowView.findViewById(R.id.eventJoin); //onClick join this event

        return rowView;
    }
}
