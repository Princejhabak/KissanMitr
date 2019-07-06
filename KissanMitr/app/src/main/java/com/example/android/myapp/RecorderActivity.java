package com.example.android.myapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapp.adapters.RecorderAdapter;
import com.example.android.myapp.helper.RecorderHelper;
import com.example.android.myapp.helper.RecorderUploadHelper;
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
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecorderActivity extends AppCompatActivity {

    final int REQUEST_PERMISSION_CODE = 100;
    private String LOG_TAG = "RECORDER";

    private MediaRecorder mediaRecorder;
    private MediaPlayer mPlayer;
    private String pathSave = "";

    private RecorderAdapter mAdapter;
    private ListView listView;

    private MenuItem menu_start, menu_stop;

    private CardView cardView;
    private Button btn_delete, btn_pause, btn_play, btn_upload;
    private TextView audio_name, start_time, end_time;
    private SeekBar seekBar;

    private Handler handler;
    private Runnable runnable;

    private Boolean playing_finished = true;

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.recorder_activity_title);

        storageReference = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.kissan_mitr_node));
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference(getResources().getString(R.string.kissan_mitr_node));
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        handler = new Handler();

        cardView = findViewById(R.id.cv_recorder);
        btn_delete = findViewById(R.id.btn_delete_recording);
        btn_pause = findViewById(R.id.btn_pause_recording);
        btn_play = findViewById(R.id.btn_play_recording);
        btn_upload = findViewById(R.id.btn_upload_recording);
        audio_name = findViewById(R.id.tv_audio_name);
        start_time = findViewById(R.id.tv_duration_start);
        end_time = findViewById(R.id.tv_duration_end);
        seekBar = findViewById(R.id.seek_bar);

        cardView.setVisibility(View.GONE);

        listView = findViewById(R.id.recording_list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mAdapter = new RecorderAdapter(this, getRecordingList());
        listView.setAdapter(mAdapter);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int i, long l) {

                RecorderHelper recorderHelper = new RecorderHelper();
                recorderHelper = (RecorderHelper) adapterView.getItemAtPosition(i);

                final String name = recorderHelper.getRecordingName();

                if (playing_finished) {
                    startPlaying(name);
                } else {
                    stopPlaying();
                    startPlaying(name);
                }

                cardView.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);
                audio_name.setText(recorderHelper.getRecordingName());
                end_time.setText(recorderHelper.getRecordingLength());
                start_time.setText("00.00.00");


                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopPlaying();

                        RecorderHelper recorderHelper = new RecorderHelper();
                        recorderHelper = (RecorderHelper) adapterView.getItemAtPosition(i);

                        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "myApp/Recordings");
                        String selected_audio_path = mediaStorageDir.getAbsolutePath() + "/" + recorderHelper.getRecordingName();

                        File selectedFile = new File(selected_audio_path);
                        selectedFile.delete();
                        Toast.makeText(RecorderActivity.this, "Recording Deleted...", Toast.LENGTH_SHORT).show();

                        cardView.setVisibility(View.GONE);
                        mAdapter = new RecorderAdapter(getBaseContext(), getRecordingList());
                        listView.setAdapter(mAdapter);


                    }
                });

                btn_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopPlaying();
                    }
                });

                btn_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_pause.setVisibility(View.VISIBLE);
                        btn_play.setVisibility(View.GONE);
                        startPlaying(name);
                    }
                });

                btn_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uploadToFirebase(name);
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recording, menu);
        menu_start = menu.findItem(R.id.start_recording);
        menu_stop = menu.findItem(R.id.stop_recording);
        menu_stop.setVisible(false);
        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.start_recording:

                if (checkPermissionFromDevice()) {

                    menu_start.setVisible(false);
                    menu_stop.setVisible(true);

                    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "myApp/Recordings");

                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d("App", "failed to create directory");
                        }
                    }

                    pathSave = mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";
                    setupMediaRecorder();

                    try {
                        if(mediaRecorder != null){
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(RecorderActivity.this, R.string.recording_started, Toast.LENGTH_SHORT).show();

                } else {
                    requestPermission();
                }

                break;

            case R.id.stop_recording:
                // Stop recording
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                menu_start.setVisible(true);
                menu_stop.setVisible(false);

                // Refresh ListView
                mAdapter = new RecorderAdapter(this, getRecordingList());
                listView.setAdapter(mAdapter);

                Toast.makeText(RecorderActivity.this, R.string.recording_stopped, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mediaRecorder == null) {
            finish();
        } else {
            Toast.makeText(this, R.string.please_stop_recording, Toast.LENGTH_SHORT).show();
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        if (mediaRecorder == null) {
            finish();
        } else {
            Toast.makeText(this, R.string.please_stop_recording, Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
        }
        handler.removeCallbacks(runnable);
    }


    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSION_CODE);
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    public List<RecorderHelper> getRecordingList() {

        String path = "";

        Date date = null;
        String formatted_date = "";

        int duration = 0;
        String formatted_duration = "";

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApp/Recordings/");

        ArrayList<RecorderHelper> recordings = new ArrayList<>();
        File[] listFile;

        if (dir.exists()) {

            listFile = dir.listFiles();
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

            if (listFile != null) {
                for (File f : listFile) {

                    if (f.isFile()) {
                        path = f.getName();

                        date = new Date(f.lastModified());
                        formatted_date = DateFormat.getDateInstance(DateFormat.SHORT).format(date);

                        MediaPlayer mp = MediaPlayer.create(this, Uri.parse(f.getAbsolutePath()));
                        duration = mp.getDuration(); // in ms

                        formatted_duration = String.format(Locale.ENGLISH, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(duration),
                                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1));

                    }

                    if (path.contains(".mp3")) {
                        recordings.add(new RecorderHelper(path, formatted_date, formatted_duration));

                    }
                }
            }
        }

        return recordings;
    }

    public void startPlaying(String name) {
        mPlayer = new MediaPlayer();

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "myApp/Recordings");
        String selected_audio_path = mediaStorageDir.getAbsolutePath() + "/" + name;

        try {

            mPlayer.setDataSource(selected_audio_path);
            mPlayer.prepare();
            mPlayer.start();
            playing_finished = false;

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        seekBar.setMax(mPlayer.getDuration());
        playCycle();

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopPlaying();
            }
        });


    }

    public void stopPlaying() {
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
        if (mPlayer != null) {
            playing_finished = true;
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            start_time.setText("00.00.00");
            seekBar.setProgress(0);
        }
    }

    public void playCycle() {
        if (!playing_finished) {

            int currentPosition = mPlayer.getCurrentPosition();

            String formattedPosition = String.format(Locale.ENGLISH, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(currentPosition),
                    TimeUnit.MILLISECONDS.toMinutes(currentPosition) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(currentPosition) % TimeUnit.MINUTES.toSeconds(1));

            start_time.setText(formattedPosition);
            seekBar.setProgress(currentPosition);

            if (mPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();
                    }
                };
                handler.postDelayed(runnable, 1000);
            } else
                return;
        }
    }

    public void uploadToFirebase(String name) {

        final String fileName = name;

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "myApp/Recordings");
        final String selected_audio_path = mediaStorageDir.getAbsolutePath() + "/" + name;

        final StorageReference file_path = storageReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Recordings").child(name);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        Uri uri = Uri.fromFile(new File(selected_audio_path));
        file_path.putFile(uri,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(RecorderActivity.this, "Upload Finished", Toast.LENGTH_SHORT).show();

                File file = new File(selected_audio_path);

                Date date = new Date(file.lastModified());
                final String formatted_date = DateFormat.getDateInstance(DateFormat.SHORT).format(date);

                file_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference databasePath = mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Recordings");
                        RecorderUploadHelper recorderUploadHelper = new RecorderUploadHelper(fileName, formatted_date,uri.toString());
                        databasePath.push().setValue(recorderUploadHelper);
                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RecorderActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

        /*file_path.putFile(uri, metadata).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();

                    File file = new File(selected_audio_path);

                    Date date = new Date(file.lastModified());
                    String formatted_date = DateFormat.getDateInstance(DateFormat.SHORT).format(date);

                    DatabaseReference databasePath = mDatabaseReference.child(firebaseAuth.getCurrentUser().getPhoneNumber()).child("Recordings");
                    RecorderUploadHelper recorderUploadHelper = new RecorderUploadHelper(fileName, formatted_date, downUri.toString());
                    databasePath.push().setValue(recorderUploadHelper);

                    Log.d(LOG_TAG, "onComplete: Url: " + downUri.toString());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RecorderActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressDialog.dismiss();
                Toast.makeText(RecorderActivity.this, "Upload Finished", Toast.LENGTH_SHORT).show();
            }
        });*/

    }


}
