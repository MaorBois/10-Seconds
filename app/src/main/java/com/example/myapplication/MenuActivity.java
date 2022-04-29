package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener
{
    NoInternetConnectionReceiver receiver_networkConnection = new NoInternetConnectionReceiver();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    static GameRoom nowCreatingGame;
    static String nowPlayingInThisPhone;
    static String gameCreatorUserName;
    static int anywayTheRoomNumberIs = -1;
    boolean thisGameExist = false;


    ImageButton btn_party, btn_join;
    EditText et_name;
    GameRoom temp_game;
    VideoView vv_background;
    Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        et_name = (EditText) findViewById(R.id.et_name);
        btn_party = (ImageButton) findViewById(R.id.btn_party);
        btn_join = (ImageButton) findViewById(R.id.btn_join);
        vv_background = (VideoView) findViewById(R.id.vv_background);

        btn_join.setOnClickListener(this);
        btn_party.setOnClickListener(this);

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

    /**
     * This function creates a menu
     * The function gets called when someone clicks the menu button
     * @param menu
     * @return
     */
    @Override
        public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * This function realizes the items's actions
     * The function gets called when someone clicks on one of the items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.it_tutorial)
        {
            Intent T = new Intent(MenuActivity.this,TutorialActivity.class);
            startActivity(T);
        }
        if (id == R.id.it_music)
        {
            musicIntent = new Intent(MenuActivity.this, backGroundMusicService.class);
            startService(musicIntent);
        }
        return super.onOptionsItemSelected(item);
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
        unregisterReceiver(receiver_networkConnection);
        stopService(musicIntent);
    }

    @Override
    public void onClick(View view)
    {
        /**
         * This function toasts a text
         * The function gets called when someone didn't typed a name
         */
        if(et_name.getText().toString().trim().length() == 0)
        {
            Toast.makeText(MenuActivity.this, "you must enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * This function creates GameRoom & passes who clicked on the "Create Party" button to the RoomSettings window
         * The function gets called when someone clicked on the "Create Party" button
         */
        if (view == btn_party)
        {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver_networkConnection,filter);
            gameCreatorUserName = et_name.getText().toString();
            nowPlayingInThisPhone = gameCreatorUserName;
            nowCreatingGame = new GameRoom(0, MenuActivity.gameCreatorUserName);

            Query q = myRef.child("all games").orderByKey();
            q.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    int num = (int) snapshot.getChildrenCount();
                    nowCreatingGame.roomNum = num;
                    anywayTheRoomNumberIs = num;
                    myRef.child("all games").child("game number "+num).setValue(nowCreatingGame);
                    Intent i1 = new Intent(MenuActivity.this,RoomSettings.class);
                    startActivity(i1);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {}
            });
        }
        /**
         * This function joins the player to the GameRoom with the code he entered & passes who clicked on the "Join Party" button to the JoinRoom window
         * The function gets called when someone clicked on the "Join Party" button
         */
        if (view == btn_join)
        {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(receiver_networkConnection,filter);

            EditText inputEditTextField = new EditText(MenuActivity.this);
            inputEditTextField.setInputType(InputType.TYPE_CLASS_TEXT);
            AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                    .setTitle("Enter party code")
                    .setView(inputEditTextField)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterfaceialog, int i)
                        {
                            String editTextInput = inputEditTextField.getText().toString();

                            if(editTextInput.equals(""))
                            {
                                Toast.makeText(MenuActivity.this, "Enter code!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Query q = myRef.child("all games").orderByKey();
                            q.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    for(DataSnapshot dst:snapshot.getChildren())
                                    {
                                        temp_game = dst.getValue(GameRoom.class);

                                        if(temp_game.roomCode.equals(editTextInput))
                                        {
                                            thisGameExist = true;
                                            anywayTheRoomNumberIs = temp_game.roomNum;

                                            if(temp_game.arr_players.size() >= 10)
                                                Toast.makeText(MenuActivity.this, "Party is full!", Toast.LENGTH_SHORT).show();
                                            else
                                            {
                                                nowPlayingInThisPhone = et_name.getText().toString();
                                                boolean youAreAlreadyIn = false;
                                                for(int i=0; i<temp_game.arr_players.size(); i++)
                                                {
                                                    if (temp_game.arr_players.get(i).getName().equals( et_name.getText().toString()))
                                                    {
                                                        Toast.makeText(MenuActivity.this, "This name already in use!", Toast.LENGTH_SHORT).show();
                                                        youAreAlreadyIn = true;
                                                    }
                                                }
                                                if (temp_game.gameStarted == true)
                                                {
                                                    Toast.makeText(MenuActivity.this, "This party already started!", Toast.LENGTH_SHORT).show();
                                                }
                                                else if (youAreAlreadyIn == false)
                                                {
                                                    Player p = new Player(0, et_name.getText().toString());
                                                    temp_game.arr_players.add(p);
                                                    myRef.child("all games").child("game number " + temp_game.roomNum).setValue(temp_game);
                                                    Intent i = new Intent(MenuActivity.this,JoinRoom.class);
                                                    startActivity(i);
                                                }
                                            }
                                        }
                                    }
                                    if (thisGameExist == false)
                                    {
                                        Toast.makeText(MenuActivity.this, "wrong code!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error)
                                {}
                            });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        }
    }

    /**
     * This function creates a dialog that asks if the player sure he wants to leave the app
     * The function gets called when someone clicked the BackPressed option on his phone
     */
    @Override
    public void onBackPressed()
    {
        AlertDialog dialog = new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Are you sure you want to leave?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterfaceialog, int i)
                    {
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .create();
        dialog.show();
        return;
    }
}