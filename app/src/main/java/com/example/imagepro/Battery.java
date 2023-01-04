package com.example.imagepro;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Battery extends AppCompatActivity {
    TextView text;
    float x1, x2, y1, y2;
    TextToSpeech textToSpeech;

//    public boolean onKeyDown(int keyCode, @Nullable KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            final Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //textToSpeech.speak("you are in main menu. just swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);
//
//                }
//            }, 1000);
//
//        }
//        return true;
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        text = findViewById(R.id.text);
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

                    BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    text.setText(percentage + " %");
                    text.getText().toString();
                    if (percentage < 50) {
                        textToSpeech.speak("Battery Percentage is" + percentage + " %" + ".please charge the phone", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("swipe left to listen again or swipe right to return back in main menu", TextToSpeech.QUEUE_ADD, null);

                    } else {
                        textToSpeech.speak("Battery percentage is" + percentage + "%." + "Mobile does not require charging ", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("swipe left to listen again or swipe right to return back to main menu", TextToSpeech.QUEUE_ADD, null);
                    }
                }
            }

        });


    }
    public void onBackPressed() {
        textToSpeech.stop();
        Intent intent = new Intent(Battery.this, Features.class);
        startActivity(intent);

    }
    public boolean onTouchEvent( MotionEvent touchEvent) {

        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1<x2){
                    textToSpeech.stop();
                    BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    text.setText("Battery Percentage is " + percentage + " %");
                    text.getText().toString();
                    textToSpeech.speak("Battery percentage is" + percentage + "%", TextToSpeech.QUEUE_FLUSH, null);

                }
                if (x1 > x2) {
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            textToSpeech.speak("you are in main menu just swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);
//                        }
//                    },1000);
                    textToSpeech.stop();
                    Intent intent= new Intent(Battery.this,Features.class);
                    startActivity(intent);
                }

                break;
        }

        return false;
    }



    public void onDestroy(){
        if (text.getText().toString().equals("exit")){
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