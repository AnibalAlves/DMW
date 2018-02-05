package eu.croussel.sportyfield.Activities;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.User;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class ChangeProfileActivity extends AppCompatActivity {

    private static String TAG = "changeProfileActivity: ";
    private static TextView email, pw, uName, phone, age, favSport, repu;
    private static ImageButton editEmail, editPw, editUname, editPhone, editAge, editFavSport, editProfilePic;
    private static Button saveButton;
    private static RelativeLayout rl;
    private static FirebaseAuth auth;
    private static GoogleApiClient mGoogleApiClient;
    static String facebookUserId = "";
    static String photoUrl;
    private static ImageView profilePic;

    private static String user_email = "";
    FirebaseUser owner;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri selectedImage;
    private StorageReference imageReference;
    private StorageReference fileRef;
    private static String utilizador = "";


    private FirebaseDBhandler mDatabase;
    private List<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        try {
            DrawerUtilActivity.getDrawer(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewInit();
        mDatabase = new FirebaseDBhandler();
        FirebaseUser u = mDatabase.getCurrentFirebaseUser();
        users = new ArrayList<User>();
        mDatabase.getUserToList(users, u.getUid());
        final Handler handler;
        handler = new Handler();

        //Every sec we check if the list has changed
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (users.size() > 0) {
                    User user = users.get(0);
                    email.setText(user.getEmail());
                    pw.setText("Password");
                    uName.setText(user.getUserName());
                    phone.setText(user.getPhone());
                    age.setText(Integer.toString(user.getAge()));
                    favSport.setText(user.getFavSport());
                    repu.setText(Integer.toString(user.getReputation()));
                    if (user.get_image() != null) {
                        Bitmap bitmap = getBitmapSavingMem(user.get_image());
                        profilePic.setImageBitmap(bitmap);

                    }
                } else
                    handler.postDelayed(this, 2000);

            }
        };
        handler.postDelayed(runnable, 1000);

        for (final UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            // check if the provider id matches "facebook.com"
            if (user.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                facebookUserId = user.getUid();
                // construct the URL to the profile picture, with a custom height alternatively, use '?type=small|medium|large' instead of ?height=
                photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                editEmail.setClickable(true);
                editEmail.setVisibility(View.VISIBLE);
                editPw.setClickable(false);
                editPw.setVisibility(View.INVISIBLE);
                editUname.setClickable(true);
                editUname.setVisibility(View.VISIBLE);
                editProfilePic.setClickable(false);
                editProfilePic.setVisibility(View.INVISIBLE);
                LinearLayout paren = findViewById(R.id.parent);
//                paren.removeView(rl);
//                rl.setVisibility(View.INVISIBLE);


                if (photoUrl != null) {
                    Picasso.with(this).load(photoUrl).into(target);
                } else {
                    Picasso.with(this).load("@drawable/ic_account_circle_black_36dp").into(profilePic);
                }

            } else if (user.getProviderId().equals("google.com")) //logged in with google
            {
                editEmail.setClickable(true);
                editEmail.setVisibility(View.INVISIBLE);
                editUname.setClickable(true);
                editUname.setVisibility(View.INVISIBLE);
                editProfilePic.setClickable(false);
                editProfilePic.setVisibility(View.INVISIBLE);
                LinearLayout paren = findViewById(R.id.parent);
                paren.removeView(rl);
                rl.setVisibility(View.INVISIBLE);


                photoUrl = String.valueOf(user.getPhotoUrl());
                if (photoUrl != null) {
                    Picasso.with(this).load(photoUrl).into(target);
                } else {
                    Picasso.with(this).load("@drawable/ic_account_circle_black_36dp").into(profilePic);
                }
            } else if (user.getProviderId().equals("password")) {

                editEmail.setClickable(true);
                editEmail.setVisibility(View.INVISIBLE);

            }
        }
    }

    private void changeProfilePic() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            profilePic.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            imageReference = FirebaseStorage.getInstance().getReference().child("images");
            fileRef = imageReference.child(utilizador + "." + getFileExtension(selectedImage));

            try {
                fileRef.putFile(selectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String url = taskSnapshot.getDownloadUrl().toString();

                                try {
//                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
//                                    DatabaseReference imgs = database.getReference("images");
//                                    mDatabase.child("user_photos_test").child(decodeUserEmail(user_email)).setValue(url);
                                } catch (Exception e) {
                                    System.out.println("Exception is: " + e);
                                }
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
            } catch (Exception e) {
                Log.i(TAG, "Exception uploading photo is: " + e);
            }
        }
    }

    private static final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //test if code reach here to get the profile pic
            Log.i(TAG, "Inside onBitMapLoaded: " + bitmap + " " + from);
            profilePic.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public static String decodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void editDialog(String str, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view, null);
        final EditText edit_dialog = (EditText) view.findViewById(R.id.edit_dialog);
        builder.setView(view);
        builder.setNegativeButton("cancel", null);
        switch (str) {
            case "e":
                builder.setTitle("New email");
                edit_dialog.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String em = "";
                        em = edit_dialog.getText().toString();
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                            email.setText(em);
                            users.get(0).setEmail(em);
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter a valid email address!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                builder.show();
                break;
            case "p":
                builder.setTitle("New password");
                edit_dialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = edit_dialog.getText().toString();
                        if (pw.length() >= 6) {
                            owner.updatePassword(pw);
                            email.setText(pw);
                        } else {
                            Toast.makeText(getApplicationContext(), "Password too short. Minimum 6 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                builder.show();
                break;
            case "u":
                builder.setTitle("New username");
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edit_dialog.getText().toString();
                        if (name.length() >= 1) {
//                            DatabaseReference insideUser = mDatabase.child("users");
//                            insideUser.child(decodeUserEmail(user_email)).child("userName").setValue(name);
                            uName.setText(name);
                            users.get(0).setUserName(name);
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter a valid email address!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                builder.show();
                break;
            case "ph":
                builder.setTitle("New phone number");
                edit_dialog.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("Teste");
                        Integer phonen = null;
                        try {
                            phonen = Integer.parseInt(edit_dialog.getText().toString());
                        } catch (Exception e) {
                            Log.i(TAG, "Exception is: " + e);
                        }
                        if (phonen != null) {
                            try {
                                String newPhone = String.valueOf(phonen);
                                phone.setText(newPhone);
                                users.get(0).setPhone(newPhone);
                            } catch (Exception e) {
                                Log.i(TAG, "Exception updating phone: " + e);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Insert a valid phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                break;
            case "a":
                builder.setTitle("New age");
                edit_dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer idade = null;
                        try {
                            idade = Integer.parseInt(edit_dialog.getText().toString());
                        } catch (Exception e) {
                        }
                        if (idade != null && idade >= 1 && idade < 100) {
                            try {
//                                DatabaseReference insideUser = mDatabase.child("users");
//                                insideUser.child(decodeUserEmail(user_email)).child("age").setValue(idade);
                                String newAge = String.valueOf(idade);
                                age.setText(newAge);
                                users.get(0).setAge(Integer.parseInt(newAge));
                            } catch (Exception e) {
                                Log.i(TAG, "Exception updating age: " + e);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Insert a valid age", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                builder.show();
                break;
            case "s":
                builder.setTitle("New favourite sport");
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String favS = edit_dialog.getText().toString();
                        if (favS.length() >= 1) {
                            try {
//                                DatabaseReference insideUser = mDatabase.child("users");
//                                insideUser.child(decodeUserEmail(user_email)).child("favSport").setValue(favS);
                                favSport.setText(favS);
                                users.get(0).setFavSport(favS);
                            } catch (Exception e) {
                                Log.i(TAG, "Exception updating sport: " + e);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Insert a valid sport", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    //Called when one of the action button is called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Drawer nav.
            case android.R.id.home:
                if (DrawerUtilActivity.result.isDrawerOpen()) {
                    DrawerUtilActivity.result.closeDrawer();
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                } else {
                    DrawerUtilActivity.result.openDrawer();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                return true;

            //Default, do nothing
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewInit() {
        email = findViewById(R.id.email);
        pw = findViewById(R.id.password);
        uName = findViewById(R.id.uName);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        favSport = findViewById(R.id.favSport);
        repu = findViewById(R.id.rep);
        rl = findViewById(R.id.passGone);

        editEmail = findViewById(R.id.editEmail);
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("e", email.getText().toString());
            }
        });

        editPw = findViewById(R.id.editPassword);
        editPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("p", email.getText().toString());
            }
        });

        editUname = findViewById(R.id.editUname);
        editUname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("u", email.getText().toString());
            }
        });

        editPhone = findViewById(R.id.editPhone);
        editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("ph", email.getText().toString());
            }
        });

        editAge = findViewById(R.id.editAge);
        editAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("a", email.getText().toString());
            }
        });

        editFavSport = findViewById(R.id.editSport);
        editFavSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog("s", email.getText().toString());
            }
        });
        saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.updateUser(users.get(0),mDatabase.getCurrentUID());
            }
        });
        profilePic = findViewById(R.id.profilePic);
        editProfilePic = findViewById(R.id.editProfilePic);
        editProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePic();
            }
        });
    }

    private static Bitmap getBitmapSavingMem(byte[] image) {
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (image != null) BitmapFactory.decodeByteArray(image, 0, image.length, options);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image, 0, image.length, options);
    }
}
