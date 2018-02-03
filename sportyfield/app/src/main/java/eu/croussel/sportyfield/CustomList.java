package eu.croussel.sportyfield;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private final String[] type_user;
    private final String[] date;
    private final String[] user_Descr;
    private final Integer[] user_reputation;
    private final byte[][] repImage;

    public CustomList(Activity context,String[] type_u, String[] da, String[] user_de, Integer[] user_rep, byte[][] rim) {
        super(context, R.layout.list_single, user_de);
        this.context = context;
        this.type_user = type_u;
        this.date = da;
        this.user_Descr = user_de;
        this.user_reputation = user_rep;
        this.repImage=rim;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        ImageView us_im = rowView.findViewById(R.id.user_image);
        us_im.setImageResource(R.drawable.user_icon);
        TextView user_t = rowView.findViewById(R.id.user_type);
        user_t.setText(type_user[position]);
        TextView dating = rowView.findViewById(R.id.date);
        dating.setText(date[position]);
        TextView descrip = rowView.findViewById(R.id.nameUser);
        descrip.setText(user_Descr[position]);

        //change this to change the report image
        try {
            ImageView rep_im = rowView.findViewById(R.id.report_image);
            Bitmap bmp = BitmapFactory.decodeByteArray(repImage[position], 0, repImage[position].length);
            rep_im.setImageBitmap(bmp);
        }
        catch (NullPointerException ex) {}
        ImageButton up_a = rowView.findViewById(R.id.imageButton);
        up_a.setImageResource(R.drawable.arrow_up);
        ImageButton down_a;
        down_a = rowView.findViewById(R.id.down);
        down_a.setImageResource(R.drawable.arrow_down);

        TextView reput = rowView.findViewById(R.id.reputation);
        if(user_reputation[position] != null) {
            if (user_reputation[position] >= 0)
                reput.setText("+" + user_reputation[position].toString());
            else
                reput.setText("-" + user_reputation[position].toString());
        }
        return rowView;
    }
}
