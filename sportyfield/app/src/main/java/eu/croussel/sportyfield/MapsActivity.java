package eu.croussel.sportyfield;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Filter appliedFilter ;
    private static final int REQUEST_FILTER = 1 ;
    private GoogleMap mMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Boolean mapConnected = false;
    Marker mClickedMark ;
    // Database Helper
    DataBaseHandler db;
    List<Field> fieldList ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Fragment of the Maps API
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get the DB
        db = new DataBaseHandler(this);
        db.clearDb();

        Log.d("Insert:", "Inserting fields...");
        db.createField(new Field("Milano Castle", 45.471944, 9.178889, false, true, 0, "This is a castle, wow.", null));
        db.createField(new Field("Duomo",45.464211, 9.191383, false, false, 0, "This is Duomo - click to go on info", null));

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
    public void onResume(){
        super.onResume();
        fieldList = getFields();
        if(mapConnected){
            mMap.clear();
            displayFields();
            onLocationChanged(mLastLocation);
        }
    }

    private List<Field> getFields(){
        if(appliedFilter == null) return db.getAllFields();
        else return db.getAllFieldsWithFilter(appliedFilter);
    }
    /////////////////////////////
    //           MAP           //
    /////////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        initGooglePlay();

        //Display all the fields of the database
        displayFields();

        //Activate listener for marker's info windows
        mMap.setOnInfoWindowClickListener(this);
        //Activate on map click listener
        mMap.setOnMapClickListener(this);
        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                int tag = (int) arg0.getTag();
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

                // Getting reference to the TextViews and ImageView
                TextView tvTitle = (TextView) v.findViewById(R.id.infoWindowTitle);
                TextView tvText = (TextView) v.findViewById(R.id.infoWindowText);
                ImageView iv = (ImageView) v.findViewById(R.id.infowindowImView);

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
                        Field f = db.getField(tag);
                        tvTitle.setText(f.getComment());
                        tvText.setText(f.getLocation());
                        byte[] image = f.getImage();
                        if(image == null) iv.setImageResource(R.drawable.field);
                        else iv.setImageBitmap(BitmapFactory.decodeByteArray(image, 0 ,image.length));
                        break;
                }
                // Returning the view containing InfoWindow contents
                return v;

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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(),
                            LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude())
                    , 11));
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
        for( Field f:fieldList )
        {
            LatLng fLatLong = new LatLng(f.getLat(), f.getLong());
            MarkerOptions fMarkOpt = new MarkerOptions()
                    .position(fLatLong)
                    .title(f.getDescription())
                    .snippet(f.getLocation());
            Marker fieldMarker = mMap.addMarker(fMarkOpt);
            fieldMarker.setTag(f.getId());
        }
    }



    /////////////////////////////
    //      MAP - LOCATION     //
    /////////////////////////////
    //Method when location changes
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if(location != null) {
            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Current Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mCurrLocationMarker.setTag(1);
        }
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
                break;
            default:
                Intent intent = new Intent(this, FieldInfo.class);
                   intent.putExtra("fieldID", tag); //added this to pass the Field ID. You need
                 //to get it from the database. When somenone click on marker, get that id from db and send to FieldInfo
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
                        .snippet("Click to add new Field"));
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
            case R.id.action_addFilter:
                Intent filterIntent = new Intent(this, FilterActivity.class);
                startActivityForResult(filterIntent, REQUEST_FILTER);
                return true;
            case R.id.action_resetFilter :
                appliedFilter = null;
                this.onResume();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case REQUEST_FILTER :
                    appliedFilter = (Filter) data.getSerializableExtra("filter");
                    Toast.makeText(this, appliedFilter.getFieldType(),
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

}
