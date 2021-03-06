package eu.croussel.sportyfield.Activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.croussel.sportyfield.Adapters.CustomListEvent;
import eu.croussel.sportyfield.Adapters.CustomListPlayers;
import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class EventInfoActivity extends AppCompatActivity {

    List<Event> events;
    List<User> players;
    List<Field> field;
    Event event;
    ImageView fieldImage;
    TextView textLocation ;
    TextView textDate;
    TextView textDescription;
    Button buttonRegister;
    int oldEventListSize = 0;
    int oldFieldListSize = 0;
    int oldPlayerListSize = 0;
    int eventId;
    int count = -1;
    private Handler handlerList ;
    private FirebaseDBhandler mDatabase;
    private Runnable runnable;
    private ListView listPlayers;
    private int registered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        getSupportActionBar().setTitle("Event");
        listPlayers = (ListView) findViewById(R.id.listViewPlayers);
        fieldImage = findViewById(R.id.imageField);
        textDate = findViewById(R.id.textEventDate);
        textLocation = findViewById(R.id.textEventLoc);
        buttonRegister = findViewById(R.id.buttonRegister);
        textDescription = findViewById(R.id.eventDescription);
        events = new ArrayList<Event>();
        players = new ArrayList<User>();
        field = new ArrayList<Field>();

        mDatabase = new FirebaseDBhandler();
        handlerList = new Handler();

        eventId = getIntent().getIntExtra("eventId", 0);

        listPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(EventInfoActivity.this, "Row number " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        //Every sec we check if the list has changed
        runnable = new Runnable() {
            @Override
            public void run() {
                if(events.size() == oldEventListSize && field.size() == oldFieldListSize && players.size() == oldPlayerListSize)
                    count = count +1;
                if(events.size() != oldEventListSize){
                    oldEventListSize = events.size();
                    if(events.get(0).getEventPlayers() != null && events.get(0).getEventPlayers().contains(mDatabase.getCurrentUID()))
                    {
                        registered = 1;
                        buttonRegister.setText("Unregister");
                    }
                    else
                    {
                        registered = 0;
                        buttonRegister.setText("Register");
                    }

                    textDescription.setText(events.get(0).getEventDescription());
                    textDate.setText(DateFormat.format("yy/MM/dd hh:mm",events.get(0).getEventDate()));
                    mDatabase.getOneFieldListener(field, events.get(0).getFieldId(),fieldImage);
                }
                if(field.size() != oldFieldListSize){
                    oldFieldListSize = field.size();
                    getSupportActionBar().setTitle(field.get(0).getDescription()+" event");
                    textLocation.setText("Event at : "+field.get(0).getLocation());
                }
                if(players.size() != oldPlayerListSize){
                    oldPlayerListSize = players.size();
                    CustomListPlayers adapter = new CustomListPlayers(EventInfoActivity.this,players);
                    listPlayers.setAdapter(adapter);
                }
                if(count < 3)
                    handlerList.postDelayed(this,2000);

            }};
            buttonRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(registered == 1) {
                        mDatabase.unApplyToEvent(events.get(0),EventInfoActivity.this);
                        onResume();
                    }
                    else if(registered == 0)
                    {
                        mDatabase.applyToEvent(events.get(0));
                        onResume();
                    }
                }
            });
    }

    @Override
    public void onResume(){
        super.onResume();
        registered = -1;
        players.clear();
        events.clear();
        oldEventListSize = 0;
        oldPlayerListSize = 0;
        if(count >= 3 || count == -1) {
            count = 0;
            listPlayers.setAdapter(null);
            mDatabase.getEventInfo(events, eventId, players);
        }


        handlerList.postDelayed(runnable, 1000);

    }
}
