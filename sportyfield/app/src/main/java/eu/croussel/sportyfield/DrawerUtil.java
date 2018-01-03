package eu.croussel.sportyfield;

/**
 * Created by afonso on 26-12-2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.ToLongBiFunction;

public class DrawerUtil extends AppCompatActivity{

    private static FirebaseAuth auth;
    DrawerHeader dHe = new DrawerHeader();
    public static Toolbar toolBar;
    static String facebookUserId = "";
    static String googleUserId = "";
    static String photoUrl;
    static String photo_url; //photo for the email pw user
    static ImageView aux=null;
    static Drawer result;
    static Drawable d = null;
    private static DatabaseReference mDatabase;
    static String userID;
    private static GoogleApiClient mGoogleApiClient;


    private static final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            System.out.println("PASSEI POR SETTING FOTO");
            aux.setImageBitmap(bitmap);
            d = aux.getDrawable();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    public static void getDrawer(final Activity activity) throws IOException {
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null)
        FirebaseUser u = auth.getCurrentUser();
        aux = new ImageView(activity);
        aux.setTag(target);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        SecondaryDrawerItem drawerItemProfile = new SecondaryDrawerItem().withIdentifier(4)
                .withName("Profile").withIcon(R.drawable.user_drawer);
        SecondaryDrawerItem drawerItemTeam = new SecondaryDrawerItem().withIdentifier(5)
                .withName("Teams").withIcon(R.drawable.teammates);
        SecondaryDrawerItem drawerItemMap = new SecondaryDrawerItem().withIdentifier(6)
                .withName("Map").withIcon(R.drawable.maps);
        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(7)
                .withName("Settings").withIcon(R.drawable.settings);
        SecondaryDrawerItem drawerItemLogout = new SecondaryDrawerItem().withIdentifier(8)
                .withName("Logout").withIcon(R.drawable.logout);

        for (final UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            System.out.println("PASSEI POR AQUI COMO SIGNEI?????" + user.getProviderId());
            // check if the provider id matches "facebook.com"
            if (user.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                facebookUserId = user.getUid();
                System.out.println("User is signed in with Facebook");
                // construct the URL to the profile picture, with a custom height alternatively, use '?type=small|medium|large' instead of ?height=
                photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                if (photoUrl != null) {
                    System.out.println("PASSEI POR photourl????? " + photoUrl);
                    Picasso.with(activity).load(photoUrl).into(target);
                } else {
                    System.out.println("PASSEI POR NULL PHOTOURL????? " + photoUrl);
                    Picasso.with(activity).load("@drawable/user_icon").into(aux);
                    d = aux.getDrawable();
                }
            } else if (user.getProviderId().equals("google.com"))
            {
                photoUrl = String.valueOf(user.getPhotoUrl());
                if (photoUrl != null) {
                    System.out.println("PASSEI POR photourl????? " + photoUrl);
                    Picasso.with(activity).load(photoUrl).into(target);
                } else {
                    System.out.println("PASSEI POR NULL PHOTOURL????? " + photoUrl);
                    Picasso.with(activity).load("@drawable/user_icon").into(aux);
                    d = aux.getDrawable();
                }
            }
            else if (user.getProviderId().equals("password"))
            {
                //get the emailId associated with the username
                System.out.println("INSIDE FIREBASE.COM THINGGGGGG????? " + u.getEmail());
                String decodedEmail = decodeUserEmail(u.getEmail());
                System.out.println("Decoded email: " + decodedEmail);
                try {
                    mDatabase.child("user_photos").child(decodedEmail)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        photo_url = dataSnapshot.getValue(String.class);
                                        System.out.println("UserPhoto: " + photo_url);
                                        try {
                                            byte[] decodedString = Base64.decode(photo_url, Base64.DEFAULT);
                                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                            aux.setImageBitmap(decodedByte);
                                            d=aux.getDrawable();
                                        }catch (Exception e) {
                                            System.out.println("After call to firebase about photo exception is: " + e);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError firebaseError) {

                                }
                            });
                }catch (Exception e)
                {
                    System.out.println("Exception is: " + e);
                }
                break;
            }
        }

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                //.withHeaderBackground(R.color.com_facebook_button_background_color)
                .addProfiles(
                        new ProfileDrawerItem().withName(u.getDisplayName()).withEmail(u.getEmail()).withIcon(d)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        //create the drawer and remember the `Drawer` result object
            result = new DrawerBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        drawerItemProfile,
                        drawerItemTeam,
                        drawerItemMap,
                        drawerItemSettings,
                        drawerItemLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(final View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 4) {
                            // load change profile activity
                            Intent intent = new Intent(activity, MapsActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 5) {
                            // load find teams activity
                            Intent intent = new Intent(activity, MapsActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 6) {
                            // load map activity
                            Intent intent = new Intent(activity, MapsActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 7) {
                            // load settings screen
                            Intent intent = new Intent(activity, MapsActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (drawerItem.getIdentifier() == 8) {
                            for (final UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                                // check if the provider id matches "facebook.com" or google or only firebase
                                if (user.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    //add flags to end all activities and out Login on the bottom of the stack
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    view.getContext().startActivity(intent);
                                } else if (user.getProviderId().equals("google.com"))
                                {
                                    FirebaseAuth.getInstance().signOut();
                                    System.out.println("mGoogleApiClient is : " + mGoogleApiClient);
                                    // Google sign out
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(@NonNull Status status) {
                                                    Intent intent = new Intent(activity, LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    view.getContext().startActivity(intent);
                                                }
                                            });
                                }
                                else if (user.getProviderId().equals("password"))
                                {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    view.getContext().startActivity(intent);
                                }
                            }
                        }
                        return true;
                    }
                })
                .build();
    }
    public static String decodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }
}
