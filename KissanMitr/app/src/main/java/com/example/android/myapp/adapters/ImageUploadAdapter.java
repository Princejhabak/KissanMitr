package com.example.android.myapp.adapters;

import android.content.Context;
import android.support.v4.widget.CircularProgressDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.myapp.helper.CameraUploadHelper;

import java.util.List;

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

