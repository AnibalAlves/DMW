package eu.croussel.sportyfield.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.Drawer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.croussel.sportyfield.DB_classes.Field;
import eu.croussel.sportyfield.DB_classes.Filter;
import eu.croussel.sportyfield.FirebaseDBhandler;
import eu.croussel.sportyfield.R;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_FILTER = 1 ;

    // Maps vars
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Boolean mapConnected = false;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker mClickedMark ;

    // Database Helper
    private FirebaseAuth auth;
    private FirebaseDBhandler mDatabase;

    //Fields acquisition vars
    List<Field> fieldList ;
    Filter appliedFilter ;
    int oldFieldListSize = 0;
    private Handler handlerFields ;
    Runnable runnable ;
    private static HashMap<String, Integer> hash;
    private static final Integer[] mThumbIds = {
            R.drawable.pictobasketball01,
            R.drawable.pictoarcherey01,
            R.drawable.pictoathletism01,
            R.drawable.pictobadminton01,
            R.drawable.pictobaseball01,
            R.drawable.pictobowling01,
            R.drawable.pictoboxe01,
            R.drawable.pictocurling01,
            R.drawable.pictodiving01,
            R.drawable.pictofish01,
            R.drawable.pictofitness01,
            R.drawable.pictogolf01,
            R.drawable.pictogym01,
            R.drawable.pictohockey01,
            R.drawable.pictojudo01,
            R.drawable.pictokayak01,
            R.drawable.pictopingpong01,
            R.drawable.pictorugby01,
            R.drawable.pictorunning01,
            R.drawable.pictosocker01,
            R.drawable.pictoswim01,
            R.drawable.pictotennis01,
            R.drawable.pictovolleyball01
    };
    private static ActionBar actionBar;
    private static Bitmap cursor1;
    private static Bitmap cursor2;
    private static Bitmap cursor3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cursor1=getBitmapSavingMem(R.drawable.toolpin01,16,18);
        cursor2=getBitmapSavingMem(R.drawable.toolpin02,16,18);
        cursor3=getBitmapSavingMem(R.drawable.toolpin03,16,18);
        hash =  new HashMap<>();
        hash.put("Basketball",0);
        hash.put("Archery",1);
        hash.put("Athletism",2);
        hash.put("Badminton",3);
        hash.put("Baseball",4);
        hash.put("Bowling",5);
        hash.put("Boxe",6);
        hash.put("Curling",7);
        hash.put("Diving",8);
        hash.put("Fishing",9);
        hash.put("Fitness",10);
        hash.put("Golf",11);
        hash.put("Gymnastic",12);
        hash.put("Hockey",13);
        hash.put("Judo",14);
        hash.put("Kayak",15);
        hash.put("Pingpong",16);
        hash.put("Rugby",17);
        hash.put("Running",18);
        hash.put("Football",19);
        hash.put("Swimming",20);
        hash.put("Tennis",21);
        hash.put("Volleyball",22);

        setContentView(R.layout.activity_maps);
        actionBar = getSupportActionBar();
        //these 3 lines show the Menu icon on the toolbar! Must be used on every activity
        //that will use the drawer menu
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        //Every sec we check if the list has changed
        handlerFields = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (fieldList.size() != oldFieldListSize) {
                    oldFieldListSize = fieldList.size();
                    displayFields();
                }
                handlerFields.postDelayed(this, 1000);
            }
        };




        //Fragment of the Maps API
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mDatabase = new FirebaseDBhandler();
//        mDatabase.createField(new Field("Marsala",45.47856593, 9.18897651, false, false, 1, "Near the road", null));

        //Init the field list which will be modified over time
        fieldList = new ArrayList<Field>();
        mDatabase.getAllFieldsListener(fieldList);
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(this, "PAUSE",
                Toast.LENGTH_SHORT).show();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        handlerFields.removeCallbacksAndMessages(null);
    }
    @Override
    public void onResume(){
        super.onResume();
        handlerFields.postDelayed(runnable, 1000);
        getSupportActionBar().hide();
        getSupportActionBar().show();

//        fieldList = getFields();
        if(mapConnected){
            mMap.clear();
            if(mLastLocation != null)
                displayCurrPos(new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()));
            displayFields();
            onLocationChanged(mLastLocation);
        }

        try {
            DrawerUtilActivity.getDrawer(this,getSupportActionBar());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFields(List<Field> fieldList){
        mDatabase.getAllFieldsListener(fieldList);
        //else return mDatabase.getAllFieldsWithFilter(fieldList, appliedFilter);
    }
    /////////////////////////////
    //           MAP           //
    /////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        initGooglePlay();
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.silver));
        //Activate listener for marker's info windows
        mMap.setOnInfoWindowClickListener(this);
        //Activate on map click listener
        mMap.setOnMapClickListener(this);
        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {

                int tag = (int) arg0.getTag();
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                // Getting reference to the TextViews and ImageView
                TextView tvTitle = (TextView) v.findViewById(R.id.infoWindowTitle);
                TextView tvText = (TextView) v.findViewById(R.id.infoWindowText);
                final ImageView iv = (ImageView) v.findViewById(R.id.infowindowImView);

                switch(tag){
                    //click marker
                    case 0 :
                        tvTitle.setText(getString(R.string.clickedMarkerTitle));
                        tvText.setText(getString(R.string.clickedMarkerText));
                        break;
                    case 1 :
                        tvTitle.setText(getString(R.string.youMarkerTitle));
                        tvText.setText(getString(R.string.youMarkerText));
                        break;
                    default:
                        Field f = fieldList.get(tag - 2);
                        tvTitle.setText(f.getDescription());
                        tvText.setText(f.getLocation());
//                        mDatabase.downloadFieldImTask(f.getId(), iv);
                        if(hash.containsKey(f.getDescription()))
                            iv.setImageBitmap(getBitmapSavingMem(mThumbIds[hash.get(f.getDescription())],50,50));
                        break;
                }
                // Returning the view containing InfoWindow contents
                return v;
//                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                return null;

            }
        });

    }

    /////////////////////////////
    //      MAP - SERVICE      //
    /////////////////////////////
    private void initGooglePlay(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    //Method to build the google API client
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    //Method called when app connects to google's api
    @Override
    public void onConnected(Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationRequest = new LocationRequest()
                    .setInterval(1000)
                    .setFastestInterval(500)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LatLng firstLatLng = new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        firstLatLng,
                        11)
                );
                displayCurrPos(firstLatLng);
            }catch(NullPointerException ex){};

            mapConnected = true;
        }
    }

    //Method when google's api is suspended
    @Override
    public void onConnectionSuspended(int i) {}

    //Method when google's api not reached
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}


    /////////////////////////////
    //      MAP - DISPLAY     //
    /////////////////////////////

    //display all the fields on the map
    private void displayFields(){
        for( Field f:fieldList ) {
            if (appliedFilter == null)
                displayField(f);
            else {
                if(
                        (f.getPriv() == appliedFilter.getPrivate()
                        || !f.getPriv() == appliedFilter.getPublic())
                        && appliedFilter.getFieldType().contains(f.getDescription())
                        && (!f.getOut() == appliedFilter.getIndoor()|| f.getOut() == appliedFilter.getOutdoor())
                        )
                {
                    displayField(f);
                }
            }
        }
    }

    private void displayField(Field f){
        LatLng fLatLong = new LatLng(f.getLat(), f.getLong());
        MarkerOptions fMarkOpt = new MarkerOptions()
                .position(fLatLong)
                .title(f.getDescription())
                .snippet(f.getLocation())
                .icon(BitmapDescriptorFactory.fromBitmap(cursor1));
        Marker fieldMarker = mMap.addMarker(fMarkOpt);
        fieldMarker.setTag(f.getId() + 1);
    }

    public Bitmap getBitmapSavingMem(int resId, int reqWidth, int reqHeight){
        // Calculate inSampleSize
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(this.getResources(), resId, options);
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
    /////////////////////////////
    //      MAP - LOCATION     //
    /////////////////////////////
    //Method when location changes
    @Override
    public void onLocationChanged(Location location)
    {
        if(location != null) {
            mLastLocation = location;
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            displayCurrPos(new LatLng(mLastLocation.getLatitude(),
                    mLastLocation.getLongitude()));
        }
    }

    //displays curr position
    void displayCurrPos(LatLng latLng){
    try {
        //Place current location marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Current Position")
                .icon(BitmapDescriptorFactory.fromBitmap(cursor2));
               // .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.setTag(1);
    }
    catch(NullPointerException ex){}
    }
    //Put marker on current location

    //Check of Location permision (Fine location here)
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the User *asynchronously* -- don't block
                // this thread waiting for the User's response! After the User
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the User once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    //Builds the client after asking for location permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    /////////////////////////////
    //      MAP - LISTENERS    //
    /////////////////////////////

    //called everytime a window is called
    @Override
    public void onInfoWindowClick(Marker marker) {
        int tag = (int) marker.getTag();
        switch (tag) {
            case 0 :
                LatLng clickedPos = marker.getPosition();

                //to attach LatLong to the intent, we need a bundle
                //https://stackoverflow.com/questions/16134682/how-to-send-a-latlng-instance-to-new-intent
                Bundle args = new Bundle();
                args.putParcelable("fieldPos", clickedPos);

                mClickedMark.remove();
                //Create the intent and launch it
                Intent intent_addField = new Intent(this, AddFieldActivity.class);
                intent_addField.putExtra("bundle",args);
                startActivity(intent_addField);

                break;
            case 1 :
                Toast.makeText(this, "Clicked on you",
                        Toast.LENGTH_SHORT).show();
                Intent intent_list = new Intent(this, ListEventsActivity.class);
                startActivity(intent_list);
                break;
            default:
                Intent intent = new Intent(this, FieldInfoActivity.class);
                intent.putExtra("fieldID", tag-1); //added this to pass the Field ID. You need
                 //to get it from the database. When somenone click on marker, get that id from db and send to FieldInfoActivity
                intent.putExtra("location",marker.getSnippet());
                   startActivity(intent);
                   break ;
        }
    }

    @Override
    public void onMapClick(LatLng position){
        if(mClickedMark != null)
            mClickedMark.remove();
        mClickedMark = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title("New Field ?")
                        .snippet("Click to add new Field")
                        .icon(BitmapDescriptorFactory.fromBitmap(cursor3)));
        mClickedMark.setTag(0);
    }


    /////////////////////////////
    //      ACTION BUTTONS     //
    /////////////////////////////

    //Add action buttons to the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbuttons, menu);
        return true ;
    }

    //Called when one of the action button is called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Add a filter
            case R.id.action_addFilter:
                Intent filterIntent = new Intent(this, FilterActivity.class);
                startActivityForResult(filterIntent, REQUEST_FILTER);
                return true;
            //Remove filter
            case R.id.action_resetFilter :
                appliedFilter = null;
                this.onResume();
                return true;
            //Drawer nav.
            case android.R.id.home:
                if (DrawerUtilActivity.result.isDrawerOpen())
                {
                    DrawerUtilActivity.result.closeDrawer();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
                else {
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

    //Callback after end of activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                //Retrieve filter
                case REQUEST_FILTER :
                    appliedFilter = (Filter) data.getSerializableExtra("filter");
                    printstackList(appliedFilter.getFieldType());
                    break;

                default:
                    break;
            }
        }
    }
    void printstackList(List<String> s){
        if(s == null)            Log.d("DEBUG LIST", "found NOTHING");

        for(String str:s)
            Log.d("DEBUG LIST", "found : " + str);
    }
}
