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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class RoomSettings extends AppCompatActivity implements View.OnClickListener
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView tv_roomCode, tv_playersNames;
    private NumberPicker numberpick_timeLimit, numberpick_roundLimit;
    private String[] pickerVals;
    String all_names="";
    Button btn_startGame;
    ImageButton ib_copyCode;
    GameRoom g;
    ValueEventListener vel;
    Query q;
    VideoView vv_background;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_settings);

        tv_roomCode = (TextView) findViewById(R.id.tv_roomCode);
        tv_playersNames = (TextView) findViewById(R.id.tv_playersNames);
        vv_background = (VideoView) findViewById(R.id.vv_background);
        ib_copyCode = (ImageButton) findViewById(R.id.ib_copyCode);
        ib_copyCode.setOnClickListener(this);
        btn_startGame = findViewById(R.id.btn_startGame);
        btn_startGame.setOnClickListener(this);

        numberpick_timeLimit = findViewById(R.id.numberpick_timeLimit);
        numberpick_timeLimit.setMaxValue(4);
        numberpick_timeLimit.setMinValue(0);

        numberpick_roundLimit = findViewById(R.id.numberpick_roundLimit);
        numberpick_roundLimit.setMaxValue(4);
        numberpick_roundLimit.setMinValue(0);

        pickerVals  = new String[] {"10", "30","25", "20", "15"};


        // this number picker is used to set the time limit of this specific game
        numberpick_timeLimit.setDisplayedValues(pickerVals);
        numberpick_timeLimit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1)
            {
                int valuePicker = numberpick_timeLimit.getValue();
                String valuePickerString = pickerVals[valuePicker];
                if (g != null)
                {
                    g.timeLimit = valuePickerString;
                    myRef.child("all games").child("game number " + g.roomNum).setValue(g);
                }
            }
        });

        // this number picker is used to set the round limit of this specific game
        numberpick_roundLimit.setDisplayedValues(pickerVals);
        numberpick_roundLimit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1)
            {
                int valuePicker = numberpick_roundLimit.getValue();
                String valuePickerString = pickerVals[valuePicker];
                if (g != null)
                {
                    g.roundLimit = valuePickerString;
                    myRef.child("all games").child("game number " + g.roomNum).setValue(g);
                }
            }
        });

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
    protected void onResume()
    {
        vv_background.resume();

        q = myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).orderByKey();
        vel = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                tv_playersNames.setText("");
                all_names="";
                g = snapshot.getValue(GameRoom.class);
                tv_roomCode.setText(""+ g.roomCode);
                for(int i=0; i<g.arr_players.size();i++)
                {
                    all_names = all_names +"\n" +g.arr_players.get(i).getName();
                }
                tv_playersNames.setText(all_names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };
        q.addValueEventListener(vel);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        vv_background.suspend();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        q.removeEventListener(vel);
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        vv_background.stopPlayback();
        super.onDestroy();
    }

    @Override
    public void onClick(View view)
    {
        if (view == btn_startGame)
        {
            if (g.arr_players.size() >= 3)
            {
                g.gameStarted = true;

                Random rn = new Random();
                int randomPlayerVoter = rn.nextInt(g.arr_players.size());
                g.voterName = g.arr_players.get(randomPlayerVoter).getName();
                myRef.child("all games").child("game number " + g.roomNum).setValue(g);

                if (g.voterName.equals(MenuActivity.nowPlayingInThisPhone))
                {
                    Intent i = new Intent(RoomSettings.this, TheVoterActivity.class);
                    startActivity(i);
                }
                else
                {
                    Intent j = new Intent(RoomSettings.this, CanvasActivity.class);
                    startActivity(j);
                }
            }
            else
            {
                Toast.makeText(RoomSettings.this, "Not enough players!", Toast.LENGTH_SHORT).show();
            }
        }

        if (view == ib_copyCode)
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("room_code", ""+g.roomCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(RoomSettings.this, "The code has been copied to clipBoard!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(RoomSettings.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }
}
