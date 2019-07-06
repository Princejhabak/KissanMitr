package com.example.android.myapp.registration;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapp.R;
import com.example.android.myapp.helper.InputUploadHelper;
import com.example.android.myapp.helper.PesticideHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CropRegistrationActivity extends AppCompatActivity {

    private Button increaseQuantity, decreaseQuantity, btnRegisterCrop;
    private TextView tvQuantity;

    private Spinner crop, company, landOccupied;

    private ArrayList<String> cropList ;
    private ArrayList<String> companyList ;
    private ArrayList<String> landOccupiedList ;

    private ArrayAdapter<String> cropAdapter;
    private ArrayAdapter<String> companyAdapter;
    private ArrayAdapter<String> landOccupiedAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private String cropName, companyName, landOccupiedQuantity;

    private int cropQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_registration);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Crop Registration");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitrAdmin");

        cropList = new ArrayList<>();
        companyList = new ArrayList<>();
        landOccupiedList = new ArrayList<>();

        increaseQuantity = findViewById(R.id.btn_add_quantity_crop);
        decreaseQuantity = findViewById(R.id.btn_subtract_quantity_crop);
        tvQuantity = findViewById(R.id.tv_quantity_crop);

        crop = findViewById(R.id.spinner_crop_crop);
        company = findViewById(R.id.spinner_company_product_crop);
        landOccupied = findViewById(R.id.spinner_land_occupied_crop);

        btnRegisterCrop = findViewById(R.id.btn_register_crop);

        setUpCropSpinner();
        setUpCompanySPinner();
        setUpLandSpinner();

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++cropQuantity;
                tvQuantity.setText(String.valueOf(cropQuantity));
            }
        });

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cropQuantity > 0) {
                    --cropQuantity;
                    tvQuantity.setText(String.valueOf(cropQuantity));
                }
            }
        });

        btnRegisterCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerCrop();
            }
        });

        crop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cropName =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                companyName =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        landOccupied.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                landOccupiedQuantity =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void setUpCropSpinner(){
        cropAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, cropList);
        cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crop.setAdapter(cropAdapter);

        mDatabaseReference.child("Crops Sown").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                cropList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    cropList.add(inputUploadHelper.getInput());
                    cropAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpCompanySPinner(){
        companyAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, companyList);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        company.setAdapter(companyAdapter);

        mDatabaseReference.child("Company Name Crop").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                companyList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    companyList.add(inputUploadHelper.getInput());
                    companyAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setUpLandSpinner(){
        landOccupiedAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, landOccupiedList);
        landOccupiedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        landOccupied.setAdapter(landOccupiedAdapter);

        mDatabaseReference.child("Land Occupied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                landOccupiedList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    landOccupiedList.add(inputUploadHelper.getInput());
                    landOccupiedAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void registerCrop(){

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference1 = firebaseDatabase1.getReference(getResources().getString(R.string.kissan_mitr_node));

        PesticideHelper pesticideHelper = new PesticideHelper(cropName, companyName, cropQuantity, landOccupiedQuantity);
        mDatabaseReference1.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Crop Registration").setValue(pesticideHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CropRegistrationActivity.this, "Crop Registerd", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CropRegistrationActivity.this, "Crop Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
