package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinRoom extends AppCompatActivity implements View.OnClickListener
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView tv_join_Room_playersNames, tv_roomCode, tv_roundLimit, tv_timeLimit;
    GameRoom g;
    Query q;
    ValueEventListener vel;
    VideoView vv_background;
    ImageButton ib_copyCode;


    @Override
    protected void onResume()
    {
        vv_background.resume();
        vel = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                g = snapshot.getValue(GameRoom.class);
                String all_names="";
                tv_roomCode.setText(""+g.roomCode);
                tv_roundLimit.setText(" "+g.roundLimit);
                tv_timeLimit.setText(" "+g.timeLimit);
                for(int i=0; i<g.arr_players.size();i++)
                {
                    all_names = all_names +"\n" +g.arr_players.get(i).getName();
                }
                tv_join_Room_playersNames.setText(all_names);

                if (g.voterName != null)
                {
                    if(g.voterName.equals(MenuActivity.nowPlayingInThisPhone))
                    {
                        Intent i3 = new Intent(JoinRoom.this,TheVoterActivity.class);
                        startActivity(i3);
                    }
                    else
                    {
                        Intent i3 = new Intent(JoinRoom.this,CanvasActivity.class);
                        startActivity(i3);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };
        q = myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).orderByKey();
        q.addValueEventListener(vel);
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        q.removeEventListener(vel);
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        vv_background.suspend();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join_room);
        tv_join_Room_playersNames = (TextView)findViewById(R.id.tv_join_Room_playersNames);
        vv_background = (VideoView) findViewById(R.id.vv_background);
        tv_roomCode = (TextView) findViewById(R.id.tv_roomCode);
        tv_roundLimit = (TextView) findViewById(R.id.tv_roundLimit);
        tv_timeLimit = (TextView) findViewById(R.id.tv_timeLimit);
        ib_copyCode = (ImageButton) findViewById(R.id.ib_copyCode);
        ib_copyCode.setOnClickListener(this);


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
    protected void onDestroy()
    {
        super.onDestroy();
        vv_background.stopPlayback();
     }


    @Override
    public void onBackPressed()
    {
        Toast.makeText(JoinRoom.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    public void onClick(View v)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("room_code", ""+g.roomCode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(JoinRoom.this, "The code has been copied to clipBoard!", Toast.LENGTH_LONG).show();
    }
}