package com.example.microphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Environment;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_8BIT;
import static android.media.MediaRecorder.AudioSource.MIC;

public class MainActivity extends AppCompatActivity {

    boolean pexstor;
    boolean pmicrophone = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private final static String TAG = MainActivity.class.getName();
    
    byte[] data;

    ArrayList<String> filenames = new ArrayList<String>();
    public AudioRecord recorder;
    boolean recording;

    Runnable r = new MyRunnable();
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.t_button);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //if the button is toggled then record
                    showWorking(true);
                    buttonView.setBackgroundResource(R.drawable.roundbuttonon);

                    int bufferSize = AudioRecord.getMinBufferSize(16000, CHANNEL_IN_MONO, ENCODING_PCM_8BIT);
                    recorder = new AudioRecord(MIC, 8000, CHANNEL_IN_MONO, ENCODING_PCM_8BIT, bufferSize);

                    Log.d("Toggle", "toggle on");

                    recording = true;
                    t = new Thread(r, "record");
                    t.start();

                    Log.d(TAG, "thread started");

                } else {
                    //stop recording if toggle is off
                    buttonView.setBackgroundResource(R.drawable.roundbutton);
                    stoprecord();
                }


            }
        });



    }


    public void stoprecord(){

        //makes sure recording thread stops
        Lock _mutex = new ReentrantLock(true);

        _mutex.lock();
        recording = false;


        _mutex.unlock();
        Log.d("Toggle", "toggle off");

        try {
            t.join();
        } catch (Exception e) {
            //exception
        }

        showWorking(false);
        Log.d(TAG, "thread ended");
    }

    @Override
    protected void onPause()
    {
        //toggles button off on pause
        super.onPause();
        ToggleButton toggle = (ToggleButton) findViewById(R.id.t_button);
        toggle.setChecked(false);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //gets permissions
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                pmicrophone = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!pmicrophone) {
                    finish();
                }
                break;

            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
        }

    }

    public void writeToExternal() {

        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, perms, 1);
    }


    private void showWorking(boolean on) {
        //does an animation when the thread is running
        View v = findViewById(R.id.activity_fib_tv_recording);
        if (on) {
            v.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            v.startAnimation(a);
        } else {
            v.setVisibility(View.INVISIBLE);
            v.clearAnimation();
        }
    }

    class MyRunnable implements Runnable {


        public void run() {
            //thread for microphone records in byte[]
            recorder.startRecording();

            byte[] tmp = new byte[8000];
            Log.d(TAG, "pre loop");


            ArrayList<Byte> bufferList = new ArrayList<Byte>();


            while (recording) {
                int cur = 0;

                cur = recorder.read(tmp, 0, 8000);
                Log.d("Write",Byte.toString(tmp[65]));

                Log.d("Write",Integer.toString(cur));

                for(int i =0;i< cur; i++)
                {
                    bufferList.add(tmp[i]);
                }


            }



            recorder.stop();
            Log.d("Write","check");

            byte[] frecording = new byte[bufferList.size()];
            for(int i = 0; i<bufferList.size(); i++)
            {
                frecording[i] = bufferList.get(i);
            }


            data = frecording;
            writeToExternal();


            Log.d(TAG, "post loop");



        }
    }


    public void externalStorage() {

        //writes the files to external storage with a timestamp

        Log.d(TAG, "start");
        pexstor = true;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "mic_recording_ds_md");

        file.mkdir();
        Timestamp time1 = new Timestamp(System.currentTimeMillis());

        String timestamp = time1.toString() + ".pcm";
        Log.d(TAG, timestamp);


        File f = new File(file, timestamp);
        FileOutputStream fos;

        try {

            fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
            Log.d(TAG, "File written");

            filenames.add(timestamp);

        } catch (FileNotFoundException e1) {
            Log.d(TAG, "File Not Found");
        } catch (IOException e2) {
            Log.d(TAG, "Error Writing!");
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
        //menu
        switch (item.getItemId()) {
            case R.id.menu_play:
                Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
                playAudio(data);
                break;

            case R.id.menu_save:
                if (data!=null) {
                    if (recording == false) {
                        externalStorage();
                        Toast.makeText(this, "Saving Audio...", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Stop Recording First", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(this, "Record something first", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.menu_view:
                Intent i = new Intent(this, viewsaved.class);
                startActivity(i);
                break;
        }

        return true;
    }
    public static void playAudio(byte[] data){

        //plays the audio
        try{
            Log.d("Audio","Playback enter");


            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_8BIT, data.length, AudioTrack.MODE_STATIC);
            audioTrack.write(data, 0, data.length);

            audioTrack.play();

        } catch(Throwable t){
            Log.d("Audio","Playback Failed");
        }
    }
}
