package com.example.android.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myapp.adapters.ImageUploadAdapter;
import com.example.android.myapp.adapters.RecordingUploadAdapter;
import com.example.android.myapp.helper.CameraUploadHelper;
import com.example.android.myapp.helper.RecorderUploadHelper;
import com.example.android.myapp.helper.UserHelper;
import com.example.android.myapp.onBoarding.OnBoardingActivity;
import com.example.android.myapp.registration.BasicRegistrationActivity_1;
import com.example.android.myapp.registration.BasicRegistrationActivity_2;
import com.example.android.myapp.registration.BasicRegistrationActivity_3;
import com.example.android.myapp.registration.CropRegistrationActivity;
import com.example.android.myapp.registration.PesticideRegistration;
import com.example.android.myapp.registration.SoilTestReportActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    private final int REQUEST_PERMISSION_CODE = 100;

    private NavigationView navigationView;
    private FloatingActionButton fab_mic, fab_camera;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private ListView listView;

    private ImageView navHeaderImageView;
    private TextView navHeaderMobileNo;

    private FirebaseAuth firebaseAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private List<RecorderUploadHelper> upoadedRecordingsList;

    private String COMPLETED_ONBOARDING_PREF_NAME = "completed_on_boarding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean onBoardingStatus = prefs.getBoolean(COMPLETED_ONBOARDING_PREF_NAME, false);

        if (!onBoardingStatus) {
            prefs.edit().putBoolean(COMPLETED_ONBOARDING_PREF_NAME, true).commit();
            startActivity(new Intent(this, OnBoardingActivity.class));
            finish();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(getResources().getString(R.string.kissan_mitr_node));

        upoadedRecordingsList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.uploadListView);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        fab_camera = findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        fab_mic = findViewById(R.id.fab_mic);
        fab_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecorderActivity.class);
                startActivity(intent);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        navHeaderImageView = headerView.findViewById(R.id.nav_header_profile_img);
        navHeaderMobileNo = headerView.findViewById(R.id.nav_header_mobile_no);


        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        Log.e(TAG, token);
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, BasicRegistrationActivity_1.class);
            startActivity(intent);
            finish();
        }
        else{
            getDetailsFromFirebase();
            getRecordingUploads();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_recording_upload) {
            getRecordingUploads();
            return true;
        }

        if (id == R.id.menu_image_upload) {
            Intent intent = new Intent(this, ImageUploadActivity.class);
            intent.putExtra("mobile",firebaseAuth.getCurrentUser().getPhoneNumber());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, CameraActivity.class));
        }
        else if (id == R.id.nav_mic) {
            startActivity(new Intent(this, RecorderActivity.class));
        }
        else if (id == R.id.nav_uploads) {
            // Handle the upload action
        }
        else if (id == R.id.nav_my_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_log_out) {
            signout();
        }
        else if(id == R.id.nav_crop_registration){
            Intent intent = new Intent(this, CropRegistrationActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_pesticide_registration){
            Intent intent = new Intent(this, PesticideRegistration.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_soil_report){
            Intent intent = new Intent(this, SoilTestReportActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_about_us){
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.nav_contact_us){
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int camera_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int location_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED
                && record_audio_result == PackageManager.PERMISSION_GRANTED
                && camera_result == PackageManager.PERMISSION_GRANTED
                && location_result == PackageManager.PERMISSION_GRANTED;
    }

    public void signout(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(firebaseAuth.getCurrentUser() == null) {
                    finish();
                    Intent intent = new Intent(MainActivity.this,BasicRegistrationActivity_1.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void getDetailsFromFirebase(){

       if(!(firebaseAuth == null)) {
           navHeaderMobileNo.setText(firebaseAuth.getCurrentUser().getPhoneNumber());

           mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Registration Data").addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   UserHelper userHelper = dataSnapshot.getValue(UserHelper.class);

                   CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                   circularProgressDrawable.setStrokeWidth(5);
                   circularProgressDrawable.setCenterRadius(30);
                   circularProgressDrawable.start() ;

                   Glide.with(MainActivity.this).load(userHelper.getImageUrl()).placeholder(circularProgressDrawable).into(navHeaderImageView);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }
    }

    public void getRecordingUploads(){
        mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Recordings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                upoadedRecordingsList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    RecorderUploadHelper recorderUploadHelper = recordingSnapshot.getValue(RecorderUploadHelper.class);
                    upoadedRecordingsList.add(recorderUploadHelper);
                }

                RecordingUploadAdapter adapter = new RecordingUploadAdapter(MainActivity.this, upoadedRecordingsList, firebaseAuth.getCurrentUser().getPhoneNumber());
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
