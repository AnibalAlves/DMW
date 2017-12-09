package eu.croussel.sportyfield;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.text.DateFormat.getDateInstance;

public class AddFieldActivity extends AppCompatActivity {

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    Bitmap photoBitmap;
    // Database Helper
    private DataBaseHandler db;
    //Position
    private LatLng fieldPos;

    //List of type of sportfields
    private String[] sports = {"Basketball", "Football", "Tennis", "Pingpong", "Handball"};
    private ListView sportsList;
    private String selectedFromList = new String();
    //lastItemSelected
    private int lastSport = 0;

    ImageView photoView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        //Call the db
        db = new DataBaseHandler(this);

        //Recover the intent
        Intent intent = getIntent();
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        fieldPos = bundle.getParcelable("fieldPos");

        //Image view
        photoView = (ImageView) findViewById(R.id.infowindowImView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        //Display the location in the activity
        TextView positionText = (TextView) findViewById(R.id.positionTextView);
        positionText.setText("Add location : \n Latitude : " + fieldPos.latitude + "\n Longitude : " + fieldPos.longitude);

        //Display the listview of the sportfields types
        sportsList = (ListView) findViewById(R.id.listViewSports);
        sportsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sports);

        sportsList.setAdapter(adapter);
        sportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFromList = sports[position];
            }
        });


        //Add the field when button clicked
        final Button addField_button = (Button) findViewById(R.id.button_addField);
        addField_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Boolean isOutdoor = ((CheckBox) findViewById(R.id.checkBox_Outdoor)).isChecked();
                Boolean isPrivate = ((CheckBox) findViewById(R.id.checkBox_Private)).isChecked();



                Field newField = new Field(getAddress(),
                        fieldPos.latitude,
                        fieldPos.longitude,
                        isPrivate,
                        isOutdoor,
                        0,
                        selectedFromList,
                        getBytesFromBitmap(photoBitmap)
                );
                db.createField(newField);
                finish();
            }
        });
    }

    private String getAddress(){
        String adrs = new String();
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(fieldPos.latitude,
                    fieldPos.longitude,
                    1);
            if(addresses == null) adrs = "No address";
            else adrs = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return adrs ;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setPic();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = photoView.getWidth();
        int targetH = photoView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        photoBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        photoView.setImageBitmap(photoBitmap);
    }


    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }
        return null;
    }
}
