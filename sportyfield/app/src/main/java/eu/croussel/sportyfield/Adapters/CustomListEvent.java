package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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


    public CustomListEvent(Activity context, List<SimplifiedEvent> events) {
        super(context, R.layout.list_single, new String[events.size()]);
        this._events = events;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.event_row_layout, null, true);

        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView image = (ImageView) rowView.findViewById(R.id.icon);
        SimplifiedEvent e = _events.get(position);
        firstLine.setText(e.getEventName());
        secondLine.setText(DateFormat.format("yyyy.MM.dd",e.getEventDate()) + " - " + e.getEventDescription());
        image.setImageResource(R.drawable.user_icon);

        return rowView;
    }


    }
