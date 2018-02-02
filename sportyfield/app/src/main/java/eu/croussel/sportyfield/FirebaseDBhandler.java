package eu.croussel.sportyfield;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.Report;

import static java.lang.Thread.sleep;

/**
 * Created by root on 30/01/18.
 */

public class FirebaseDBhandler {
    private DatabaseReference db;
    private int id ;
    private StorageReference storage ;


    public FirebaseDBhandler() {
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();
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
                                        StorageReference imRef = storage.child("images/field/"+fi.getId());
                                        final long ONE_MEGABYTE = 1024*1024;
                                        Task<byte[]> downloadTask = imRef.getBytes(ONE_MEGABYTE)
                                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        fi.setImage(bytes);
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


    public void createField(final Field f){
        db.child("field").orderByChild("id").limitToLast(1)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                            Field fi = snap.getValue(Field.class);
                                            id = fi.getId();
                                            Log.d("DEBUG", "Id : " + id);
                                            f.setId(id + 1);
                                            Log.d("Set id :", " id = "+ f.getId());
                                            if(f.getImage() != null) {
                                                putFieldPic(f.getId(), f.getImage());
                                                f.setImage(null);
                                            }
                                            db.child("field").push().setValue(f);

                                    }
                                }
                                catch(NullPointerException e){
                                    id = 0;
                                    f.setId(id + 1);
                                    Log.d("Set id :", " id = "+ f.getId());
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
                                if(report.getRepImage() != null) {
                                    putReportPic(report.getreportId(), report.getRepImage());
                                    report.setRepImage(null);
                                }
                                report.setreportId(maxRepId+1);
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
    public void getAllReportsListener(final List<Report> reports, int fieldId) {
        db.child("report").orderByChild("id").equalTo(fieldId)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    reports.clear();
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        final Report r = snap.getValue(Report.class);
                                        StorageReference imRef = storage.child("images/report/"+r.getreportId());
                                        final long ONE_MEGABYTE = 1024*1024;
                                        Task<byte[]> downloadTask = imRef.getBytes(ONE_MEGABYTE)
                                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        r.setRepImage(bytes);
                                                        reports.add(r);
                                                        Log.d("DL IMAGE", "SUCCESS");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        r.setRepImage(null);
                                                        reports.add(r);
                                                        Log.d("DL IMAGE", "FAIL");

                                                    }
                                                });
                                    }
                                }
                                catch(NullPointerException e){
                                    reports.clear();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("BUG","Didn't get data");
                            }
                        }
                );
    }
}

