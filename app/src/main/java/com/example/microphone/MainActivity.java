package com.example.microphone;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.microphone.R.id.t_button;

public class MainActivity extends AppCompatActivity {

    boolean pexstor;
    boolean pmicrophone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        externalStorage();
        pmic();

    }


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
