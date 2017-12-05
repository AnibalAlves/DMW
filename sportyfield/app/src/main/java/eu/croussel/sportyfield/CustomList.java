package eu.croussel.sportyfield;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by afonso on 05-12-2017.
 */

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final Integer[] user_image;
    private final String[] type_user;
    private final String[] date;
    private final String[] user_Descr;
    private final Integer[] report_image;
    private final String[] user_reputation;

    public CustomList(Activity context, Integer[] user_i,String[] type_u
            , String[] da, String[] user_de,Integer[] report_im, String[] user_rep) {
        super(context, R.layout.list_single, user_de);
        this.context = context;
        this.user_image = user_i;
        this.type_user = type_u;
        this.date = da;
        this.user_Descr = user_de;
        this.report_image = report_im;
        this.user_reputation = user_rep;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        ImageView us_im = (ImageView) rowView.findViewById(R.id.user_image);
        us_im.setImageResource(R.drawable.user_icon);
        TextView user_t = (TextView) rowView.findViewById(R.id.user_type);
        user_t.setText(type_user[position]);
        TextView dating = (TextView) rowView.findViewById(R.id.date);
        dating.setText(date[position]);
        TextView descrip = (TextView) rowView.findViewById(R.id.descr);
        descrip.setText(user_Descr[position]);

        //change this to change the report image
        ImageView rep_im = (ImageView) rowView.findViewById(R.id.report_image);
        rep_im.setImageResource(R.drawable.broken_ring);

        ImageButton up_a = (ImageButton) rowView.findViewById(R.id.imageButton);
        up_a.setImageResource(R.drawable.arrow_up);
        ImageButton down_a = (ImageButton) rowView.findViewById(R.id.down);
        down_a.setImageResource(R.drawable.arrow_down);
        TextView reput = (TextView) rowView.findViewById(R.id.reputation);
        descrip.setText(user_reputation[position]);
        return rowView;
    }
}
