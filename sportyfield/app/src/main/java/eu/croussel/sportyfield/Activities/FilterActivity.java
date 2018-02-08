package eu.croussel.sportyfield.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import eu.croussel.sportyfield.Adapters.ImageAdapter;
import eu.croussel.sportyfield.DB_classes.Filter;
import eu.croussel.sportyfield.R;

public class FilterActivity extends AppCompatActivity {
    //List of type of sportfields
    private List<String> selectedFromList;
    private static ImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_v2);
        final GridView gv = (GridView) findViewById(R.id.gridView);
        adapter = new ImageAdapter();
        final List<Integer> positions = new ArrayList<>();
        selectedFromList = new ArrayList<>();
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gv.setClickable(false);
                ImageView imageView = (ImageView) view;
                String s = selectToString(position);
                if (selectedFromList != null && selectedFromList.contains(s)) {
                    selectedFromList.remove(s);
                    imageView.setBackgroundColor(Color.GRAY);
                } else {
                    selectedFromList.add(selectToString(position));
                    imageView.setBackgroundColor(Color.parseColor("#1976D2"));

                }
                printstackList(selectedFromList);
                gv.setClickable(true);
            }

        });
        //Add the field when button clicked
        final Button applyFilter_button = (Button) findViewById(R.id.applyFilter);
        applyFilter_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Boolean isOutdoor = ((Switch) findViewById(R.id.switchOutdoor)).isChecked();
                Boolean isIndoor = ((Switch) findViewById(R.id.switchIndoor)).isChecked();
                Boolean isPrivate = ((Switch) findViewById(R.id.switchPrivate)).isChecked();
                Boolean isPublic = ((Switch) findViewById(R.id.switchPublic)).isChecked();

                Filter filter = new Filter(isOutdoor, isIndoor, isPrivate, isPublic, selectedFromList);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("filter", filter);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
        private String selectToString(int position){
            String s;
            switch(position){
                case 0 :
                    s = "Basketball";
                    break;
                case 1 :
                    s = "Archery";
                    break;
                case 2 :
                    s = "Athletism";
                    break;
                case 3 :
                    s = "Badminton";
                    break;
                case 4 :
                    s = "Baseball";
                    break;
                case 5 :
                    s = "Bowling";
                    break;
                case 6 :
                    s = "Boxe";
                    break;
                case 7 :
                    s = "Curling";
                    break;
                case 8 :
                    s = "Diving";
                    break;
                case 9 :
                    s = "Fishing";
                    break;
                case 10 :
                    s = "Fitness";
                    break;
                case 11 :
                    s = "Golf";
                    break;
                case 12 :
                    s = "Gymnastic";
                    break;
                case 13 :
                    s = "Hockey";
                    break;
                case 14 :
                    s = "Judo";
                    break;
                case 15 :
                    s = "Kayak";
                    break;
                case 16 :
                    s = "Pingpong";
                    break;
                case 17 :
                    s = "Rugby";
                    break;
                case 18 :
                    s = "Running";
                    break;
                case 19 :
                    s = "Soccer";
                    break;
                case 20 :
                    s = "Swimming";
                    break;
                case 21 :
                    s = "Tennis";
                    break;
                default :
                    s = "Volleyball";
                    break;
            }
        return s;
    }

    void printstackList(List<String> s){
            for(String str:s)
                Log.d("LIST", "found : " + str);
    }
}
