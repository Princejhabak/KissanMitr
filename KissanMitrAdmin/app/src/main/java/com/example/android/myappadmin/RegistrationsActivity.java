package com.example.android.myappadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myappadmin.adapters.AllRegistrationsListAdapter;
import com.example.android.myappadmin.adapters.ImageUploadAdapter;
import com.example.android.myappadmin.adapters.StatisticsAdapter;
import com.example.android.myappadmin.helper.AllRegistrationsListHelper;
import com.example.android.myappadmin.helper.StatsHelper;
import com.example.android.myappadmin.helper.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegistrationsActivity extends AppCompatActivity {

    private List<String> registrationsList;
    private List<AllRegistrationsListHelper> registrationsListDetailed;
    private ListView listView;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrations");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");

        registrationsList = getIntent().getStringArrayListExtra("all_registrations");
        registrationsListDetailed = new ArrayList<>();

        listView = findViewById(R.id.list_all_registrations);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AllRegistrationsListHelper allRegistrationsListHelper = (AllRegistrationsListHelper) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(RegistrationsActivity.this, RegistrationDetailsActivity.class);
                intent.putExtra("mobile_no", allRegistrationsListHelper.getMobileNo());
                startActivity(intent);
            }
        });
        getAllRegistrationDetails();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getAllRegistrationDetails(){
        for (int i = 0; i < registrationsList.size(); i++) {

            mDatabaseReference.child(registrationsList.get(i)).child("Registration Data").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserHelper userHelper = dataSnapshot.getValue(UserHelper.class);

                    if (userHelper != null) {

                        AllRegistrationsListHelper allRegistrationsListHelper = new AllRegistrationsListHelper(userHelper.getImageUrl(),userHelper.getPhone(),userHelper.getName());
                        registrationsListDetailed.add(allRegistrationsListHelper);
                        AllRegistrationsListAdapter adapter = new AllRegistrationsListAdapter(RegistrationsActivity.this, registrationsListDetailed);
                        listView.setAdapter(adapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
