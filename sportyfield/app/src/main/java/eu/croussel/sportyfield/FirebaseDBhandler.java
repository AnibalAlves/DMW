package eu.croussel.sportyfield;

import android.util.Log;

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

import static java.lang.Thread.sleep;

/**
 * Created by root on 30/01/18.
 */

public class FirebaseDBhandler {
    public DatabaseReference db;
    private int id ;

    public FirebaseDBhandler() {
        db = FirebaseDatabase.getInstance().getReference();
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
                                        Field fi = snap.getValue(Field.class);
                                        fields.add(fi);
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
    private Field fi;
    public void createField(final Field f){

//                .runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                for(MutableData m : mutableData.getChildren())
//                {
//                    for(MutableData m2 : m.getChildren()) {
//                        id = ((Field) m2.getValue(Field.class)).getId();
//                        Log.d("FAFZAZ2", " blarf " + id + "field : " + fi);
//                    }
//                }
//                //f.setId(id + 1);
//                //Log.d("Set id :", " id = "+ f.getId());
//                //db.child("field").push().setValue(f);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });
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
//
//    public int getFieldId(){
//
//        db.child("field").orderByChild("id").limitToLast(1)
//                .addValueEventListener(
//                        new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                try {
//                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                        Field f = postSnapshot.getValue(Field.class);
//                                        id = f.getId();
//                                        Log.d("FAFZAZ", " blarf " + f.getId());
//
//                                    }
//                                }
//                                catch(NullPointerException e){
//                                    id = 0;
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        }
//                );
//            return id;
//    }




}
