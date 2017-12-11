package eu.croussel.sportyfield;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class AddReport extends Activity {

    DataBaseHandler db;
    int fieldId;
    String userName;
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        db = new DataBaseHandler(getApplicationContext());
        im = (ImageView) findViewById(R.id.imageView);
        Intent mIntent = getIntent();
        fieldId = mIntent.getIntExtra("fieldId", 0);
        userName = mIntent.getStringExtra("uName");
    }

    public void imageSearch(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            im.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }


    }

    public void repAdd(View view) {
        //add report to the db, but first get info from image and edit text
        EditText descr = (EditText) findViewById(R.id.descr);
        String des = descr.getText().toString();
        byte[] imageInByte;
        try {
            Bitmap bitmap = ((BitmapDrawable) im.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageInByte = baos.toByteArray();
        }
        catch(NullPointerException ex){
            imageInByte = null;
        }
        Report r = new Report(des,fieldId,userName,imageInByte);

        db.createReport(r);
        finish();
    }
}
