package com.example.android.myappadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myappadmin.helper.PesticideHelper;
import com.example.android.myappadmin.helper.SoilReportHelper;
import com.example.android.myappadmin.helper.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationDetailsActivity extends AppCompatActivity {

    private String mobileNo;

    private TextView name, fName, gender, mobile, state, district, tehsil, village, address, aadhar, birthYear, registrationType;
    private ImageView imageView;

    private TextView cropCrop, companyNameCrop, quantityCrop, landOccupiedCrop;
    private TextView cropPesticide, companyNamePesticide, quantityPesticide, landOccupiedPesticide;
    private TextView kissanId, landAvailable, typeOfLand, cropsGrown, estimatedIncome, ph, nitrogen, phosphorous, pottasium, average;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_details);

        Intent intent = getIntent();
        mobileNo = intent.getStringExtra("mobile_no");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registration Details");
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitr");

        imageView = findViewById(R.id.iv_profile_pic);
        name = findViewById(R.id.tv_user_name);
        fName = findViewById(R.id.tv_user_f_name);
        gender = findViewById(R.id.tv_user_gender);
        mobile = findViewById(R.id.tv_user_mobile);
        state = findViewById(R.id.tv_user_state);
        district = findViewById(R.id.tv_user_district);
        tehsil = findViewById(R.id.tv_user_tehsil);
        village = findViewById(R.id.tv_user_village);
        address = findViewById(R.id.tv_user_address);
        aadhar = findViewById(R.id.tv_user_aadhar);
        birthYear = findViewById(R.id.tv_user_birth_year);
        registrationType = findViewById(R.id.tv_user_registered_as);

        cropCrop = findViewById(R.id.tv_user_crop_crop);
        companyNameCrop = findViewById(R.id.tv_user_company_product_crop);
        quantityCrop = findViewById(R.id.tv_user_quantity_crop);
        landOccupiedCrop = findViewById(R.id.tv_user_land_occupied_crop);

        cropPesticide = findViewById(R.id.tv_user_crop_pesticide);
        companyNamePesticide = findViewById(R.id.tv_user_company_product_pesticide);
        quantityPesticide = findViewById(R.id.tv_user_quantity_pesticide);
        landOccupiedPesticide = findViewById(R.id.tv_user_land_occupied_pesticide);

        kissanId = findViewById(R.id.tv_user_kissan_id);
        landAvailable = findViewById(R.id.tv_user_land_available);
        typeOfLand = findViewById(R.id.tv_user_type_of_land);
        cropsGrown = findViewById(R.id.tv_user_crops_grown);
        estimatedIncome = findViewById(R.id.tv_user_estimated_income);

        ph = findViewById(R.id.tv_user_ph);
        nitrogen = findViewById(R.id.tv_user_nitrogen_score);
        phosphorous = findViewById(R.id.tv_user_phosphorus_score);
        pottasium = findViewById(R.id.tv_user_pottasium_score);
        average = findViewById(R.id.tv_user_average_score);

        getUserRegistrationDetails();
        getUserCropRegistrationDetails();
        getUserPesticideRegistrationDetails();
        getUserSoilReport();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getUserRegistrationDetails() {
        mDatabaseReference.child(mobileNo).child("Registration Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserHelper userHelper = dataSnapshot.getValue(UserHelper.class);

                if (userHelper == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationDetailsActivity.this);
                    builder.setTitle(mobileNo);
                    builder.setMessage("The provided mobile number is not registered !!");
                    builder.setIcon(R.drawable.ic_local_phone_black_24dp);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.create().show();
                } else {

                    name.setText(userHelper.getName());
                    fName.setText(userHelper.getfName());
                    gender.setText(userHelper.getGender());
                    mobile.setText(userHelper.getPhone());

                    state.setText(userHelper.getState());
                    district.setText(userHelper.getDistrict());
                    tehsil.setText(userHelper.getTehsil());
                    village.setText(userHelper.getVillage());
                    address.setText(userHelper.getPlace());

                    aadhar.setText(userHelper.getAadharNo());
                    birthYear.setText(userHelper.getBirthYear());
                    registrationType.setText(userHelper.getRegistrationType());

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(getApplicationContext());
                    circularProgressDrawable.setStrokeWidth(5);
                    circularProgressDrawable.setCenterRadius(30);
                    circularProgressDrawable.start();

                    Glide.with(RegistrationDetailsActivity.this).load(userHelper.getImageUrl()).placeholder(circularProgressDrawable).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserCropRegistrationDetails() {
        mDatabaseReference.child(mobileNo).child("Crop Registration").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PesticideHelper pesticideHelper = dataSnapshot.getValue(PesticideHelper.class);

                if (pesticideHelper != null) {
                    cropCrop.setText(pesticideHelper.getCrop());
                    companyNameCrop.setText(pesticideHelper.getCompanyProduct());
                    quantityCrop.setText(String.valueOf(pesticideHelper.getQuantity()));
                    landOccupiedCrop.setText(pesticideHelper.getLandOccupied());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getUserPesticideRegistrationDetails() {
        mDatabaseReference.child(mobileNo).child("Pesticide Registration").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PesticideHelper pesticideHelper = dataSnapshot.getValue(PesticideHelper.class);

                if (pesticideHelper != null) {
                    cropPesticide.setText(pesticideHelper.getCrop());
                    companyNamePesticide.setText(pesticideHelper.getCompanyProduct());
                    quantityPesticide.setText(String.valueOf(pesticideHelper.getQuantity()));
                    landOccupiedPesticide.setText(pesticideHelper.getLandOccupied());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getUserSoilReport() {
        mDatabaseReference.child(mobileNo).child("Soil Report").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SoilReportHelper soilReportHelper = dataSnapshot.getValue(SoilReportHelper.class);

                if (soilReportHelper != null) {
                    kissanId.setText(soilReportHelper.getKissanId());
                    landAvailable.setText(soilReportHelper.getLandAvailable());
                    typeOfLand.setText(soilReportHelper.getTypeOfLand());
                    cropsGrown.setText(soilReportHelper.getCropsGrown());
                    estimatedIncome.setText(soilReportHelper.getEstimatedIncome());

                    ph.setText(soilReportHelper.getPh());
                    nitrogen.setText(soilReportHelper.getNitrogen());
                    phosphorous.setText(soilReportHelper.getPhosphorous());
                    pottasium.setText(soilReportHelper.getPotassium());
                    average.setText(soilReportHelper.getAverage());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
