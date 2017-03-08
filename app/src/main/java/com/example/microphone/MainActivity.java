package com.example.microphone;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.MediaRecorder.AudioSource.MIC;
import static com.example.microphone.R.id.t_button;

public class MainActivity extends AppCompatActivity {

    boolean pexstor;
    boolean pmicrophone = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private final static String TAG = MainActivity.class.getName();


    boolean recording;
    Runnable r = new MyRunnable();
    Thread t;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                pmicrophone  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!pmicrophone ) finish();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        //recording= false;

        //externalStorage();
        //pmic();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.t_button);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Toggle", "toggle on");
                    recording = true;
                    //showWorking(true);
                    t = new Thread(r, "record");
                    t.start();
                    Log.d(TAG, "thread started");


                } else {
                    Lock _mutex = new ReentrantLock(true);

                    _mutex.lock();
                    recording = false;


                    _mutex.unlock();
                    Log.d("Toggle", "toggle off");

                    try {
                        t.join();
                    }catch (Exception e)
                    {

                    }
                    //showWorking(false);

                    Log.d(TAG, "thread ended");
                }
            }
        });
    }

    /*
    private void showWorking(boolean on) {
        View v = findViewById(R.id.imageView);
        if (on) {
            v.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            v.setAnimation(a);
            v.animate();
        } else {
            v.setVisibility(View.VISIBLE);
            v.clearAnimation();
        }
    }
    */

    public void pmic()
    {
        String[] perms = new String[]{Manifest.permission.RECORD_AUDIO};

        ActivityCompat.requestPermissions(this, perms, 1);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "mic perm", Toast.LENGTH_LONG).show();

            pmicrophone = true;

        }
        else
        {
            pmicrophone = false;
            Toast.makeText(this, "no mic", Toast.LENGTH_LONG).show();
        }


    }

    class MyRunnable implements Runnable{


        public void run() {
            int bufferSize = AudioRecord.getMinBufferSize(44100, CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

            AudioRecord recorder = new AudioRecord(MIC,44100, CHANNEL_IN_MONO, ENCODING_PCM_16BIT, bufferSize);
            recorder.startRecording();

            Log.d(TAG, "pre loop");
            short[] a = new short[AudioRecord.getMinBufferSize(44100, CHANNEL_IN_MONO, ENCODING_PCM_16BIT)];
            Log.d(TAG, Boolean.toString(recording));
            while (recording == true)
            {



                //recorder.read(,buffer);
                recorder.read(a,0,bufferSize);

                //Log.d(TAG, Boolean.toString(recording));

            }

            recorder.stop();
            Log.d(TAG, Boolean.toString(recording));
            
            Log.d(TAG, "post loop");
            return;

        }
    }



    public void externalStorage()
    {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, perms, 1);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "writeable", Toast.LENGTH_LONG).show();

            pexstor = true;

        }
        else
        {
            pexstor = false;
            Toast.makeText(this, "not writeable", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu, m);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_play:
                Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
                break;

            case R.id.menu_save:
                break;
            case R.id.menu_view:
                break;
        }

        return true;
    }
/*
    private class record extends AsyncTask<Long, Integer, Long>
    {
        @Override
        protected void onPreExcecute(){ showWorking(true)}


    }
    */
}
