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
    private final List<Report> _reports ;


    public CustomList(Activity context, List<Report> reports) {
        super(context, R.layout.list_single, new String[reports.size()]);
        this.context = context;
        this._reports=reports;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        Report r = _reports.get(position);

        ((TextView) rowView.findViewById(R.id.reportText))
        .setText(android.text.format.DateFormat.format("yyyy/MM/dd - HH:MM",r.getDate())+" : "+r.getDescr());

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
