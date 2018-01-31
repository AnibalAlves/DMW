package eu.croussel.sportyfield.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import eu.croussel.sportyfield.R;

public class ChangeProfileActivity extends AppCompatActivity {

    private static String TAG = "changeProfileActivity: ";
    private static TextView email, pw, uName, phone, age, favSport;
    private static ImageButton editEmail, editPw, editUname ,editPhone, editAge, editFavSport;
    private static FirebaseAuth auth;
    private static DatabaseReference mDatabase;
    private static GoogleApiClient mGoogleApiClient;
    static String facebookUserId = "";
    static String photoUrl;
    private static ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        email = findViewById(R.id.email);
        pw = findViewById(R.id.password);
        uName = findViewById(R.id.uName);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        favSport = findViewById(R.id.favSport);
        editEmail = findViewById(R.id.editEmail);
        editPw = findViewById(R.id.editPassword);
        editUname = findViewById(R.id.editUname);
        editPhone = findViewById(R.id.editPhone);
        editAge = findViewById(R.id.editAge);
        editFavSport = findViewById(R.id.editSport);
        profilePic = findViewById(R.id.profilePic);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null)
        FirebaseUser u = auth.getCurrentUser();
        u.getEmail();

        for (final UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            // check if the provider id matches "facebook.com"
            if (user.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                facebookUserId = user.getUid();
                // construct the URL to the profile picture, with a custom height alternatively, use '?type=small|medium|large' instead of ?height=
                photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                editEmail.setClickable(false);
                editEmail.setVisibility(View.INVISIBLE);
                editUname.setClickable(false);
                editUname.setVisibility(View.INVISIBLE);
                if (photoUrl != null) {
                    Picasso.with(this).load(photoUrl).into(target);
                } else {
                    Picasso.with(this).load("@drawable/ic_account_circle_black_36dp").into(profilePic);
                }
            } else if (user.getProviderId().equals("google.com"))
            {
                photoUrl = String.valueOf(user.getPhotoUrl());
                if (photoUrl != null) {
                    Picasso.with(this).load(photoUrl).into(target);
                } else {
                    Picasso.with(this).load("@drawable/ic_account_circle_black_36dp").into(profilePic);
                }
            }
            else if (user.getProviderId().equals("password"))
            {
                mDatabase.child("user_photos_test").child(decodedEmail)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    photoUrl = dataSnapshot.getValue(String.class);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

                            }
                        });
                Picasso.with(this).load(photoUrl).into(target);
                mDatabase.child("userName_ids").child(decodedEmail)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    uname = dataSnapshot.getValue(String.class);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

                            }
                });
            }
        }
    }

    private static final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //test if code reach here to get the profile pic
            Log.i(TAG,"Inside onBitMapLoaded: " + bitmap + " " + from);
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

}
