package eu.croussel.sportyfield.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import eu.croussel.sportyfield.DB_classes.Filter;
import eu.croussel.sportyfield.R;

public class FilterActivity extends AppCompatActivity {
    //List of type of sportfields
    private String[] sports = {"Basketball", "Football", "Tennis", "Pingpong", "Handball"};
    private ListView sportsList;
    private String selectedFromList = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //Display the listview of the sportfields types
        sportsList = (ListView) findViewById(R.id.listViewSports_filter);
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
        final Button applyFilter_button = (Button) findViewById(R.id.button_addField_filter);
        applyFilter_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Boolean isOutdoor = ((CheckBox) findViewById(R.id.checkBox_Outdoor_filter)).isChecked();
                Boolean isIndoor = ((CheckBox) findViewById(R.id.checkBox_Indoor_filter)).isChecked();
                Boolean isPrivate = ((CheckBox) findViewById(R.id.checkBox_Private_filter)).isChecked();
                Boolean isPublic = ((CheckBox) findViewById(R.id.checkBox_Public_filter)).isChecked();
                String fieldType = selectedFromList;

                Filter filter = new Filter(isOutdoor,isIndoor,isPrivate,isPublic,fieldType);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("filter",filter);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
