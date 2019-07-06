package com.example.android.myapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myapp.helper.InputUploadHelper;
import com.example.android.myapp.helper.LocationUploadHelper;
import com.example.android.myapp.helper.UserHelper;
import com.example.android.myapp.registration.BasicRegistrationActivity_2;
import com.example.android.myapp.registration.BasicRegistrationActivity_3;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class ProfileActivity extends AppCompatActivity {

    private final int PLACE_PICKER_REQUEST = 100;
    private final int REQUEST_CAMERA_CODE = 10;
    private final int REQUEST_PERMISSION_CODE = 1000;

    private String profile_image_path;

    private FloatingActionButton fab;

    private EditText name, fName, address, aadhar, birthYear;
    private ImageView imageView;
    private Spinner state, district, tehsil, village, gender, registrationType;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ArrayList<String> stateList;
    private ArrayList<String> districtList;
    private ArrayList<String> tehsilList;
    private ArrayList<String> villageList;
    private ArrayList<String> genderList;
    private ArrayList<String> registeredAsList;

    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> tehsilAdapter;
    private ArrayAdapter<String> villageAdapter;
    private ArrayAdapter<String> genderAdapter;
    private ArrayAdapter<String> registeredAsAdapter;

    private String selectedState;
    private String selectedDistrict;
    private String selectedtehsil;
    private String selectedVillage;
    private String selectedGender;
    private String selectedRegisteredAs;

    private Place place;

    private Boolean profilePicClicked = false;

    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("My Profile");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitrAdmin");

        fab = findViewById(R.id.fab_profile);

        imageView = findViewById(R.id.iv_profile_pic);
        name = findViewById(R.id.et_add_name);
        fName = findViewById(R.id.et_add_fname);
        address = findViewById(R.id.et_add_location);
        aadhar = findViewById(R.id.et_add_aadhar_no);
        birthYear = findViewById(R.id.et_add_birth_year);

        gender = findViewById(R.id.spinner_gender);
        state = findViewById(R.id.spinner_state);
        district = findViewById(R.id.spinner_district);
        tehsil = findViewById(R.id.spinner_tehsil);
        village = findViewById(R.id.spinner_village);
        registrationType = findViewById(R.id.spinner_registered_as);

        stateList = new ArrayList<>();
        districtList = new ArrayList<>();
        tehsilList = new ArrayList<>();
        villageList = new ArrayList<>();
        registeredAsList = new ArrayList<>();
        genderList = new ArrayList<>();

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedState = adapterView.getItemAtPosition(i).toString();
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
                selectedDistrict = adapterView.getItemAtPosition(i).toString();
                setUpTehsilSpinner(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedtehsil = adapterView.getItemAtPosition(i).toString();
                setUpVillageSpinner(selectedtehsil);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVillage = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registrationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRegisteredAs = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkPermissionFromDevice()) {
                    requestPermission();
                    return;
                }
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(ProfileActivity.this), PLACE_PICKER_REQUEST);
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
                if (!checkPermissionFromDevice()) {
                    requestPermission();
                    return;
                }
                openCamera();
            }
        });

        setUpStateSpinner();
        setUpGenderSpinner();
        setUpRegisteredAsSpinner();
        getUserRegistrationDetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);

                if (data != null) {
                    address.setText(place.getAddress());
                }

            }
        }

        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(profile_image_path, options);

            // Set inSampleSize
            options.inSampleSize = 4;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap myBitmap = BitmapFactory.decodeFile(profile_image_path, options);
            imageView.setImageBitmap(myBitmap);
            profilePicClicked = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                saveUserData();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getUserRegistrationDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Registration Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserHelper userHelper = dataSnapshot.getValue(UserHelper.class);

                if (userHelper != null) {
                    name.setText(userHelper.getName());
                    fName.setText(userHelper.getfName());
                    aadhar.setText(userHelper.getAadharNo());
                    birthYear.setText(userHelper.getBirthYear());
                    address.setText(userHelper.getPlace());

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                    circularProgressDrawable.setStrokeWidth(5);
                    circularProgressDrawable.setCenterRadius(30);
                    circularProgressDrawable.start();

                    imageUrl = userHelper.getImageUrl();

                    Glide.with(ProfileActivity.this).load(userHelper.getImageUrl()).placeholder(circularProgressDrawable).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpStateSpinner() {

        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state.setAdapter(stateAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                stateList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
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

    public void setUpDistrictSpinner(final String selectedState) {

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, districtList);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        district.setAdapter(districtAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                districtList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if (locationUploadHelper.getState().equals(selectedState)) {
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

    public void setUpTehsilSpinner(final String selectedDistrict) {

        tehsilAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tehsilList);
        tehsilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tehsil.setAdapter(tehsilAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tehsilList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if (locationUploadHelper.getDistrict().equals(selectedDistrict)) {
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

    public void setUpVillageSpinner(final String selectedtehsil) {

        villageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, villageList);
        villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        village.setAdapter(villageAdapter);

        mDatabaseReference.child("Location Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                villageList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    LocationUploadHelper locationUploadHelper = recordingSnapshot.getValue(LocationUploadHelper.class);
                    if (locationUploadHelper.getTehsil().equals(selectedtehsil)) {
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

    public void setUpGenderSpinner() {
        genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        mDatabaseReference.child("Gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                genderList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    genderList.add(inputUploadHelper.getInput());
                    genderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpRegisteredAsSpinner() {
        registeredAsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, registeredAsList);
        registeredAsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registrationType.setAdapter(registeredAsAdapter);

        mDatabaseReference.child("Registered As").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                registeredAsList.clear();

                for (DataSnapshot recordingSnapshot : dataSnapshot.getChildren()) {
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    registeredAsList.add(inputUploadHelper.getInput());
                    registeredAsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void openCamera() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageFile = new File(Environment.getExternalStorageDirectory(), "myApp/" + "ProfiePicture" + ".jpg");

        profile_image_path = imageFile.getAbsolutePath();

        Uri uri = Uri.fromFile(imageFile);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camIntent.putExtra("return-data", true);
        startActivityForResult(camIntent, REQUEST_CAMERA_CODE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int camera_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int location_result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        return camera_result == PackageManager.PERMISSION_GRANTED && storage_result == PackageManager.PERMISSION_GRANTED
                && location_result == PackageManager.PERMISSION_GRANTED;
    }

    public void saveUserData() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");
        final UserHelper userHelper = new UserHelper();

        userHelper.setName(name.getText().toString());
        userHelper.setfName(fName.getText().toString());

        userHelper.setAadharNo(aadhar.getText().toString());
        userHelper.setBirthYear(birthYear.getText().toString());

        userHelper.setRegistrationType(selectedRegisteredAs);
        userHelper.setGender(selectedGender);
        userHelper.setState(selectedState);
        userHelper.setDistrict(selectedDistrict);
        userHelper.setTehsil(selectedtehsil);
        userHelper.setVillage(selectedVillage);

        userHelper.setPhone(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        userHelper.setPlace(address.getText().toString());


        if(profilePicClicked) {

            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Saving User Data...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            final StorageReference storageReference = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.kissan_mitr_node)).child(userHelper.getPhone()).child("Profile Picture");

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            Uri uri = Uri.fromFile(new File(profile_image_path));

            storageReference.putFile(uri, metadata).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        userHelper.setImageUrl(downUri.toString());

                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Registration Data").setValue(userHelper);
                        Log.d("Profile Activity", "onComplete: Url: " + downUri.toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, " User Profile Updated", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            userHelper.setImageUrl(imageUrl);

            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Registration Data").setValue(userHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this, " User Profile Updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, " Failed to save data", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


}
