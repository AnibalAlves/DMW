package eu.croussel.sportyfield.DB_classes;

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

import java.util.List;

import eu.croussel.sportyfield.CustomList;
import eu.croussel.sportyfield.R;

/**
 * Created by afonso on 03-02-2018.
 */

public class CustomList_classify extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] user_Name;
    private final Integer[] user_reputation;
    private final byte[][] profileImage;
    private final List<User> _users ;

    public CustomList_classify(Activity context,List<User> users, String[] user_de, Integer[] user_rep, byte[][] rim) {
        super(context, R.layout.list_classify_players);
        this._users = users;
        this.context = context;
        this.user_Name = user_de;
        this.user_reputation = user_rep;
        this.profileImage=rim;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_classify_players, null, true);

        ImageView us_im = rowView.findViewById(R.id.profilePic);
        byte[] image = _users.get(position).get_image();

        Bitmap bitmap = getBitmapSavingMem(image);
        if(bitmap != null) us_im.setImageBitmap(bitmap);
        else us_im.setImageResource(R.drawable.user_icon);


        TextView user_t = rowView.findViewById(R.id.nameUser);
        user_t.setText(user_Name[position]);
        TextView dating = rowView.findViewById(R.id.Reput);
        dating.setText(user_reputation[position]);

        ImageButton thumUp = rowView.findViewById(R.id.thumb_up);
        ImageButton down_a = rowView.findViewById(R.id.thumb_down);

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
