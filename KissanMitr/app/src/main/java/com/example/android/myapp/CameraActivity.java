package com.example.android.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.myapp.adapters.CameraAdapter;
import com.example.android.myapp.helper.CameraHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private GridView gridView;
    private CameraAdapter mAdapter;

    private final int REQUEST_CAMERA_CODE = 100;
    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Photos");

        // To ignore uri exposure so that camera will work on sdk level >24
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        gridView =  findViewById(R.id.grid_view);
        View emptyView = findViewById(R.id.empty_view);
        gridView.setEmptyView(emptyView);

        mAdapter = new CameraAdapter(this, getImagesList());
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CameraHelper cameraHelper = new CameraHelper();
                cameraHelper =(CameraHelper) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(CameraActivity.this, ImageViewerActivity.class);
                Bundle extras = new Bundle();
                extras.putString("IMAGE_PATH",cameraHelper.getPath());
                extras.putString("IMAGE_NAME",cameraHelper.getName());
                extras.putInt("IMAGE_POSITION",i);
                intent.putExtras(extras);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_camera:
                if (checkPermissionFromDevice()){
                    openCamera();
                }
                else {
                    requestPermission();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK){
            mAdapter = new CameraAdapter(this, getImagesList());
            gridView.setAdapter(mAdapter);
        }
    }

    public List<CameraHelper> getImagesList() {

        String path = "";
        String name = "";
        Date date = null;

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApp/Pictures/");

        ArrayList<CameraHelper> images = new ArrayList<>();
        File[] listFile;

        if (dir.exists()) {

            listFile = dir.listFiles();
            if (listFile != null){
                Arrays.sort( listFile, new Comparator()
                {
                    public int compare(Object o1, Object o2) {

                        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                            return -1;
                        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });}

            if (listFile != null) {
                for (File f : listFile) {

                    if (f.isFile()) {

                        path = f.getAbsolutePath();
                        name = f.getName();
                        date = new Date(f.lastModified());

                    }

                    if (path.contains(".jpg")) {
                        images.add(new CameraHelper(path, name, date));

                    }
                }
            }
        }

        return images;
    }

    public void openCamera(){
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/myApp/Pictures") ;
        dir.mkdirs();
        String path = dir.toString();
        File imageFile = new File(path,System.currentTimeMillis() + ".jpg" );

        //File imageFile = new File(Environment.getExternalStorageDirectory(), "myApp/Pictures/" + timeStamp + ".jpg");
        Uri uri = Uri.fromFile(imageFile);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camIntent.putExtra("return-data", true);
        startActivityForResult(camIntent, 100);
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera_result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && camera_result == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_PERMISSION_CODE);
    }

}
