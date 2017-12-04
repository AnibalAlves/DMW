package eu.croussel.sportyfield;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class fieldInfo extends AppCompatActivity {

    // Database Helper
    DataBaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_info);

        db = new DataBaseHandler(getApplicationContext());

        //Creating a test user
        user u = new user("Test",25,"test@gmail.com",123456789,25,"Basketball");
        db.createUser(u);
        user f = db.getUser("Test");
        System.out.println("Result - " + f.getEmail());
        //Creating a field
        field fi = new field("MIlanChina",25.2,25.2,true,true);
        db.createField(fi);
        field aux = db.getField(1);
        System.out.println("Result - " + aux.getLocation());
    }
}
