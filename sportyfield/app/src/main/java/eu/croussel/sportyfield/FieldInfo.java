package eu.croussel.sportyfield;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FieldInfo extends Activity {

    // Database Helper
    DataBaseHandler db;
    int fieldId;
    String testUsername = "Afonso";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);

        db = new DataBaseHandler(getApplicationContext());

        ImageView iv = (ImageView) findViewById(R.id.field_image);

        Intent intent = getIntent();
        fieldId = intent.getIntExtra("fieldID",0); //get the Field id from Maps class
        System.out.println("field id is = " + fieldId);

        //CREATE SOME DB TABLES FOR TESTING
//        //Creating a Field with id=2
//        Field fi = new Field("MILAN POLITECNICO",25.2,25.2,false
//                ,true,fieldId,"Great for 5vs5 football");
//        db.createField(fi);
        //CREATING USER
        User afon = new User("Afonso",22,"test@gmail.com"
                ,123456789,25,"Basketball","PRO USER");
        db.createUser(afon);
        //CREATING SOME REPORTS OF THE FIELD
        Report newRe = new Report("Net with some holes", fieldId, "Afonso");
        db.createReport(newRe);

        Report newRe1 = new Report("Ring broken ofc", fieldId, "Afonso");
        db.createReport(newRe1);

        Report newRe2 = new Report("Nothing more", fieldId, "Afonso");
        db.createReport(newRe2);

        TextView field_loc = (TextView) findViewById(R.id.field_location);
        Field f = db.getField(fieldId);
        System.out.println("field info + " + f.getLocation());
        byte[] image = f.getImage();
        if(image == null) iv.setImageResource(R.drawable.field);
        else iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0 ,image.length));
        String theLocation = f.getLocation();
        field_loc.setText(theLocation);

//        switch (fieldId) {
//            case 1:
//                iv.setImageResource(R.drawable.field);
//                break;
//            case 2:
//                iv.setImageResource(R.drawable.field);
//                break;
//            case 3:
//                iv.setImageResource(R.drawable.field);
//                break;
//            default:
//                iv.setImageResource(R.drawable.field);
//                break;
//        }

        List<Report> reports = db.getAllReport(fieldId);
        System.out.println("number of reports is " + reports.size());

        String[] repDate = new String[reports.size()];
        String[] repDescr = new String[reports.size()];
        String[] userTy = new String[reports.size()];
        Integer[] userReput = new Integer[reports.size()];
        for (int i=0;i<reports.size();i++)
        {
            repDate[i] = reports.get(i).getDate();
            repDescr[i] = reports.get(i).getDescr();
            String uNameToReport = reports.get(i).getUserName();
            userTy[i] = db.getUser(uNameToReport).getType();
            userReput[i] = db.getUser(uNameToReport).getReputatio();
        }

        CustomList adapter = new CustomList(FieldInfo.this, userTy,repDate,repDescr,userReput);
        ListView rep = (ListView) findViewById(R.id.reports);
        System.out.println("CHECKING WHERE THE APP IS CRASHING!!!!!!!!!!!!!!!!! ");
        rep.setAdapter(adapter);
        rep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        }
                                    });
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

    public void addRep(View view) {
        Intent newRe = new Intent(this,AddReport.class);
        newRe.putExtra("fieldId",fieldId);
        newRe.putExtra("uName",testUsername);
        startActivity(newRe);
        finish();
    }

    public void getBackToMaps(View view) {
        finish();
    }
}
