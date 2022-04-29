package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class backGroundMusicService extends Service
{
    MediaPlayer mp;

    public backGroundMusicService()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mp = MediaPlayer.create(this, R.raw.vibe);
        mp.setLooping(true);
        mp.setVolume(1f, 1f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mp.start();
        return 1;
    }

    @Override
    public void onDestroy()
    {
        mp.stop();
        mp.release();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public void onStop()
    {
        mp.stop();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}