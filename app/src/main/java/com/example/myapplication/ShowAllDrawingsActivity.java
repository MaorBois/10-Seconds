package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowAllDrawingsActivity extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView[] arr_tv_players_rate, arr_tv_show_all_players_names;
    TextView tv_showAllTitle;
    ImageButton[] arr_ib_show_all_players_drawing;
    GameRoom g;
    ArrayList<String> arr_names_without_voter;
    boolean drawingsReady = false;
    int voter_index_in_arr=-1;
    CountDownTimer timer;
    VideoView vv_background;
    ValueEventListener myValueEventListener;
    Query q;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_drawings);


        arr_names_without_voter = new ArrayList<>();
        tv_showAllTitle = (TextView) findViewById(R.id.tv_showAllTitle);
        vv_background = (VideoView) findViewById(R.id.vv_background);


        arr_tv_players_rate = new TextView[9];
        arr_tv_players_rate[0]=(TextView)findViewById(R.id.tv_player_rate_1);
        arr_tv_players_rate[1]=(TextView)findViewById(R.id.tv_player_rate_2);
        arr_tv_players_rate[2]=(TextView)findViewById(R.id.tv_player_rate_3);
        arr_tv_players_rate[3]=(TextView)findViewById(R.id.tv_player_rate_4);
        arr_tv_players_rate[4]=(TextView)findViewById(R.id.tv_player_rate_5);
        arr_tv_players_rate[5]=(TextView)findViewById(R.id.tv_player_rate_6);
        arr_tv_players_rate[6]=(TextView)findViewById(R.id.tv_player_rate_7);
        arr_tv_players_rate[7]=(TextView)findViewById(R.id.tv_player_rate_8);
        arr_tv_players_rate[8]=(TextView)findViewById(R.id.tv_player_rate_9);

        arr_tv_show_all_players_names = new TextView[9];
        arr_tv_show_all_players_names[0] = (TextView) findViewById(R.id.tv_showAll_players_name_1);
        arr_tv_show_all_players_names[1] = (TextView) findViewById(R.id.tv_showAll_players_name_2);
        arr_tv_show_all_players_names[2] = (TextView) findViewById(R.id.tv_showAll_players_name_3);
        arr_tv_show_all_players_names[3] = (TextView) findViewById(R.id.tv_showAll_players_name_4);
        arr_tv_show_all_players_names[4] = (TextView) findViewById(R.id.tv_showAll_players_name_5);
        arr_tv_show_all_players_names[5] = (TextView) findViewById(R.id.tv_showAll_players_name_6);
        arr_tv_show_all_players_names[6] = (TextView) findViewById(R.id.tv_showAll_players_name_7);
        arr_tv_show_all_players_names[7] = (TextView) findViewById(R.id.tv_showAll_players_name_8);
        arr_tv_show_all_players_names[8] = (TextView) findViewById(R.id.tv_showAll_players_name_9);

        arr_ib_show_all_players_drawing = new ImageButton[9];
        arr_ib_show_all_players_drawing[0] = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_1);
        arr_ib_show_all_players_drawing[1]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_2);
        arr_ib_show_all_players_drawing[2]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_3);
        arr_ib_show_all_players_drawing[3]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_4);
        arr_ib_show_all_players_drawing[4]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_5);
        arr_ib_show_all_players_drawing[5]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_6);
        arr_ib_show_all_players_drawing[6]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_7);
        arr_ib_show_all_players_drawing[7]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_8);
        arr_ib_show_all_players_drawing[8]  = (ImageButton) findViewById(R.id.ib_showAll_players_drawing_9);


        for(int i=0; i<9; i++)
        {
            arr_ib_show_all_players_drawing[i].setEnabled(false);
            arr_ib_show_all_players_drawing[i].setVisibility(View.INVISIBLE);
            arr_tv_show_all_players_names[i].setVisibility(View.INVISIBLE);
        }


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

        q = myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).orderByKey();
        myValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                g = snapshot.getValue(GameRoom.class);

                if(drawingsReady == false) {
                    tv_showAllTitle.setText("" + g.voterName + " is rating you now!");

                    for (int i=0; i<g.arr_players.size(); i++)
                    {
                        if (!g.voterName.equals(g.arr_players.get(i).getName()))
                        {
                            arr_names_without_voter.add(g.arr_players.get(i).getName());
                        }
                    }

                    for (int i=0; i<g.arr_players.size(); i++)
                    {
                        if (g.voterName.equals(g.arr_players.get(i).getName())) voter_index_in_arr = i;
                    }

                    for (int i = 0; i < g.arr_players.size() ; i++)
                    {
                        if(i<voter_index_in_arr)
                        {
                            arr_ib_show_all_players_drawing[i].setVisibility(View.VISIBLE);
                            arr_tv_show_all_players_names[i].setVisibility(View.VISIBLE);
                            arr_tv_show_all_players_names[i].setText(arr_names_without_voter.get(i));
                            Picasso.get().load(g.arr_players.get(i).getDrawingUrl()).fit().centerCrop().into(arr_ib_show_all_players_drawing[i]);
                        }
                        if(i>voter_index_in_arr)
                        {
                            arr_ib_show_all_players_drawing[i-1].setVisibility(View.VISIBLE);
                            arr_tv_show_all_players_names[i-1].setVisibility(View.VISIBLE);
                            arr_tv_show_all_players_names[i-1].setText(arr_names_without_voter.get(i-1));
                            Picasso.get().load(g.arr_players.get(i).getDrawingUrl()).fit().centerCrop().into(arr_ib_show_all_players_drawing[i-1]);
                        }
                    }
                    drawingsReady= true;
                }
                else
                {
                    if (g.game_arr_round_rates != null)
                    {
                        for (int i = 0; i < g.game_arr_round_rates.size(); i++)
                        {
                            Rating update_rating = g.game_arr_round_rates.get(i);
                            arr_tv_players_rate[update_rating.getArr_index()].setText("" + update_rating.getPlayer_rate());
                        }
                    }
                }
                if (g.finish == true)
                {
                    timer = new CountDownTimer( 5000, 1000)
                    {
                        @Override
                        public void onTick(long secondsUntilFinish)
                        {}
                        @Override
                        public void onFinish()
                        {
                            q.removeEventListener(myValueEventListener);
                            Intent i = new Intent(ShowAllDrawingsActivity.this,WinnerListActivity.class);
                            startActivity(i);
                        }
                    }.start();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };
        q.addValueEventListener(myValueEventListener);
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
        Toast.makeText(ShowAllDrawingsActivity.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }
}