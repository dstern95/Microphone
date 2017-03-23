package com.example.microphone;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_8BIT;
import static android.media.MediaRecorder.AudioSource.MIC;
import static com.example.microphone.R.id.t_button;

public class MainActivity extends AppCompatActivity {

    boolean pexstor;
    boolean pmicrophone = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private final static String TAG = MainActivity.class.getName();
    byte[] data;

    ArrayList<String> filenames = new ArrayList<String>();
    public AudioRecord recorder;
    short[] d2;
    boolean recording;
    Runnable r = new MyRunnable();
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        //recording= false;

        //writeToExternal();
        //pmic();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.t_button);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showWorking(true);
                    int bufferSize = AudioRecord.getMinBufferSize(16000, CHANNEL_IN_MONO, ENCODING_PCM_8BIT);
                    recorder = new AudioRecord(MIC, 8000, CHANNEL_IN_MONO, ENCODING_PCM_8BIT, bufferSize);
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
                    } catch (Exception e) {
                        //exception
                    }
                    //showWorking(false);

                    showWorking(false);
                    Log.d(TAG, "thread ended");
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                pmicrophone = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!pmicrophone) {
                    finish();
                }
                break;

            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //externalStorage();
                }
                break;
        }

    }

    public void writeToExternal() {
        String[] perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, perms, 1);
    }




    private void showWorking(boolean on) {
        View v = findViewById(R.id.t_button);
        if (on) {
            v.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            v.startAnimation(a);
        } else {
            v.setVisibility(View.INVISIBLE);
            v.clearAnimation();
        }
    }


    public void pmic() {
        String[] perms = new String[]{Manifest.permission.RECORD_AUDIO};

        ActivityCompat.requestPermissions(this, perms, 1);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "mic perm", Toast.LENGTH_LONG).show();

            pmicrophone = true;

        } else {
            pmicrophone = false;
            Toast.makeText(this, "no mic", Toast.LENGTH_LONG).show();
        }


    }

    class MyRunnable implements Runnable {


        public void run() {

            recorder.startRecording();

            //byte[] tmp = new byte[AudioRecord.getMinBufferSize(8000, CHANNEL_IN_MONO, ENCODING_PCM_8BIT)];
            byte[] tmp = new byte[8000];
            //byte[] a = new byte[bufferSize];
            Log.d(TAG, "pre loop");

            Log.d(TAG, Boolean.toString(recording));
            int tot = 0;
            ArrayList<byte[]> holder = new ArrayList<byte[]>();
            ArrayList<Integer> h2 = new ArrayList<Integer>();
            //ArrayList<byte> h1 = new ArrayList<byte>();
            //byte[] tmp = new byte[1024];
            ArrayList<Byte> h3 = new ArrayList<Byte>();


            while (recording == true) {
                int cur = 0;

                //recorder.read(,buffer);
                cur = recorder.read(tmp, 0, 8000);
                Log.d("Write",Byte.toString(tmp[65]));

                Log.d("Write",Integer.toString(cur));

                for(int i =0;i< cur; i++)
                {
                    h3.add(tmp[i]);
                }
                //holder.add(tmp);


                //h2.add(cur);
                //tot += cur;

            }



            recorder.stop();
            Log.d("Write","check");

            //int place = 0;
            byte[] a = new byte[h3.size()];
            for(int i = 0; i<h3.size(); i++)
            {
                a[i] = h3.get(i);
            }

            Log.d("Write",Byte.toString(a[a.length/2]));
            Log.d("Write",Byte.toString(a[a.length/2+1]));
            Log.d("Write",Byte.toString(a[a.length/2+2]));
            Log.d("Write",Byte.toString(a[a.length/2+3]));


            data = a;
            writeToExternal();


            Log.d(TAG, "post loop");

            return;

        }
    }


    public void externalStorage() {
        Log.d(TAG, "start");
        pexstor = true;

        //File loc = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "mic_recording_ds_md");

        //loc.mkdirs();
        file.mkdir();

        long time = System.currentTimeMillis()/10000;
        String timestamp = Long.toString(time) + ".pcm";
        Log.d(TAG, timestamp);
        //File f = new File(loc, timestamp);
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
        switch (item.getItemId()) {
            case R.id.menu_play:
                Toast.makeText(this, "Play", Toast.LENGTH_LONG).show();
                playAudio(data);
                //playMp3(data);
                break;

            case R.id.menu_save:
                externalStorage();
                Toast.makeText(this, "Saving Audio...", Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_view:
                Intent i = new Intent(this, viewsaved.class);
                startActivity(i);
                break;
        }

        return true;
    }
    public static void playAudio(byte[] data){
        try{
            Log.d("Audio","Playback enter");

            Log.d("Audio",data.toString());
            //AudioTrack audioTrack = new  AudioTrack(AudioManager.STREAM_MUSIC, 44100,
              //      AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT, 500000, AudioTrack.MODE_STATIC);

            //AudioTrack audioTrack = new AudioTrack()
            Log.d("Audio",Integer.toString(data.length));
            Log.d("Audio",Byte.toString(data[data.length/2]));
            Log.d("Audio",Byte.toString(data[data.length/2+1]));
            Log.d("Audio",Byte.toString(data[data.length/2+2]));
            Log.d("Audio",Byte.toString(data[data.length/2+3]));


            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_8BIT, data.length, AudioTrack.MODE_STATIC);
            audioTrack.write(data, 0, data.length);

            audioTrack.play();

        } catch(Throwable t){
            Log.d("Audio","Playback Failed");
        }
    }
}
