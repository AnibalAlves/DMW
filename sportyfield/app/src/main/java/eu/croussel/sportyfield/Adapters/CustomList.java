package eu.croussel.sportyfield.Adapters;

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

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.R;

/**
 * Created by afonso on 05-12-2017.
 */

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final List<User> _users ;
    private final List<Report> _reports ;


    public CustomList(Activity context, List<User> users, List<Report> reports) {
        super(context, R.layout.list_single, new String[users.size()]);
        this._users = users;
        this.context = context;
        this._reports=reports;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        ImageView us_im = rowView.findViewById(R.id.user_image);
        byte[] image = _users.get(position).get_image();
        if(image != null) {
            Bitmap bitmap = getBitmapSavingMem(image);
            us_im.setImageBitmap(bitmap);
        }else us_im.setImageResource(R.drawable.user_icon);


        TextView user_t = rowView.findViewById(R.id.user_type);
        user_t.setText(_users.get(position).getType());

        TextView dating = rowView.findViewById(R.id.date);
        dating.setText(android.text.format.DateFormat.format("yyyy.MM.dd",_reports.get(position).getDate()).toString());

        TextView descrip = rowView.findViewById(R.id.nameUser);
        descrip.setText(_reports.get(position).getDescr());


        ImageButton up_a = rowView.findViewById(R.id.imageButton);
        up_a.setImageResource(R.drawable.arrow_up);
        ImageButton down_a;
        down_a = rowView.findViewById(R.id.down);
        down_a.setImageResource(R.drawable.arrow_down);

        TextView reput = rowView.findViewById(R.id.reputation);
        int user_reputation = _users.get(position).getReputation();
            if (user_reputation >= 0)
                reput.setText("+" + Integer.toString(user_reputation));
            else
                reput.setText("-" + Integer.toString(user_reputation));
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
