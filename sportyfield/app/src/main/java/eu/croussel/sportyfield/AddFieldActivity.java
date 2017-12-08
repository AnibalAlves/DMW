package eu.croussel.sportyfield;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class AddFieldActivity extends AppCompatActivity {

    // Database Helper
    private DataBaseHandler db;
    //Position
    private LatLng fieldPos;

    //List of type of sportfields
    private String[] sports = {"Basketball", "Football", "Tennis", "Pingpong", "Handball"};
    private ListView sportsList;
    private String selectedFromList = new String();
    //lastItemSelected
    private int lastSport = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);

        //Call the db
        db = new DataBaseHandler(this);

        //Recover the intent
        Intent intent = getIntent();
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        fieldPos = bundle.getParcelable("fieldPos");

        //Display the location in the activity
        TextView positionText = (TextView) findViewById(R.id.positionTextView);
        positionText.setText("Add location : \n Latitude : " + fieldPos.latitude + "\n Longitude : " + fieldPos.longitude);

        //Display the listview of the sportfields types
        sportsList = (ListView) findViewById(R.id.listViewSports);
        sportsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sports);

        sportsList.setAdapter(adapter);
        sportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFromList = sports[position];
            }
        });
        //Add the field when button clicked
        final Button addField_button = (Button) findViewById(R.id.button_addField);
        addField_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Boolean isOutdoor = ((CheckBox) findViewById(R.id.checkBox_Outdoor)).isChecked();
                Boolean isPrivate = ((CheckBox) findViewById(R.id.checkBox_Private)).isChecked();

                String description;
                if(isOutdoor) description = "Outdoor "+ selectedFromList+ " field";
                else description = "Indoor "+ selectedFromList+ " field";

                Field newField = new Field(getAddress(),
                        fieldPos.latitude,
                        fieldPos.longitude,
                        isPrivate,
                        isOutdoor,
                        0,
                        description
                );
                db.createField(newField);
                Toast.makeText(getBaseContext(), description,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private String getAddress(){
        String adrs = new String();
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(fieldPos.latitude,
                    fieldPos.longitude,
                    1);
            if(addresses == null) adrs = "No address";
            else adrs = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return adrs ;
    }
}
