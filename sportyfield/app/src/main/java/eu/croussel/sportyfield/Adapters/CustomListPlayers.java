package eu.croussel.sportyfield.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.R;

/**
 * Created by root on 05/02/18.
 */

public class CustomListPlayers extends ArrayAdapter<String> {
    private final Activity context;
    private final List<User> _players ;


    public CustomListPlayers(Activity context, List<User> players) {
        super(context, R.layout.list_single, new String[players.size()]);
        this._players = players;
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.event_row_layout, null, true);


        ImageView image = ((ImageView) rowView.findViewById(R.id.icon));
        User user = _players.get(position);

        ((TextView) rowView.findViewById(R.id.firstLine)).setText(user.getUserName());

        ((TextView) rowView.findViewById(R.id.secondLine)).setText(user.getType());
        ((TextView) rowView.findViewById(R.id.thirdLine)).setText(user.getFavSport());


        byte[] imageBytes = user.get_image();
        if(imageBytes != null) {
            Bitmap bitmap = getBitmapSavingMem(imageBytes);
            bitmap = getCroppedBitmap(bitmap);
            ((ImageView) rowView.findViewById(R.id.icon)).setImageBitmap(bitmap);
        }


        return rowView;
    }


    public Bitmap getBitmapSavingMem(byte[] image){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if(image != null) BitmapFactory.decodeByteArray(image, 0 ,image.length, options);

        options.inSampleSize = calculateInSampleSize(options, 100, 100);
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
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int delta = height - width;
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(width / 2, height / 2,
                width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap bmp = Bitmap.createBitmap(output, 0,delta/2,width,width);
//        Bitmap _bmp = Bitmap.createScaledBitmap(output, 100, 100, false);
//        return _bmp;
        return bmp;
    }
}
