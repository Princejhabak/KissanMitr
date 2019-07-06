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

public class PesticideRegistration extends AppCompatActivity {

    private Button btnRegisterPesticide;
    private Spinner crop, company, landOccupied, pesticide;

    private ArrayList<String> cropList ;
    private ArrayList<String> companyList ;
    private ArrayList<String> landOccupiedList ;
    private ArrayList<String> pesticideList;

    private ArrayAdapter<String> cropAdapter;
    private ArrayAdapter<String> companyAdapter;
    private ArrayAdapter<String> landOccupiedAdapter;
    private ArrayAdapter<String> pesticideAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private String cropName, companyName, landOccupiedQuantity, pesticideQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesticide_registration);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Pesticide Registration");

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitrAdmin");

        cropList = new ArrayList<>();
        companyList = new ArrayList<>();
        landOccupiedList = new ArrayList<>();
        pesticideList = new ArrayList<>();

        crop = findViewById(R.id.spinner_crop_pesticide);
        company = findViewById(R.id.spinner_company_product_pesticide);
        landOccupied = findViewById(R.id.spinner_land_occupied_pesticide);
        pesticide = findViewById(R.id.spinner_pesticide_quantity);

        btnRegisterPesticide = findViewById(R.id.btn_register_pesticide);

        setUpCropSpinner();
        setUpCompanySPinner();
        setUpLandSpinner();
        setUpQuantitySpinner();

        btnRegisterPesticide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPesticide();
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

        pesticide.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pesticideQuantity =  adapterView.getItemAtPosition(i).toString();
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

        mDatabaseReference.child("Company Name Pesticide").addValueEventListener(new ValueEventListener() {
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

    public void registerPesticide(){

        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference1 = firebaseDatabase1.getReference(getResources().getString(R.string.kissan_mitr_node));

        PesticideHelper pesticideHelper = new PesticideHelper(cropName, companyName, Integer.parseInt(pesticideQuantity), landOccupiedQuantity);
        mDatabaseReference1.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Pesticide Registration").setValue(pesticideHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PesticideRegistration.this, "Pesticide Registered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PesticideRegistration.this, "Pesticide Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setUpQuantitySpinner(){
        pesticideAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, pesticideList);
        pesticideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pesticide.setAdapter(pesticideAdapter);

        mDatabaseReference.child("Pesticide Quantity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pesticideList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    pesticideList.add(inputUploadHelper.getInput());
                    pesticideAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
