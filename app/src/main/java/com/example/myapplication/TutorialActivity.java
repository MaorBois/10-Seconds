package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class TutorialActivity extends AppCompatActivity
{
    VideoView vv_background;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        vv_background = (VideoView) findViewById(R.id.vv_background);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wow);
        vv_background.setVideoURI(uri);
        vv_background.start();

        vv_background.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mp.setLooping(true);
            }
        });
    }

    @Override
    protected void onPause()
    {
        vv_background.suspend();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        vv_background.resume();
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        vv_background.stopPlayback();
        super.onDestroy();
    }
}
