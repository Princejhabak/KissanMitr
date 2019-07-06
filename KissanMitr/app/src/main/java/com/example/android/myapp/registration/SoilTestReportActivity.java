package com.example.android.myapp.registration;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapp.R;
import com.example.android.myapp.helper.InputUploadHelper;
import com.example.android.myapp.helper.PesticideHelper;
import com.example.android.myapp.helper.SoilReportHelper;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SoilTestReportActivity extends AppCompatActivity {

    private TextView kissanId;

    private  int average = 0;

    private Spinner spLandAvailable, spTypeOfLand, spIncome, spCrop;

    private ArrayList<String> landAvailableList ;
    private ArrayList<String> typeOfLandList ;
    private ArrayList<String> incomeList ;
    private ArrayList<String> cropList ;

    private ArrayAdapter<String> landAvailableAdapter;
    private ArrayAdapter<String> typeOfLandAdapter;
    private ArrayAdapter<String> incomeAdapter;
    private ArrayAdapter<String> cropAdapter;

    private DatabaseReference mDatabaseReference;

    private String landAvailable, typeOfLand, income, crop;

    private EditText et_ph, et_nitrogen, et_phosphorous, et_potassium;
    private Button btn_plot, btnSubmit ;
    private String phLevel, nitrogenLevel, phosphorusLevel, potassiumLevel;

    private ProgressBar pbNitrogen, pbPhosphorous, pbPotassium, pbAverage;
    private TextView tvNitrogen, tvPhosphorus, tvPotassium, tvAverage;

    private TextView tvph, tvph5, tvph6, tvph7, tvph8 ,ph6, ph7, ph8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_test_report);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Soil Test Report");

        kissanId = findViewById(R.id.tvKissanId);
        kissanId.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("KissanMitrAdmin");

        landAvailableList = new ArrayList<>();
        typeOfLandList = new ArrayList<>();
        incomeList = new ArrayList<>();
        cropList = new ArrayList<>();

        spLandAvailable = findViewById(R.id.spinner_soil_report_land_available);
        spTypeOfLand = findViewById(R.id.spinner_soil_report_type_of_land);
        spIncome = findViewById(R.id.spinner_soil_report_income);
        spCrop = findViewById(R.id.spinner_soil_report_crop);

        et_ph = findViewById(R.id.et_ph);
        et_nitrogen = findViewById(R.id.et_nitrogen);
        et_phosphorous = findViewById(R.id.et_phosphorus);
        et_potassium = findViewById(R.id.et_potassium);
        btn_plot = findViewById(R.id.plot_graphs);

        pbNitrogen = findViewById(R.id.progress_nitrogen);
        pbPhosphorous = findViewById(R.id.progress_phosphorous);
        pbPotassium = findViewById(R.id.progress_potassium);
        pbAverage = findViewById(R.id.progress_average);
        tvNitrogen = findViewById(R.id.tv_nitrogen);
        tvPhosphorus = findViewById(R.id.tv_phosphorous);
        tvPotassium = findViewById(R.id.tv_potassium);
        tvAverage = findViewById(R.id.tv_average);

        tvph = findViewById(R.id.soil_ph);
        tvph5 = findViewById(R.id.tv_ph5);
        tvph6 = findViewById(R.id.tv_ph6);
        tvph7 = findViewById(R.id.tv_ph7);
        tvph8 = findViewById(R.id.tv_ph8);
        ph6 = findViewById(R.id.ph6);
        ph7 = findViewById(R.id.ph7);
        ph8 = findViewById(R.id.ph8);

        btnSubmit = findViewById(R.id.submit_soil_report);

        btn_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plotGraphs();
            }
        });

        setUpLandAvailableSpinner();
        setUpTypeOFLandSpinner();
        setUpIncomeSpinner();
        setUpCropSpinner();

        spLandAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                landAvailable =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spTypeOfLand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeOfLand =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spIncome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                income =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spCrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                crop =  adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReport();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void plotGraphs(){

        phLevel = et_ph.getText().toString();
        nitrogenLevel = et_nitrogen.getText().toString();
        phosphorusLevel = et_phosphorous.getText().toString();
        potassiumLevel = et_potassium.getText().toString();

        if(TextUtils.isEmpty(et_ph.getText())){
            Toast.makeText(this,"enter pH value",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Double.parseDouble(phLevel) > 7.0 || Double.parseDouble(phLevel) < 5.0){
            Toast.makeText(this,"pH must be b/w 5-7",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_nitrogen.getText())){
            Toast.makeText(this,"enter nitrogen value",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Integer.parseInt(nitrogenLevel) > 150 || Integer.parseInt(nitrogenLevel) < 0){
            Toast.makeText(this,"Nitrogen level must be below 150",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_phosphorous.getText())){
            Toast.makeText(this,"enter phosphorous value",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Integer.parseInt(phosphorusLevel) > 150 || Integer.parseInt(phosphorusLevel) < 0){
            Toast.makeText(this,"Phosphorous level must be below 150",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_potassium.getText())){
            Toast.makeText(this,"enter potassium value",Toast.LENGTH_SHORT).show();
            return;
        }
        else if(Integer.parseInt(potassiumLevel) > 150 || Integer.parseInt(potassiumLevel) < 0){
            Toast.makeText(this,"Potassium level must be below 150",Toast.LENGTH_SHORT).show();
            return;
        }

        pbNitrogen.setProgress(Integer.parseInt(nitrogenLevel));
        pbPhosphorous.setProgress(Integer.parseInt(phosphorusLevel));
        pbPotassium.setProgress(Integer.parseInt(potassiumLevel));

        average = (Integer.parseInt(nitrogenLevel) + Integer.parseInt(phosphorusLevel) + Integer.parseInt(potassiumLevel) )/3;
        pbAverage.setProgress(average);

        tvNitrogen.setText(nitrogenLevel + " ppm");
        tvPhosphorus.setText(phosphorusLevel + " ppm");
        tvPotassium.setText(potassiumLevel + " ppm");
        tvAverage.setText(average + " ppm");

        tvph.setText(phLevel);

        if(Double.parseDouble(phLevel) == 5){
            ph6.setVisibility(View.INVISIBLE);
            ph7.setVisibility(View.INVISIBLE);
            ph8.setVisibility(View.INVISIBLE);

            tvph5.setVisibility(View.VISIBLE);
            tvph6.setVisibility(View.INVISIBLE);
            tvph7.setVisibility(View.INVISIBLE);
            tvph8.setVisibility(View.INVISIBLE);
        }

        if(Double.parseDouble(phLevel) > 5.0 && Double.parseDouble(phLevel) <= 6.0){
            ph6.setVisibility(View.VISIBLE);
            ph7.setVisibility(View.INVISIBLE);
            ph8.setVisibility(View.INVISIBLE);

            tvph5.setVisibility(View.VISIBLE);
            tvph6.setVisibility(View.VISIBLE);
            tvph7.setVisibility(View.INVISIBLE);
            tvph8.setVisibility(View.INVISIBLE);
        }

        if(Double.parseDouble(phLevel) > 6.0 && Double.parseDouble(phLevel) <= 7.0){
            ph6.setVisibility(View.VISIBLE);
            ph7.setVisibility(View.VISIBLE);
            ph8.setVisibility(View.INVISIBLE);
            tvph7.setVisibility(View.VISIBLE);
            tvph5.setVisibility(View.VISIBLE);
            tvph6.setVisibility(View.VISIBLE);
            tvph8.setVisibility(View.INVISIBLE);
        }
    }

    public void setUpLandAvailableSpinner(){
        landAvailableAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, landAvailableList);
        landAvailableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLandAvailable.setAdapter(landAvailableAdapter);

        mDatabaseReference.child("Land Occupied").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                landAvailableList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    landAvailableList.add(inputUploadHelper.getInput());
                    landAvailableAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpTypeOFLandSpinner(){
        typeOfLandAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, typeOfLandList);
        typeOfLandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfLand.setAdapter(typeOfLandAdapter);

        mDatabaseReference.child("Type Of Land").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                typeOfLandList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    typeOfLandList.add(inputUploadHelper.getInput());
                    typeOfLandAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpIncomeSpinner(){
        incomeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, incomeList);
        incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIncome.setAdapter(incomeAdapter);

        mDatabaseReference.child("Estimated Income").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                incomeList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    InputUploadHelper inputUploadHelper = recordingSnapshot.getValue(InputUploadHelper.class);
                    incomeList.add(inputUploadHelper.getInput());
                    incomeAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void submitReport(){
        if(TextUtils.isEmpty(et_ph.getText())){
            Toast.makeText(this,"enter pH value",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_nitrogen.getText())){
            Toast.makeText(this,"enter nitrogen value",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_phosphorous.getText())){
            Toast.makeText(this,"enter phosphorous value",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(et_potassium.getText())){
            Toast.makeText(this,"enter potassium value",Toast.LENGTH_SHORT).show();
            return;
        }

        if(average == 0){
            Toast.makeText(this,"please plot graph to compute average",Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mDatabaseReference1 = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.kissan_mitr_node));

        SoilReportHelper soilReportHelper = new SoilReportHelper(kissanId.getText().toString(),landAvailable,typeOfLand,crop,
                income,phLevel,nitrogenLevel,phosphorusLevel,potassiumLevel,String.valueOf(average));

        mDatabaseReference1.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("Soil Report").setValue(soilReportHelper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SoilTestReportActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SoilTestReportActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpCropSpinner(){
        cropAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, cropList);
        cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCrop.setAdapter(cropAdapter);

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

}
