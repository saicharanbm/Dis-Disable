package com.example.imagepro;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagepro.calculate.Calculator;
import com.example.imagepro.call.Call;
import com.example.imagepro.expression.CameraActivity;
import com.example.imagepro.location.CurrentLocation;
import com.example.imagepro.message.Message;
import com.example.imagepro.weather.weatherHome;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Features extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

    private static int firstTime = 0;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;

    private static TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.features_bac1);
        videoview.setVideoURI(uri);
        videoview.start();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(1);
                    Set<String> a=new HashSet<>();
                    a.add("male");//here you can give male if you want to select male voice.
                    Voice v=new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 1, false, a);
                    textToSpeech.getDefaultVoice();
                    textToSpeech.setVoice(v);

                    if (firstTime == 0)
                        textToSpeech.speak("say Time and date to know current time. Say calculator to open calculator.  Say expression to know the mood of people around you.  Say weather to know the weather info.  Say location to Know your current location. say Battery to know your battery percentage. say call to make a phone call. Say message to send message to your friend.Say read to read out the text in front of you. Say exit for closing the application . Swipe right and say what you want ", TextToSpeech.QUEUE_FLUSH, null);
                    //when user return from another activities to main activities.
                    if(firstTime!=0)
                        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.features_bac);

                        textToSpeech.speak("you are in main menu. just swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);


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
                if (x1 > x2) {
                    firstTime = 1;
                    textToSpeech.stop();
                    startVoiceInput();
                }
                break;
        }
        return false;
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mVoiceInputTv.setText(result.get(0));
                if (mVoiceInputTv.getText().toString().equals("exit")) {
                    finishAffinity();
                    System.exit(0);
                }

                else if (mVoiceInputTv.getText().toString().equals("time and date")) {
                    Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("battery")) {
                    Intent intent = new Intent(getApplicationContext(), Battery.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("location")) {
                    Intent intent = new Intent(getApplicationContext(), CurrentLocation.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("calculator")) {
                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("expression")) {
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("weather")) {
                    Intent intent = new Intent(getApplicationContext(), weatherHome.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("message")) {
                    Intent intent = new Intent(getApplicationContext(), Message.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("call")) {
                    Intent intent = new Intent(getApplicationContext(), Call.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().equals("read")) {
                    Intent intent = new Intent(getApplicationContext(), com.example.imagepro.reader.CameraRead.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }


//               else if (mVoiceInputTv.getText().toString().equals("yes")) {
//                    textToSpeech.speak("  Say Read for reading,  calculator for calculator,  time and date,  battery for battery. Do you want to listen again", TextToSpeech.QUEUE_FLUSH, null);
//                    mVoiceInputTv.setText(null);
//                }
//               else if ((mVoiceInputTv.getText().toString().equals("no"))) {
//                    textToSpeech.speak("then Swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);
//
//                }
                else {
                    textToSpeech.speak("Do not understand just Swipe right  Say again", TextToSpeech.QUEUE_FLUSH, null);
                }



            }
        }


    }
    public void onDestroy(){
        if (mVoiceInputTv.getText().toString().contains("exit")){
            finish();
        }
        super.onDestroy();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}