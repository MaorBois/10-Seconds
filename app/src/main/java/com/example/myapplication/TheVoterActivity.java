package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TheVoterActivity extends AppCompatActivity implements View.OnClickListener
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView tv_timer;
    TextView[] arr_tv_players_name,  arr_tv_voter_rate;
    ImageButton[] arr_ib_players_drawing;
    EditText et_theSubject;
    Button btn_sendTheSubject;
    GameRoom g;
    String subject;
    String nextRoundVoter;
    CountDownTimer timer;
    ArrayList <String> arr_names_without_voter;
    StorageReference storageReference;
    int voter_index_in_arr = -1;
    ArrayList <Rating> arr_round_rates;
    int click_counter = 0;
    VideoView vv_background;
    ValueEventListener myValueEventListener;

    Query q ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_voter);


        storageReference = FirebaseStorage.getInstance().getReference();
        arr_names_without_voter = new ArrayList<>();
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        et_theSubject = (EditText)findViewById(R.id.et_theSubject);
        btn_sendTheSubject = (Button)findViewById(R.id.btn_sendTheSubject);
        vv_background = (VideoView) findViewById(R.id.vv_background);
        arr_round_rates = new ArrayList<>();


        arr_tv_voter_rate = new TextView[9];
        arr_tv_voter_rate[0]=(TextView)findViewById(R.id.tv_voter_rate_1);
        arr_tv_voter_rate[1]=(TextView)findViewById(R.id.tv_voter_rate_2);
        arr_tv_voter_rate[2]=(TextView)findViewById(R.id.tv_voter_rate_3);
        arr_tv_voter_rate[3]=(TextView)findViewById(R.id.tv_voter_rate_4);
        arr_tv_voter_rate[4]=(TextView)findViewById(R.id.tv_voter_rate_5);
        arr_tv_voter_rate[5]=(TextView)findViewById(R.id.tv_voter_rate_6);
        arr_tv_voter_rate[6]=(TextView)findViewById(R.id.tv_voter_rate_7);
        arr_tv_voter_rate[7]=(TextView)findViewById(R.id.tv_voter_rate_8);
        arr_tv_voter_rate[8]=(TextView)findViewById(R.id.tv_voter_rate_9);

        arr_tv_players_name = new TextView[9];
        arr_tv_players_name[0] = (TextView) findViewById(R.id.tv_players_name_1);
        arr_tv_players_name[1] = (TextView) findViewById(R.id.tv_players_name_2);
        arr_tv_players_name[2] = (TextView) findViewById(R.id.tv_players_name_3);
        arr_tv_players_name[3] = (TextView) findViewById(R.id.tv_players_name_4);
        arr_tv_players_name[4] = (TextView) findViewById(R.id.tv_players_name_5);
        arr_tv_players_name[5] = (TextView) findViewById(R.id.tv_players_name_6);
        arr_tv_players_name[6] = (TextView) findViewById(R.id.tv_players_name_7);
        arr_tv_players_name[7] = (TextView) findViewById(R.id.tv_players_name_8);
        arr_tv_players_name[8] = (TextView) findViewById(R.id.tv_players_name_9);

        arr_ib_players_drawing = new ImageButton[9];
        arr_ib_players_drawing[0] = (ImageButton) findViewById(R.id.ib_players_drawing_1);
        arr_ib_players_drawing[1]  = (ImageButton) findViewById(R.id.ib_players_drawing_2);
        arr_ib_players_drawing[2]  = (ImageButton) findViewById(R.id.ib_players_drawing_3);
        arr_ib_players_drawing[3]  = (ImageButton) findViewById(R.id.ib_players_drawing_4);
        arr_ib_players_drawing[4]  = (ImageButton) findViewById(R.id.ib_players_drawing_5);
        arr_ib_players_drawing[5]  = (ImageButton) findViewById(R.id.ib_players_drawing_6);
        arr_ib_players_drawing[6]  = (ImageButton) findViewById(R.id.ib_players_drawing_7);
        arr_ib_players_drawing[7]  = (ImageButton) findViewById(R.id.ib_players_drawing_8);
        arr_ib_players_drawing[8]  = (ImageButton) findViewById(R.id.ib_players_drawing_9);

        q = myRef.child("all games").child("game number " + MenuActivity.anywayTheRoomNumberIs).orderByKey();
    }

    @Override
    public void onClick(View view)
    {
        click_counter++;
        if (click_counter == g.arr_players.size()-1)
        {
            g.finish = true;
        }

        int points = 150-(click_counter-1)*15;
        for (int i=0; i<g.arr_players.size()-1; i++)
        {
            if (view == arr_ib_players_drawing[i])
            {
                arr_tv_voter_rate[i].setText(""+points);
                Rating now_rating = new Rating(i, points);

                for (int j = 0; j < g.arr_players.size(); j++)
                {
                    if (g.arr_players.get(j).getName().equals(arr_tv_players_name[i].getText()))
                    {
                        if (click_counter == 1)
                        {
                            nextRoundVoter = arr_tv_players_name[i].getText().toString();
                        }
                        g.arr_players.get(j).setScore(g.arr_players.get(j).getScore()+points);
                        break;
                    }
                }
                arr_round_rates.add(now_rating);
                g.game_arr_round_rates = arr_round_rates;
                arr_ib_players_drawing[i].setEnabled(false);
                myRef.child("all games").child("game number " + g.roomNum).setValue(g);
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
                    g.voterName = nextRoundVoter;
                    g.roundNum ++;
                    g.roomSubject = "no subject";
                    g.game_arr_round_rates.clear();
                    for (int i = 0; i <g.arr_players.size(); i++)
                    {
                        g.arr_players.get(i).setDrawingIsReady(false);
                        if (g.arr_players.get(i).getDrawingUrl() != null)
                        {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(g.arr_players.get(i).getDrawingUrl());
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    // File deleted
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception exception)
                                {
                                    // Error
                                }
                            });
                            g.arr_players.get(i).setDrawingUrl(null);
                        }
                    }
                    myRef.child("all games").child("game number " + g.roomNum).setValue(g);

                    Intent i = new Intent(TheVoterActivity.this,WinnerListActivity.class);
                    startActivity(i);
                }
            }.start();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

            for (int i = 0; i < 9; i++)
            {
                arr_ib_players_drawing[i].setOnClickListener(this);
                arr_ib_players_drawing[i].setEnabled(false);
                arr_ib_players_drawing[i].setVisibility(View.INVISIBLE);
                arr_tv_players_name[i].setVisibility(View.INVISIBLE);
            }

            btn_sendTheSubject.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (et_theSubject.getText().toString().trim().length() == 0)
                    {
                        Toast.makeText(TheVoterActivity.this, "you must enter a subject!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        AlertDialog dialog = new AlertDialog.Builder(TheVoterActivity.this)
                                .setTitle("Are you sure?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterfaceialog, int i)
                                    {
                                        subject = et_theSubject.getText().toString();
                                        g.roomSubject = et_theSubject.getText().toString();

                                        for (int v=0; v<g.arr_players.size(); v++)
                                        {
                                            if (g.arr_players.get(v).getName().equals(g.voterName))
                                            {
                                                g.arr_players.get(v).setScore(g.arr_players.get(v).getScore()+125);
                                                break;
                                            }
                                        }
                                        myRef.child("all games").child("game number " + g.roomNum).setValue(g);

                                        btn_sendTheSubject.setEnabled(false);
                                        et_theSubject.setEnabled(false);
                                        if (!g.roomSubject.equals("no subject"))
                                        {
                                            timer = new CountDownTimer(Integer.valueOf(g.timeLimit) * 1000, 1000)
                                            {
                                                @Override
                                                public void onTick(long secondsUntilFinish)
                                                {
                                                    tv_timer.setText("" + secondsUntilFinish / 1000);
                                                }

                                                @Override
                                                public void onFinish()
                                                {
                                                    tv_timer.setText("Rate the players!");

                                                    for (int i = 0; i < 9; i++)
                                                    {
                                                        arr_ib_players_drawing[i].setEnabled(true);
                                                    }
                                                }
                                            }.start();
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .create();
                        dialog.show();
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
    protected void onPause()
    {
        vv_background.suspend();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        q.removeEventListener(myValueEventListener);
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        myValueEventListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                g = snapshot.getValue(GameRoom.class);
                et_theSubject.setText(subject);

                for (int i = 0; i < g.arr_players.size(); i++)
                {
                    if (!g.voterName.equals(g.arr_players.get(i).getName()))
                    {
                        arr_names_without_voter.add(g.arr_players.get(i).getName());
                    }
                }
                for (int i = 0; i < g.arr_players.size(); i++)
                {
                    if (g.voterName.equals(g.arr_players.get(i).getName()))
                        voter_index_in_arr = i;
                }
                for (int i = 0; i < g.arr_players.size(); i++)
                {
                    if (i < voter_index_in_arr)
                    {
                        arr_ib_players_drawing[i].setVisibility(View.VISIBLE);
                        arr_tv_players_name[i].setVisibility(View.VISIBLE);
                        arr_tv_players_name[i].setText(arr_names_without_voter.get(i));
                        Picasso.get().load(g.arr_players.get(i).getDrawingUrl()).fit().centerCrop().into(arr_ib_players_drawing[i]);
                    }
                    if (i > voter_index_in_arr)
                    {
                        arr_ib_players_drawing[i - 1].setVisibility(View.VISIBLE);
                        arr_tv_players_name[i - 1].setVisibility(View.VISIBLE);
                        arr_tv_players_name[i - 1].setText(arr_names_without_voter.get(i - 1));
                        Picasso.get().load(g.arr_players.get(i).getDrawingUrl()).fit().centerCrop().into(arr_ib_players_drawing[i - 1]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };
        q.addValueEventListener(myValueEventListener);
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
        Toast.makeText(TheVoterActivity.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }
}