package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static HashMap<String, Integer> hash;
    private static Integer[] mThumbIds = {
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

        SimplifiedEvent e = _events.get(position);
        ((TextView) rowView.findViewById(R.id.firstLine)).setText(e.getEventName());
        ((TextView) rowView.findViewById(R.id.secondLine)).setText(DateFormat.format("yyyy/MM/dd - hh:mm",e.getEventDate()));
        ((TextView) rowView.findViewById(R.id.thirdLine)).setText(e.getEventDescription());
        if(hash.containsKey(e.get_eventSport()))
            ((ImageView) rowView.findViewById(R.id.icon)).setImageBitmap(getBitmapSavingMem(mThumbIds[hash.get(e.get_eventSport())],40,40));

        return rowView;
    }
    public Bitmap getBitmapSavingMem(int resId, int reqWidth, int reqHeight){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(((Activity) context).getResources(), resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(((Activity) context).getResources(), resId, options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    }
