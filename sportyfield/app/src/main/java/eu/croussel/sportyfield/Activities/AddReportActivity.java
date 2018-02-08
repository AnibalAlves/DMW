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

        //DB init
        mDatabase = new FirebaseDBhandler();
        loca = findViewById(R.id.field_location);


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

        Date date = Calendar.getInstance().getTime();
        Report r = new Report(des,fieldId,uId,date,null);

        mDatabase.createReport(r);
        finish();
    }


}
