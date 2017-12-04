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
        Log.d("User", "User test: " + db.getUser("Test"));
    }
}
