package com.example.android.myappadmin.adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.myappadmin.R;
import com.example.android.myappadmin.helper.CameraUploadHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ImageUploadAdapter extends ArrayAdapter<CameraUploadHelper> {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 250;
    private Context mContext;

    public ImageUploadAdapter(Context context, List<CameraUploadHelper> images) {
        super(context, 0, images);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CameraUploadHelper currentFile = getItem(position);

        ImageView imageView = (ImageView) convertView;

        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext.getApplicationContext());
        circularProgressDrawable.setStrokeWidth(5);
        circularProgressDrawable.setCenterRadius(30);
        circularProgressDrawable.start() ;

        Glide.with(mContext).load(currentFile.getImageDownloadUrl()).placeholder(circularProgressDrawable).into(imageView);

        return imageView;
    }

}
