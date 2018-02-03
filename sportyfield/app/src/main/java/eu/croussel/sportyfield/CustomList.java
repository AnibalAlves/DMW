package eu.croussel.sportyfield;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.User;

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
    private final List<User> _users ;
    ;

    public CustomList(Activity context,List<User> users, String[] type_u, String[] da, String[] user_de, Integer[] user_rep, byte[][] rim) {
        super(context, R.layout.list_single, user_de);
        this._users = users;
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
        byte[] image = _users.get(position).get_image();

        Bitmap bitmap = getBitmapSavingMem(image);
        if(bitmap != null) us_im.setImageBitmap(bitmap);
        else us_im.setImageResource(R.drawable.user_icon);


        TextView user_t = rowView.findViewById(R.id.user_type);
        user_t.setText(type_user[position]);
        TextView dating = rowView.findViewById(R.id.date);
        dating.setText(date[position]);
        TextView descrip = rowView.findViewById(R.id.descr);
        descrip.setText(user_Descr[position]);

        //change this to change the report image
        try {
            ImageView rep_im = rowView.findViewById(R.id.report_image);
            bitmap = getBitmapSavingMem(repImage[position]);
            rep_im.setImageBitmap(bitmap);
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

    public Bitmap getBitmapSavingMem(byte[] image){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if(image != null) BitmapFactory.decodeByteArray(image, 0 ,image.length, options);

        options.inSampleSize = calculateInSampleSize(options, 50, 50);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image, 0 ,image.length, options);
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
