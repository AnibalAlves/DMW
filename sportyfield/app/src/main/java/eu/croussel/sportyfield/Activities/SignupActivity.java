package eu.croussel.sportyfield.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.R;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUserName, inputPhone, inputAge;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String[] sports = {"Basketball", "Football", "Tennis", "Pingpong", "Handball"};
    private Spinner sportsList;
    private String selectedFromList = new String();
    private DatabaseReference mDatabase;
    private StorageReference imageReference;
    private StorageReference fileRef;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;
    ImageView im;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

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

                if (im.getDrawable()==null)
                {
                    Toast.makeText(getApplicationContext(),"Select a profile image!", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.VISIBLE);

                final User u = new User();
                u.setAge(Integer.parseInt(String.valueOf(inputAge.getText())));
                u.setEmail(email);
                selectedFromList=sportsList.getSelectedItem().toString();
                u.setFavSport(selectedFromList);
                u.setPhone(Integer.parseInt(String.valueOf(inputPhone.getText())));
                u.setPw(password);
                u.setReputation(0);
                u.setType("Amateur");
                final String usName = inputUserName.getText().toString().trim();
                u.setUserName(usName);

                imageReference = FirebaseStorage.getInstance().getReference().child("images");
                fileRef = imageReference.child(usName + "." + getFileExtension(selectedImage));

                fileRef.putFile(selectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String url = taskSnapshot.getDownloadUrl().toString();

                                // use Firebase Realtime Database to store [name + url]
                                writeNewImageInfoToDB(u.getEmail(), url);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // ...
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            }
                        })
                        .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                // ...
                            }
                        });

                Bitmap bitmap = ((BitmapDrawable) im.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteFormat = stream.toByteArray();
                final String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
                u.setPhoto(encodedImage);

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                writeNewUser(u,usName);
                                mDatabase.child("email_ids").child(usName).setValue(email);
                                String replacedEmail = encodeUserEmail(email);
                                mDatabase.child("userName_ids").child(replacedEmail).setValue(u.getUserName());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed!" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    System.out.println("SIGNUP EXCEPTION IS: " + task.getException());
                                } else {
                                    Toast.makeText(SignupActivity.this, "User created with success!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    private void writeNewUser(User u, String uName)
    {

        mDatabase.child("users").child(uName).setValue(u);
    }

    private void writeNewImageInfoToDB(String name, String url) {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference imgs = database.getReference("images");
            mDatabase.child("user_photos_test").child(encodeUserEmail(name)).setValue(url);
        }catch (Exception e)
        {
            System.out.println("Exception is: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void imageSearch(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            im.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
