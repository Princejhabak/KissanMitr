package com.example.android.myapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.android.myapp.helper.CameraHelper;

import java.util.List;

public class CameraAdapter extends ArrayAdapter<CameraHelper> {

    private static final int PADDING = 8;
    private static final int WIDTH = 250;
    private static final int HEIGHT = 250;
    private Context mContext;

    public CameraAdapter(Context context, List<CameraHelper> images) {
        super(context, 0, images);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CameraHelper currentFile = getItem(position);

        ImageView imageView = (ImageView) convertView;

        // if convertView's not recycled, initialize some attributes
        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
            //imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentFile.getPath(), options);

        // Set inSampleSize
        options.inSampleSize = 4;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap myBitmap = BitmapFactory.decodeFile(currentFile.getPath(), options);

        imageView.setImageBitmap(myBitmap);

        return imageView;
    }
}
