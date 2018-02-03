package eu.croussel.sportyfield.Activities;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class AddReportActivity extends AppCompatActivity{

    int fieldId;
    String uId;
    ImageView im;
    Button addReportButton ;
    // Database Helper
    private FirebaseDBhandler mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        //DB init
        mDatabase = new FirebaseDBhandler();

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
        uId = mDatabase.getCurrentUID();
    }



    public void addRep() {
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
        Report r = new Report(des,fieldId,uId,imageInByte);

        mDatabase.createReport(r);
        finish();
    }
}
