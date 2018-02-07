package eu.croussel.sportyfield.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import eu.croussel.sportyfield.R;

/**
 * Created by root on 07/02/18.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        int id = mThumbIds[position];
        Resources res = mContext.getResources();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, id,options);

        options.inSampleSize = calculateInSampleSize(options, 75, 75);
        options.inJustDecodeBounds = false;

        Bitmap b = BitmapFactory.decodeResource(res, id,options);
        imageView.setBackgroundColor(Color.GRAY);

        imageView.setImageBitmap(b);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.picto_basketball_04,
            R.drawable.picto_archerey_04,
            R.drawable.picto_athle_04,
            R.drawable.picto_bad_04,
            R.drawable.pictobaseball04,
            R.drawable.pictobowling04,
            R.drawable.pictoboxe04,
            R.drawable.pictocurling04,
            R.drawable.pictodiving04,
            R.drawable.pictofish04,
            R.drawable.pictofitness04,
            R.drawable.pictogolf04,
            R.drawable.pictogym04,
            R.drawable.pictohockey04,
            R.drawable.pictojudo04,
            R.drawable.pictokayak04,
            R.drawable.pictopingpong04,
            R.drawable.pictorugby04,
            R.drawable.pictorunning04,
            R.drawable.pictosocker04,
            R.drawable.pictoswim04,
            R.drawable.pictotennis04,
            R.drawable.pictovolleyball04
    };
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
