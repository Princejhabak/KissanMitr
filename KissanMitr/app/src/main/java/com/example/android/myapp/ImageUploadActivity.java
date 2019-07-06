package com.example.android.myapp;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.myapp.adapters.ImageUploadAdapter;
import com.example.android.myapp.helper.CameraUploadHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageUploadActivity extends AppCompatActivity {

    private String TAG = "ImageUploadActivity";

    private GridView gridView;
    private List<CameraUploadHelper> uploadedImagesList;
    private DatabaseReference databaseReference;
    private String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Uploaded Images");

        mobileNo = getIntent().getExtras().getString("mobile");

        databaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");

        gridView = findViewById(R.id.grid_view);
        View emptyView = findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);

        uploadedImagesList = new ArrayList<>();

        getUploads();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CameraUploadHelper cameraUploadHelper = (CameraUploadHelper) adapterView.getItemAtPosition(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(ImageUploadActivity.this);
                builder.setMessage("Select an action or click outside the box to cancel.");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Download",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                downloadFile(cameraUploadHelper);
                            }
                        });

                builder.setNegativeButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFile(cameraUploadHelper);
                            }
                        });


                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getUploads() {
        databaseReference.child(mobileNo).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                uploadedImagesList.clear();

                for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {
                    CameraUploadHelper cameraUploadHelper = imageSnapshot.getValue(CameraUploadHelper.class);
                    uploadedImagesList.add(cameraUploadHelper);
                }

                ImageUploadAdapter adapter = new ImageUploadAdapter(ImageUploadActivity.this, uploadedImagesList);
                gridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void downloadFile(CameraUploadHelper instance) {
        Uri uri = Uri.parse(instance.getImageDownloadUrl());

        Toast.makeText(this, "Download Started", Toast.LENGTH_SHORT).show();

        DownloadManager.Request r = new DownloadManager.Request(uri);
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, instance.getImageName());
        r.allowScanningByMediaScanner();
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager dm = (DownloadManager) ImageUploadActivity.this.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }

    public void deleteFile(CameraUploadHelper instance) {

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("KissanMitr");

        Query query = mDatabaseReference.child(mobileNo).child("Images").orderByChild("imageName").equalTo(instance.getImageName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(instance.getImageDownloadUrl());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
                Toast.makeText(ImageUploadActivity.this, "Deleted !!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //  An error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });

        getUploads();
    }

}
