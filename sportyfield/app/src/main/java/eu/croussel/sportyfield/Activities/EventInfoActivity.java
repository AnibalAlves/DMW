package eu.croussel.sportyfield.Activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
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

    int oldEventListSize = 0;
    int oldFieldListSize = 0;
    int oldPlayerListSize = 0;


    private Handler handlerList ;
    private FirebaseDBhandler mDatabase;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        final ListView listPlayers = (ListView) findViewById(R.id.listViewPlayers);
        fieldImage = findViewById(R.id.imageField);
        textDate = findViewById(R.id.textEventDate);
        textLocation = findViewById(R.id.textEventLoc);

        events = new ArrayList<Event>();
        players = new ArrayList<User>();
        field = new ArrayList<Field>();

        mDatabase = new FirebaseDBhandler();
        handlerList = new Handler();

        int eventId = getIntent().getIntExtra("eventId", 0);
        mDatabase.getEventInfo(events,eventId,players);

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
                if(events.size() != oldEventListSize){
                    oldEventListSize = events.size();
                    event = events.get(0);
                    mDatabase.getOneFieldListener(field, event.getFieldId(),fieldImage);
                }
                if(field.size() != oldFieldListSize){
                    oldFieldListSize = field.size();
                    textDate.setText("Date of event : " + DateFormat.format("yy/MM/dd hh:mm",event.getEventDate()));
                    textLocation.setText("Event at : "+field.get(0).getLocation());
                }
                if(players.size() != oldPlayerListSize){
                    oldPlayerListSize = players.size();
                    CustomListPlayers adapter = new CustomListPlayers(EventInfoActivity.this,players);
                    listPlayers.setAdapter(adapter);
                }
                handlerList.postDelayed(this,2000);

            }};
    }

    @Override
    public void onResume(){
        super.onResume();
        handlerList.postDelayed(runnable, 1000);

    }
}
