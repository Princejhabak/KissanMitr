package com.example.android.myapp.registration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.myapp.MainActivity;
import com.example.android.myapp.R;
import com.example.android.myapp.helper.InputUploadHelper;
import com.example.android.myapp.helper.UserHelper;
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

public class BasicRegistrationActivity_3 extends AppCompatActivity {

    private final String TAG = "Registration";

    private final int REQUEST_PERMISSION_CODE = 10;
    private final int REQUEST_CAMERA_CODE = 200;

    private String profile_image_path ;

    private ImageView imageView;
    private EditText aadharNo, birthYear;
    private FloatingActionButton fabCamera, fabNext;

    private UserHelper userHelper;

    private ProgressDialog progressDialog ;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private Spinner registrationTypeSpinner;
    private ArrayList<String> arrayList ;
    private ArrayAdapter<String> arrayAdapter;

    private String selectedSpinnerItem;

    private Boolean profilePicClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_registration_3);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.login);

        final Intent intent = getIntent();
        userHelper = intent.getParcelableExtra("user_data_1");

        // To ignore uri exposure so that camera will work on sdk level >24
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(getResources().getString(R.string.kissan_mitr_node));

        progressDialog = new ProgressDialog(this);

        fabCamera = findViewById(R.id.fab_profile_camera);
        fabNext = findViewById(R.id.basicRegistration3Next);
        aadharNo = findViewById(R.id.et_aadhar_no);
        birthYear = findViewById(R.id.et_birth_year);
        imageView = findViewById(R.id.iv_profile_pic);

        arrayList = new ArrayList<>();
        registrationTypeSpinner = findViewById(R.id.spinner_registration_type);

        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        setUpSpinner();

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(birthYear.getText())){
                    Toast.makeText(BasicRegistrationActivity_3.this, "Enter Birth Year", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(aadharNo.getText())){
                    Toast.makeText(BasicRegistrationActivity_3.this, "Enter Aadhar No", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!profilePicClicked){
                    Toast.makeText(BasicRegistrationActivity_3.this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser();
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissionFromDevice()) {
                    requestPermission();
                    return;
                }
                openCamera();
            }
        });

        registrationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinnerItem =  adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }

    private boolean checkPermissionFromDevice() {
        int camera_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return camera_result == PackageManager.PERMISSION_GRANTED && storage_result == PackageManager.PERMISSION_GRANTED;
    }

    public void openCamera(){
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageFile = new File(Environment.getExternalStorageDirectory(), "myApp/" + "ProfiePicture" + ".jpg");

        profile_image_path = imageFile.getAbsolutePath();

        Uri uri = Uri.fromFile(imageFile);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camIntent.putExtra("return-data", true);
        startActivityForResult(camIntent, REQUEST_CAMERA_CODE);
    }

    public void registerUser(){

        userHelper.setAadharNo(aadharNo.getText().toString());
        userHelper.setBirthYear(birthYear.getText().toString());
        userHelper.setRegistrationType(selectedSpinnerItem);

        progressDialog.setMessage("Registering User ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        storageReference = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.kissan_mitr_node)).child(userHelper.getPhone()).child("Profile Picture");

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        Uri uri = Uri.fromFile(new File(profile_image_path));

        storageReference.putFile(uri, metadata).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downUri = task.getResult();
                    userHelper.setImageUrl(downUri.toString());

                    mDatabaseReference.child(userHelper.getPhone()).child("Registration Data").setValue(userHelper);
                    Log.d(TAG, "onComplete: Url: "+ downUri.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BasicRegistrationActivity_3.this, "Registration failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressDialog.dismiss();
                Toast.makeText(BasicRegistrationActivity_3.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(BasicRegistrationActivity_3.this, MainActivity.class);
                intent.putExtra("user_data", userHelper);
                startActivity(intent);
            }
        });

    }
    public void setUpSpinner(){

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference1 = firebaseDatabase1.getReference(getResources().getString(R.string.kissan_mitr_admin_node));

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registrationTypeSpinner.setAdapter(arrayAdapter);

        mDatabaseReference1.child("Registered As").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    arrayList.add(inputUploadHelper.getInput());
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
