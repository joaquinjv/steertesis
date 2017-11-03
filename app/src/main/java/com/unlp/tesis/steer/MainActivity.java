package com.unlp.tesis.steer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unlp.tesis.steer.entities.GeofencePoint;
import com.unlp.tesis.steer.entities.PaidParkingArea;
import com.unlp.tesis.steer.entities.PointOfSale;
import com.unlp.tesis.steer.entities.User;
import com.unlp.tesis.steer.utils.MessagesUtils;
import com.unlp.tesis.steer.utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.unlp.tesis.steer.Constants.E_CHARGE_POINT_OF_SALES;
import static com.unlp.tesis.steer.Constants.E_END_PARKING;
import static com.unlp.tesis.steer.Constants.E_START_PARKING;
import static com.unlp.tesis.steer.Constants.E_STATE_OF_PARKING;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener
{

    /*NAV VIEW*/
//    private Toolbar appbar;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private TextView titleNavView;

    private static final String TAG = MainActivity.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Tracks the bound state of the service.
    private String token;

    protected LocationListener locationListener;

    protected LocationManager locationManager;

    protected Location actualLocation;

    //Google Map
    public static GoogleMap mMap;

    private static Boolean parkingStarted = Boolean.FALSE;
    private static Boolean endParkingForced = Boolean.FALSE;

    public static SingletonVo singletonVo = SingletonVo.getInstance();

    public static FloatingActionButton fabparked = null;

    public static  FloatingActionButton microphone = null;

    public static FloatingActionButton parkingButton = null;

    public static Marker mCurrLocation = null;

    public static LatLng eventZone = null;

    /**
     * The list of points of sales markers used in this sample.
     */
    public static List<Marker> pointOfSalesMarkers = new ArrayList<Marker>();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    private User userData;
    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            mService = binder.getService();

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        appbar = (Toolbar)findViewById(R.id.appbar);
//        setSupportActionBar(appbar);
//
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_nav_menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        titleNavView = (TextView)findViewById(R.id.titleNavView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return navigationItemSelected(menuItem);
                    }
                });

        myReceiver = new MyReceiver();

        // Check that the user hasn't revoked permissions by going to Settings.
        if (!checkPermissions()) {
            requestPermissions();
        }

        //Obtenemos en fagmento del mapa definido en activity_main layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //The voice is an big additional in the app, we keep this element in the main activiy
        microphone = (FloatingActionButton) findViewById(R.id.microphone);
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        /*/ If I keep the button pressed I parked directly
        microphone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public synchronized boolean onLongClick(View v) {
                if (!getParkingStarted()){
                    startParking();
                } else {
                    endParking();
                }

                return true;
            }
        });*/

        // If I press the button, I parked or finished itself
        parkingButton = (FloatingActionButton) findViewById(R.id.fab);
        parkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                if (!getParkingStarted()){
                    startParking();
                } else {
                    endParking();
                }
            }
        });

        FloatingActionButton locationButton = (FloatingActionButton) findViewById(R.id.fabMyLocation);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateCamera();
            }
        });

        final Button menuButton = (Button) findViewById(R.id.buttonMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenNavigationView();
            }
        });

        new RequestLoginTask(this).execute("");

                // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User  is unexpectedly null");
                            Toast.makeText(getApplicationContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            titleNavView = (TextView)findViewById(R.id.titleNavView);
                            titleNavView.setText(user.getName());
                            MenuItem menuPlate = (MenuItem) navView.getMenu().findItem(R.id.menu_plate);
                            menuPlate.setTitle(user.getPlate());
                        }
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

        //SoloUnaVez();
    }

    public void SoloUnaVez (){

        List<GeofencePoint> lg = new ArrayList<GeofencePoint>();
        String newPPAKey = mDatabase.child("paidParkingAreas").push().getKey();
        lg.add(new GeofencePoint(50,-34.9428192,-57.9656825, newPPAKey));
        lg.add(new GeofencePoint(50,-34.9419671,-57.9646975, newPPAKey));
        lg.add(new GeofencePoint(50,-34.9410528,-57.9637056, newPPAKey));
        PaidParkingArea zonaPedro = new PaidParkingArea("PEDRO", 5, 8, 20, lg);
        mDatabase.child("paidParkingAreas").child(newPPAKey).setValue(zonaPedro);

        List<GeofencePoint> lg2 = new ArrayList<GeofencePoint>();
        newPPAKey = mDatabase.child("paidParkingAreas").push().getKey();
        lg2.add(new GeofencePoint(50,-34.9306643,-57.9727699, newPPAKey));
        PaidParkingArea zonaJoaco = new PaidParkingArea("JOACO", 5, 8, 20, lg2);
        mDatabase.child("paidParkingAreas").child(newPPAKey).setValue(zonaJoaco);

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){ //replace this with actual function which returns if the drawer is open
            drawerLayout.closeDrawers();     // replace this with actual function which closes drawer
        }
        else{
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo que aplica el reconocimiento de voz y envía un resultado al Activity
     */
    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Diciendo algo...");

        // We save the actual position in the case that it's an alert
        eventZone = new LatLng(mCurrLocation.getPosition().latitude, mCurrLocation.getPosition().longitude);

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "No se la banca",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void callParkingStatus(Context paramContext){
        new RequestServiceTask(paramContext).execute(E_STATE_OF_PARKING);
    }

    /**
     * Receiving speech input, call the Task to process de result
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new SpeechTask(this,requestCode,resultCode,data).execute("");
    }

    protected void startParking() {
        new RequestServiceTask(this).execute(E_START_PARKING);
    }

    protected void endParking() {
        new RequestServiceTask(this).execute(E_END_PARKING);
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //Set audio preferences switch method
        SwitchCompat switchItem = (SwitchCompat) navView.getMenu().findItem(R.id.menu_audio_preference_switch).getActionView();
        switchItem.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener(){
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Preferences.setAudioPreference(getApplicationContext(), isChecked);
            }
        });
        //Set audio preferences
        switchItem.setChecked(Preferences.getAudioPreference(this));
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
        Preferences.setGeofenceShowmessage(this, true);

        // Search the alerts of all users
        new RequestCenitListOfEventsTask(this).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(Constants.ACTION_BROADCAST));
        checkGeofenceStatus(Preferences.getGeofenceStatus(this), Preferences.getGeofenceShowmessage(this));
        Preferences.setGeofenceShowmessage(this, false);
    }



    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        Preferences.setGeofenceShowmessage(this, true);
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.drawer_layout),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }


    //private Marker locationMarker;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Preferences.KEY_GEOFENCE_STATUS)) {
            checkGeofenceStatus(Preferences.getGeofenceStatus(this), true);
        }
    }

    /*
    * check the geofence status and update the view
    **/
    public void checkGeofenceStatus(String value, boolean bShowMessage)
    {
        switch (value) {
            case Preferences.KEY_GEOFENCE_STATUS_IN:
                parkingButton.setEnabled(true);
//                parkingButton.setImageResource(R.drawable.ic_logo_parking);
                if (bShowMessage) {
                    if (Preferences.getGeofenceStatusTriggeredId(this).length() > 0) {
                        mDatabase.child("paidParkingAreas").child(Preferences.getGeofenceStatusTriggeredId(this)).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get paidParkingArea

                                        PaidParkingArea ppa = dataSnapshot.getValue(PaidParkingArea.class);
                                        Log.e(TAG, "read ppa");
                                        if (ppa != null) {
                                            // User is null, error out
                                            Log.e(TAG, "read ppa");
                                            Log.e(TAG, ppa.toString());
                                            GenerteGeofenceAlert(ppa.toString());
                                        }                                     // [END_EXCLUDE]
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                    }
                }
                //MessagesUtils.generteGeofenceAlert(this, Preferences.getGeofenceStatusMessage(this));
                break;
            case Preferences.KEY_GEOFENCE_STATUS_OUT:
                parkingButton.setEnabled(false);
//                parkingButton.setImageResource(R.drawable.ic_logo_parking_disabled);
                if (getParkingStarted()){
                    setEndParkingForced(Boolean.TRUE);
                    new RequestServiceTask(this).execute(E_END_PARKING);
                }
                break;
//            case Preferences.KEY_GEOFENCE_STATUS_PAID:
//                parkingButton.setImageResource(R.drawable.ic_logo_parking_green);
//                break;
        }
    }

    public void GenerteGeofenceAlert(String message)
    {
        MessagesUtils.generteGeofenceAlert(this, message);
    }
    /**
     * Receiver for broadcasts sent by {@link LocationService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //La primera vez actualiza la camara
            if (actualLocation == null)
            {
                actualLocation = intent.getParcelableExtra(Constants.EXTRA_LOCATION);
                UpdateCamera();
            }
            else {
                actualLocation = intent.getParcelableExtra(Constants.EXTRA_LOCATION);
            }

            if (actualLocation != null) {

                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(false);
                    LatLng latLng = new LatLng(actualLocation.getLatitude(), actualLocation.getLongitude());
                    new RequestPositionTask(MainActivity.this, actualLocation).execute();
                    /*
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition camPos = CameraPosition
                            .builder(
                                    mMap.getCameraPosition() // current Camera
                            )
                            .bearing(location.getBearing())
                            .target(latLng)   //Centramos en mi ubicacion
                            .zoom(19)         //Establecemos el zoom en 19
                            //.bearing(45)      //Establecemos la orientación con el noreste arriba
                            .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);//newLatLngZoom(latLng, zoom);
                    mMap.animateCamera(cameraUpdate);*/
                    //startGeofence();
                }
            }
        }
    }

    /**
     *Create and initialize the map position
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(17.0f);
        mMap.setMinZoomPreference(16.0f);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        this.setPointOfSales();

        //PARA PRUEBA
        //populateGeofenceMarker();
    }

    /**
     * Returns the {@code location} object as a human readable string.
     * @param location  The {@link Location}.
     */
    static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }


    private void setPointOfSales2() {
        DatabaseReference dbRef =
                FirebaseDatabase.getInstance().getReference().child("pointOfSales");
        dbRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.e(TAG, "dataChange reading db");
                            // Get Post object and use the values to update the UI
                            PointOfSale pos = ds.getValue(PointOfSale.class);
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(new LatLng(pos.getLatitude(), pos.getLongitude()))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                    .title(pos.getName())
                                    .snippet(pos.getDetails());
                            if (mMap != null) {
                                // Remove last geoFenceMarker
                                //if (geoFenceMarker != null)
                                //    geoFenceMarker.remove();
                                pointOfSalesMarkers.add(mMap.addMarker(markerOptions));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error reading db");
                    }
                }
        );
    }

    private void setPointOfSales() {
        new RequestServiceTask(this).execute(E_CHARGE_POINT_OF_SALES);
    }

    public boolean navigationItemSelected(MenuItem menuItem) {

//        boolean fragmentTransaction = false;
//        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.menu_plate:
//                fragment = new Fragment1();
//                fragmentTransaction = true;
                break;
        }

