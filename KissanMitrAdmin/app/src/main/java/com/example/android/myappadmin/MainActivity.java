package com.example.android.myappadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myappadmin.adapters.StatisticsAdapter;
import com.example.android.myappadmin.helper.InputUploadHelper;
import com.example.android.myappadmin.helper.LocationUploadHelper;
import com.example.android.myappadmin.helper.StatsHelper;
import com.example.android.myappadmin.helper.UserHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etState, etDistrict, etTehsil, etVillage, etRegistrationType, etTypeOfLand, etCropsSown, etLandOccupied, etCompanyName, etCompanyNamePesticide, etEstimatedincome, etAddPesticide;
    private Button addLocationDetails, addRegistrationType, addTypeOfLand, addCropsSown, addLandOccupied, addCompanyName, addCompanyNamePesticide, addEstimatedIncome, addPesticide;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference statsDatabaseReference;

    private List<String> locationList;
    private List<String> imageList;
    private List<String> audioList;
    private List<String> addressList;
    private List<String> registrationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitrAdmin");

        statsDatabaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");

        locationList = new ArrayList<>();
        imageList = new ArrayList<>();
        audioList = new ArrayList<>();
        addressList = new ArrayList<>();
        registrationsList = new ArrayList<>();

        etState = findViewById(R.id.et_add_state);
        etDistrict = findViewById(R.id.et_add_district);
        etTehsil = findViewById(R.id.et_add_tehsil);
        etVillage = findViewById(R.id.et_add_village);

        etRegistrationType = findViewById(R.id.et_add_registration_type);
        etTypeOfLand = findViewById(R.id.et_add_land_type);
        etCropsSown = findViewById(R.id.et_add_crops_sown);
        etLandOccupied = findViewById(R.id.et_add_land_occupied);
        etCompanyName = findViewById(R.id.et_add_company_name);
        etCompanyNamePesticide = findViewById(R.id.et_add_company_name_pesticide);
        etEstimatedincome = findViewById(R.id.et_add_estimated_income);
        etAddPesticide = findViewById(R.id.et_add_pesticide_quantity);

        addLocationDetails = findViewById(R.id.add_details);

        addRegistrationType = findViewById(R.id.btn_add_registration_type);
        addTypeOfLand = findViewById(R.id.btn_add_land_type);
        addCropsSown = findViewById(R.id.btn_add_crops_sown);
        addLandOccupied = findViewById(R.id.btn_add_land_occupied);
        addCompanyName = findViewById(R.id.btn_add_company_name);
        addCompanyNamePesticide = findViewById(R.id.btn_add_company_name_pesticide);
        addEstimatedIncome = findViewById(R.id.btn_add_estimated_income);
        addPesticide = findViewById(R.id.btn_add_pesticide_quantity);

        getStats();
        getAllRegistrations();

        addLocationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLocationDetails();
            }
        });

        addRegistrationType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etRegistrationType.getText())){
                    Toast.makeText(MainActivity.this, "Please enter registration type", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etRegistrationType.getText().toString(),"Registered As");
                etRegistrationType.setText("");
            }
        });

        addTypeOfLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etTypeOfLand.getText())){
                    Toast.makeText(MainActivity.this, "Please enter type of land", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etTypeOfLand.getText().toString(),"Type Of Land");
                etTypeOfLand.setText("");
            }
        });

        addCropsSown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCropsSown.getText())){
                    Toast.makeText(MainActivity.this, "Please enter crops sown", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etCropsSown.getText().toString(),"Crops Sown");
                etCropsSown.setText("");
            }
        });

        addLandOccupied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etLandOccupied.getText())){
                    Toast.makeText(MainActivity.this, "Please enter land occupied", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etLandOccupied.getText().toString(),"Land Occupied");
                etLandOccupied.setText("");
            }
        });

        addCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCompanyName.getText())){
                    Toast.makeText(MainActivity.this, "Please enter company name", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etCompanyName.getText().toString(),"Company Name Crop");
                etCompanyName.setText("");
            }
        });

        addCompanyNamePesticide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCompanyNamePesticide.getText())){
                    Toast.makeText(MainActivity.this, "Please enter company name", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etCompanyNamePesticide.getText().toString(),"Company Name Pesticide");
                etCompanyNamePesticide.setText("");
            }
        });

        addEstimatedIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etEstimatedincome.getText())){
                    Toast.makeText(MainActivity.this, "Please enter estimated income", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etEstimatedincome.getText().toString(),"Estimated Income");
                etEstimatedincome.setText("");
            }
        });

        addPesticide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etAddPesticide.getText())){
                    Toast.makeText(MainActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDetails(etAddPesticide.getText().toString(),"Pesticide Quantity");
                etAddPesticide.setText("");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.registration_details:
                createAlertDialog(0);
                break;

            case R.id.uploads:
                createAlertDialog(1);
                break;

            case R.id.statistics:
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                intent.putStringArrayListExtra("location", (ArrayList<String>) locationList);
                intent.putStringArrayListExtra("address", (ArrayList<String>) addressList);
                intent.putStringArrayListExtra("audioCount", (ArrayList<String>) audioList);
                intent.putStringArrayListExtra("imageCount", (ArrayList<String>) imageList);
                intent.putStringArrayListExtra("allRegistrations", (ArrayList<String>) registrationsList);

                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void createAlertDialog(final int n) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mobile No.");

        final View view = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null, false);

        final EditText editTextSnippet = (EditText) view.findViewById(R.id.alert_mobile_no);

        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mobileNo = editTextSnippet.getText().toString();
                String mobileNoWithCode = "+91" + mobileNo;

                if (n == 0) {
                    Intent intent = new Intent(MainActivity.this, RegistrationDetailsActivity.class);
                    intent.putExtra("mobile_no", mobileNoWithCode);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, UploadsActivity.class);
                    intent.putExtra("mobile_no", mobileNoWithCode);
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public void addLocationDetails() {
        String state = etState.getText().toString();
        String district = etDistrict.getText().toString();
        String tehsil = etTehsil.getText().toString();
        String village = etVillage.getText().toString();

        if(TextUtils.isEmpty(etState.getText())){
            Toast.makeText(this, "Please enter a state", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etDistrict.getText())){
            Toast.makeText(this, "Please enter a district", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etTehsil.getText())){
            Toast.makeText(this, "Please enter a tehsil", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(etVillage.getText())){
            Toast.makeText(this, "Please enter a village", Toast.LENGTH_SHORT).show();
            return;
        }

        LocationUploadHelper locationUploadHelper = new LocationUploadHelper(state.toUpperCase(), district.toUpperCase(), tehsil.toUpperCase(), village.toUpperCase());
        mDatabaseReference.child("Location Details").push().setValue(locationUploadHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Details Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
            }
        });
       /* DistrictHelper districtHelper = new DistrictHelper(district.toUpperCase());
        mDatabaseReference.child("Location Details").child(state.toUpperCase()).child(district.toUpperCase()).child(tehsil.toUpperCase()).push().setValue(districtHelper);*/

        etState.setText("");
        etDistrict.setText("");
        etTehsil.setText("");
        etVillage.setText("");

    }

    public void addDetails(final String value, final String parentName){

        InputUploadHelper inputUploadHelper= new InputUploadHelper(value.toUpperCase());
        mDatabaseReference.child(parentName).push().setValue(inputUploadHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Details Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
            }
        });

       /* mDatabaseReference.child(parentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (!data.getValue(InputUploadHelper.class).getInput().equals(value.toUpperCase())) {

                        InputUploadHelper inputUploadHelper= new InputUploadHelper(value.toUpperCase());
                        mDatabaseReference.child(parentName).push().setValue(inputUploadHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Details Added", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Unexpected Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Value already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });*/

    }

    public void getStats() {

        statsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    getImagesCount(recordingSnapshot.getKey());
                    getAudioCount(recordingSnapshot.getKey());
                    getLocation(recordingSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getImagesCount(String parent) {
        final String[] imagesCount = new String[1];
        statsDatabaseReference.child(parent).child("Images").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "");
                imagesCount[0] = String.valueOf(dataSnapshot.getChildrenCount());
                imageList.add(imagesCount[0]);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void getAudioCount(String parent) {
        final String[] audioCount = new String[1];
        statsDatabaseReference.child(parent).child("Recordings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "");
                audioCount[0] = String.valueOf(dataSnapshot.getChildrenCount());
                audioList.add(audioCount[0]);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void getLocation(String parent) {

        final String[] location = new String[4];
        statsDatabaseReference.child(parent).child("Registration Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserHelper userHelper = dataSnapshot.getValue(UserHelper.class);
                if(userHelper != null){
                        location[0] = userHelper.getState();
                        location[1] = userHelper.getDistrict();
                        location[2] = userHelper.getTehsil();
                        location[3] = userHelper.getVillage();
                }

                Log.e(dataSnapshot.getKey(), location[0] + " - " + location[1] + " - " + location[2] + " - " + location[3]);
                locationList.add(location[0] + " - " + location[1] + " - " + location[2] + " - " + location[3]);
                if (userHelper != null) {
                    addressList.add(userHelper.getPlace());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAllRegistrations(){
        statsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                registrationsList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    registrationsList.add(snapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
