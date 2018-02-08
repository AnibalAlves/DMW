package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.content.Intent;
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

import eu.croussel.sportyfield.Activities.AddReportActivity;
import eu.croussel.sportyfield.Activities.EventInfoActivity;
import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

/**
 * Created by afonso on 05-02-2018.
 */

public class CustomListFieldEvents extends ArrayAdapter<String> {

    private final Activity context;
    private final List<SimplifiedEvent> events ;
    private FirebaseDBhandler mDatabase;


    public CustomListFieldEvents(Activity context, List<SimplifiedEvent> eve) {
        super(context, R.layout.list_single, new String[eve.size()]);
        this.context = context;
        this.events=eve;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_events, null, true);
        mDatabase = new FirebaseDBhandler();
        SimplifiedEvent event = events.get(position);
        TextView name = rowView.findViewById(R.id.name);
        name.setText(event.getEventName());

        TextView date = rowView.findViewById(R.id.date);
        date.setText(android.text.format.DateFormat.format("yyyy/MM/dd - HH:MM",event.getEventDate()));

        TextView description = rowView.findViewById(R.id.eventDescription);
        description.setText(event.getEventDescription());

        /*Button joinEvent = rowView.findViewById(R.id.eventJoin); //onClick join this event
        joinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabase.applyToEvent(events.get(position));
                Intent x = new Intent(context,EventInfoActivity.class);
                x.putExtra("eventId",events.get(position).getEventId());
                context.startActivity(x);
            }
        });*/

        return rowView;
    }
}
