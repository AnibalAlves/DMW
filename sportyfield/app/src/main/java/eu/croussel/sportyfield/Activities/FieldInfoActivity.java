package eu.croussel.sportyfield.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.croussel.sportyfield.Adapters.CustomList;
import eu.croussel.sportyfield.Adapters.CustomListEvent;
import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class FieldInfoActivity extends AppCompatActivity {
    private static Integer[] mThumbIds = {
            R.drawable.pictobasketball01,
            R.drawable.pictoarcherey01,
            R.drawable.pictoathletism01,
            R.drawable.pictobadminton01,
            R.drawable.pictobaseball01,
            R.drawable.pictobowling01,
            R.drawable.pictoboxe01,
            R.drawable.pictocurling01,
            R.drawable.pictodiving01,
            R.drawable.pictofish01,
            R.drawable.pictofitness01,
            R.drawable.pictogolf01,
            R.drawable.pictogym01,
            R.drawable.pictohockey01,
            R.drawable.pictojudo01,
            R.drawable.pictokayak01,
            R.drawable.pictopingpong01,
            R.drawable.pictorugby01,
            R.drawable.pictorunning01,
            R.drawable.pictosocker01,
            R.drawable.pictoswim01,
            R.drawable.pictotennis01,
            R.drawable.pictovolleyball01
    };
    // Database Helper
//    DataBaseHandler db;
    private int fieldId;
    private String theLocation;
    private int count = 0;
    // Database Helper
    private FirebaseDBhandler mDatabase;

    //Fields acquisition vars
    private List<Report> reports ;
    private List<SimplifiedEvent> events;
    private int oldReportListSize = 0;
    private int oldEventListSize = 0;
    private Handler handlerReports ;
    private List<Field> field;
    private static Runnable runnable;
    private ListView eventListView;
    private ListView repListView;
    private static HashMap<String, Integer> hash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(false);
//        try {
//            DrawerUtilActivity.getDrawer(this,getSupportActionBar());
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        Intent intent = getIntent();
        fieldId = intent.getIntExtra("fieldID", 0); //get the Field id from Maps class
        theLocation = intent.getStringExtra("location");
        ((TextView) findViewById(R.id.textLocation)).setText(theLocation.replace(',','\n'));
        eventListView = findViewById(R.id.events);
        repListView = (ListView) findViewById(R.id.reports);
        hash =  new HashMap<>();
        hash.put("Basketball",0);
        hash.put("Archery",1);
        hash.put("Athletism",2);
        hash.put("Badminton",3);
        hash.put("Baseball",4);
        hash.put("Bowling",5);
        hash.put("Boxe",6);
        hash.put("Curling",7);
        hash.put("Diving",8);
        hash.put("Fishing",9);
        hash.put("Fitness",10);
        hash.put("Golf",11);
        hash.put("Gymnastic",12);
        hash.put("Hockey",13);
        hash.put("Judo",14);
        hash.put("Kayak",15);
        hash.put("Pingpong",16);
        hash.put("Rugby",17);
        hash.put("Running",18);
        hash.put("Football",19);
        hash.put("Swimming",20);
        hash.put("Tennis",21);
        hash.put("Volleyball",22);


        mDatabase = new FirebaseDBhandler();
        //Every sec we check if the list has changed
        handlerReports = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                int eventSize = events.size();
                int reportSize = reports.size();
                if(eventSize == oldEventListSize && reportSize == oldReportListSize)
                    count = count + 1;
                if(events.size() != oldEventListSize && events.size() > 0) {
                    oldEventListSize = events.size();
                    Log.d("EVENTS", events.size() + " events found. First event's description : " + events.get(0).getEventDescription());
                    CustomListEvent eventAdaper = new CustomListEvent(FieldInfoActivity.this,events);
                    eventListView.setAdapter(eventAdaper);
                    eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent x = new Intent(FieldInfoActivity.this, EventInfoActivity.class);
                            x.putExtra("eventId",events.get(position).getEventId());
                            startActivity(x);
                        }
                    });
                }
                if(reports.size() != oldReportListSize) {
                    oldReportListSize = reports.size();
                    Collections.sort(reports);
                    CustomList adapter = new CustomList(FieldInfoActivity.this, reports);

                    repListView.setAdapter(adapter);
                    repListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
                }
                if(field.size()>0){
                    if(field.get(0).getOut())
                        ((TextView) findViewById(R.id.textTypeField)).setText(field.get(0).getDescription()+"\n"+"Outdoor");
                    else
                        ((TextView) findViewById(R.id.textTypeField)).setText(field.get(0).getDescription()+"\n"+"Indoor");
                    if(hash.containsKey(field.get(0).getDescription()))
                    {
                        int position = hash.get(field.get(0).getDescription());
                        ((ImageView) findViewById(R.id.imageTypeSport)).setImageBitmap(getBitmapSavingMem(mThumbIds[position],40,40));
                    }
                }
                if(count < 3)
                    handlerReports.postDelayed(this,2000);

            }
        };

        field = new ArrayList<Field>();
        mDatabase.getOneFieldListener(field, fieldId, (ImageView) findViewById(R.id.field_image));
        System.out.println("field id is = " + fieldId);


        reports = new ArrayList<Report>();
        events = new ArrayList<SimplifiedEvent>();
    }
    public Bitmap getBitmapSavingMem(int resId, int reqWidth, int reqHeight){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(this.getResources(), resId, options);
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
    @Override
    public void onStop(){
        super.onStop();
        handlerReports.removeCallbacksAndMessages(null);
    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        reports.clear();
        events.clear();
        oldEventListSize = 0;
        oldReportListSize = 0;
        mDatabase.getEventsForField(events, fieldId);
        mDatabase.getAllReportsListener(reports, null, fieldId);

        count = 0;
        handlerReports.postDelayed(runnable, 1000);
        eventListView.setAdapter(null);
        repListView.setAdapter(null);
//        try {
//            DrawerUtilActivity.getDrawer(this,getSupportActionBar());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
                s.putExtra("sport",field.get(0).getDescription());
                startActivity(s);
                return true;
//            case android.R.id.home:
//                if (DrawerUtilActivity.result.isDrawerOpen())
//                {
//                    DrawerUtilActivity.result.closeDrawer();
//                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(false);
//                }
//                else {
//                    DrawerUtilActivity.result.openDrawer();
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                    getSupportActionBar().setHomeButtonEnabled(false);
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
