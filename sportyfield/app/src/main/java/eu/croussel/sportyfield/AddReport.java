package eu.croussel.sportyfield;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddReport extends AppCompatActivity {

    DataBaseHandler db;
    int fieldId;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        db = new DataBaseHandler(getApplicationContext());

        Intent mIntent = getIntent();
        fieldId = mIntent.getIntExtra("fieldId", 0);
        userName = mIntent.getStringExtra("uName");
    }

    public void imageSearch(View view) {
        //implement method to select an image from the phone
    }

    public void repAdd(View view) {
        //add report to the db, but first get info from image and edit text
        EditText descr = (EditText) findViewById(R.id.descr);
        String des = descr.getText().toString();
        Report r = new Report(des,fieldId,userName);
        db.createReport(r);
        Intent back = new Intent(this,FieldInfo.class);
        startActivity(back);
        finish();
    }
}
