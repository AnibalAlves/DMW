package eu.croussel.sportyfield;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FieldInfo extends AppCompatActivity {

    // Database Helper
    DataBaseHandler db;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);

        db = new DataBaseHandler(getApplicationContext());

        ImageView iv = (ImageView) findViewById(R.id.field_image);

        Intent intent = getIntent();
        int fieldId = intent.getIntExtra("fieldID",0); //get the Field id from Maps class
        System.out.println("field id is = " + fieldId);

        TextView field_loc = (TextView) findViewById(R.id.field_location);
        Field f = db.getField(fieldId);
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

        ArrayList<Report> reports = new ArrayList<Report>();
        reports = (ArrayList<Report>) db.getAllReport(fieldId);
        String[] repDate = new String[0];
        String[] repDescr;
        String[] userTy;
        Integer[] userReput;
        for (int i=0;i<reports.size();i++)
        {
            //repDate[i] = reports[i].getDate();

        }
        CustomList adapter = new CustomList(this, web, imageId);

        //Creating a test User
        /*
        User u = new User("Test",25,"test@gmail.com",123456789,25,"Basketball");
        db.createUser(u);
        User f = db.getUser("Test");
        System.out.println("Result - " + f.getEmail());
        //Creating a Field
        Field fi = new Field("MIlanChina",25.2,25.2,true,true);
        db.createField(fi);
        Field aux = db.getField(1);
        System.out.println("Result - " + aux.getLocation());
        */
        db.closeDB();
    }

    public void upArrow(View v)
    {

    }

    public void downArrow(View v)
    {

    }
}
