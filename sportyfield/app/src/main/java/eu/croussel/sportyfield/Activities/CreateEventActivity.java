package eu.croussel.sportyfield.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class CreateEventActivity extends AppCompatActivity {

    private static String TAG = "CreateEventActivity: ";
    int fieldId;
    String location;
    EditText name, descript;
    TextView locat, dateText;
    Button create;
    ImageView date;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private int mYear, mMonth, mDay;
    Calendar finalDate = Calendar.getInstance();
    int aux=0;
    private FirebaseDBhandler mDatabase;
    private String sportType;
    private HashMap<String, Integer> hash;
    private Integer[] mThumbIds = {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
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

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        try {
            DrawerUtilActivity.getDrawer(this,getSupportActionBar());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Intent mIntent = getIntent();
        fieldId = mIntent.getIntExtra("fieldId", 0);
        sportType = mIntent.getStringExtra("sport");
        try
        {
            ((ImageView) findViewById(R.id.imageSport)).setImageResource(mThumbIds[hash.get(sportType)]);
        }catch(Exception ex){}

        mDatabase = new FirebaseDBhandler();
        locat = findViewById(R.id.textLocation);
        locat.setText(mIntent.getStringExtra("location").replace(',','\n'));
        name = findViewById(R.id.event_Name);
        descript = findViewById(R.id.event_Description);
        dateText = findViewById(R.id.textDate);
        date = findViewById(R.id.imageDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        create = findViewById(R.id.addEvent);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });


        location = mIntent.getStringExtra("location"); //event location
        locat.setText(location);

    }

    private void createEvent() {
        if (dateText.getText().equals("Calendar") || name.getText().length()<1 || descript.getText().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Please fill all the parameters!", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            //////////////////////
            ///Event Parameters///
            //////////////////////
            Event e;

            String eventName = name.getText().toString();
            //location is on variable location
            String formatedDate = mFormatter.format(finalDate.getTime());
            System.out.println("FormatedDate: " + formatedDate);
            Date eventDate = null;
            try {
                eventDate = mFormatter.parse(formatedDate);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            List<String> players = new ArrayList<String>();
            String eventDescription = descript.getText().toString();
            System.out.println("Variables: " + eventName + "\n" + eventDate + "\n" + players.size() + "\n" + eventDescription + "\n" + fieldId);
            e = new Event(0,"",fieldId,eventName,eventDate,players,eventDescription, sportType);
            mDatabase.createEvent(e);
            finish();
        }
    }

    private void selectDate() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                if (aux==0) { //doing this solves a bug where each timer picker and date picker are called twice
                    mTimePicker = new TimePickerDialog(CreateEventActivity.this,AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 10 && selectedMinute <10) {
                                dateText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +"0"+selectedHour + ":" + "0"+selectedMinute );
                            }
                            if (selectedHour < 10 && selectedMinute >10) {
                                dateText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +"0"+selectedHour + ":" + selectedMinute );
                            }
                            if (selectedHour > 10 && selectedMinute <10) {
                                dateText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +selectedHour + ":" + "0"+selectedMinute );
                            }
                            if (selectedHour > 10 && selectedMinute >10) {
                                dateText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +selectedHour + ":"+selectedMinute);
                            }
                            finalDate.set(year,monthOfYear,dayOfMonth,hour,minute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    aux++;
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Select Day");
        datePickerDialog.show();
        aux=0;
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