//        if(fragmentTransaction) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content, fragment)
//                    .commit();
//
//            menuItem.setChecked(true);
//            getSupportActionBar().setTitle(menuItem.getTitle());
//        }

        drawerLayout.closeDrawers();

        return true;
    }

    public void OpenNavigationView()
    {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    public void UpdateCamera()
    {
        LatLng latLng = new LatLng(actualLocation.getLatitude(), actualLocation.getLongitude());
        CameraPosition camPos = CameraPosition
                .builder(
                        mMap.getCameraPosition() // current Camera
                )
                .bearing(actualLocation.getBearing())
                .target(latLng)   //Centramos en mi ubicacion
                .zoom(19)         //Establecemos el zoom en 19
                //.bearing(45)      //Establecemos la orientación con el noreste arriba
                .tilt(70)         //Bajamos el punto de vista de la cámara 70 grados
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(camPos);//newLatLngZoom(latLng, zoom);
        mMap.animateCamera(cameraUpdate);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public FloatingActionButton getFabparked() {
        return fabparked;
    }

    public void setFabparked(FloatingActionButton fabparked) {
        this.fabparked = fabparked;
    }

    public static Boolean getParkingStarted() {
        return parkingStarted;
    }

    public static void setParkingStarted(Boolean parkingStartedParam) {
        parkingStarted = parkingStartedParam;
    }

    public static FloatingActionButton getParkingButton() {
        return parkingButton;
    }

    public static void setParkingButton(FloatingActionButton parkingButton) {
        MainActivity.parkingButton = parkingButton;
    }

    public static Boolean getEndParkingForced() {
        return endParkingForced;
    }

    public static void setEndParkingForced(Boolean endParkingForced) {
        MainActivity.endParkingForced = endParkingForced;
    }

    /**
     * PARA PRUEBA
     *
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceMarker() {
        mDatabase.child("paidParkingAreas").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double v1, v2;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.e(TAG, "dataChange reading db");
                            // Get Post object and use the values to update the UI
                            PaidParkingArea ppa = ds.getValue(PaidParkingArea.class);

                            for (GeofencePoint gp : ppa.getGeofencePoints()){

                                CircleOptions circleOptions = new CircleOptions();

                                v1 = gp.getLatitude();
                                v2= gp.getLongitude();
                                circleOptions.center(new LatLng(v1,v2));
                                circleOptions.strokeWidth(5);
                                circleOptions.radius(gp.getRadius());
                                circleOptions.strokeColor(Color.RED);
                                circleOptions.fillColor(Color.BLUE);
                                mMap.addCircle(circleOptions);

                            }
                        }
                        CircleOptions circleOptions = new CircleOptions();

                        v1 = -34.9315649;
                        v2= -57.9558615;
                        circleOptions.center(new LatLng(v1,v2));
                        circleOptions.strokeWidth(5);
                        circleOptions.radius(300);
                        circleOptions.strokeColor(Color.RED);
                        circleOptions.fillColor(Color.BLUE);
                        mMap.addCircle(circleOptions);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error reading db");
                    }
                }
        );
    }
}
