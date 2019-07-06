package com.example.android.myappadmin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.android.myappadmin.adapters.ImageUploadAdapter;
import com.example.android.myappadmin.adapters.RecordingUploadAdapter;
import com.example.android.myappadmin.helper.CameraUploadHelper;
import com.example.android.myappadmin.helper.RecorderUploadHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UploadsActivity extends AppCompatActivity {

    private String mobileNo;

    private ListView listView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private List<RecorderUploadHelper> upoadedRecordingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploads);

        Intent intent = getIntent();
        mobileNo = intent.getStringExtra("mobile_no");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mobileNo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("KissanMitr");

        upoadedRecordingsList = new ArrayList<>();

        listView = findViewById(R.id.uploadListView);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        getRecordingUploads();

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_uploads, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_recording_upload) {
            getRecordingUploads();
            return true;
        }

        if (id == R.id.menu_image_upload) {
            Intent intent = new Intent(this, ImageUploadActivity.class);
            intent.putExtra("mobile",mobileNo);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getRecordingUploads(){
        mDatabaseReference.child(mobileNo).child("Recordings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                upoadedRecordingsList.clear();

                for(DataSnapshot recordingSnapshot:dataSnapshot.getChildren()){
                    RecorderUploadHelper recorderUploadHelper = recordingSnapshot.getValue(RecorderUploadHelper.class);
                    upoadedRecordingsList.add(recorderUploadHelper);
                }

                RecordingUploadAdapter adapter = new RecordingUploadAdapter(UploadsActivity.this, upoadedRecordingsList, mobileNo);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
