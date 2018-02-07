package eu.croussel.sportyfield.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class AddReportActivity extends AppCompatActivity{

    int fieldId;
    String uId;
    ImageView im;
    TextView loca;
    Button addReportButton ;
    // Database Helper
    private FirebaseDBhandler mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        try {
            DrawerUtilActivity.getDrawer(this,getSupportActionBar());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //DB init
        mDatabase = new FirebaseDBhandler();
        loca = findViewById(R.id.field_location);

        im = (ImageView) findViewById(R.id.imageView);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                PickImageDialog.build(new PickSetup().setWidth(im.getMaxWidth()).setHeight(im.getMaxHeight()))
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult pickResult) {
                                Log.d("PATH",pickResult.getPath() +"-"+pickResult);
//                                setPic(pickResult.getPath());
                                im.setImageBitmap(pickResult.getBitmap());
                            }
                        }).show(AddReportActivity.this);
            }
        });

        addReportButton = (Button) findViewById(R.id.buttonToAdd);
        addReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRep();
            }
        });
        Intent mIntent = getIntent();
        fieldId = mIntent.getIntExtra("fieldId", 0);
        loca.setText(mIntent.getStringExtra("location"));
        uId = mDatabase.getCurrentUID();
    }



    public void addRep() {
        //add report to the db, but first get info from image and edit text
        @SuppressLint("WrongViewCast") EditText descr = findViewById(R.id.descr);
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
        Date date = Calendar.getInstance().getTime();
        Report r = new Report(des,fieldId,uId,date,imageInByte);

        mDatabase.createReport(r);
        finish();
    }

    //Called when one of the action button is called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Drawer nav.
            case android.R.id.home:
                if (DrawerUtilActivity.result.isDrawerOpen())
                {
                    DrawerUtilActivity.result.closeDrawer();
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                else {
                    DrawerUtilActivity.result.openDrawer();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                return true;

            //Default, do nothing
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
