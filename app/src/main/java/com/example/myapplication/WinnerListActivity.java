package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WinnerListActivity extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    ListView gameScores;
    ArrayList<Player> personArr = new ArrayList<>();
    GameRoom g;
    CountDownTimer timer;
    TextView tv_nextRoundTimer, tv_gameOver, tv_parenthesis;
    VideoView vv_background;
    ValueEventListener myValueEventListener;
    Query q;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_list);


        tv_nextRoundTimer = (TextView) findViewById(R.id.tv_nextRoundTimer);
        tv_gameOver = (TextView) findViewById(R.id.tv_gameOver);
        vv_background = (VideoView) findViewById(R.id.vv_background);
        tv_parenthesis = (TextView) findViewById(R.id.tv_parenthesis);
        tv_parenthesis.setVisibility(View.INVISIBLE);

        q = myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).orderByKey();

        myValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                g = snapshot.getValue(GameRoom.class);

                for (int i=0; i<g.arr_players.size(); i++)
                {
                    personArr.add(new Player(g.arr_players.get(i).getScore(), g.arr_players.get(i).getName()));
                }
                if (g.finish == true)
                {
                    gameScores = findViewById(R.id.gameScores);

                    Collections.sort(personArr, new Comparator<Player>()
                    {
                        public int compare(Player p1, Player p2)
                        {
                            // ## Descending order
                            return Integer.valueOf(p2.getScore()).compareTo(Integer.valueOf(p1.getScore())); // To compare integer values of scores
                        }
                    });

                    ScoresAdapter scoresAdapter = new ScoresAdapter(WinnerListActivity.this, 0, 0, personArr);
                    gameScores.setAdapter(scoresAdapter);
                }
                if (g.roundNum == Integer.parseInt(g.roundLimit))
                {
                    tv_gameOver.setText("Game ends in");
                    tv_parenthesis.setVisibility(View.VISIBLE);
                    timer = new CountDownTimer(20000, 1000)
                    {
                        @Override
                        public void onTick(long secondsUntilFinish)
                        {
                            tv_nextRoundTimer.setText(" " + secondsUntilFinish / 1000);
                        }

                        @Override
                        public void onFinish()
                        {
                            Intent M = new Intent(WinnerListActivity.this, MenuActivity.class);
                            startActivity(M);

                            timer = new CountDownTimer(5000, 1000)
                            {
                                @Override
                                public void onTick(long secondsUntilFinish)
                                {}

                                @Override
                                public void onFinish()
                                {
                                    myRef.child("all games").child("game number " + g.roomNum).removeValue();
                                }
                            }.start();
                        }
                    }.start();
                }
                else
                {
                    timer = new CountDownTimer(10000, 1000)
                    {
                        @Override
                        public void onTick(long secondsUntilFinish)
                        {
                            tv_nextRoundTimer.setText("" + secondsUntilFinish / 1000);
                        }

                        @Override
                        public void onFinish()
                        {
                            if (g.voterName.equals(MenuActivity.nowPlayingInThisPhone))
                            {
                                Intent T = new Intent(WinnerListActivity.this, TheVoterActivity.class);
                                startActivity(T);
                            }
                            else
                            {
                                Intent C = new Intent(WinnerListActivity.this, CanvasActivity.class);
                                startActivity(C);
                            }
                            g.finish = false;

                            myRef.child("all games").child("game number " + g.roomNum).setValue(g);
                        }
                    }.start();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };

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
        q.removeEventListener(myValueEventListener);
        vv_background.suspend();
        super.onPause();
    }

    @Override
    protected void onStart()
    {
        q.addValueEventListener(myValueEventListener);
        super.onStart();
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

    @Override
    public void onBackPressed()
    {
        Toast.makeText(WinnerListActivity.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }
}