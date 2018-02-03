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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.croussel.sportyfield.R;

public class CreateEventActivity extends AppCompatActivity {

    private static String TAG = "CreateEventActivity: ";
    int fieldId;
    String location;
    EditText name, descript;
    TextView locat;
    Button date, create;
    NumberPicker numberPla;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    private int mYear, mMonth, mDay;
    int aux=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        locat = findViewById(R.id.locati);
        name = findViewById(R.id.eveName);
        descript = findViewById(R.id.descriptor);

        date = findViewById(R.id.dateChoice);
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

        numberPla = findViewById(R.id.numbers);
        Intent mIntent = getIntent();
        fieldId = mIntent.getIntExtra("fieldId", 0);

        location = mIntent.getStringExtra("location"); //event location
        locat.setText(location);

        numberPla.setMinValue(0);
        numberPla.setMaxValue(25);
        numberPla.setWrapSelectorWheel(true);
    }

    private void createEvent() {
        if (date.getText().equals("Calendar") || name.getText().length()<1 || descript.getText().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Please fill all the parameters!", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            //////////////////////
            ///Event Parameters///
            //////////////////////
            String eventName = name.getText().toString();
            //location is on variable location
            String eventDate = date.getText().toString();
            Integer numberPlayers = numberPla.getValue(); //stored as a list of users with this size
            String eventDescription = descript.getText().toString();
            //event owner = firebase user
            //list of users with the size of numberPlayers
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
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                if (aux==0) { //doing this solves a bug where each timer picker and date picker are called twice
                    mTimePicker = new TimePickerDialog(CreateEventActivity.this,AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (selectedHour < 10 && selectedMinute <10) {
                                date.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +"0"+selectedHour + ":" + "0"+selectedMinute + " Hh:mm");
                            }
                            if (selectedHour < 10 && selectedMinute >10) {
                                date.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +"0"+selectedHour + ":" + selectedMinute + " Hh:mm");
                            }
                            if (selectedHour > 10 && selectedMinute <10) {
                                date.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +selectedHour + ":" + "0"+selectedMinute + " Hh:mm");
                            }
                            if (selectedHour > 10 && selectedMinute >10) {
                                date.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth + " - " +selectedHour + ":"+selectedMinute + " Hh:mm");
                            }
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
}

