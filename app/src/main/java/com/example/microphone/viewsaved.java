package com.example.microphone;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class viewsaved extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewsaved);

        //Intent i = getIntent();
        //ArrayList<String> filenames = i.getStringArrayListExtra("filenames");

        File loc = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "mic_recording_ds_md");
        File[] files = loc.listFiles();

        String[] fileArray = new String[files.length];

        for (int i=0; i < fileArray.length; i++) {
            fileArray[i] = files[i].getName();
        }

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileArray);

        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = listView.getItemAtPosition(position).toString();

                readFromExternal(filename);
            }
        });

    }

    public void readFromExternal(String filename) {
        File loc = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "mic_recording_ds_md");
        loc.mkdirs();

        File f = new File(loc, filename);
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            byte[] data = new byte[(int)f.length()];

            int result = fis.read(data);
            if (result == -1) {
                throw new IOException("fis.read result = -1");
            }

            fis.close();

            MainActivity.playAudio(data);

        } catch (FileNotFoundException e1) {
            Log.d(TAG, "File not found");

        } catch (IOException e2) {
            Log.d(TAG, "Cannot read file");

        }
    }
}
