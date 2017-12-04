package eu.croussel.sportyfield;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class fieldInfo extends AppCompatActivity {

    // Database Helper
    DataBaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);

        db = new DataBaseHandler(getApplicationContext());

        ImageView iv = (ImageView) findViewById(R.id.field_image);

        Intent intent = getIntent();
        int fieldId = getIntent().getIntExtra("fieldID",0); //get the field id from Maps class
        TextView field_loc = (TextView) findViewById(R.id.field_location);
        field f = db.getField(fieldId);
        String theLocation = f.getLocation();
        field_loc.setText(theLocation);

        switch (fieldId) {
            case 1:
                iv.setImageResource(R.drawable.field);
                break;
            case 2:
                iv.setImageResource(R.drawable.field);
                break;
            case 3:
                iv.setImageResource(R.drawable.field);
                break;
        }
        //Creating a test user
        /*
        user u = new user("Test",25,"test@gmail.com",123456789,25,"Basketball");
        db.createUser(u);
        user f = db.getUser("Test");
        System.out.println("Result - " + f.getEmail());
        //Creating a field
        field fi = new field("MIlanChina",25.2,25.2,true,true);
        db.createField(fi);
        field aux = db.getField(1);
        System.out.println("Result - " + aux.getLocation());
        */
        db.closeDB();
    }
}
