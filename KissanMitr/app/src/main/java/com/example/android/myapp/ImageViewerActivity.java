package com.example.android.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.myapp.helper.CameraHelper;
import com.example.android.myapp.helper.CameraUploadHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ImageViewerActivity extends AppCompatActivity {

    private float x1,x2;
    private static final int MIN_DISTANCE = 150;

    private ImageView imageView ;

    private String imageName, imagePath ;
    private int imagePosition ;

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        imagePath = extras.getString("IMAGE_PATH");
        imageName = extras.getString("IMAGE_NAME");

        // To ignore uri exposure so that camera will work on sdk level >24
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        storageReference = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.kissan_mitr_node));
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(getResources().getString(R.string.kissan_mitr_node));
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.imageView);

        showImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.upload_image:
                uploadImageToFirebase();
                break;
            case R.id.delete_image:
                File file = new File(imagePath);
                file.delete();
                Toast.makeText(this, "Image deleted" ,Toast.LENGTH_SHORT).show();
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1) {
                        getImage();
                    }
                    // Right to left swipe action
                    else {
                        getImage();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public void showImage(){

        imageView.setScaleType(ImageView.ScaleType.CENTER);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options);

        imageView.setImageBitmap(myBitmap);

    }

    public void getImage(){

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApp/Pictures/");

        ArrayList<CameraHelper> images = new ArrayList<>();
        File[] listFile;

        if (dir.exists()) {

            listFile = dir.listFiles();
            if (listFile != null) {
                Arrays.sort(listFile, new Comparator() {
                    public int compare(Object o1, Object o2) {

                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });
                if (x1 > x2)
                {
                    if(imagePosition < listFile.length - 1){
                        ++imagePosition;
                        imagePath = listFile[imagePosition].getAbsolutePath();
                    }
                    else
                        return;
                }

                else
                {
                    if(imagePosition > 0){
                        --imagePosition;
                        imagePath = listFile[imagePosition].getAbsolutePath();
                    }
                    else
                        return;
                }
                showImage();
            }
        }
    }

    public void uploadImageToFirebase(){

        final StorageReference file_path = storageReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Images").child(imageName);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        Uri uri = Uri.fromFile(new File(imagePath));
        file_path.putFile(uri,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ImageViewerActivity.this, "Upload Finished", Toast.LENGTH_SHORT).show();

                File file = new File(imagePath);

                Date date = new Date(file.lastModified());
                final String formatted_date = DateFormat.getDateInstance(DateFormat.SHORT).format(date);

                file_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference databasePath = mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Images");
                        CameraUploadHelper cameraUploadHelper = new CameraUploadHelper(imageName, formatted_date,uri.toString());
                        databasePath.push().setValue(cameraUploadHelper);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageViewerActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
