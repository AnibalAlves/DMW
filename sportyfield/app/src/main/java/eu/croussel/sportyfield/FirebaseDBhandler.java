package eu.croussel.sportyfield;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import eu.croussel.sportyfield.Activities.LoginActivity;
import eu.croussel.sportyfield.Activities.MapsActivity;
import eu.croussel.sportyfield.Activities.SignupActivity;
import eu.croussel.sportyfield.DB_classes.Event;
import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.Report;
import eu.croussel.sportyfield.DB_classes.User;

import static java.lang.Thread.sleep;

/**
 * Created by root on 30/01/18.
 */

public class FirebaseDBhandler {
    private DatabaseReference db;
    private int id ;
    private StorageReference storage ;
    private FirebaseAuth auth;
    final long ONE_MEGABYTE = 1024*1024*5;


    public FirebaseDBhandler() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public String getCurrentUID(){
        return auth.getCurrentUser().getUid();
    }
    public void getAllFieldsListener(final List<Field> fields) {
        db.child("field").orderByChild("id")
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    fields.clear();
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        final Field fi = snap.getValue(Field.class);
                                        fields.add(fi);
//                                        StorageReference imRef = storage.child("images/field/"+fi.getId());
//                                        Task<byte[]> downloadTask = imRef.getBytes(ONE_MEGABYTE)
//                                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                                    @Override
//                                                    public void onSuccess(byte[] bytes) {
//                                                        fi.setImage(bytes);
//                                                        fields.add(fi);
//                                                        Log.d("DL IMAGE", "SUCCESS");
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        fi.setImage(null);
//                                                        fields.add(fi);
//                                                        Log.d("DL IMAGE", "FAIL");
//
//                                                    }
//                                                });

                                        Log.d("DEBUG getAllfields ", "Here is the field id found : "+ fi.getId());
                                    }
                                }
                                catch(NullPointerException e){
                                    fields.clear();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }


    public void createField(final Field f){
        db.child("field").orderByChild("id").limitToLast(1)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int maxId = 0;
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    try {
                                        Field fi = snap.getValue(Field.class);
                                        maxId = fi.getId();
                                    } catch (NullPointerException e) {
                                    }
                                    f.setId(maxId + 1);
                                    if (f.getImage() != null) {
                                        putFieldPic(f.getId(), f.getImage());
                                        f.setImage(null);
                                    }
                                    db.child("field").push().setValue(f);

                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }

    private void putFieldPic(int fieldId, byte[] image) {
        StorageReference imRef = storage.child("images/field/"+fieldId);
        UploadTask uploadTask = imRef.putBytes(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public void getOneFieldListener(final List<Field> fields, int fieldId, final ImageView imageView) {
        Log.d("GET ONE FIELD", "field id is :"+fieldId);
        db.child("field").orderByChild("id").equalTo(fieldId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    fields.clear();
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        final Field fi = snap.getValue(Field.class);
                                        fields.add(fi);
                                        StorageReference imRef = storage.child("images/field/"+fi.getId());
                                        Task<byte[]> downloadTask = imRef.getBytes(ONE_MEGABYTE)
                                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        fi.setImage(bytes);
                                                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length));
                                                        fields.add(fi);
                                                        Log.d("DL IMAGE", "SUCCESS");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        fi.setImage(null);
                                                        fields.add(fi);
                                                        Log.d("DL IMAGE", "FAIL");

                                                    }
                                                });

                                        Log.d("DEBUG getAllfields ", "Here is the field id found : "+ fi.getId());
                                    }
                                }
                                catch(NullPointerException e){
                                    fields.clear();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }
    public void createReport(final Report report){
        db.child("report").orderByChild("reportId").limitToLast(1)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int maxRepId = 0;
                                try {
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        Report r = snap.getValue(Report.class);
                                        maxRepId = r.getreportId();
                                    }
                                }
                                catch(NullPointerException e){

                                }
                                report.setreportId(maxRepId+1);
                                if(report.getRepImage() != null) {
                                    putReportPic(report.getreportId(), report.getRepImage());
                                    report.setRepImage(null);
                                }
                                db.child("report").push().setValue(report);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }
    private void putReportPic(int reportId, byte[] image) {
        StorageReference imRef = storage.child("images/report/"+reportId);
        UploadTask uploadTask = imRef.putBytes(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });
    }
    public void getAllReportsListener(final List<Report> reports, final List<User> users, int fieldId) {
        db.child("report").orderByChild("id").equalTo(fieldId)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                    reports.clear();
                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                        try {
                                            final Report r = snap.getValue(Report.class);
                                            Log.d("UID of report",  "uid : " + r.getuId()
                                            + " descr : "+r.getDescr()
                                            + " id : " + r.getId());
                                            reports.add(r);
                                            db.child("users").orderByChild("uid").equalTo(r.getuId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                                        try {
                                                            final User u = snap.getValue(User.class);
                                                            StorageReference imRef = storage.child("images/users/" + u.getUid());
                                                            Task<byte[]> downloadTask = imRef.getBytes(ONE_MEGABYTE)
                                                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                        @Override
                                                                        public void onSuccess(byte[] bytes) {
                                                                            u.setImage(bytes);
                                                                            users.add(u);
                                                                            Log.d("DL IMAGE USER", "SUCCESS");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            u.setImage(null);
                                                                            users.add(u);
                                                                        }
                                                                    });

                                                        }catch(Exception e){Log.d("Get users ex ","exception : " +e);}
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });

                        }
                        catch(Exception e){Log.d("Get reports ex ","exception : " +e);}
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("BUG","Didn't get data");
            }
        });
    }


    ///////////////////////
    ///   EVENTS       ///
    ///////////////////////

    public void createEvent(final Event event){
        db.child("events").orderByChild("eventId").limitToLast(1)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int maxId = 0;
                                try {
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        Event e = snap.getValue(Event.class);
                                        maxId = e.getEventId();
                                    }
                                }
                                catch(NullPointerException e){

                                }
                                event.setEventId(maxId+1);
                                event.set_organizerUID(auth.getUid());
                                db.child("events").push().setValue(event);
                                System.out.println("Cheguei aqui?");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }



    ///////////////////////
    ///   CREATE USER   ///
    ///////////////////////
    public void createUser(final User u, final byte[] image, String email, String password, ProgressBar progressBar, final Context context) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(context, "User created with success!", Toast.LENGTH_SHORT).show();
                        FirebaseUser currentUser = auth.getCurrentUser();
                        u.setUid(currentUser.getUid());
                        db.child("users").push().setValue(u);
                        if(image != null)
                            putUserPic(currentUser.getUid(),image);
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("CREATE USER", "Couldn't create the user, exception : " + e);
                    }
                });
    }
    private void putUserPic(String uId, byte[] image) {
        StorageReference imRef = storage.child("images/users/"+uId);
        UploadTask uploadTask = imRef.putBytes(image);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });
    }
    ///////////////////////
    /////    LOG IN  //////
    ///////////////////////
    public void logIn(AccessToken token, final Context context){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("FB sign in", "signInWithCredential:success");
                        final FirebaseUser firebaseUser = auth.getCurrentUser();
                        final String uId = firebaseUser.getUid();
                        db.child("users")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            Log.i("FB sign in", "Inside login with facebook - Table already exists! " + dataSnapshot.child("type").getValue());
                                            Intent intent = new Intent(context, MapsActivity.class);
                                            context.startActivity(intent);
                                        } else {
                                            try {
                                                Log.i("FB sign in", "Inside login with facebook - Creating table!");
                                                final User u = new User(uId, firebaseUser.getDisplayName(), 0, firebaseUser.getEmail(), firebaseUser.getPhoneNumber(), 0, "", "");
                                                db.child("users").push().setValue(u);
                                                Intent intent = new Intent(context, MapsActivity.class);
                                                context.startActivity(intent);
                                            } catch (Exception e) {
                                                Log.i("FB login", "Exception creating fb table is: " + e);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("FB sign in", "Canceled");
                                    }
                                });
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FB sign in", "signInWithCredential:failure", e);
                        Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void logIn(String email,final String password,final ProgressBar progressBar, final Context context) {
        //  Check if it is an email or not
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            performLogin(email, password, progressBar, context);
        } else {
            //get the emailId associated with the username
            db.child("email_ids").child(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                String userId = dataSnapshot.getValue(String.class);
                                System.out.println("UserId: " + userId);
                                if (userId != null) {
                                    performLogin(userId, password, progressBar, context);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Username or password incorrect", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
        }
    }
    private void performLogin(String emailId, final String pas,final ProgressBar progressBar, final Context context) {
        auth.signInWithEmailAndPassword(emailId, pas)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    public void onSuccess(AuthResult authResult) {
                        Intent intent = new Intent(context, MapsActivity.class);
                        context.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (pas.length() < 6) {
                            Toast.makeText(context, context.getString(R.string.minimum_password), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public Bitmap getBitmapSavingMem(byte[] image){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if(image != null) BitmapFactory.decodeByteArray(image, 0 ,image.length, options);

        options.inSampleSize = calculateInSampleSize(options, 50, 50);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(image, 0 ,image.length, options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

