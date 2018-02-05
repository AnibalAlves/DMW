package eu.croussel.sportyfield.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.croussel.sportyfield.Adapters.CustomListEvent;
import eu.croussel.sportyfield.DB_classes.SimplifiedEvent;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class ListEventsActivity extends AppCompatActivity {

    List<SimplifiedEvent> events;
    int oldEventListSize = -1;
    private Handler handlerList ;
    private FirebaseDBhandler mDatabase;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        final ListView listView = (ListView) findViewById(R.id.listEvents);
        events = new ArrayList<SimplifiedEvent>();
        mDatabase = new FirebaseDBhandler();
        handlerList = new Handler();

        mDatabase.getAllOwnedSimplifiedEvents(events);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListEventsActivity.this, "Row number " + position,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListEventsActivity.this, EventInfoActivity.class);
                intent.putExtra("eventId", events.get(position).getEventId());
                startActivity(intent);
            }
        });
        //Every sec we check if the list has changed
        runnable = new Runnable() {
            @Override
            public void run() {
                if(events.size() != oldEventListSize){
                    oldEventListSize = events.size();
                    CustomListEvent adapter = new CustomListEvent(ListEventsActivity.this,events);
                    listView.setAdapter(adapter);
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
