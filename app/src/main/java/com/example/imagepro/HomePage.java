package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.opencv.android.OpenCVLoader;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class HomePage extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }


    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static int firstTime = 0;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;
    private static TextToSpeech textToSpeech;
    //private DBHandler dbHandler;
    static String Readmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        if(checkIfAlreadyhavePermission()){
            Toast.makeText(getApplicationContext(), "Permission is granted", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(HomePage.this,
                    new String[]{Manifest.permission.READ_SMS},
                    1);
        }
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    textToSpeech.setSpeechRate(1);
                    Set<String> a=new HashSet<>();
                    a.add("male");//here you can give male if you want to select male voice.
                    Voice v=new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 1, false, a);
                    //textToSpeech.setPitch(100f);
                    textToSpeech.getDefaultVoice();
//                    Voice voiceobj = new Voice("it-it-x-kda#male_2-local",
//                            Locale.getDefault(), 2, 0, false, null);

                    textToSpeech.setVoice(v);
                    if (firstTime == 0)
                        textToSpeech.speak("Welcome to Dis-Disable App. Swipe left to listen the features of the Blind module or swipe right for the sign language module.", TextToSpeech.QUEUE_FLUSH, null);
                    //when user return from another activities to main activities.
                    if(firstTime!=0)
                        textToSpeech.speak("you are in main menu. just swipe left to view features of the app", TextToSpeech.QUEUE_FLUSH, null);

                }
            }
        });

        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);

    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    public boolean onTouchEvent(MotionEvent touchEvent) {
        firstTime = 1;
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    firstTime = 1;
                    textToSpeech.stop();
                    Intent intent = new Intent(HomePage.this, Features.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                if (x1 > x2) {
                    textToSpeech.stop();
                    Intent intent = new Intent(HomePage.this, com.example.imagepro.sign.CombineLettersActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                }


                break;
        }

        return false;
    }
}
