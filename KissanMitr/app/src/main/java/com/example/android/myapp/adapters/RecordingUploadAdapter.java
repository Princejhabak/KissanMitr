package com.example.android.myapp.adapters;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapp.MainActivity;
import com.example.android.myapp.R;
import com.example.android.myapp.helper.RecorderUploadHelper;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

public class RecordingUploadAdapter extends ArrayAdapter<RecorderUploadHelper> /*implements MediaPlayer.OnPreparedListener*/{

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ProgressDialog progressDialog;
    private MediaPlayer mMediaplayer;

    private Activity context;
    private List<RecorderUploadHelper> list;
    private String mobileNo;

    private String TAG = "Recording Adapter";

    public RecordingUploadAdapter(Activity context, List<RecorderUploadHelper> list, String mobileNo){
        super(context, R.layout.uploads_list_item, list);
        this.context = context;
        this.list = list;
        this.mobileNo = mobileNo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.uploads_list_item, null, true);

        TextView tvName = listViewItem.findViewById(R.id.uploadFileName);
        TextView tvDate = listViewItem.findViewById(R.id.uploadFileDate);

        ImageView download = listViewItem.findViewById(R.id.download);
        ImageView delete = listViewItem.findViewById(R.id.delete);

        final RecorderUploadHelper recorderUploadHelper = list.get(position);

        tvName.setText(recorderUploadHelper.getRecordingName());
        tvDate.setText(recorderUploadHelper.getRecordingDate());

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(context.getResources().getString(R.string.kissan_mitr_node));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = mDatabaseReference.child(mobileNo).child(context.getResources().getString(R.string.recordings_node)).orderByChild(
                        "recordingName").equalTo(recorderUploadHelper.getRecordingName());

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

                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReferenceFromUrl(recorderUploadHelper.getRecordingDownloadUrl());
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                        Toast.makeText(context,"Deleted !!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //  An error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(recorderUploadHelper.getRecordingDownloadUrl());

                Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();

                DownloadManager.Request r = new DownloadManager.Request(uri);
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, recorderUploadHelper.getRecordingName());
                r.allowScanningByMediaScanner();
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager dm = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(r);
            }
        });

        /*download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(
                        context.getResources().getString(R.string.kissan_mitr_node)).child(mobileNo).child(context.getResources().getString(R.string.recordings_node)).child(recorderUploadHelper.getRecordingName());

                mMediaplayer = new MediaPlayer();
                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                fetchAudioUrlFromFirebase(mStorageReference);
            }
        });*/

        return listViewItem;
    }

    /*@Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    private void fetchAudioUrlFromFirebase(StorageReference storageReference) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/kissanmitr-2f984.appspot.com/o/KissanMitr%2F%2B919782004122%2FRecordings%2F1549358334235.mp3?alt=media&token=36239291-f0b0-45db-8fb1-643e2822d2d8");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    final String url = uri.toString();
                    mMediaplayer.setDataSource(url);
                    // wait for media player to get prepare
                    mMediaplayer.setOnPreparedListener(RecordingUploadAdapter.this);
                    mMediaplayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

    }*/

}
