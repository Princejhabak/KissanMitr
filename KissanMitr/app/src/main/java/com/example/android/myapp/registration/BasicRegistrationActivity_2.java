package com.example.android.myapp.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.myapp.R;
import com.example.android.myapp.helper.LocationUploadHelper;
import com.example.android.myapp.helper.UserHelper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicRegistrationActivity_2 extends AppCompatActivity {

    private final int PLACE_PICKER_REQUEST = 100;
    private final int REQUEST_PERMISSION_CODE = 10;

    private Spinner state, district, tehsil, village;
    private EditText location;
    private FloatingActionButton fab;

    private Place place;
    private UserHelper userHelper;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ArrayList<String> stateList ;
    private ArrayList<String> districtList ;
    private ArrayList<String> tehsilList ;
    private ArrayList<String> villageList ;

    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> tehsilAdapter;
    private ArrayAdapter<String> villageAdapter;

    private String selectedState;
    private String selectedDistrict;
    private String selectedtehsil;
    private String selectedVillage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_registration_2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(getResources().getString(R.string.kissan_mitr_admin_node));

        state = findViewById(R.id.spinner_state);
        district = findViewById(R.id.spinner_district);
        tehsil = findViewById(R.id.spinner_tehsil);
        village = findViewById(R.id.spinner_village);

        location = findViewById(R.id.et_location);
        fab = findViewById(R.id.basicRegistration2Next);

        final Intent intent = getIntent();
        userHelper = intent.getParcelableExtra("user_data");

        stateList = new ArrayList<>();
        districtList = new ArrayList<>();
        tehsilList = new ArrayList<>();
        villageList = new ArrayList<>();

        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        setUpStateSpinner();

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissionFromDevice()) {
                    requestPermission();
                    return;
                }

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(BasicRegistrationActivity_2.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(location.getText())) {
                    Toast.makeText(BasicRegistrationActivity_2.this, "Enter your location", Toast.LENGTH_SHORT).show();
                    return;
                }

                userHelper.setState(selectedState);
                userHelper.setDistrict(selectedDistrict);
                userHelper.setTehsil(selectedtehsil);
                userHelper.setVillage(selectedVillage);
                userHelper.setPlace(place.getAddress().toString());
                finish();

                Intent intent1 = new Intent(BasicRegistrationActivity_2.this, BasicRegistrationActivity_3.class);
                intent1.putExtra("user_data_1", userHelper);
                startActivity(intent1);

            }
        });

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState =  adapterView.getItemAtPosition(i).toString();
                //districtList.clear();
                setUpDistrictSpinner(selectedState);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDistrict =  adapterView.getItemAtPosition(i).toString();
                setUpTehsilSpinner(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedtehsil =  adapterView.getItemAtPosition(i).toString();
                setUpVillageSpinner(selectedtehsil);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVillage =  adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);

                if (data != null) {
                    location.setText(place.getAddress());
                }

            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int location_result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        return location_result == PackageManager.PERMISSION_GRANTED;
    }

    public void setUpStateSpinner() {

        stateAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stateList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    stateList.add(locationUploadHelper.getState());
                    HashSet<String> hashSet = new HashSet<>();
                    hashSet.addAll(stateList);
                    stateList.clear();
                    stateList.addAll(hashSet);
                    stateAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpDistrictSpinner(final String selectedState){

        districtAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(districtAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                districtList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if(locationUploadHelper.getState().equals(selectedState)) {
                        districtList.add(locationUploadHelper.getDistrict());
                        HashSet<String> hashSet = new HashSet<>();
                        hashSet.addAll(districtList);
                        districtList.clear();
                        districtList.addAll(hashSet);
                        districtAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpTehsilSpinner(final String selectedDistrict){

        tehsilAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, tehsilList);
        tehsilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tehsil.setAdapter(tehsilAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tehsilList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if(locationUploadHelper.getDistrict().equals(selectedDistrict)) {
                        tehsilList.add(locationUploadHelper.getTehsil());
                        HashSet<String> hashSet = new HashSet<>();
                        hashSet.addAll(tehsilList);
                        tehsilList.clear();
                        tehsilList.addAll(hashSet);
                        tehsilAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpVillageSpinner(final String selectedtehsil){

        villageAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, villageList);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        village.setAdapter(villageAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                villageList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if(locationUploadHelper.getTehsil().equals(selectedtehsil)) {
                        villageList.add(locationUploadHelper.getVillage());
                        HashSet<String> hashSet = new HashSet<>();
                        hashSet.addAll(villageList);
                        villageList.clear();
                        villageList.addAll(hashSet);
                        villageAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static ArrayList<String> removeDuplicates(ArrayList<String> list)
    {
        ArrayList<String> newList = new ArrayList<>();

        for (String element : list) {

            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

}
