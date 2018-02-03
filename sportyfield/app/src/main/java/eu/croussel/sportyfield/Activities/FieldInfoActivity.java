package eu.croussel.sportyfield.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.croussel.sportyfield.CustomList;
import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.Filter;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.DataBaseHandler;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class FieldInfoActivity extends AppCompatActivity {

    // Database Helper
//    DataBaseHandler db;
    int fieldId;
    String theLocation;

    // Database Helper
    private FirebaseAuth auth;
    private FirebaseDBhandler mDatabase;

    //Fields acquisition vars
    List<Report> reports ;
    List<User> users ;
    int oldReportListSize = -1;
    private Handler handlerReports ;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);
        mDatabase = new FirebaseDBhandler();
        //Every sec we check if the list has changed
        handlerReports = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("Number of reports", "number of reports is " + reports.size() + " n of users :" + users.size());
                if(users.size() != oldReportListSize && users.size()==reports.size()){
                    oldReportListSize = users.size();
                    System.out.println("number of reports is " + reports.size() + " n of users :" + users.size());

                    if (users.size()>0)
                    {
                        String[] repDate = new String[reports.size()];
                        String[] repDescr = new String[reports.size()];
                        String[] userTy = new String[reports.size()];
                        Integer[] userReput = new Integer[reports.size()];
                        byte[][] repImage = new byte[reports.size()][];
                        for (int i = 0; i < reports.size(); i++) {
                            repDate[i] = reports.get(i).getDate();
                            repDescr[i] = reports.get(i).getDescr();
                            repImage[i] = reports.get(i).getRepImage();
                        }

                        CustomList adapter = new CustomList(FieldInfoActivity.this,users, userTy, repDate, repDescr, userReput, repImage);

                        ListView rep = (ListView) findViewById(R.id.reports);
                        rep.setAdapter(adapter);
                        rep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            }
                        });
                }
            }
            handlerReports.postDelayed(this,2000);

            }};
        handlerReports.postDelayed(runnable, 1000);

        Intent intent = getIntent();
        fieldId = intent.getIntExtra("fieldID", 0); //get the Field id from Maps class
        System.out.println("field id is = " + fieldId);


        reports = new ArrayList<Report>();
        mDatabase.getAllReportsListener(reports, fieldId);

        //CREATING SOME REPORTS OF THE FIELD
        Bitmap src=BitmapFactory.decodeFile("/storage/emulated/0/Download/download.jpeg");
        if(src != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            src.compress(Bitmap.CompressFormat.PNG, 100, baos);
            Report newRe = new Report("Net with some holes", fieldId, "John", baos.toByteArray());
            mDatabase.createReport(newRe);
        }
        TextView field_loc = (TextView) findViewById(R.id.field_location);
        //Field f = db.getField(fieldId);
        //System.out.println("field info + " + f.getLocation());
        //byte[] image = f.getImage();
        //if(image == null) iv.setImageResource(R.drawable.basket_field);
        //else iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0 ,image.length));

        //theLocation = f.getLocation();
        users = new ArrayList<User>();
        mDatabase.getAllReportsListener(reports, users, fieldId);

        TextView field_loc = (TextView) findViewById(R.id.field_location);
//        theLocation = f.getLocation();
        field_loc.setText(theLocation);
        onResume();
    }

    @Override
    public void onStop(){
        super.onStop();
        handlerReports.removeCallbacksAndMessages(null);
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        try {
            DrawerUtilActivity.getDrawer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upArrow(View view) {
        //get the row the clicked button is in
        RelativeLayout vwParentRow = (RelativeLayout) view.getParent();
        ImageButton btnChild = (ImageButton)vwParentRow.getChildAt(5);
        ImageButton btnChildd = (ImageButton)vwParentRow.getChildAt(7);
        TextView rep = (TextView) vwParentRow.getChildAt(6);
        String repu = (String) rep.getText();
        String splitt = repu.substring(1);
        Integer aux = Integer.parseInt(splitt);
        aux++;
//        db.updateUserRep(testUsername,aux);
        if (aux>=0)
            rep.setText("+" + aux);
        else
            rep.setText("-" + aux);
        btnChild.setClickable(false);
        btnChildd.setClickable(false);
    }

    public void downArrow(View view) {
        //get the row the clicked button is in
        RelativeLayout vwParentRow = (RelativeLayout) view.getParent();
        ImageButton btnChildu = (ImageButton)vwParentRow.getChildAt(5);
        ImageButton btnChild = (ImageButton)vwParentRow.getChildAt(7);
        TextView rep = (TextView) vwParentRow.getChildAt(6);
        String repu = (String) rep.getText();
        String splitt = repu.substring(1);
        Integer aux = Integer.parseInt(splitt);
        aux--;
//        db.updateUserRep(testUsername,aux);
        if (aux>=0)
            rep.setText("+" + aux);
        else
            rep.setText("-" + aux);
        btnChildu.setClickable(false);
        btnChild.setClickable(false);
    }

    /////////////////////////////
    //      ACTION BUTTONS     //
    /////////////////////////////

    //Add action buttons to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.field_info_actionbuttons, menu);
        return true ;
    }

    //Called when one of the action button is called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_addReport:
                Intent newRe = new Intent(this,AddReportActivity.class);
                newRe.putExtra("fieldId",fieldId);
                startActivity(newRe);
                return true;
            case R.id.action_backMap :
                finish();
                return true;
            case R.id.createEvent:
                Intent s = new Intent(this,AddReportActivity.class);
                s.putExtra("fieldId",fieldId);
                s.putExtra("location",theLocation);
                startActivity(s);
                return true;
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
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
