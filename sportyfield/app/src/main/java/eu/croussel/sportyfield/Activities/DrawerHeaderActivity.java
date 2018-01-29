package eu.croussel.sportyfield.Activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

import eu.croussel.sportyfield.R;

/**
 * Created by afonso on 26-12-2017.
 */

@NonReusable
@Layout(R.layout.drawer_header)

public class DrawerHeaderActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @View(R.id.profileImageView)
    private ImageView profilePic;

    @View(R.id.userName)
    private TextView uName;

    @View(R.id.emailTxt)
    private TextView email;

    public DrawerHeaderActivity() {
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser u = auth.getInstance().getCurrentUser();
        String facebookUserId = "";
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                System.out.println("User is signed in with Facebook");
                facebookUserId = u.getUid();
                // construct the URL to the profile picture, with a custom height
                // alternatively, use '?type=small|medium|large' instead of ?height=
                String photoUrl = "https://www.facebook.com/" + facebookUserId;
                // (optional) use Picasso to download and show to image
                System.out.println("PASSEI NO FACEBOOK CENAS: " + facebookUserId);
                //Picasso.with(this).load(photoUrl).into(profilePic);
                System.out.println("APÓS LOAD FOTO");
                uName.setText(u.getDisplayName());
                email.setText(u.getEmail());

            } else if (u.getProviderId().equals("google.com")) {
                //code for info with google
            } else {
                //code for info with email + pw
            }
        }
    }
}
