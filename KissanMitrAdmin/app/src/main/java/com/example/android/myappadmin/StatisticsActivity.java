package com.example.android.myappadmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.myappadmin.adapters.StatisticsAdapter;
import com.example.android.myappadmin.helper.StatsHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private ListView listView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private StatisticsAdapter adapter;

    private List<StatsHelper> statsList;

    private List<String> locationList;
    private List<String> imageList;
    private List<String> audioList;
    private List<String> addressList;

    private List<String> registrationsList;

    private final String[] registrationCount = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Statistics");
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitr");

        statsList = new ArrayList<>();

        locationList = getIntent().getStringArrayListExtra("location");
        addressList = getIntent().getStringArrayListExtra("address");
        audioList = getIntent().getStringArrayListExtra("audioCount");
        imageList = getIntent().getStringArrayListExtra("imageCount");
        registrationsList = getIntent().getStringArrayListExtra("allRegistrations");

        listView = findViewById(R.id.statsListView);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        getRegistrationCount();
        getStatistics();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_no_of_reg) {
            createAlertDialog();
            return true;
        }
        if (id == R.id.menu_all_reg) {
            Intent intent = new Intent(this, RegistrationsActivity.class);
            intent.putStringArrayListExtra("all_registrations", (ArrayList<String>) registrationsList);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRegistrationCount() {

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "");
                registrationCount[0] = String.valueOf(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void createAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StatisticsActivity.this);
        builder.setMessage("Number Of Registrations : " + registrationCount[0]);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void getStatistics(){

        for (int i = 0; i < addressList.size(); i++) {
            StatsHelper statsHelper = new StatsHelper(locationList.get(i), addressList.get(i), audioList.get(i), imageList.get(i));
            statsList.add(statsHelper);

        }
        adapter = new StatisticsAdapter(StatisticsActivity.this, statsList);
        listView.setAdapter(adapter);
    }


}
