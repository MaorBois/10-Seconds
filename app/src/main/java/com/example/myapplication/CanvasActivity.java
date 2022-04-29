package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileOutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

public class CanvasActivity extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    //in order to get the reference of the View
    private DrawView paint;
    //creating objects of type button
    private ImageButton ib_color,ib_stroke,ib_undo, ib_blackColor;
    //creating a RangeSlider object, which will
    // help in selecting the width of the Stroke
    private RangeSlider range_slider_bar;
    CountDownTimer timer;
    TextView tv_canvasTimeLimit, tv_canvasSubject;
    GameRoom g;
    ProgressDialog progressDialog;
    Uri imageUri;
    private static final int IMAGE_REQUEST = 2;
    static Uri uriFromFile;
    static String url;
    boolean stam;
    ValueEventListener vel;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        //getting the reference of the views from their ids
        paint = (DrawView)findViewById(R.id.draw_view);
        range_slider_bar = (RangeSlider)findViewById(R.id.range_slider_bar);
        ib_undo = (ImageButton)findViewById(R.id.ib_undo);
        ib_color = (ImageButton)findViewById(R.id.ib_color);
        ib_stroke = (ImageButton)findViewById(R.id.ib_stroke);
        ib_blackColor = (ImageButton)findViewById(R.id.ib_blackColor);
        tv_canvasSubject = (TextView)findViewById(R.id.tv_canvasSubject);
        tv_canvasTimeLimit = (TextView)findViewById(R.id.tv_canvasTimeLimit);
        stam = true;
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST  && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
        }
    }

    public void saveImage(Context context, Bitmap bitmap, String name, String extension)
    {
        name = name + "." + extension;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            uriFromFile = Uri.fromFile(getFileStreamPath(name));
            uploadImage(uriFromFile);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(Uri uri)
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading your drawing");
        pd.show();

        if (uri != null)
        {
            StorageReference filesRef = FirebaseStorage.getInstance().getReference().child("game number "+ MenuActivity.anywayTheRoomNumberIs).child("player's name "+ MenuActivity.nowPlayingInThisPhone+"."+getFileExtension(uri));
            filesRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    filesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri)
                        {
                            url = uri.toString();
                            for (int i=0; i<g.arr_players.size(); i++)
                            {
                                if (g.arr_players.get(i).getName().equals(MenuActivity.nowPlayingInThisPhone))
                                {
                                    myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).child("arr_players").child(String.valueOf(i)).child("drawingIsReady").setValue(true);
                                    myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).child("arr_players").child(String.valueOf(i)).child("drawingUrl").setValue(url);
                                    timer = new CountDownTimer(2000, 1000)
                                    {
                                        @Override
                                        public void onTick(long secondsUntilFinish)
                                        {}
                                        @Override
                                        public void onFinish()
                                        {
                                            Intent S = new Intent(CanvasActivity.this,ShowAllDrawingsActivity.class);
                                            startActivity(S);
                                        }
                                    }.start();
                                }
                            }
                            pd.dismiss();
                        }
                    });
                }
            });
        }
    }

    
    @Override
    protected void onStart()
    {
        super.onStart();
        Query q = myRef.child("all games").child("game number "+ MenuActivity.anywayTheRoomNumberIs).orderByKey();
        vel = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                g = snapshot.getValue(GameRoom.class);
                tv_canvasTimeLimit.setText(g.timeLimit + "(s)");
                tv_canvasSubject.setText("" + g.voterName + " is picking the subject! ");

                if (g.roomSubject != null && !g.roomSubject.equals("no subject") && stam)
                {
                    tv_canvasSubject.setText("The subject is " + g.roomSubject);
                    timer = new CountDownTimer(Integer.valueOf(g.timeLimit)*1000, 1000)
                    {
                        @Override
                        public void onTick(long secondsUntilFinish)
                        {
                            tv_canvasTimeLimit.setText("  " + secondsUntilFinish/1000);
                        }
                        @Override
                        public void onFinish()
                        {
                            Bitmap bmp = paint.save();
                            tv_canvasTimeLimit.setText(" Time's up");
                            tv_canvasTimeLimit.setTextColor(Color.parseColor("#FF0000"));
                            q.removeEventListener(vel);

                            stam = false;

                            saveImage(CanvasActivity.this,bmp,"my drawing",".png");
                        }
                    }.start();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {}
        };
        q.addValueEventListener(vel);
        //the undo button will remove the most recent stroke from the canvas
        ib_undo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                paint.undo();
            }
        });
        ib_blackColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DrawView.currentColor = Color.BLACK;
            }
        });
        //the color button will allow the user to select the color of his brush
        ib_color.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final ColorPicker colorPicker = new ColorPicker(CanvasActivity.this);
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener()
                {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color)
                    {
                        //get the integer value of color selected from the dialog box and
                        // set it as the stroke color
                        paint.setColor(color);
                    }

                    @Override
                    public void onCancel()
                    {
                        colorPicker.dismissDialog();
                    }
                })
                        //set the number of color columns you want to show in dialog.
                        .setColumns(5)
                        //set a default color selected in the dialog
                        .setDefaultColorButton(Color.parseColor("#000000"))
                        .show();
            }
        });
        // the button will toggle the visibility of the RangeBar/RangeSlider
        ib_stroke.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(range_slider_bar.getVisibility()==View.VISIBLE)
                    range_slider_bar.setVisibility(View.GONE);
                else
                    range_slider_bar.setVisibility(View.VISIBLE);
            }
        });
        //set the range of the RangeSlider
        range_slider_bar.setValueFrom(0f);
        range_slider_bar.setValueTo(100f);
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        range_slider_bar.addOnChangeListener(new RangeSlider.OnChangeListener()
        {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser)
            {
                paint.setStrokeWidth((int) value);
            }
        });

        //pass the height and width of the custom view to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Toast.makeText(CanvasActivity.this, "You can't go back!", Toast.LENGTH_SHORT).show();
        return;
    }
}