package eu.croussel.sportyfield.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class ClassifyPlayersActivity extends AppCompatActivity {

    // Database Helper
    private FirebaseAuth auth;
    private FirebaseDBhandler mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_players);

        mDatabase = new FirebaseDBhandler();
        //Fields acquisition vars
        List<User> users ;
        //get last event from that user and check how many teammates the user had

        //String[] repDate = new String[events.size()];
        //String[] repDescr = new String[reports.size()];
        //String[] userTy = new String[reports.size()];
        //Integer[] userReput = new Integer[reports.size()];
        //byte[][] repImage = new byte[reports.size()][];
    }
}
