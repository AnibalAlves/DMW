package eu.croussel.sportyfield.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SIGNUP.";
    private EditText inputEmail, inputPassword, inputUserName, inputPhone, inputAge;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String[] sports = {"Basketball", "Football", "Tennis", "Pingpong", "Handball"};
    private Spinner sportsList;
    private String selectedFromList = new String();
    private Uri selectedImage;
    ImageView im;

    private FirebaseDBhandler db;
    Bitmap photoBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        db = new FirebaseDBhandler();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUserName = findViewById(R.id.userName);
        inputPhone = findViewById(R.id.phone);
        inputAge = findViewById(R.id.age);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        im = findViewById(R.id.imageView);

        //Display the listview of the sportfields types
        sportsList = findViewById(R.id.listViewSports_signup);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, sports);
        sportsList.setAdapter(adapter);

        AdapterView.OnItemSelectedListener sport = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,
                                       int position, long id) {
                selectedFromList=spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup().setWidth(im.getMaxWidth()).setHeight(im.getMaxHeight()))
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult pickResult) {
                                Log.d("PATH",pickResult.getPath() +"-"+pickResult);
//                                setPic(pickResult.getPath());
                                im.setImageBitmap(pickResult.getBitmap());
                            }
                        }).show(SignupActivity.this);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String uName = inputUserName.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();
                String age = inputAge.getText().toString().trim();
                selectedFromList=sportsList.getSelectedItem().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (uName.length() < 1 || phone.length() < 1 || age.length() < 1)
                {
                    Toast.makeText(getApplicationContext(),"Please fill all the parameters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                final User u = new User();
                u.setAge(Integer.parseInt(String.valueOf(inputAge.getText())));
                u.setEmail(email);
                selectedFromList=sportsList.getSelectedItem().toString();
                u.setFavSport(selectedFromList);
                u.setPhone(inputPhone.getText().toString().trim());
                u.setReputation(0);
                u.setType("Amateur");
                final String usName = inputUserName.getText().toString().trim();
                u.setUserName(usName);

                byte[] imageInByte;
                try {
                    Bitmap bitmap = ((BitmapDrawable) im.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageInByte = baos.toByteArray();
                }
                catch(NullPointerException ex){
                    imageInByte = null;
                }
                //create user
                db.createUser(u, imageInByte, u.getEmail(),password,progressBar,getBaseContext());

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
