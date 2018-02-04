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
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.DataBaseHandler;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class FieldInfoActivity extends AppCompatActivity {

    // Database Helper
//    DataBaseHandler db;
    int fieldId;
    String theLocation;
    TextView location;

    // Database Helper
    private FirebaseAuth auth;
    private FirebaseDBhandler mDatabase;

    //Fields acquisition vars
    List<Report> reports ;
    List<User> users ;
    List<SimplifiedEvent> events;
    int oldReportListSize = -1;
    private Handler handlerReports ;

    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        try {
            DrawerUtilActivity.getDrawer(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        location = findViewById(R.id.field_location);
        Intent intent = getIntent();
        fieldId = intent.getIntExtra("fieldID", 0); //get the Field id from Maps class
        theLocation = intent.getStringExtra("location");
        location.setText(theLocation);

        mDatabase = new FirebaseDBhandler();
        //Every sec we check if the list has changed
        handlerReports = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("Number of reports", "number of reports is " + reports.size() + " n of users :" + users.size());
                if(events.size() > 0)
                    Log.d("EVENTS", events.size() + " events found. First event's description : "+ events.get(0).getEventDescription());
                if(users.size() != oldReportListSize && users.size()==reports.size()){
                    oldReportListSize = users.size();
                    CustomList adapter = new CustomList(FieldInfoActivity.this,users, reports);

                    ListView rep = (ListView) findViewById(R.id.reports);
                    rep.setAdapter(adapter);
                    rep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
            }
            handlerReports.postDelayed(this,2000);

            }};

        List<Field> field = new ArrayList<Field>();
        mDatabase.getOneFieldListener(field, fieldId, (ImageView) findViewById(R.id.field_image));
        System.out.println("field id is = " + fieldId);


        reports = new ArrayList<Report>();
        users = new ArrayList<User>();
        events = new ArrayList<SimplifiedEvent>();
        mDatabase.getAllReportsListener(reports, users, fieldId);
        mDatabase.getEventsForField(events, fieldId);
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
        handlerReports.postDelayed(runnable, 1000);

        try {
            DrawerUtilActivity.getDrawer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void upArrow(View view) {
        //get the row the clicked button is in
        RelativeLayout vwParentRow = (RelativeLayout) view.getParent();
        ImageButton btnChild = (ImageButton)vwParentRow.getChildAt(4);
        ImageButton btnChildd = (ImageButton)vwParentRow.getChildAt(6);
        TextView rep = (TextView) vwParentRow.getChildAt(5);
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
                newRe.putExtra("location",theLocation);
                startActivity(newRe);
                return true;
            case R.id.action_backMap :
                finish();
                return true;
            case R.id.createEvent:
                Intent s = new Intent(this,CreateEventActivity.class);
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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
